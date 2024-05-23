package com.example.projectfinaltth.ui.model_temp;

public class Course {
    private String title;
    private String author;
    private int imageResId;

    public Course(String title, String author, int imageResId) {
        this.title = title;
        this.author = author;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getImageResId() {
        return imageResId;
    }
}
