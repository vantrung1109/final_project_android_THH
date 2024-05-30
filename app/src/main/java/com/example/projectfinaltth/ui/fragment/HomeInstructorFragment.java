package com.example.projectfinaltth.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.course.CourseItem;
import com.example.projectfinaltth.data.model.response.profile.User;
import com.example.projectfinaltth.ui.adapter.InstructorCourseAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeInstructorFragment extends Fragment {

    private RecyclerView instructorCoursesRecyclerView;
    private InstructorCourseAdapter instructorCourseAdapter;
    private List<CourseItem> courseItemList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<User> userCurrent = new MutableLiveData<>();

    public HomeInstructorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.e("course", "Account");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("course", "Reload Account");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_instructor_course, container, false);

        instructorCoursesRecyclerView = view.findViewById(R.id.recyclerView);
        instructorCoursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        courseItemList = new ArrayList<>();
        instructorCourseAdapter = new InstructorCourseAdapter(getContext(), courseItemList, new InstructorCourseAdapter.OnItemInteractionListener() {
            @Override
            public void onDeleteCourse(int position, CourseItem courseItem) {
                deleteCourseItem(position, courseItem);
            }

        });

        instructorCoursesRecyclerView.setAdapter(instructorCourseAdapter);

        // Nhận instructorId từ Intent hoặc bất kỳ nguồn nào khác
        String instructorId = "1";

        compositeDisposable.add(
                ApiService.apiService.getUserDetails("Bearer " + DataLocalManager.getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            userCurrent.setValue(user.getUser());
                        }, throwable -> {
                            Log.e("MyInstructorCourse", "Error loading user: " + throwable.getMessage());
                        })
        );

        if (instructorId != null && !instructorId.isEmpty()) {
            userCurrent.observe(getViewLifecycleOwner(), user -> {
                loadInstructorCourses(userCurrent.getValue().getId());
            });
        } else {
            Log.e("MyInstructorCourse", "Instructor ID is null or empty");
        }

        return view;
    }

    private void loadInstructorCourses(String instructorId) {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        Log.e("MyInstructorCourse", "Token: " + token);
        if (token != null) {
            Log.d("MyInstructorCourse", "Token: " + token);
            compositeDisposable.add(
                    ApiService.apiService.getInstructorCourses("Bearer " + token, instructorId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(courseListResponse -> {
                                if (courseListResponse != null && courseListResponse.getCourses() != null) {
                                    List<CourseItem> filteredCourses = new ArrayList<>();
                                    for (CourseItem course : courseListResponse.getCourses()) {
                                        if (course.isStatus())
                                            filteredCourses.add(course);
                                    }
                                    courseItemList.clear();
                                    courseItemList.addAll(filteredCourses);
                                    instructorCourseAdapter.notifyDataSetChanged();
                                } else {
                                    Log.e("MyInstructorCourse", "Course response is null or has no items");
                                }
                            }, throwable -> {
                                Log.e("MyInstructorCourse", "Error loading courses: " + throwable.getMessage());
                            })
            );
        } else {
            Log.e("MyInstructorCourse", "Token is null");
        }
    }

    private void deleteCourseItem(int position, CourseItem courseItem) {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        // Xóa mục khỏi giao diện ngay lập tức
        courseItemList.remove(position);
        instructorCourseAdapter.notifyItemRemoved(position);
        instructorCourseAdapter.notifyItemRangeChanged(position, courseItemList.size());

        if (token != null) {
            compositeDisposable.add(
                    ApiService.apiService.deleteCourse("Bearer " + token, courseItem.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        Snackbar.make(instructorCoursesRecyclerView, "Delete successful", Snackbar.LENGTH_SHORT).show();
                                    },
                                    throwable -> {
                                        Log.e("CartFragment", "Error removing item from cart: " + throwable.getMessage());
                                        Snackbar.make(instructorCoursesRecyclerView, "Error removing item: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                            )
            );
        } else {
            Log.e("CartFragment", "Token is null");
            Snackbar.make(instructorCoursesRecyclerView, "Token is null", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
