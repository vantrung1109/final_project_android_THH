package com.example.projectfinaltth.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.data.model.response.courseIntro.MyCoursesResponse;
import com.example.projectfinaltth.ui.adapter.MyCourseAdapter;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CourseFragment extends Fragment {
    private RecyclerView courseRecyclerView;
    private MyCourseAdapter courseAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        courseRecyclerView = view.findViewById(R.id.courseRecyclerView);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchUserCourses();

        return view;
    }

    private void fetchUserCourses() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        Log.d("CourseFragment", "Token: " + token);

        compositeDisposable.add(
                ApiService.apiService.getMyCourses("Bearer " + token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            // Cập nhật adapter với dữ liệu từ API
                            Log.d("CourseFragment", "Courses: " + response.getCourses());
                            courseAdapter = new MyCourseAdapter(getContext(), new ArrayList<>(response.getCourses()));
                            courseRecyclerView.setAdapter(courseAdapter);
                        }, throwable -> {
                            // Xử lý lỗi nếu có
                            Log.e("CourseFragment", "Error fetching courses", throwable);
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
