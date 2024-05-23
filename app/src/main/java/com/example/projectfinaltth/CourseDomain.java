package com.example.projectfinaltth;

public class CourseDomain {
    private String title;
    private String owner;
    private double price;
    private double star;
    private String imageUrl;

    public CourseDomain(String title, String owner, double price, double star, String imageUrl) {
        this.title = title;
        this.owner = owner;
        this.price = price;
        this.star = star;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getStar() {
        return star;
    }

    public void setStar(double star) {
        this.star = star;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
