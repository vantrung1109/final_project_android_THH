package com.example.projectfinaltth.data.model.response.course;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseItem {
    @SerializedName("_id")
    private String id;
    private String userId;
    private String cloudinary;
    private String title;
    private String topic;
    private String picture;
    private String description;
    private double price;
    private boolean visibility;
    private boolean status;
    private String createdAt;
    private String updatedAt;
    private int __v;
}
