package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.profile.User;
import com.example.projectfinaltth.model.api.Course;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UpdateCourseActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICKER = 1;

    private EditText titleEditText;
    private EditText priceEditText;
    private EditText topicEditText;
    private EditText descriptionEditText;
    private Button createButton;
    private Button chooseImageButton;
    private ImageView imageView;

    private Uri selectedImageUri;
    private boolean initialImageSet = false;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<User> user = new MutableLiveData<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_course);

        titleEditText = findViewById(R.id.edit_text_title);
        priceEditText = findViewById(R.id.edit_text_price);
        topicEditText = findViewById(R.id.edit_text_topic);
        descriptionEditText = findViewById(R.id.edit_text_description);
        createButton = findViewById(R.id.button_update);
        chooseImageButton = findViewById(R.id.button_choose_image);
        imageView = findViewById(R.id.image_view);

        String des = getIntent().getStringExtra("description");
        String title = getIntent().getStringExtra("title");
        String topic = getIntent().getStringExtra("topic");
        String price = getIntent().getStringExtra("price");
        String picture = getIntent().getStringExtra("picture");

        if (picture != null && !picture.isEmpty()) {
            Glide.with(this)
                    .load(picture)
                    .into(imageView);
            initialImageSet = true;
        }

        titleEditText.setText(title);
        descriptionEditText.setText(des);
        topicEditText.setText(topic);
        priceEditText.setText(price);

        chooseImageButton.setOnClickListener(v -> chooseImage());

        compositeDisposable.add(
                ApiService.apiService.getUserDetails("Bearer " + DataLocalManager.getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userResponse -> {
                                    user.setValue(userResponse.getUser());

                        }, throwable -> {
                            Log.e("UpdateCourse", "Error loading user details: " + throwable.getMessage());
                            Toast.makeText(this, "Failed to load user details", Toast.LENGTH_SHORT).show();
                        }
                        )
        );

        user.observe(this, user -> {
            createButton.setOnClickListener(v -> updateCourse(user.getId()));
        });


    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void updateCourse(String userId) {
        String token = DataLocalManager.getToken(); // Get token from local storage

        String title = titleEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String topic = topicEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty() || topic.isEmpty() || description.isEmpty() || userId.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare image part only if a new image is selected or if no initial image was set
        MultipartBody.Part filePart = null;
        if (selectedImageUri != null || !initialImageSet) {
            File imageFile = new File(getCacheDir(), "image.jpg");
            try (InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                 OutputStream outputStream = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            } catch (Exception e) {
                Log.e("UpdateCourse", "Error creating image file", e);
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
                return;
            }

            RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
            filePart = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestBodyFile);
        }

        String courseId = getIntent().getStringExtra("courseId");
        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody requestBodyPrice = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody requestBodyCourseId = RequestBody.create(MediaType.parse("text/plain"), courseId);
        RequestBody requestBodyTopic = RequestBody.create(MediaType.parse("text/plain"), topic);
        RequestBody requestBodyDescription = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody requestBodyUserId = RequestBody.create(MediaType.parse("text/plain"), userId);

        compositeDisposable.add(
                ApiService.apiService.updateCourse("Bearer " + token, courseId, requestBodyTitle, requestBodyPrice, requestBodyTopic, requestBodyDescription, requestBodyUserId, filePart)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(courseItem -> {
                            Toast.makeText(this, "Update course successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }, throwable -> {
                            Log.e("UpdateCourse", "Error updating course: " + throwable.getMessage());
                            Toast.makeText(this, "Error updating course: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
