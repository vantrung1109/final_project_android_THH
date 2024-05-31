package com.example.projectfinaltth.data.model.response.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateComment {
    String lessonId;
    String userId;
    String content;
    String _id;
    String createdAt;
    String updatedAt;
    Integer __v;
}
