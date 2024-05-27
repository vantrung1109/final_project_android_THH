package com.example.projectfinaltth.data.model.response.course;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseListResponse {
    private List<CourseItem> courses;
    String totalRevenuePersonal;
}
