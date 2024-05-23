package com.example.projectfinaltth;

public class InstructorCourse {
    private String title;
    private String type;
    private String price;
    private String description;
    private int imageResource;

    // Constructor, getters, and setters
    public InstructorCourse(String title, String type, String price, String description, int imageResource) {
        this.title = title;
        this.type = type;
        this.price = price;
        this.description = description;
        this.imageResource = imageResource;
    }

    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getPrice() { return price; }
    public String getDescription() { return description; }
    public int getImageResource() { return imageResource; }
}
