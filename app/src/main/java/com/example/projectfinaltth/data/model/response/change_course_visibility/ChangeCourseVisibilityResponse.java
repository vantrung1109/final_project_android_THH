package com.example.projectfinaltth.data.model.response.change_course_visibility;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeCourseVisibilityResponse {
    String success;
    String error;
    Boolean visibility;
}
