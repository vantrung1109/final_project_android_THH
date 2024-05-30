package com.example.projectfinaltth.ui.instructor;

import android.os.Bundle;
import android.util.Log;

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
import com.example.projectfinaltth.ui.adapter.InstructorLessonAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InstructorLessonActivity extends AppCompatActivity {

    private RecyclerView lessonsRecyclerView;
    private InstructorLessonAdapter lessonAdapter;
    private List<LessonItem> lessonItemList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_lesson);

        lessonsRecyclerView = findViewById(R.id.recyclerView);
        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lessonItemList = new ArrayList<>();
        lessonAdapter = new InstructorLessonAdapter(this, lessonItemList, new InstructorLessonAdapter.OnItemInteractionListener() {
            @Override
            public void onDeleteLesson(int position, LessonItem lessonItem) {
                deleteLessonItem(position,lessonItem);
            }
        });

        lessonsRecyclerView.setAdapter(lessonAdapter);

        // Get courseId from Intent or other sources
        String courseId = getIntent().getStringExtra("courseId");

        if (courseId != null && !courseId.isEmpty()) {
            loadCourseLessons(courseId);
        } else {
            Log.e("InstructorLesson", "Course ID is null or empty");
        }
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
                                        Snackbar.make(lessonsRecyclerView, "Delete successful", Snackbar.LENGTH_SHORT).show();
                                    },
                                    throwable -> {
                                        Log.e("CartFragment", "Error removing item from cart: " + throwable.getMessage());
                                        Snackbar.make(lessonsRecyclerView, "Error removing item: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                            )
            );
        } else {
            Log.e("CartFragment", "Token is null");
            Snackbar.make(lessonsRecyclerView, "Token is null", Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
