package com.example.projectfinaltth.data;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.ShareRefences.PreferencesService;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Application application;

    public AuthInterceptor(Application application) {
        this.application = application;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request.Builder newRequest = chain.request().newBuilder();
        DataLocalManager.init(application.getApplicationContext());
        String isIgnore = chain.request().header("IgnoreAuth");
        if (isIgnore != null && isIgnore.equals("1")) {
            newRequest = chain.request().newBuilder();
            newRequest.removeHeader("IgnoreAuth");
            return chain.proceed(newRequest.build());
        }
        //Add Authentication
        newRequest = chain.request().newBuilder();
        String token = DataLocalManager.getToken();
        if (token != null && !token.equals("")) {
            newRequest.addHeader("Authorization", "Bearer " + token);
        }


        return chain.proceed(newRequest.build());
    }
}
