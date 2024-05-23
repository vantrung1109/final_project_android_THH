package com.example.projectfinaltth;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private InstructorCourseAdapter instructorCourseAdapter;
    private List<InstructorCourse> instructorCourseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructor_course);
        recyclerView = findViewById(R.id.recyclerView);

        instructorCourseList = new ArrayList<>();
        instructorCourseList.add(new InstructorCourse("HTML and CSS", "Web Development", "50$", " okok", R.drawable.avatar));
        instructorCourseAdapter = new InstructorCourseAdapter(instructorCourseList);
        recyclerView.setAdapter(instructorCourseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}