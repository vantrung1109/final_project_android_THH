package com.example.projectfinaltth.data.model.response.lesson;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonByCourseResponse {
    String courseId;
    List<Lesson> lessons;
}
