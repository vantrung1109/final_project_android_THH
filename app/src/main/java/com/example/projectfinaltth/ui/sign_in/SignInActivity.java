package com.example.projectfinaltth.ui.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.SignInRequest;
import com.example.projectfinaltth.databinding.ActivitySignInBinding;
import com.example.projectfinaltth.ui.main.MainActivity;
import com.example.projectfinaltth.ui.profile.ProfileActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding mActivitySignInBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignInBinding.getRoot());

        // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        // Xử lý sự kiện khi người dùng click vào nút đăng nhập
        mActivitySignInBinding.btnLogin.setOnClickListener(v -> {
            SignInRequest signInRequest = new SignInRequest();
            signInRequest.setEmail(mActivitySignInBinding.editEmail.getText().toString());
            signInRequest.setPassword(mActivitySignInBinding.editPassword.getText().toString());
            DataLocalManager.init(this);

            compositeDisposable.add(
                    // Thực hiện gọi API đăng nhập
                    ApiService.apiService.signIn(signInRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(signInResponse -> {

                                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                                // Lưu token vào SharePreferences
                                DataLocalManager.setToken(signInResponse.getToken());

                                Intent intent = new Intent(this, ProfileActivity.class);
                                startActivity(intent);

                                Log.e("TAG", "===============> Login Success: " + signInResponse.getToken());

                            }, throwable -> {
                                Log.e("TAG", "Error: " + throwable.getMessage());
                                Toast.makeText(this, "Bạn nhập sai emai hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                            }));
        });

    }


}
