package com.example.projectfinaltth.data.model.response.review;

import com.example.projectfinaltth.data.model.response.courseIntro.Review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    String message;
    Review review;
}
