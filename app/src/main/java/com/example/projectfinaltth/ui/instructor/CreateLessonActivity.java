package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.LessonRequest;
import com.example.projectfinaltth.data.model.response.lesson.LessonItem;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CreateLessonActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button btnCreateLesson;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String courseId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_lesson);

        courseId = getIntent().getStringExtra("courseId");

        etTitle = findViewById(R.id.eTextTitle);
        etDescription = findViewById(R.id.eTextDescription);
        btnCreateLesson = findViewById(R.id.btn_createLesson);

        btnCreateLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createLesson();
            }
        });

        ImageView btnBack = findViewById(R.id.button_back);
        btnBack.setOnClickListener(v -> finish());
    }
    // 21110194 - Đặng Xuân Hùng

    private void createLesson() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String courseId = getIntent().getStringExtra("courseId");
        // Bạn có thể lấy courseId từ intent nếu cần
        String token = DataLocalManager.getToken(); // Lấy token từ local storage

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(courseId, title, description);

        if (token != null) {
            compositeDisposable.add(
                    ApiService.apiService.createLesson("Bearer " + token, lessonRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(lessonItem -> {
                                // Tạo bài học thành công, chuyển đến InstructorLessonActivity
                                Intent resultIntent = new Intent();
                                setResult(RESULT_OK, resultIntent);
                                finish();
                            }, throwable -> {
                                Log.e("CreateLesson", "Error creating lesson: " + throwable.getMessage());
                                Toast.makeText(CreateLessonActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );
        } else {
            Log.e("CreateLesson", "Token is null");
            Toast.makeText(this, "Token is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
