package com.example.projectfinaltth.ui.instructor;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.CourseIdRequest;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.data.model.response.lesson.LessonItem;
import com.example.projectfinaltth.ui.adapter.InstructorLessonAdapter;
import com.google.android.material.snackbar.Snackbar;

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

    private TextView tvCourseName, tvCourseAuthor, tvDescriptionContent, tvCoursePrice;
    private RatingBar ratingBar;
    private ImageView imgCourseIntro;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_lesson);

        // Initialize views
        lessonsRecyclerView = findViewById(R.id.recyclerView);
        tvCourseName = findViewById(R.id.tv_course_name);
        tvCourseAuthor = findViewById(R.id.tv_course_author);
        tvDescriptionContent = findViewById(R.id.tv_description_content);
        tvCoursePrice = findViewById(R.id.tv_course_price);
        //ratingBar = findViewById(R.id.tv_rating);
        imgCourseIntro = findViewById(R.id.img_course_intro);

        lessonsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        lessonItemList = new ArrayList<>();
        lessonAdapter = new InstructorLessonAdapter(this, lessonItemList, new InstructorLessonAdapter.OnItemInteractionListener() {
            @Override
            public void onDeleteLesson(int position, LessonItem lessonItem) {
                deleteLessonItem(position, lessonItem);
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
        CourseIntroResponse courseIntroResponse = (CourseIntroResponse) getIntent().getSerializableExtra("courseIntro");
        if (courseIntroResponse == null) {
            Toast.makeText(this, "No course data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate course details
        tvCourseName.setText(courseIntroResponse.getCourse().getTitle());
        tvCourseAuthor.setText(courseIntroResponse.getCourse().getInstructorName());
        tvDescriptionContent.setText(courseIntroResponse.getCourse().getDescription());
        tvCoursePrice.setText(String.valueOf(courseIntroResponse.getCourse().getPrice()));
        //ratingBar.setRating(courseIntroResponse.getAverageStars().floatValue());
        Glide.with(this).load(courseIntroResponse.getCourse().getPicture()).into(imgCourseIntro);
    }

    private void loadCourseLessons(String courseId) {
        compositeDisposable.add(
                ApiService.apiService.getInstructorLessons(new CourseIdRequest(courseId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(lessonListResponse -> {
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
        String token = DataLocalManager.getToken();
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
                                        Log.e("InstructorLesson", "Error removing item: " + throwable.getMessage());
                                        Snackbar.make(lessonsRecyclerView, "Error removing item: " + throwable.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                            )
            );
        } else {
            Log.e("InstructorLesson", "Token is null");
            Snackbar.make(lessonsRecyclerView, "Token is null", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
