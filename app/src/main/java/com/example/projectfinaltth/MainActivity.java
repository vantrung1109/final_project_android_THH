package com.example.projectfinaltth;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.SignInRequest;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setEmail("admin@gmail.com");
        signInRequest.setPassword("123");
        DataLocalManager.init(this);
        compositeDisposable.add(
                ApiService.apiService.signIn(signInRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signInResponse -> {

                    Log.e("TAG", "onCreate: " + signInResponse.toString());
                    DataLocalManager.setToken(signInResponse.getToken());
                }, throwable -> {
                    Log.e("TAG", "Error: " + throwable.getMessage());
                }));
        Log.e("TAG", "Print: " + DataLocalManager.getToken());
    }
}