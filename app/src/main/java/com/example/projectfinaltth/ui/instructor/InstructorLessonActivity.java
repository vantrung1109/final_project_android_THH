package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.CourseIdRequest;
import com.example.projectfinaltth.data.model.response.course.CourseItem;
import com.example.projectfinaltth.data.model.response.lesson.LessonItem;
import com.example.projectfinaltth.databinding.ActivityCourseDetailBinding;
import com.example.projectfinaltth.databinding.ActivityCourseDetailInstructorBinding;
import com.example.projectfinaltth.databinding.ActivityCourseLessonBinding;
import com.example.projectfinaltth.ui.adapter.InstructorLessonAdapter;
import com.example.projectfinaltth.ui.courseDetail.CourseDetailActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InstructorLessonActivity extends AppCompatActivity {


    private InstructorLessonAdapter lessonAdapter;
    private List<LessonItem> lessonItemList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    ActivityCourseDetailInstructorBinding mActivityCourseDetailInstructorBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityCourseDetailInstructorBinding = ActivityCourseDetailInstructorBinding.inflate(getLayoutInflater());
        setContentView(mActivityCourseDetailInstructorBinding.getRoot());


        mActivityCourseDetailInstructorBinding.rcvLessons.setLayoutManager(new LinearLayoutManager(this));

        lessonItemList = new ArrayList<>();
        lessonAdapter = new InstructorLessonAdapter(this, lessonItemList, new InstructorLessonAdapter.OnItemInteractionListener() {
            @Override
            public void onDeleteLesson(int position, LessonItem lessonItem) {
                deleteLessonItem(position,lessonItem);
            }
        });

        mActivityCourseDetailInstructorBinding.rcvLessons.setAdapter(lessonAdapter);

        // Get courseId from Intent or other sources
        String courseId = getIntent().getStringExtra("courseId");

        if (courseId != null && !courseId.isEmpty()) {
            loadCourseLessons(courseId);
        } else {
            Log.e("InstructorLesson", "Course ID is null or empty");
        }

        mActivityCourseDetailInstructorBinding.btnCreateLesson.setOnClickListener(v -> {
            // Gửi dữ liệu cần thiết sang activity mới
            Intent intent = new Intent(this, CreateLessonActivity.class);
            intent.putExtra("courseId", courseId); // Chuyển ID của khóa học
            startActivity(intent);
        });
    }

    private void loadCourseLessons(String courseId) {
        compositeDisposable.add(
                ApiService.apiService.getInstructorLessons(new CourseIdRequest(courseId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(lessonListResponse -> {
                            Log.e("kaka", lessonListResponse.getLessons().toString());
                            if (lessonListResponse != null && lessonListResponse.getLessons() != null) {
                                lessonItemList.clear();
                                lessonItemList.addAll(lessonListResponse.getLessons());
                                lessonAdapter.notifyDataSetChanged();
                            } else {
                                Log.e("InstructorLesson", "Lesson response is null or has no items");
                            }
                        }, throwable -> {
                            Log.e("InstructorLesson", "Error loading lessons: " + throwable.getMessage());
                        })
        );

    }
    private void deleteLessonItem(int position, LessonItem lessonItem) {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        // Xóa mục khỏi giao diện ngay lập tức
        lessonItemList.remove(position);
        lessonAdapter.notifyItemRemoved(position);
        lessonAdapter.notifyItemRangeChanged(position, lessonItemList.size());

        if (token != null) {
            compositeDisposable.add(
                    ApiService.apiService.deleteLesson("Bearer " + token, lessonItem.getId())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    },
                                    throwable -> {
                                        Log.e("CartFragment", "Error removing item from cart: " + throwable.getMessage());
                                        Toast.makeText(this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            )
            );
        } else {
            Log.e("CartFragment", "Token is null");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
