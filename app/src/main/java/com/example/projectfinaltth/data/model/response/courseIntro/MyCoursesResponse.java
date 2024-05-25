package com.example.projectfinaltth.data.model.response.courseIntro;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyCoursesResponse {
    @SerializedName("courses")
    private List<Course> courses;

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
