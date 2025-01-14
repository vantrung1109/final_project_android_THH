package com.example.projectfinaltth.ui.review;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.review.ReviewData;
import com.example.projectfinaltth.data.model.request.review.ReviewRequest;
import com.example.projectfinaltth.databinding.ActivityReviewBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
public class ReviewActivity extends AppCompatActivity {
    ActivityReviewBinding mActivityReviewBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityReviewBinding = ActivityReviewBinding.inflate(getLayoutInflater());
        setContentView(mActivityReviewBinding.getRoot());

        // Lấy token từ DataLocalManager
        String token = "Bearer " + DataLocalManager.getToken();
        // Lấy courseId từ intent
        String courseId = getIntent().getStringExtra("courseId");

        mActivityReviewBinding.buttonBack.setOnClickListener(v -> finish());
        // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        // Xử lý sự kiện khi người dùng click vào nút gửi đánh giá
        mActivityReviewBinding.btnSend.setOnClickListener(v -> {
            ReviewRequest reviewRequest = new ReviewRequest();
            reviewRequest.setCourseId(courseId);
            reviewRequest.setReviewData(new ReviewData(
                    (double) mActivityReviewBinding.ratingBar.getRating(),
                    mActivityReviewBinding.editReview.getText().toString()));

            // Gửi đánh giá lên server
            compositeDisposable.add(ApiService.apiService.sendReview(token, courseId, reviewRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }, throwable -> {
                        Toast.makeText(this, "Bạn đã đánh giá khóa học này rồi", Toast.LENGTH_SHORT).show();
                        finish();
                    }));
        });
    }
}
