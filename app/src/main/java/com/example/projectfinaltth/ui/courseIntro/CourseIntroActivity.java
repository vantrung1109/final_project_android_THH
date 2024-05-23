package com.example.projectfinaltth.ui.courseIntro;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.databinding.ActivityCourseIntroBinding;

public class CourseIntroActivity extends AppCompatActivity {
    ActivityCourseIntroBinding mActivityCourseIntroBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mActivityCourseIntroBinding = ActivityCourseIntroBinding.inflate(getLayoutInflater());
        Glide.with(this).load(R.drawable.hehe).into(mActivityCourseIntroBinding.imgCourseIntro);


        setContentView(mActivityCourseIntroBinding.getRoot());

    }
}
