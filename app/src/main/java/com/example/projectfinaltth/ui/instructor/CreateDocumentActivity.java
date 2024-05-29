package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;

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

public class CreateDocumentActivity extends AppCompatActivity {

    private static final int REQUEST_VIDEO_PICKER = 1;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Button createButton;
    private Button chooseVideoButton;
    private VideoView videoView;

    private Uri selectedVideoUri;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_document);

        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);
        createButton = findViewById(R.id.button_create);
        chooseVideoButton = findViewById(R.id.button_choose_video);
        videoView = findViewById(R.id.video_view);

        chooseVideoButton.setOnClickListener(v -> chooseVideo());
        createButton.setOnClickListener(v -> createDocument());
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_VIDEO_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VIDEO_PICKER && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                videoView.setVideoURI(selectedVideoUri);
                videoView.requestFocus();
                videoView.start();
            }
        }
    }

    private void createDocument() {
        String token = DataLocalManager.getToken(); // Get token from local storage

        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String lessonId = "6640fda7aea886b32ee43925";

        if (title.isEmpty() || description.isEmpty() || selectedVideoUri == null) {
            Toast.makeText(this, "Please fill in all fields and choose a video", Toast.LENGTH_SHORT).show();
            return;
        }

        File videoFile = new File(getCacheDir(), "video.mp4");
        try (InputStream inputStream = getContentResolver().openInputStream(selectedVideoUri);
             OutputStream outputStream = new FileOutputStream(videoFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            Log.e("CreateDocument", "Error creating video file", e);
            Toast.makeText(this, "Error creating video file", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBodyLessonId = RequestBody.create(MediaType.parse("text/plain"), lessonId);
        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody requestBodyDescription = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("video/mp4"), videoFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("content", videoFile.getName(), requestBodyFile);

        compositeDisposable.add(
                ApiService.apiService.createDocument("Bearer " + token, requestBodyLessonId, requestBodyTitle, requestBodyDescription, filePart)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(CreateDocumentRequest -> {
                            Toast.makeText(this, "Document created successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }, throwable -> {
                            Log.e("CreateDocument", "Error creating document: " + throwable.getMessage());
                            Toast.makeText(this, "Error creating document: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
