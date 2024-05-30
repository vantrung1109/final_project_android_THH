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

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText eTextName;
    private ImageView imageView;
    private FloatingActionButton floatingActionButton;
    private Uri selectedImageUri; // Add this line to store the selected image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        eTextName = findViewById(R.id.eTextName);
        imageView = findViewById(R.id.imageView);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnBack = findViewById(R.id.btnBack);

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnBack.setOnClickListener(v -> finish());
        floatingActionButton.setOnClickListener(v -> openImagePicker());

        loadUserData();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedImageUri = data.getData(); // Store the selected image URI
                Glide.with(this).load(selectedImageUri).into(imageView); // Show the selected image in the ImageView
            }
        }
    }

    private void updateProfile() {
        updateName(); // Update the name first
        if (selectedImageUri != null) {
            updateProfilePicture(selectedImageUri); // Update the profile picture if a new one was selected
        }
    }

    private void updateProfilePicture(Uri selectedImageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
            File file = createFileFromBitmap(bitmap);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), requestFile);

            String token = DataLocalManager.getToken();

            ApiService.apiService.changePicture("Bearer " + token, body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            () -> {
                                Toast.makeText(EditProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                            },
                            throwable -> {
                                if (throwable instanceof HttpException) {
                                    HttpException httpException = (HttpException) throwable;
                                    String errorBody = httpException.response().errorBody().string();
                                    Log.e("EditProfileActivity", "Error updating profile picture: " + errorBody);
                                    Toast.makeText(EditProfileActivity.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("EditProfileActivity", "Error updating profile picture", throwable);
                                    Toast.makeText(EditProfileActivity.this, "Error updating profile picture", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
        } catch (Exception e) {
            Log.e("EditProfileActivity", "Error selecting image", e);
        }
    }

    private File createFileFromBitmap(Bitmap bitmap) throws IOException {
        File file = new File(getCacheDir(), "profile_picture.jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        return file;
    }

    private void loadUserData() {
        String token = DataLocalManager.getToken();
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

    private void updateName() {
        String token = DataLocalManager.getToken();
        String newName = eTextName.getText().toString();
        ChangeNameRequest request = new ChangeNameRequest(newName);

        ApiService.apiService.changeName("Bearer " + token, request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            finish();
                        },
                        throwable -> {
                            Log.e("EditProfileActivity", "Error updating name", throwable);
                        }
                );
    }
}
