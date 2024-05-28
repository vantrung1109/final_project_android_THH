package com.example.projectfinaltth.data.model.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonRequest {
    private String courseId;
    private String title;
    private String description;


}
