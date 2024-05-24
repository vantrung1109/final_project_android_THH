package com.example.projectfinaltth.data.model.response.courseIntro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    String _id;
    String userId;
    String cloudinary;
    String title;
    String topic;
    String picture;
    String description;
    Double price;
    Boolean visibility;
    Boolean status;
    String createdAt;
    String updatedAt;
    Integer __v;
    String instructorName;
}
