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
import com.example.projectfinaltth.ui.adapter.MyCourseAdapter;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CourseFragment extends Fragment {
    private RecyclerView courseRecyclerView; // RecyclerView để hiển thị danh sách khóa học
    private MyCourseAdapter courseAdapter; // Adapter cho RecyclerView
    private CompositeDisposable compositeDisposable = new CompositeDisposable(); // Quản lý các disposable để tránh rò rỉ bộ nhớ

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);

        // Liên kết RecyclerView từ layout
        courseRecyclerView = view.findViewById(R.id.courseRecyclerView);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // Đặt layout cho RecyclerView

        fetchUserCourses(); // Gọi phương thức để lấy danh sách khóa học của người dùng

        return view;
    }

    private void fetchUserCourses() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        Log.d("CourseFragment", "Token: " + token);

        compositeDisposable.add(
                ApiService.apiService.getMyCourses("Bearer " + token)
                        .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                        .subscribe(response -> {
                            // Cập nhật adapter với dữ liệu từ API
                            Log.d("CourseFragment", "Courses: " + response.getCourses());
                            courseAdapter = new MyCourseAdapter(getContext(), new ArrayList<>(response.getCourses()));
                            courseRecyclerView.setAdapter(courseAdapter); // Thiết lập adapter cho RecyclerView
                        }, throwable -> {
                            // Xử lý lỗi nếu có
                            Log.e("CourseFragment", "Error fetching courses", throwable);
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear(); // Xóa tất cả các disposable khi Fragment bị hủy
    }
}
