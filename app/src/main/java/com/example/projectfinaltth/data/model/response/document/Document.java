package com.example.projectfinaltth.data.model.response.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    String _id;
    String lessonId;
    String cloudinary;
    String title;
    String description;
    String content;
    String createdAt;
    String updatedAt;
    Integer __v;
}
