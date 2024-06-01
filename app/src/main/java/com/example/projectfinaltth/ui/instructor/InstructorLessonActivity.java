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
    String courseId;
    // 21110194 - Đặng Xuân Hùng


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo binding cho Activity
        mActivityCourseDetailInstructorBinding = ActivityCourseDetailInstructorBinding.inflate(getLayoutInflater());
        setContentView(mActivityCourseDetailInstructorBinding.getRoot());

        // Thiết lập LayoutManager cho RecyclerView
        mActivityCourseDetailInstructorBinding.rcvLessons.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo danh sách bài học và adapter

        lessonItemList = new ArrayList<>();
        lessonAdapter = new InstructorLessonAdapter(this, lessonItemList, new InstructorLessonAdapter.OnItemInteractionListener() {
            @Override
            public void onDeleteLesson(int position, LessonItem lessonItem) {
                // Xử lý sự kiện xóa bài học
                deleteLessonItem(position,lessonItem);
            }

            @Override
            public void onEditLesson(int position, LessonItem lessonItem) {
                Intent intent = new Intent(InstructorLessonActivity.this, UpdateLessonActivity.class);
                // Xử lý sự kiện chỉnh sửa bài học, truyền dữ liệu cho view update
                intent.putExtra("lessonId", lessonItem.getId());
                intent.putExtra("courseId", lessonItem.getCourseId());
                intent.putExtra("lessonTitle", lessonItem.getTitle());
                intent.putExtra("lessonDescription", lessonItem.getDescription());
                startActivityForResult(intent, 1);
            }
        });
        // Thiết lập adapter cho RecyclerView
        mActivityCourseDetailInstructorBinding.rcvLessons.setAdapter(lessonAdapter);

        // Lấy courseId từ Intent
        courseId = getIntent().getStringExtra("courseId");

        if (courseId != null && !courseId.isEmpty()) {
            loadCourseLessons(courseId);// Tải các bài học của khóa học
        } else {
            Log.e("InstructorLesson", "Course ID is null or empty");
        }
        // Thiết lập sự kiện click cho nút tạo bài học mới
        mActivityCourseDetailInstructorBinding.btnCreateLesson.setOnClickListener(v -> {
            // Gửi dữ liệu cần thiết sang activity mới
            Intent intent = new Intent(this, CreateLessonActivity.class);
            intent.putExtra("courseId", courseId); // Chuyển ID của khóa học
            startActivityForResult(intent, 1);
        });
        // Thiết lập sự kiện click cho nút quay lại
        mActivityCourseDetailInstructorBinding.buttonBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadCourseLessons(courseId);// Tải lại danh sách bài học sau khi cập nhật
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    // Phương thức để tải các bài học của khóa học
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
    // Phương thức để xóa một bài học
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
