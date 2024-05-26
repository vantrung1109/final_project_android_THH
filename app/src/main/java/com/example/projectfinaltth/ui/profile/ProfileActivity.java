package com.example.projectfinaltth.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.databinding.ActivityFragmentAccountBinding;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {
    ActivityFragmentAccountBinding mActivityFragmentAccountBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityFragmentAccountBinding = ActivityFragmentAccountBinding.inflate(getLayoutInflater());
        setContentView(mActivityFragmentAccountBinding.getRoot());
        fetchUserDetails();

    }

    private void fetchUserDetails() {
        String token = DataLocalManager.getToken(); // Lấy token từ local storage
        //Log.e("",token);
        if (token != null) {
            Log.d("AccountFragment", "Token: " + token);
            compositeDisposable.add(
                    ApiService.apiService.getUserDetails("Bearer " + token)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(userResponse -> {
                                mActivityFragmentAccountBinding.textName.setText(userResponse.getUser().getName());
                                mActivityFragmentAccountBinding.textEmail.setText(userResponse.getUser().getEmail());
                                Glide.with(this).load(userResponse.getUser().getPicture()).into(mActivityFragmentAccountBinding.imgProfile);;
                            }, throwable -> {
                                Log.e("AccountFragment", "Error loading user details: " + throwable.getMessage());
                                Toast.makeText(ProfileActivity.this, "Failed to load user details", Toast.LENGTH_SHORT).show();
                            })
            );

        } else {
            Log.e("AccountFragment", "Token is null");
        }
    }
}