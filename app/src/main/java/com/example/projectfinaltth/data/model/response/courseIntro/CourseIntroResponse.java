package com.example.projectfinaltth.data.model.response.courseIntro;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseIntroResponse {
    private Course course;
    private List<Review> reviews;
    private Double averageStars;
}
