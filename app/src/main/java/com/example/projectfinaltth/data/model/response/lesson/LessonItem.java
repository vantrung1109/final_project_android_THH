package com.example.projectfinaltth.data.model.response.lesson;

import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonItem {

    @SerializedName("_id")
    private String id;
    private String courseId;
    private String title;
    private String description;
    private boolean status;
    private String createdAt;
    private String updatedAt;
    private int __v;
}
