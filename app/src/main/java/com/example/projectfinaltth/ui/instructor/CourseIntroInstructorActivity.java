package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.course.CourseItem;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.databinding.ActivityCourseIntroInstructorBinding;
import com.example.projectfinaltth.ui.main.MainInstructorActivity;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CourseIntroInstructorActivity extends AppCompatActivity {
    ActivityCourseIntroInstructorBinding mActivityCourseIntroInstructorBinding;
    private String courseId;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CourseIntroResponse courseIntent;
    FlexibleAdapter flexibleAdapterReviews;
    // 21110194 - Đặng Xuân Hùng


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCourseIntroInstructorBinding = ActivityCourseIntroInstructorBinding.inflate(getLayoutInflater());
        setContentView(mActivityCourseIntroInstructorBinding.getRoot());
        // Lấy courseId từ Intent
        courseId = getIntent().getStringExtra("courseId");
        // Hiển thị ProgressBar trong khi tải dữ liệu
        mActivityCourseIntroInstructorBinding.progressBar.setVisibility(ProgressBar.VISIBLE);
        // Gọi API lấy thông tin khóa học
        compositeDisposable.add(
                ApiService.apiService.getCourseIntroById(courseId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            // Set dữ liệu lên view
                            mActivityCourseIntroInstructorBinding.tvCourseName.setText(response.getCourse().getTitle());
                            mActivityCourseIntroInstructorBinding.tvCourseAuthor.setText(response.getCourse().getInstructorName());
                            mActivityCourseIntroInstructorBinding.tvDescriptionContent.setText(response.getCourse().getDescription());
                            mActivityCourseIntroInstructorBinding.tvCoursePrice.setText(String.valueOf(response.getCourse().getPrice()));
                            mActivityCourseIntroInstructorBinding.ratingBar.setRating(response.getAverageStars().floatValue());
                            Glide.with(this).load(response.getCourse().getPicture()).into(mActivityCourseIntroInstructorBinding.imgCourseIntro);
                            // Lưu response để sử dụng sau
                            courseIntent = response;
                            Log.e("CourseIntroInstructor", "courseIntro: " + courseIntent);
                            // Thiết lập adapter cho RecyclerView hiển thị đánh giá
                            flexibleAdapterReviews = new FlexibleAdapter(response.getReviews());
                            mActivityCourseIntroInstructorBinding.rcvReviews.setAdapter(flexibleAdapterReviews);
                            mActivityCourseIntroInstructorBinding.rcvReviews.setLayoutManager(new LinearLayoutManager(this));
                            // Ẩn ProgressBar sau khi dữ liệu đã tải xong
                            mActivityCourseIntroInstructorBinding.progressBar.setVisibility(ProgressBar.GONE);
                        }, throwable -> {
                            // Log lỗi và ẩn ProgressBar nếu có lỗi xảy ra
                            Log.e("TAG", "Error: " + throwable.getMessage());

                            mActivityCourseIntroInstructorBinding.progressBar.setVisibility(ProgressBar.GONE);
                        })
        );
        // Thiết lập sự kiện click cho nút Back
        mActivityCourseIntroInstructorBinding.buttonBack.setOnClickListener(v -> finish());
        // Thiết lập sự kiện click cho nút Edit Introduction
        mActivityCourseIntroInstructorBinding.btnEditIntro.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateCourseActivity.class);
            Bundle bundle = new Bundle();
            // Truyền dữ liệu khóa học sang UpdateCourseActivity
            bundle.putSerializable("courseIntro", courseIntent);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        // Thiết lập sự kiện click cho nút Edit Details
        mActivityCourseIntroInstructorBinding.btnEditDetail.setOnClickListener(v -> {
            Intent intent = new Intent(this, InstructorLessonActivity.class);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });
        // Thiết lập sự kiện click cho nút Delete Course
        mActivityCourseIntroInstructorBinding.btnDeleteCourse.setOnClickListener(v -> {
            String token = "Bearer " + DataLocalManager.getToken();
            // Gọi API để xóa khóa học
            compositeDisposable.add(
                    ApiService.apiService.deleteCourse(token, courseId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                Log.e("CourseIntroInstructor", "Delete course success");
                                Intent intent = new Intent(this, MainInstructorActivity.class);
                                startActivity(intent);

                            }, throwable -> {
                                Log.e("CourseIntroInstructor", "Delete course fail", throwable);
                            })
            );
        });
    }
}
