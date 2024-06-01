package com.example.projectfinaltth.ui.sign_up;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.model.request.signup.OtpRequest;
import com.example.projectfinaltth.databinding.ActivityOtpBinding;
import com.example.projectfinaltth.ui.sign_in.SignInActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
public class OtpActivity extends AppCompatActivity {

    ActivityOtpBinding mActivityOtpBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String email = "";
    String otp = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivityOtpBinding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(mActivityOtpBinding.getRoot());

        Bundle bundle = getIntent().getExtras();

        // Lấy email từ intent
        if (bundle != null) {
            email = bundle.getString("email");
            mActivityOtpBinding.tvEmail.setText(email);
        } else
            finish();

        mActivityOtpBinding.btnVerify.setOnClickListener(v -> {
            otp = mActivityOtpBinding.edtOtp1.getText().toString()
                + mActivityOtpBinding.edtOtp2.getText().toString()
                + mActivityOtpBinding.edtOtp3.getText().toString()
                + mActivityOtpBinding.edtOtp4.getText().toString()
                + mActivityOtpBinding.edtOtp5.getText().toString()
                + mActivityOtpBinding.edtOtp6.getText().toString();

            // Kiểm tra xem người dùng đã nhập đủ 6 số chưa
            if (otp.length() != 6) {
                Toast.makeText(this, "Bạn phải nhập đủ 6 số", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API verifyOtp
            compositeDisposable.add(
                    ApiService.apiService.verifyOtp(new OtpRequest(email, otp))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    response -> {
                                        if (response.getSuccess() != null) {
                                            // Nếu đăng ký thành công, chuyển sang màn hình đăng nhập
                                            Toast.makeText(this, response.getSuccess(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(OtpActivity.this, SignInActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(this, response.getError(), Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    throwable -> {
                                Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            )
            );
        });
    }
}
