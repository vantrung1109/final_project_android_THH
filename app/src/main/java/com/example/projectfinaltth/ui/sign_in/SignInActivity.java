package com.example.projectfinaltth.ui.sign_in;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.SignInRequest;
import com.example.projectfinaltth.data.model.response.SignInResponse;
import com.example.projectfinaltth.ui.main.MainActivity;
import com.example.projectfinaltth.ui.main.MainInstructorActivity;
import com.example.projectfinaltth.ui.mlkit.FaceMlKitCameraActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignInActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private Button btnLogin, btnLoginWithCamera;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Khởi tạo các view
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLoginWithCamera = findViewById(R.id.btnLoginWithCamera);

        DataLocalManager.init(this);

        // Kiểm tra nếu đã lưu thông tin đăng nhập, chuyển sang đăng nhập bằng nhận diện khuôn mặt
        if (DataLocalManager.getToken() != null && !DataLocalManager.getToken().isEmpty()) {
            Intent intent = new Intent(SignInActivity.this, FaceMlKitCameraActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Xử lý sự kiện khi người dùng click vào nút đăng nhập
        btnLogin.setOnClickListener(v -> {
            SignInRequest signInRequest = new SignInRequest();
            signInRequest.setEmail(editEmail.getText().toString());
            signInRequest.setPassword(editPassword.getText().toString());

            compositeDisposable.add(
                    // Thực hiện gọi API đăng nhập
                    ApiService.apiService.signIn(signInRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(signInResponse -> {
                                handleSignInResponse(signInResponse);
                            }, throwable -> {
                                Log.e("TAG", "Error: " + throwable.getMessage());
                                Toast.makeText(this, "Bạn nhập sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                            }));
        });

        // Chuyển sang FaceMlKitCameraActivity khi bấm vào nút "Login with camera"
        btnLoginWithCamera.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, FaceMlKitCameraActivity.class);
            startActivity(intent);
        });
    }

    private void handleSignInResponse(SignInResponse signInResponse) {
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
        // Lưu token và cartId vào SharePreferences
        DataLocalManager.setToken(signInResponse.getToken());
        DataLocalManager.setCartId(signInResponse.getCartId());

        // Lưu thông tin đăng nhập khác
        DataLocalManager.setEmail(editEmail.getText().toString());
        DataLocalManager.setPassword(editPassword.getText().toString());

        // Lấy thông tin hình ảnh profile
        fetchUserProfile(signInResponse.getToken());

        // Kiểm tra vai trò người dùng và điều hướng
        String userRole = signInResponse.getRole();
        Intent intent;
        if ("INSTRUCTOR".equalsIgnoreCase(userRole)) {
            intent = new Intent(this, MainInstructorActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
        finish(); // Kết thúc SignInActivity để người dùng không quay lại sau khi đăng nhập

        Log.e("TAG", "===============> Login Success: " + signInResponse.getToken());
    }

    private void fetchUserProfile(String token) {
        compositeDisposable.add(
                ApiService.apiService.getUserDetails("Bearer " + token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userResponse -> {
                            // Lưu thông tin hình ảnh profile vào DataLocalManager
                            DataLocalManager.setProfilePicture(userResponse.getUser().getPicture());
                        }, throwable -> {
                            Log.e("TAG", "Error fetching user profile: " + throwable.getMessage());
                        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
