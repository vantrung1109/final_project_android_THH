package com.example.projectfinaltth.ui.comment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.comment.CreateCommentRequest;
import com.example.projectfinaltth.data.model.request.review.ReviewData;
import com.example.projectfinaltth.data.model.request.review.ReviewRequest;
import com.example.projectfinaltth.data.model.response.lesson.Lesson;
import com.example.projectfinaltth.databinding.ActivityCommentBinding;
import com.example.projectfinaltth.databinding.ActivityReviewBinding;
import com.example.projectfinaltth.ui.lesson_detail.LessonDetailActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding mActivityCommentBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCommentBinding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(mActivityCommentBinding.getRoot());

        String token = "Bearer " + DataLocalManager.getToken();
        Bundle bundleGet = getIntent().getExtras();
        Lesson lesson = (Lesson) bundleGet.getSerializable("lesson");

        mActivityCommentBinding.buttonBack.setOnClickListener(v -> finish());
        // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        // Xử lý sự kiện khi người dùng click vào nút gửi đánh giá
        mActivityCommentBinding.btnSend.setOnClickListener(v -> {
            CreateCommentRequest commentRequest = new CreateCommentRequest();
            commentRequest.setLessonId(lesson.get_id());
            commentRequest.setContent(mActivityCommentBinding.editComment.getText().toString());

            // Gửi đánh giá lên server
            compositeDisposable.add(ApiService.apiService.createComment(token, commentRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Toast.makeText(this, "Tạo bình luận thành công", Toast.LENGTH_SHORT).show();
                        Intent resultIntent = new Intent();
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }, throwable -> {
                        Toast.makeText(this, "Tạo bình luận thất bại", Toast.LENGTH_SHORT).show();
                        finish();
                    }));
        });
    }
}
