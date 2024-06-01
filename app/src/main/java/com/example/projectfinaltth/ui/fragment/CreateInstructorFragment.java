package com.example.projectfinaltth.ui.fragment;

import android.content.Intent;
import android.net.Uri;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.profile.User;
import com.example.projectfinaltth.data.model.response.profile.UserResponse;
import com.example.projectfinaltth.ui.adapter.Topic.Topic;
import com.example.projectfinaltth.ui.adapter.Topic.TopicAdapter;
import com.example.projectfinaltth.ui.main.MainInstructorActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CreateInstructorFragment extends Fragment {

    private static final int REQUEST_IMAGE_PICKER = 1;// Hằng số xác định yêu cầu chọn ảnh

    private EditText titleEditText;
    private EditText priceEditText;
    private Spinner topicSpinner;
    private TopicAdapter topicAdapter;
    private EditText descriptionEditText;
    private EditText userIdEditText;
    private Button createButton;
    private Button chooseImageButton;
    private ImageView imageView;

    private Uri selectedImageUri;// URI của ảnh đã chọn
    private ProgressBar progressBar;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<UserResponse> userCurrent = new MutableLiveData<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_course, container, false);
        // Khởi tạo các thành phần giao diện
        titleEditText = view.findViewById(R.id.edit_text_title);
        priceEditText = view.findViewById(R.id.edit_text_price);
        topicSpinner =  view.findViewById(R.id.spinner_topic);
        descriptionEditText = view.findViewById(R.id.edit_text_description);
        createButton = view.findViewById(R.id.button_create);
        chooseImageButton = view.findViewById(R.id.button_choose_image);
        imageView = view.findViewById(R.id.image_view);
        progressBar = view.findViewById(R.id.progress_bar);
        // Thiết lập sự kiện click cho nút chọn ảnh
        chooseImageButton.setOnClickListener(v -> chooseImage());
        // Gọi API để lấy thông tin người dùng
        compositeDisposable.add(
                ApiService.apiService.getUserDetails("Bearer " + DataLocalManager.getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(user -> {
                            userCurrent.setValue(user);
                        }, throwable -> {
                            Log.e("CreateCourse", "Error getting user: " + throwable.getMessage());
                            Toast.makeText(getContext(), "Error getting user: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );
        // Thiết lập sự kiện khi nhận được thông tin người dùng
        userCurrent.observe(getViewLifecycleOwner(), userResponse -> {
            createButton.setOnClickListener(v -> createCourse(userCurrent.getValue().getUser().getId()));
        });
        // Thiết lập adapter cho Spinner
        topicAdapter = new TopicAdapter(getContext(), R.layout.item_topic, getTopics());
        topicSpinner.setAdapter(topicAdapter);
        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Selected topic: " + topicAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        return view;
    }
    // Phương thức để lấy danh sách các chủ đề
    public List<Topic> getTopics() {
        List topics = new ArrayList<>();
        topics.add( new Topic("WEB"));
        topics.add( new Topic("AI"));
        topics.add( new Topic("DATA"));
        topics.add( new Topic("MOBILE"));
        topics.add( new Topic("GAME"));
        topics.add( new Topic("SOFTWARE"));
        return topics;
    }
    // Phương thức để chọn ảnh từ thiết bị
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
    // Phương thức để tạo khóa học mới
    private void createCourse(String userId) {
        String token = DataLocalManager.getToken(); // Get token from local storage

        String title = titleEditText.getText().toString().trim();
        String price = priceEditText.getText().toString().trim();
        String topic = ((Topic) topicSpinner.getSelectedItem()).getName();
        String description = descriptionEditText.getText().toString().trim();
        // Kiểm tra các trường thông tin bắt buộc
        if (title.isEmpty() || price.isEmpty() || topic.isEmpty() || description.isEmpty() || userId.isEmpty() || selectedImageUri == null) {
            Toast.makeText(getContext(), "Please fill in all fields and choose an image", Toast.LENGTH_SHORT).show();
            return;
        }
        // Tạo tệp ảnh từ URI đã chọn
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
        // Tạo các phần của request body
        RequestBody requestBodyTitle = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody requestBodyPrice = RequestBody.create(MediaType.parse("text/plain"), price);
        RequestBody requestBodyTopic = RequestBody.create(MediaType.parse("text/plain"), topic);
        RequestBody requestBodyDescription = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody requestBodyUserId = RequestBody.create(MediaType.parse("text/plain"), userId);
        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/jpeg"), imageFile);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("picture", imageFile.getName(), requestBodyFile);

        progressBar.setVisibility(View.VISIBLE);
        // Gọi API để tạo khóa học mới
        compositeDisposable.add(
                ApiService.apiService.createCourse("Bearer " + token, requestBodyTitle, requestBodyPrice, requestBodyTopic, requestBodyDescription, requestBodyUserId, filePart)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(courseItem -> {
                            Toast.makeText(getContext(), "Create course successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), MainInstructorActivity.class);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);
                        }, throwable -> {
                            Log.e("CreateCourse", "Error creating course: " + throwable.getMessage());
                            Toast.makeText(getContext(), "Error creating course: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        })
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();// Xóa tất cả các subscription khi fragment bị hủy
    }

}
