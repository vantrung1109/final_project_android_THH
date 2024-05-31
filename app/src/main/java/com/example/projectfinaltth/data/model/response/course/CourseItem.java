package com.example.projectfinaltth.data.model.response.course;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseItem implements Serializable {
    @SerializedName("_id")
    private String id;
    private String userId;
    private String cloudinary;
    private String title;
    private String topic;
    private String picture;
    private String description;
    private Double price;
    private Boolean visibility;
    private Boolean status;
    private String createdAt;
    private String updatedAt;
    private Integer __v;
}
