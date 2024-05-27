package com.example.projectfinaltth.data.model.request;

public class CourseIdRequest {
    private String courseId;

    public CourseIdRequest(String courseId) {
        this.courseId = courseId;
    }
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
