package com.example.projectfinaltth.ui.courseDetail;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.request.course_detail.CourseDetailRequest;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.data.model.response.lesson.Lesson;
import com.example.projectfinaltth.databinding.ActivityCourseDetailBinding;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
public class CourseDetailActivity extends AppCompatActivity{
    ActivityCourseDetailBinding mActivityCourseDetailBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FlexibleAdapter mFlexibleAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCourseDetailBinding = ActivityCourseDetailBinding.inflate(getLayoutInflater());
        setContentView(mActivityCourseDetailBinding.getRoot());

        // Lấy dữ liệu CoursenIntroResponse từ intent chuyển từ Course Intro
        Bundle bundle = getIntent().getExtras();
        CourseIntroResponse courseFromIntent;
        if (bundle != null) {
            courseFromIntent = (CourseIntroResponse) bundle.getSerializable("courseIntro");
        } else {
            return;
        }

        // Set dữ liệu vào view của cousre Detail
        mActivityCourseDetailBinding.tvCourseName.setText(courseFromIntent.getCourse().getTitle());
        mActivityCourseDetailBinding.tvCourseAuthor.setText(courseFromIntent.getCourse().getInstructorName());
        mActivityCourseDetailBinding.tvDescriptionContent.setText(courseFromIntent.getCourse().getDescription());
        mActivityCourseDetailBinding.tvCoursePrice.setText(String.valueOf(courseFromIntent.getCourse().getPrice()));
        mActivityCourseDetailBinding.ratingBar.setRating(courseFromIntent.getAverageStars().floatValue());
        Glide.with(this).load(courseFromIntent.getCourse().getPicture()).into(mActivityCourseDetailBinding.imgCourseIntro);

        mActivityCourseDetailBinding.progressBar.setVisibility(ProgressBar.VISIBLE);
        compositeDisposable.add(
                ApiService.apiService.getLessonsByCourse(new CourseDetailRequest(courseFromIntent.getCourse().get_id()))
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
                            }
                        )
        );

        mActivityCourseDetailBinding.buttonBack.setOnClickListener(v -> {
            this.finish();
        });

    }


}
