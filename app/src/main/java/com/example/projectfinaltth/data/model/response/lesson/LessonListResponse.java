package com.example.projectfinaltth.data.model.response.lesson;
import com.example.projectfinaltth.data.model.response.course.CourseItem;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonListResponse {
    private List<LessonItem> lessons;
    String courseId;
}
