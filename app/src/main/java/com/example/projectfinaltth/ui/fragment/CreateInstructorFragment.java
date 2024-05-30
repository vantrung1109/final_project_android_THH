package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class CreateInstructorFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICKER = 1;

    private EditText titleEditText;
    private EditText priceEditText;
    private EditText topicEditText;
    private EditText descriptionEditText;
    private EditText userIdEditText;
    private Button createButton;
    private Button chooseImageButton;
    private ImageView imageView;

    private Uri selectedImageUri;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_course, container, false);

        titleEditText = view.findViewById(R.id.edit_text_title);
        priceEditText = view.findViewById(R.id.edit_text_price);
        topicEditText = view.findViewById(R.id.edit_text_topic);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        createButton = view.findViewById(R.id.button_create);
        chooseImageButton = view.findViewById(R.id.button_choose_image);
        imageView = view.findViewById(R.id.image_view);

        chooseImageButton.setOnClickListener(v -> chooseImage());
        createButton.setOnClickListener(v -> createCourse("6640fa54aea886b32ee43883"));

        return view;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == getActivity().RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imageView.setImageURI(selectedImageUri);
            }
        }
    }

    private void createCourse(String userId) {
        String token = DataLocalManager.getToken(); // Get token from local storage

        String title = titleEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String topic = topicEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty() || topic.isEmpty() || description.isEmpty() || userId.isEmpty() || selectedImageUri == null) {
            Toast.makeText(getContext(), "Please fill in all fields and choose an image", Toast.LENGTH_SHORT).show();
            return;
        }

        File imageFile = new File(getContext().getCacheDir(), "image.jpg");
        try (InputStream inputStream = getContext().getContentResolver().openInputStream(selectedImageUri);
             OutputStream outputStream = new FileOutputStream(imageFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            Log.e("CreateCourse", "Error creating image file", e);
            Toast.makeText(getContext(), "Error creating image file", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody requestBodyPrice = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody requestBodyTopic = RequestBody.create(MediaType.parse("text/plain"), topic);
        RequestBody requestBodyDescription = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody requestBodyUserId = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestBodyFile);

        compositeDisposable.add(
                ApiService.apiService.createCourse("Bearer " + token, requestBodyTitle, requestBodyPrice, requestBodyTopic, requestBodyDescription, requestBodyUserId, filePart)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(courseItem -> {
                            Toast.makeText(getContext(), "Create course successfully", Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }, throwable -> {
                            Log.e("CreateCourse", "Error creating course: " + throwable.getMessage());
                            Toast.makeText(getContext(), "Error creating course: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
