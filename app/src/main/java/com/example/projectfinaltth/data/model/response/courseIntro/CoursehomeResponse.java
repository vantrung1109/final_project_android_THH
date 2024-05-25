package com.example.projectfinaltth.data.model.response.courseIntro;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursehomeResponse {
    private List<Course> courses;
}
