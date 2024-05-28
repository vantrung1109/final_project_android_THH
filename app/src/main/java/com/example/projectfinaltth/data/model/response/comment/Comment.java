package com.example.projectfinaltth.data.model.response.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    String _id;
    String lessonId;
    String userId;
    String content;
    String createdAt;
    String updatedAt;
    String userName;
    String userPicture;

}
