package com.example.projectfinaltth.ui.courseIntro;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.SignInRequest;
import com.example.projectfinaltth.databinding.ActivityCourseIntroBinding;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
// Xử lý khi người dùng nhấn vào view intro của 1 khóa học
public class CourseIntroActivity extends AppCompatActivity {
    ActivityCourseIntroBinding mActivityCourseIntroBinding;
    FlexibleAdapter flexibleAdapterReviews;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityCourseIntroBinding = ActivityCourseIntroBinding.inflate(getLayoutInflater());

        setContentView(mActivityCourseIntroBinding.getRoot());

        String courseId = "6640fd03aea886b32ee438c3";

        //MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        // Xử lý sự kiện khi người dùng click vào nút đăng nhập

        compositeDisposable.add(
                // Thực hiện gọi API lấy course by id
                ApiService.apiService.getCourseIntroById(courseId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {

                            // Thực hiện set dữ liệu lên view
                            mActivityCourseIntroBinding.tvCourseName.setText(response.getCourse().getTitle());
                            mActivityCourseIntroBinding.tvCourseAuthor.setText(response.getCourse().getInstructorName());
                            mActivityCourseIntroBinding.tvDescriptionContent.setText(response.getCourse().getDescription());
                            mActivityCourseIntroBinding.tvCoursePrice.setText(String.valueOf(response.getCourse().getPrice()));
                            mActivityCourseIntroBinding.ratingBar.setRating(response.getAverageStars().floatValue());
                            Glide.with(this).load(response.getCourse().getPicture()).into(mActivityCourseIntroBinding.imgCourseIntro);

                            flexibleAdapterReviews = new FlexibleAdapter(response.getReviews());
                            mActivityCourseIntroBinding.rcvReviews.setAdapter(flexibleAdapterReviews);
                            mActivityCourseIntroBinding.rcvReviews.setLayoutManager(new LinearLayoutManager(this));

                            }, throwable -> {
                                Log.e("TAG", "Error: " + throwable.getMessage());
                            }
                        )
        );



    }
}
