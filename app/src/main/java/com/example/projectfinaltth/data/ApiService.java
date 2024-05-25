package com.example.projectfinaltth.data;

import android.app.Application;

import com.example.projectfinaltth.data.model.request.SignInRequest;
import com.example.projectfinaltth.data.model.response.SignInResponse;
import com.example.projectfinaltth.data.model.response.checkout.CartItemResponse;
import com.example.projectfinaltth.data.model.response.checkout.CartResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.CoursehomeResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.MyCoursesResponse;

import java.util.List;
import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build();

    // Xử dụng retrofit kết hợp với Rxjava để thực hiện gọi API
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://online-courses-web-v2.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService.class);

    @POST("api/users/login")
    Observable<SignInResponse> signIn(@Body SignInRequest request);

    @GET("api/carts/getCart")
    Observable<CartResponse> getCart(@Header("Authorization") String token);

    @GET("api/courses/get_course/{id}")
    Observable<CourseIntroResponse> getCourseIntroById(@Path("id") String courseId);

    @GET("api/courses/all")
    Observable<CoursehomeResponse> getAllCourses();

    @GET("api/invoices/my_course")
    Observable<MyCoursesResponse> getMyCourses(@Header("Authorization") String token);

}
