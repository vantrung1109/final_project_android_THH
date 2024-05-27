package com.example.projectfinaltth.ui.courseIntro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.SignInRequest;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.MyCoursesResponse;
import com.example.projectfinaltth.databinding.ActivityCourseIntroBinding;
import com.example.projectfinaltth.ui.courseDetail.CourseDetailActivity;

import java.util.ArrayList;
import java.util.List;

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
    MutableLiveData<MyCoursesResponse> listMyCourses = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        mActivityCourseIntroBinding = ActivityCourseIntroBinding.inflate(getLayoutInflater());
        setContentView(mActivityCourseIntroBinding.getRoot());

        CourseIntroResponse course;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            course = (CourseIntroResponse) bundle.getSerializable("course");
        } else {
            course = null;
        }

        // Thực hiện set dữ liệu lên view sau khi lấy được dữ liệu từ intent
        mActivityCourseIntroBinding.tvCourseName.setText(course.getCourse().getTitle());
        mActivityCourseIntroBinding.tvCourseAuthor.setText(course.getCourse().getInstructorName());
        mActivityCourseIntroBinding.tvDescriptionContent.setText(course.getCourse().getDescription());
        mActivityCourseIntroBinding.tvCoursePrice.setText(String.valueOf(course.getCourse().getPrice()));
        mActivityCourseIntroBinding.ratingBar.setRating(course.getAverageStars().floatValue());
        Glide.with(this).load(course.getCourse().getPicture()).into(mActivityCourseIntroBinding.imgCourseIntro);
        flexibleAdapterReviews = new FlexibleAdapter(course.getReviews());
        mActivityCourseIntroBinding.rcvReviews.setAdapter(flexibleAdapterReviews);
        mActivityCourseIntroBinding.rcvReviews.setLayoutManager(new LinearLayoutManager(this));

        String token = "Bearer " +
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NjQxMDBkNWFlYTg4NmIzMmVlNDNhZTEiLCJpYXQiOjE3MTY1ODYwODMsImV4cCI6MTcxNzQ1MDA4M30.qScWoSaR1ctGu9UZbnCrmHaNe82pwMUi7dPe1clMAZs";


        mActivityCourseIntroBinding.progressBar.setVisibility(ProgressBar.VISIBLE);
        compositeDisposable.add(
                ApiService.apiService.getMyCourses(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            listMyCourses.setValue(response);
                        }, throwable -> {
                            Log.e("TAG", "Error: " + throwable.getMessage());
                        }
                        )
        );

        listMyCourses.observe(this, myCoursesResponse -> {
            if (myCoursesResponse != null) {
                List<String> listCourseId = new ArrayList<>();
                for (Course course_temp : myCoursesResponse.getCourses()) {
                    listCourseId.add(course_temp.get_id());
                }
                if (listCourseId.contains(course.getCourse().get_id())) {
                    mActivityCourseIntroBinding.imgAddToCart.setImageResource(R.drawable.eye);
                    mActivityCourseIntroBinding.imgAddToCart.setBackground(getResources().getDrawable(R.drawable.background_custom_border_blue, null));
                    mActivityCourseIntroBinding.tvAddToCart.setText("Detail");
                    mActivityCourseIntroBinding.progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

//        compositeDisposable.add(
//                // Thực hiện gọi API lấy course by id
//                ApiService.apiService.getCourseIntroById(courseId)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(response -> {
//
//                            // Thực hiện set dữ liệu lên view
//                            mActivityCourseIntroBinding.tvCourseName.setText(response.getCourse().getTitle());
//                            mActivityCourseIntroBinding.tvCourseAuthor.setText(response.getCourse().getInstructorName());
//                            mActivityCourseIntroBinding.tvDescriptionContent.setText(response.getCourse().getDescription());
//                            mActivityCourseIntroBinding.tvCoursePrice.setText(String.valueOf(response.getCourse().getPrice()));
//                            mActivityCourseIntroBinding.ratingBar.setRating(response.getAverageStars().floatValue());
//                            Glide.with(this).load(response.getCourse().getPicture()).into(mActivityCourseIntroBinding.imgCourseIntro);
//
//                            flexibleAdapterReviews = new FlexibleAdapter(response.getReviews());
//                            mActivityCourseIntroBinding.rcvReviews.setAdapter(flexibleAdapterReviews);
//                            mActivityCourseIntroBinding.rcvReviews.setLayoutManager(new LinearLayoutManager(this));
//
//                            }, throwable -> {
//                                Log.e("TAG", "Error: " + throwable.getMessage());
//                            }
//                        )
//        );

        mActivityCourseIntroBinding.imgAddToCart.setOnClickListener(v -> {
            if (mActivityCourseIntroBinding.tvAddToCart.getText().equals("Detail")) {
                Intent intent = new Intent(this, CourseDetailActivity.class);
                Bundle bundle2 = new Bundle();
                bundle.putSerializable("course", course);
                intent.putExtras(bundle2);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Add to cart", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
