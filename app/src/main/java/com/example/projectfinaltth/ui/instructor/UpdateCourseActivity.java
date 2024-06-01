package com.example.projectfinaltth.ui.instructor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.data.model.response.profile.User;
import com.example.projectfinaltth.model.api.Course;
import com.example.projectfinaltth.ui.adapter.Topic.Topic;
import com.example.projectfinaltth.ui.adapter.Topic.TopicAdapter;
import com.example.projectfinaltth.ui.main.MainInstructorActivity;
import com.google.android.material.textfield.TextInputLayout;

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

public class UpdateCourseActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICKER = 1;

    private EditText titleEditText;
    private EditText priceEditText;
    private Spinner topicSpinner;
    private TopicAdapter topicAdapter;
    private EditText descriptionEditText;
    private Button createButton;
    private Button chooseImageButton;
    private ImageView imageView;

    private Uri selectedImageUri;
    private boolean initialImageSet = false;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    MutableLiveData<User> user = new MutableLiveData<>();

    CourseIntroResponse course;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_course);

        titleEditText = findViewById(R.id.edit_text_title);
        priceEditText = findViewById(R.id.edit_text_price);
        topicSpinner = findViewById(R.id.spinner_topic);

        descriptionEditText = findViewById(R.id.edit_text_description);
        createButton = findViewById(R.id.button_update);
        chooseImageButton = findViewById(R.id.button_choose_image);
        imageView = findViewById(R.id.image_view);



        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            course = (CourseIntroResponse) bundle.getSerializable("courseIntro");
            Glide.with(this)
                    .load(course.getCourse().getPicture())
                    .into(imageView);
            initialImageSet = true;


            titleEditText.setText(course.getCourse().getTitle());
            descriptionEditText.setText(course.getCourse().getDescription());
            //topicEditText(course.getCourse().getTopic());
            priceEditText.setText(String.valueOf(course.getCourse().getPrice()));
        }

        chooseImageButton.setOnClickListener(v -> chooseImage());

        topicAdapter = new TopicAdapter(this, R.layout.item_topic_selected, getTopics());
        topicSpinner.setAdapter(topicAdapter);
        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UpdateCourseActivity.this, "Selected topic: " + topicAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Log.e("UpdateCourse", "Topic: " + course.getCourse().getTopic());
        setSpinnerSelection(topicSpinner, course.getCourse().getTopic());
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

        ImageView back = findViewById(R.id.button_back);
        back.setOnClickListener(v -> {
            finish();
        });
    }

    private void setSpinnerSelection(Spinner spinner, String topicName) {
        ArrayAdapter<Topic> adapter = (ArrayAdapter<Topic>) spinner.getAdapter();
        Log.e("UpdateCourse", "Adapter count: " + adapter.getCount());
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).getName().equals(topicName)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

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
        String topic = ((Topic) topicSpinner.getSelectedItem()).getName().toString();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty() || price.isEmpty()  || description.isEmpty() || userId.isEmpty()) {
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

        String courseId = course.getCourse().get_id();
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

                            Intent intent = new Intent(this, MainInstructorActivity.class);
                            startActivity(intent);
                            finish();
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
