package com.example.projectfinaltth.data.model.response.cart;

import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @SerializedName("_id")
    private String id;

    @SerializedName("cartId")
    private String cartId;

    @SerializedName("courseId")
    private String courseId;

    @SerializedName("course")
    private Course course;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("__v")
    private int v;
}
