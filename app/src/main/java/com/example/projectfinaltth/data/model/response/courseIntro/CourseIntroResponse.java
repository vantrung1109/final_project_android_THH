package com.example.projectfinaltth.data.model.response.courseIntro;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseIntroResponse implements Serializable {
    private Course course;
    private List<Review> reviews;
    private Double averageStars;


}
