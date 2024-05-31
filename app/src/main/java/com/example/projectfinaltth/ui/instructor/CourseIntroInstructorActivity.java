package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.response.course.CourseItem;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.databinding.ActivityCourseIntroInstructorBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCourseIntroInstructorBinding = ActivityCourseIntroInstructorBinding.inflate(getLayoutInflater());
        setContentView(mActivityCourseIntroInstructorBinding.getRoot());

        courseId = getIntent().getStringExtra("courseId");

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
                            courseIntent = response;
                            Log.e("CourseIntroInstructor", "courseIntro: " + courseIntent);
                            flexibleAdapterReviews = new FlexibleAdapter(response.getReviews());
                            mActivityCourseIntroInstructorBinding.rcvReviews.setAdapter(flexibleAdapterReviews);
                            mActivityCourseIntroInstructorBinding.rcvReviews.setLayoutManager(new LinearLayoutManager(this));
                            mActivityCourseIntroInstructorBinding.progressBar.setVisibility(ProgressBar.GONE);
                        }, throwable -> {
                            Log.e("TAG", "Error: " + throwable.getMessage());
                            mActivityCourseIntroInstructorBinding.progressBar.setVisibility(ProgressBar.GONE);
                        })
        );

        mActivityCourseIntroInstructorBinding.buttonBack.setOnClickListener(v -> finish());
        mActivityCourseIntroInstructorBinding.btnEditIntro.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateCourseActivity.class);
            Bundle bundle = new Bundle();

            bundle.putSerializable("courseIntro", courseIntent);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        mActivityCourseIntroInstructorBinding.btnEditDetail.setOnClickListener(v -> {
            Intent intent = new Intent(this, InstructorLessonActivity.class);
            intent.putExtra("courseId", courseId);
            startActivity(intent);
        });
    }
}
