package com.example.projectfinaltth.ui.sign_up;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.request.signup.SignUpRequest;
import com.example.projectfinaltth.databinding.ActivitySignUpBinding;

import com.example.projectfinaltth.ui.sign_in.SignInActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding mActivitySignUpBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignUpBinding.getRoot());

        // Xử lý sự kiện khi người dùng click vào nút Đăng ký
        mActivitySignUpBinding.btnSignUp.setOnClickListener(v -> {
            compositeDisposable.add(
                    ApiService.apiService.signUp(
                                    new SignUpRequest(
                                            mActivitySignUpBinding.editEmail.getText().toString(),
                                            mActivitySignUpBinding.editName.getText().toString(),
                                            mActivitySignUpBinding.editPassword.getText().toString(),
                                            mActivitySignUpBinding.editConfirmPassword.getText().toString()
                                    )
                            ).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                if (response.getSuccess() != null){
                                    // Hiển thị thông báo và chuyển sang màn hình nhập mã OTP
                                    Toast.makeText(this, response.getSuccess(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignUpActivity.this, OtpActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("email", mActivitySignUpBinding.editEmail.getText().toString());
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(this, response.getError(), Toast.LENGTH_SHORT).show();
                                }

                            }, throwable -> {
                                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            })
            );
        });

        // Xử lý sự kiện khi người dùng click vào nút Đăng nhập
        mActivitySignUpBinding.tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });



    }
}
