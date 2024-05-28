package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.LessonRequest;
import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UpdateLessonActivity extends AppCompatActivity {

    private TextInputEditText eTextTitle;
    private TextInputEditText eTextDescription;
    private Button btnUpdateLesson;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String lessonId;
    private String courseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_lesson);

        eTextTitle = findViewById(R.id.eTextTitle);
        eTextDescription = findViewById(R.id.eTextDescription);
        btnUpdateLesson = findViewById(R.id.btn_updateLesson);

        lessonId = getIntent().getStringExtra("lessonId");
        courseId = getIntent().getStringExtra("courseId");

        // Load existing lesson data if available
        loadLessonData();

        btnUpdateLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLesson();
            }
        });
    }

    private void loadLessonData() {

        String lessonTitle = getIntent().getStringExtra("lessonTitle");
        String lessonDescription = getIntent().getStringExtra("lessonDescription");

        if (lessonTitle != null) {
            eTextTitle.setText(lessonTitle);
        }
        if (lessonDescription != null) {
            eTextDescription.setText(lessonDescription);
        }
    }

    private void updateLesson() {
        String title = eTextTitle.getText().toString();
        String description = eTextDescription.getText().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(courseId, title, description);

        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        if (token != null) {
            compositeDisposable.add(
                    ApiService.apiService.updateLesson("Bearer " + token,lessonId, lessonRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe((lessonItem) -> {
                                Toast.makeText(UpdateLessonActivity.this, "Lesson updated successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(UpdateLessonActivity.this, InstructorLessonActivity.class);
                                intent.putExtra("courseId", courseId);
                                startActivity(intent);
                            }, throwable -> {
                                Log.e("UpdateLessonActivity", "Error updating lesson: " + throwable.getMessage());
                                Toast.makeText(UpdateLessonActivity.this, "Error updating lesson: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );
        } else {
            Log.e("UpdateLessonActivity", "Token is null");
            Toast.makeText(this, "Token is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
