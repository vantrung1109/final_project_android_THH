package com.example.projectfinaltth.ui.changepassword;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.password.ChangePasswordRequest;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import retrofit2.HttpException;
import java.io.IOException;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
//MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
public class ChangePasswordActivity extends AppCompatActivity {

    private EditText eTextCurrentPassword, eTextNewPassword, eTextReNewPassword;
    private Button btnChangePassword, btnChangePasswordBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        // Khởi tạo các thành phần giao diện
        eTextCurrentPassword = findViewById(R.id.eTextCurrentPassword);
        eTextNewPassword = findViewById(R.id.eTextNewPassword);
        eTextReNewPassword = findViewById(R.id.eTextReNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnChangePasswordBack = findViewById(R.id.btnChangePasswordBack);

        // Thiết lập sự kiện khi nhấn nút "Change Password"
        btnChangePassword.setOnClickListener(v -> handleChangePassword());
        // Thiết lập sự kiện khi nhấn nút "Back"
        btnChangePasswordBack.setOnClickListener(v -> finish()); // Đóng Activity khi nhấn nút Back
    }

    private void handleChangePassword() {
        // Lấy giá trị từ các trường nhập liệu
        String currentPassword = eTextCurrentPassword.getText().toString().trim();
        String newPassword = eTextNewPassword.getText().toString().trim();
        String reNewPassword = eTextReNewPassword.getText().toString().trim();

        // Kiểm tra mật khẩu hiện tại không được bỏ trống
        if (currentPassword.isEmpty()) {
            Toast.makeText(this, "Current password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu mới và mật khẩu xác nhận không được bỏ trống
        if (newPassword.isEmpty() || reNewPassword.isEmpty()) {
            Toast.makeText(this, "New password fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu mới và mật khẩu xác nhận phải trùng khớp
        if (!newPassword.equals(reNewPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy token người dùng từ DataLocalManager
        String token = DataLocalManager.getToken();
        // Tạo yêu cầu thay đổi mật khẩu
        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword, reNewPassword);

        // Gọi API để thay đổi mật khẩu
        ApiService.apiService.changePassword("Bearer " + token, request)
                .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Xử lý sự kiện onSubscribe nếu cần
                    }

                    @Override
                    public void onComplete() {
                        // Hiển thị thông báo thành công và đóng Activity
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Kiểm tra loại lỗi
                        if (e instanceof HttpException) {
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            try {
                                // Phân tích lỗi từ JSON
                                JSONObject jsonObject = new JSONObject(responseBody.string());
                                String errorMessage = jsonObject.getString("error");

                                // Nếu lỗi là "Current password is incorrect!", hiển thị thông báo tương ứng
                                if (errorMessage.equals("Current password is incorrect!")) {
                                    Toast.makeText(ChangePasswordActivity.this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                                } else {
                                    // Hiển thị các lỗi khác
                                    Toast.makeText(ChangePasswordActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }

                            } catch (IOException | JSONException jsonException) {
                                // Hiển thị lỗi khi phân tích JSON thất bại
                                Toast.makeText(ChangePasswordActivity.this, "Error parsing error message", Toast.LENGTH_SHORT).show();
                                jsonException.printStackTrace();
                            }
                        } else {
                            // Hiển thị lỗi chung nếu không phải HttpException
                            Toast.makeText(ChangePasswordActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
