package com.example.projectfinaltth.ui.sign_in;

import android.content.Intent; // Import các lớp cần thiết cho Intent, Bundle, và các lớp khác.
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R; // Import tài nguyên như layout, chuỗi, v.v.
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.SignInRequest;
import com.example.projectfinaltth.data.model.response.SignInResponse;
import com.example.projectfinaltth.databinding.ActivitySignInBinding;
import com.example.projectfinaltth.ui.ai.LoginFaceAI;
import com.example.projectfinaltth.ui.main.MainActivity;
import com.example.projectfinaltth.ui.main.MainInstructorActivity;
import com.example.projectfinaltth.ui.sign_up.SignUpActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignInActivity extends AppCompatActivity { // Định nghĩa lớp SignInActivity kế thừa từ AppCompatActivity
    ActivitySignInBinding mActivitySignInBinding; // Khai báo đối tượng ActivitySignInBinding
    CompositeDisposable compositeDisposable = new CompositeDisposable(); // Khai báo đối tượng CompositeDisposable để quản lý các tài nguyên phế bỏ

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Phương thức onCreate, được gọi khi activity được tạo ra
        super.onCreate(savedInstanceState); // Gọi phương thức của lớp cha
        mActivitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater()); // Khởi tạo layout sử dụng ViewBinding
        setContentView(mActivitySignInBinding.getRoot()); // Thiết lập nội dung hiển thị là gốc của layout được inflate

        // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        // Xử lý sự kiện khi người dùng click vào nút đăng nhập
        mActivitySignInBinding.btnLogin.setOnClickListener(v -> { // Thiết lập onClickListener cho nút đăng nhập
            SignInRequest signInRequest = new SignInRequest(); // Tạo đối tượng SignInRequest
            signInRequest.setEmail(mActivitySignInBinding.editEmail.getText().toString()); // Thiết lập email từ ô nhập liệu
            signInRequest.setPassword(mActivitySignInBinding.editPassword.getText().toString()); // Thiết lập mật khẩu từ ô nhập liệu
            DataLocalManager.init(this); // Khởi tạo DataLocalManager với context

            compositeDisposable.add( // Thêm tài nguyên phế bỏ vào CompositeDisposable
                    // Thực hiện gọi API đăng nhập
                    ApiService.apiService.signIn(signInRequest) // Gọi API signIn
                            .subscribeOn(Schedulers.io()) // Đăng ký trên luồng IO
                            .observeOn(AndroidSchedulers.mainThread()) // Quan sát trên luồng chính
                            .subscribe(signInResponse -> { // Đăng ký để nhận phản hồi
                                handleSignInResponse(signInResponse, signInRequest.getEmail(), signInRequest.getPassword()); // Xử lý phản hồi đăng nhập
                            }, throwable -> { // Xử lý lỗi
                                Log.e("TAG", "Error: " + throwable.getMessage()); // Log lỗi
                                Toast.makeText(this, "You entered the wrong email or password", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                            }));
        });
        mActivitySignInBinding.btnFaceAi.setOnClickListener(v -> { // Thiết lập onClickListener cho nút AI nhận dạng khuôn mặt
            Intent intent = new Intent(SignInActivity.this, LoginFaceAI.class); // Tạo intent để mở activity LoginFaceAI
            startActivity(intent); // Mở activity LoginFaceAI
        });
        Intent intent = getIntent(); // Lấy intent từ activity trước đó
        String userName = intent.getStringExtra("USERNAME"); // Lấy tên người dùng từ intent
        String pass = intent.getStringExtra("PASSWORD"); // Lấy mật khẩu từ intent
        mActivitySignInBinding.editEmail.setText(userName); // Thiết lập tên người dùng vào ô email
        mActivitySignInBinding.editPassword.setText(pass); // Thiết lập mật khẩu vào ô mật khẩu

        mActivitySignInBinding.tvRegister.setOnClickListener(v -> { // Thiết lập onClickListener cho TextView đăng ký
            Intent intent1 = new Intent(SignInActivity.this, SignUpActivity.class); // Tạo intent để mở SignUpActivity
            startActivity(intent1); // Mở SignUpActivity
        });
    }
    //MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
    private void handleSignInResponse(SignInResponse signInResponse, String email, String password) { // Phương thức xử lý phản hồi đăng nhập
        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo đăng nhập thành công
        // Lưu token, cartId, email và password vào SharePreferences
        DataLocalManager.setToken(signInResponse.getToken()); // Thiết lập token trong DataLocalManager
        DataLocalManager.setCartId(signInResponse.getCartId()); // Thiết lập cartId trong DataLocalManager
        DataLocalManager.setEmail(email); // Thiết lập email trong DataLocalManager
        DataLocalManager.setPassword(password); // Thiết lập mật khẩu trong DataLocalManager

        // Kiểm tra vai trò người dùng và điều hướng
        String userRole = signInResponse.getRole(); // Lấy vai trò của người dùng từ phản hồi
        Intent intent; // Khai báo intent
        if ("INSTRUCTOR".equalsIgnoreCase(userRole)) { // Kiểm tra nếu người dùng là giáo viên
            intent = new Intent(this, MainInstructorActivity.class); // Nếu là giáo viên, thiết lập intent để mở MainInstructorActivity
        } else { // Nếu không phải là giáo viên
            intent = new Intent(this, MainActivity.class); // Thiết lập intent để mở MainActivity
        }
        startActivity(intent); // Mở activity dựa trên vai trò của người dùng
        finish(); // Kết thúc SignInActivity để ngăn người dùng quay lại sau khi đăng nhập

        Log.e("TAG", "===============> Login Success: " + signInResponse.getToken()); // Log thông báo đăng nhập thành công với token
    }

    @Override
    protected void onDestroy() { // Phương thức onDestroy, được gọi khi activity bị hủy
        super.onDestroy(); // Gọi phương thức của lớp cha
        compositeDisposable.clear(); // Xóa các tài nguyên phế bỏ
    }
}
