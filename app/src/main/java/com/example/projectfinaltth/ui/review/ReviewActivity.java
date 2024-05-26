package com.example.projectfinaltth.ui.review;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.request.review.ReviewData;
import com.example.projectfinaltth.data.model.request.review.ReviewRequest;
import com.example.projectfinaltth.databinding.ActivityReviewBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ReviewActivity extends AppCompatActivity {
    ActivityReviewBinding mActivityReviewBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityReviewBinding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(mActivityReviewBinding.getRoot());

        String token = "Bearer " +
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI2NjQxMDBkNWFlYTg4NmIzMmVlNDNhZTEiLCJpYXQiOjE3MTY1ODYwODMsImV4cCI6MTcxNzQ1MDA4M30.qScWoSaR1ctGu9UZbnCrmHaNe82pwMUi7dPe1clMAZs";

        String courseId = "6640fd03aea886b32ee438c3";
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setCourseId(courseId);
        reviewRequest.setReviewData(new ReviewData(5.0, "haha"));
        // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        // Xử lý sự kiện khi người dùng click vào nút gửi đánh giá
        mActivityReviewBinding.btnSend.setOnClickListener(v -> {
            // Gửi đánh giá lên server
            compositeDisposable.add(ApiService.apiService.sendReview(token, courseId, reviewRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        // Xử lý kết quả trả về
                        Log.d("ReviewActivity", "onCreate: " + response.getMessage());
                    }, throwable -> {
                        // Xử lý lỗi
                    }));
        });
    }
}
