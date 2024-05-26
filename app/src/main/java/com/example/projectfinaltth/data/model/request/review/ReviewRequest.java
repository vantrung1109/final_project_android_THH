package com.example.projectfinaltth.data.model.request.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    String courseId;
    ReviewData reviewData;
}
