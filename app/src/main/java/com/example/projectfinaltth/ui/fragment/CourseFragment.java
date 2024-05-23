package com.example.projectfinaltth.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.ui.model_temp.Course;
import com.example.projectfinaltth.ui.adapter.MyCourseAdapter;
import com.example.projectfinaltth.R;

import java.util.ArrayList;
import java.util.List;

public class CourseFragment extends Fragment {
    private RecyclerView courseRecyclerView;
    private MyCourseAdapter courseAdapter;
    private List<Course> courseList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        courseRecyclerView = view.findViewById(R.id.courseRecyclerView);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        courseList = new ArrayList<>();
        courseList.add(new Course("Course 1", "Author 1", R.drawable.pic1));
        courseList.add(new Course("Course 2", "Author 2", R.drawable.pic2));
        courseList.add(new Course("Course 1", "Author 1", R.drawable.pic1));
        courseList.add(new Course("Course 2", "Author 2", R.drawable.pic2));
        courseList.add(new Course("Course 1", "Author 1", R.drawable.pic1));
        courseList.add(new Course("Course 2", "Author 2", R.drawable.pic2));
        // Add more courses as needed

        courseAdapter = new MyCourseAdapter(getContext(), courseList);
        courseRecyclerView.setAdapter(courseAdapter);

        return view;
    }
}
