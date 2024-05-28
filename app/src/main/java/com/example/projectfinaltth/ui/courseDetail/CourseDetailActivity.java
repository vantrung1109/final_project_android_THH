package com.example.projectfinaltth.ui.courseDetail;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.request.course_detail.CourseDetailRequest;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.databinding.ActivityCourseDetailBinding;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
public class CourseDetailActivity extends AppCompatActivity {
    ActivityCourseDetailBinding mActivityCourseDetailBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FlexibleAdapter mFlexibleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCourseDetailBinding = ActivityCourseDetailBinding.inflate(getLayoutInflater());
        setContentView(mActivityCourseDetailBinding.getRoot());

        // Lấy dữ liệu CourseIntroResponse từ intent
        CourseIntroResponse courseIntroResponse = (CourseIntroResponse) getIntent().getSerializableExtra("courseIntro");
        if (courseIntroResponse == null) {
            Toast.makeText(this, "No course data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set dữ liệu vào view của Course Detail
        mActivityCourseDetailBinding.tvCourseName.setText(courseIntroResponse.getCourse().getTitle());
        mActivityCourseDetailBinding.tvCourseAuthor.setText(courseIntroResponse.getCourse().getInstructorName());
        mActivityCourseDetailBinding.tvDescriptionContent.setText(courseIntroResponse.getCourse().getDescription());
        mActivityCourseDetailBinding.tvCoursePrice.setText(String.valueOf(courseIntroResponse.getCourse().getPrice()));
        mActivityCourseDetailBinding.ratingBar.setRating(courseIntroResponse.getAverageStars().floatValue());
        Glide.with(this).load(courseIntroResponse.getCourse().getPicture()).into(mActivityCourseDetailBinding.imgCourseIntro);

        mActivityCourseDetailBinding.progressBar.setVisibility(ProgressBar.VISIBLE);
        compositeDisposable.add(
                ApiService.apiService.getLessonsByCourse(new CourseDetailRequest(courseIntroResponse.getCourse().get_id()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            mFlexibleAdapter = new FlexibleAdapter(response.getLessons());
                            mActivityCourseDetailBinding.rcvLessons.setAdapter(mFlexibleAdapter);
                            mActivityCourseDetailBinding.rcvLessons.setLayoutManager(new LinearLayoutManager(this));
                            mActivityCourseDetailBinding.progressBar.setVisibility(ProgressBar.GONE);
                        }, throwable -> {
                            Toast.makeText(this, "Lỗi gọi API Lessons", Toast.LENGTH_SHORT).show();
                            mActivityCourseDetailBinding.progressBar.setVisibility(ProgressBar.GONE);
                        })
        );

        mActivityCourseDetailBinding.buttonBack.setOnClickListener(v -> {
            this.finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
