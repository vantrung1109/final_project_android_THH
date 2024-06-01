package com.example.projectfinaltth.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.profile.ChangeNameRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.HttpException;
//MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Hằng số để xác định yêu cầu chọn ảnh

    private TextInputEditText eTextName; // EditText để nhập tên
    private ImageView imageView; // ImageView để hiển thị ảnh đại diện
    private FloatingActionButton floatingActionButton; // Nút để mở bộ chọn ảnh
    private Uri selectedImageUri; // Lưu trữ URI của ảnh đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile); // Thiết lập giao diện từ file XML

        eTextName = findViewById(R.id.eTextName); // Liên kết EditText với ID trong XML
        imageView = findViewById(R.id.imageView); // Liên kết ImageView với ID trong XML
        floatingActionButton = findViewById(R.id.floatingActionButton); // Liên kết FloatingActionButton với ID trong XML
        Button btnUpdate = findViewById(R.id.btnUpdate); // Liên kết Button "Update" với ID trong XML
        Button btnBack = findViewById(R.id.btnBack); // Liên kết Button "Back" với ID trong XML

        btnUpdate.setOnClickListener(v -> updateProfile()); // Thiết lập sự kiện click cho nút "Update"
        btnBack.setOnClickListener(v -> finish()); // Thiết lập sự kiện click cho nút "Back"
        floatingActionButton.setOnClickListener(v -> openImagePicker()); // Thiết lập sự kiện click cho FloatingActionButton

        loadUserData(); // Tải dữ liệu người dùng
    }

    private void openImagePicker() {
        // Tạo intent để mở bộ chọn ảnh
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Khởi động activity với mã yêu cầu PICK_IMAGE_REQUEST
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Kiểm tra kết quả trả về từ bộ chọn ảnh
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedImageUri = data.getData(); // Lưu URI của ảnh đã chọn
                Glide.with(this).load(selectedImageUri).into(imageView); // Hiển thị ảnh đã chọn trong ImageView
            }
        }
    }

    private void updateProfile() {
        updateName(); // Cập nhật tên trước
        if (selectedImageUri != null) {
            updateProfilePicture(selectedImageUri); // Cập nhật ảnh đại diện nếu có ảnh mới được chọn
        }
    }

    private void updateProfilePicture(Uri selectedImageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri); // Lấy bitmap từ URI
            File file = createFileFromBitmap(bitmap); // Tạo file từ bitmap

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file); // Tạo RequestBody từ file
            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile); // Tạo MultipartBody.Part cho file ảnh

            String token = DataLocalManager.getToken(); // Lấy token từ local storage

            // Gọi API để thay đổi ảnh đại diện
            ApiService.apiService.changePicture("Bearer " + token, body)
                    .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                    .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                    .subscribe(
                            () -> {
                                Toast.makeText(EditProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo thành công
                            },
                            throwable -> {
                                if (throwable instanceof HttpException) {
                                    HttpException httpException = (HttpException) throwable;
                                    String errorBody = httpException.response().errorBody().string(); // Lấy nội dung lỗi từ phản hồi
                                    Log.e("EditProfileActivity", "Error updating profile picture: " + errorBody);
                                    Toast.makeText(EditProfileActivity.this, "Error: " + errorBody, Toast.LENGTH_LONG).show(); // Hiển thị thông báo lỗi
                                } else {
                                    Log.e("EditProfileActivity", "Error updating profile picture", throwable);
                                    Toast.makeText(EditProfileActivity.this, "Error updating profile picture", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
                                }
                            }
                    );
        } catch (Exception e) {
            Log.e("EditProfileActivity", "Error selecting image", e); // Log lỗi nếu có
        }
    }

    private File createFileFromBitmap(Bitmap bitmap) throws IOException {
        // Tạo file từ bitmap
        File file = new File(getCacheDir(), "profile_picture.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Nén bitmap và ghi vào file
        }
        return file;
    }

    private void loadUserData() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        // Gọi API để lấy thông tin người dùng
        ApiService.apiService.getUserDetails("Bearer " + token)
                .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                .subscribe(
                        userResponse -> {
                            eTextName.setText(userResponse.getUser().getName()); // Hiển thị tên người dùng trong EditText
                            Glide.with(this)
                                    .load(userResponse.getUser().getPicture()) // Hiển thị ảnh đại diện người dùng trong ImageView
                                    .into(imageView);
                        },
                        throwable -> {
                            Log.e("EditProfileActivity", "Error fetching user data", throwable); // Log lỗi nếu có
                        }
                );
    }

    private void updateName() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        String newName = eTextName.getText().toString(); // Lấy tên mới từ EditText
        ChangeNameRequest request = new ChangeNameRequest(newName); // Tạo yêu cầu thay đổi tên

        // Gọi API để thay đổi tên
        ApiService.apiService.changeName("Bearer " + token, request)
                .subscribeOn(Schedulers.io()) // Chạy yêu cầu trên luồng I/O
                .observeOn(AndroidSchedulers.mainThread()) // Quan sát kết quả trên luồng chính
                .subscribe(
                        () -> {
                            finish(); // Đóng Activity sau khi thay đổi tên thành công
                        },
                        throwable -> {
                            Log.e("EditProfileActivity", "Error updating name", throwable); // Log lỗi nếu có
                        }
                );
    }
}
