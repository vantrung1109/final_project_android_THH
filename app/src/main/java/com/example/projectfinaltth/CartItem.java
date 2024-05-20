package com.example.projectfinaltth;

public class CartItem {
    private int imageResource;
    private String title;
    private String fee;
    private String author; // Thêm thuộc tính author

    public CartItem(int imageResource, String title, String author, String fee) {
        this.imageResource = imageResource;
        this.title = title;
        this.author = author;
        this.fee = fee; // Khởi tạo thuộc tính author
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getTitle() {
        return title;
    }

    public String getFee() {
        return fee;
    }

    public String getAuthor() {
        return author; // Getter cho author
    }
}
