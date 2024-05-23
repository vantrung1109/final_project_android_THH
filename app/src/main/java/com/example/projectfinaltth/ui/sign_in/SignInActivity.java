package com.example.projectfinaltth.ui.sign_in;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.SignInRequest;
import com.example.projectfinaltth.databinding.ActivitySignInBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding mActivitySignInBinding;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignInBinding.getRoot());

        mActivitySignInBinding.btnLogin.setOnClickListener(v -> {
            SignInRequest signInRequest = new SignInRequest();
            signInRequest.setEmail(mActivitySignInBinding.editEmail.getText().toString());
            signInRequest.setPassword(mActivitySignInBinding.editPassword.getText().toString());
            DataLocalManager.init(this);
            compositeDisposable.add(
                    ApiService.apiService.signIn(signInRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(signInResponse -> {
                                Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
                                Log.e("TAG", "onCreate: " + signInResponse.toString());
                                DataLocalManager.setToken(signInResponse.getToken());
                            }, throwable -> {
                                Log.e("TAG", "Error: " + throwable.getMessage());
                            }));
            Log.e("TAG", "Print: " + DataLocalManager.getToken());
        });

    }


}
