package com.example.projectfinaltth.ui.instructor;


public class CourseLesson {
    private String title;
    private String description;
    private int imageResource;

    // Constructor, getters, and setters
    public CourseLesson(String title,String description, int imageResource) {
        this.title = title;

        this.description = description;
        this.imageResource = imageResource;
    }

    public String getTitle() { return title; }

    public String getDescription() { return description; }
    public int getImageResource() { return imageResource; }
}
