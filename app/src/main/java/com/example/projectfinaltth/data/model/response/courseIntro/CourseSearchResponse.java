package com.example.projectfinaltth.data.model.response.courseIntro;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchResponse {
    private List<Course> course;
}
