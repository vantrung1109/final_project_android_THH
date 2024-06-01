package com.example.projectfinaltth.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.databinding.ActivityFragmentAccountBinding;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {
    ActivityFragmentAccountBinding mActivityFragmentAccountBinding; // Khai báo biến cho binding để liên kết giao diện
    CompositeDisposable compositeDisposable = new CompositeDisposable(); // Khai báo biến để quản lý các disposable từ RxJava

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityFragmentAccountBinding = ActivityFragmentAccountBinding.inflate(getLayoutInflater()); // Khởi tạo binding với layout inflater
        setContentView(mActivityFragmentAccountBinding.getRoot()); // Thiết lập nội dung giao diện từ binding
        fetchUserDetails(); // Gọi phương thức để lấy chi tiết người dùng
    }

    private void fetchUserDetails() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        //Log.e("",token);
        if (token != null) { // Kiểm tra token không null
            Log.d("AccountFragment", "Token: " + token); // Ghi lại token trong log để kiểm tra
            compositeDisposable.add(
                    ApiService.apiService.getUserDetails("Bearer " + token) // Gọi API để lấy chi tiết người dùng
                            .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                            .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                            .subscribe(userResponse -> {
                                // Thiết lập dữ liệu người dùng vào các view
                                mActivityFragmentAccountBinding.textName.setText(userResponse.getUser().getName()); // Hiển thị tên người dùng
                                mActivityFragmentAccountBinding.textEmail.setText(userResponse.getUser().getEmail()); // Hiển thị email người dùng
                                Glide.with(this).load(userResponse.getUser().getPicture()).into(mActivityFragmentAccountBinding.imgProfile); // Hiển thị ảnh đại diện người dùng
                            }, throwable -> {
                                Log.e("AccountFragment", "Error loading user details: " + throwable.getMessage()); // Ghi lại lỗi nếu có
                                Toast.makeText(ProfileActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                            })
            );

        } else {
            Log.e("AccountFragment", "Token is null"); // Ghi lại log khi token null
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear(); // Xóa tất cả disposable khi activity bị hủy
    }
}
