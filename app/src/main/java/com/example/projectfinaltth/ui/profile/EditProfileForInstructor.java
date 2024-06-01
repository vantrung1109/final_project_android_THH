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
public class EditProfileForInstructor extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1; // Mã yêu cầu để chọn ảnh

    private TextInputEditText eTextName; // TextInputEditText để nhập tên người dùng
    private ImageView imageView; // ImageView để hiển thị ảnh đại diện
    private FloatingActionButton floatingActionButton; // FloatingActionButton để chọn ảnh đại diện mới
    private Uri selectedImageUri; // Lưu trữ URI của ảnh đã chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile); // Thiết lập layout cho Activity

        // Liên kết các view từ layout
        eTextName = findViewById(R.id.eTextName);
        imageView = findViewById(R.id.imageView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnBack = findViewById(R.id.btnBack);

        // Thiết lập sự kiện click cho các nút
        btnUpdate.setOnClickListener(v -> updateProfile());
        btnBack.setOnClickListener(v -> finish());
        floatingActionButton.setOnClickListener(v -> openImagePicker());

        // Tải dữ liệu người dùng từ server
        loadUserData();
    }

    // Mở bộ chọn ảnh để người dùng chọn ảnh đại diện mới
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Xử lý kết quả trả về từ bộ chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedImageUri = data.getData(); // Lưu URI của ảnh đã chọn
                Glide.with(this).load(selectedImageUri).into(imageView); // Hiển thị ảnh đã chọn trong ImageView
            }
        }
    }

    // Cập nhật thông tin người dùng
    private void updateProfile() {
        updateName(); // Cập nhật tên người dùng trước
        if (selectedImageUri != null) {
            updateProfilePicture(selectedImageUri); // Cập nhật ảnh đại diện nếu người dùng đã chọn ảnh mới
        }
    }

    // Cập nhật ảnh đại diện người dùng
    private void updateProfilePicture(Uri selectedImageUri) {
        try {
            // Chuyển URI thành Bitmap
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            File file = createFileFromBitmap(bitmap); // Tạo file từ Bitmap

            // Tạo RequestBody từ file
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

            String token = DataLocalManager.getToken(); // Lấy token từ local storage

            // Gửi yêu cầu cập nhật ảnh đại diện đến server
            ApiService.apiService.changePicture("Bearer " + token, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                Toast.makeText(EditProfileForInstructor.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                            },
                            throwable -> {
                                if (throwable instanceof HttpException) {
                                    HttpException httpException = (HttpException) throwable;
                                    String errorBody = httpException.response().errorBody().string();
                                    Log.e("EditProfileActivity", "Error updating profile picture: " + errorBody);
                                    Toast.makeText(EditProfileForInstructor.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("EditProfileActivity", "Error updating profile picture", throwable);
                                    Toast.makeText(EditProfileForInstructor.this, "Error updating profile picture", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
        } catch (Exception e) {
            Log.e("EditProfileActivity", "Error selecting image", e);
        }
    }

    // Tạo file từ Bitmap
    private File createFileFromBitmap(Bitmap bitmap) throws IOException {
        File file = new File(getCacheDir(), "profile_picture.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        return file;
    }

    // Tải dữ liệu người dùng từ server và hiển thị lên giao diện
    private void loadUserData() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        ApiService.apiService.getUserDetails("Bearer " + token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userResponse -> {
                            eTextName.setText(userResponse.getUser().getName());
                            Glide.with(this)
                                    .load(userResponse.getUser().getPicture())
                                    .into(imageView);
                        },
                        throwable -> {
                            Log.e("EditProfileActivity", "Error fetching user data", throwable);
                        }
                );
    }

    // Cập nhật tên người dùng
    private void updateName() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        String newName = eTextName.getText().toString(); // Lấy tên mới từ TextInputEditText
        ChangeNameRequest request = new ChangeNameRequest(newName); // Tạo yêu cầu thay đổi tên

        // Gửi yêu cầu thay đổi tên đến server
        ApiService.apiService.changeName("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            finish(); // Đóng Activity khi cập nhật thành công
                        },
                        throwable -> {
                            Log.e("EditProfileActivity", "Error updating name", throwable);
                        }
                );
    }
}
