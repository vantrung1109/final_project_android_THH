package com.example.projectfinaltth.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.profile.ChangeNameRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText eTextName;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        eTextName = findViewById(R.id.eTextName);
        imageView = findViewById(R.id.imageView);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnBack = findViewById(R.id.btnBack);

        btnUpdate.setOnClickListener(v -> updateName());
        btnBack.setOnClickListener(v -> finish());

        loadUserData();
    }

    private void loadUserData() {
        String token = DataLocalManager.getToken(); // Replace with actual token
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
        String token = DataLocalManager.getToken(); // Replace with actual token
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
                            // Handle error
                            Log.e("EditProfileActivity", "Error updating name", throwable);
                        }
                );
    }
}
