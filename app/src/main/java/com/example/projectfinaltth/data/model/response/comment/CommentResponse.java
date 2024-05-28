package com.example.projectfinaltth.data.model.response.comment;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    List<Comment> comments;
    String lessonId;
}
