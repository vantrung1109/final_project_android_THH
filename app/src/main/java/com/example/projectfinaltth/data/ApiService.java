package com.example.projectfinaltth.data;
import com.example.projectfinaltth.data.model.request.AddToCartRequest;
import com.example.projectfinaltth.data.model.request.CourseIdRequest;
import com.example.projectfinaltth.data.model.request.LessonRequest;
import com.example.projectfinaltth.data.model.request.SignInRequest;
import com.example.projectfinaltth.data.model.request.checkout.CheckoutRequest;
import com.example.projectfinaltth.data.model.request.comment.CommentRequest;
import com.example.projectfinaltth.data.model.request.course_detail.CourseDetailRequest;
import com.example.projectfinaltth.data.model.request.document.CreateDocumentRequest;
import com.example.projectfinaltth.data.model.request.document.DocumentRequest;
import com.example.projectfinaltth.data.model.request.password.ChangePasswordRequest;
import com.example.projectfinaltth.data.model.request.review.ReviewRequest;
import com.example.projectfinaltth.data.model.request.signup.OtpRequest;
import com.example.projectfinaltth.data.model.request.signup.SignUpRequest;
import com.example.projectfinaltth.data.model.response.SignInResponse;
import com.example.projectfinaltth.data.model.response.changepassword.ChangePasswordResponse;
import com.example.projectfinaltth.data.model.response.checkout.CartItemResponse;

import com.example.projectfinaltth.data.model.response.comment.CommentResponse;
import com.example.projectfinaltth.data.model.response.course.CourseItem;
import com.example.projectfinaltth.data.model.response.document.Document;
import com.example.projectfinaltth.data.model.response.document.DocumentResponse;
import com.example.projectfinaltth.data.model.response.lesson.LessonByCourseResponse;
import com.example.projectfinaltth.data.model.response.lesson.LessonItem;
import com.example.projectfinaltth.data.model.response.profile.User;

import com.example.projectfinaltth.data.model.response.cart.CartListItemResponse;
import com.example.projectfinaltth.data.model.response.checkout.CartResponse;
import com.example.projectfinaltth.data.model.response.checkout.CheckoutResponse;
import com.example.projectfinaltth.data.model.response.course.CourseListResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.CoursehomeResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.MyCoursesResponse;
import com.example.projectfinaltth.data.model.response.lesson.LessonListResponse;
import com.example.projectfinaltth.data.model.response.profile.UserResponse;
import com.example.projectfinaltth.data.model.response.review.ReviewResponse;
import com.example.projectfinaltth.data.model.response.signup.OtpResponse;
import com.example.projectfinaltth.data.model.response.signup.SignUpResponse;

import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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
    @POST("api/users/register")
    Observable<SignUpResponse> signUp(@Body SignUpRequest request);
    @POST("api/users/otp-authentication")
    Observable<OtpResponse> verifyOtp(@Body OtpRequest request);


    @GET("api/carts/getCart")
    Observable<CartResponse> getCart(@Header("Authorization") String token);

    @GET("api/courses/get_course/{id}")
    Observable<CourseIntroResponse> getCourseIntroById(@Path("id") String courseId);

    @GET("api/courses/all")
    Observable<CoursehomeResponse> getAllCourses();

    @GET("api/invoices/my_course")
    Observable<MyCoursesResponse> getMyCourses(@Header("Authorization") String token);

    @GET("api/carts/getCart")
    Observable<CartListItemResponse> getCartItem(@Header("Authorization") String token);

    @POST("api/invoices/checkout")
    Observable<CheckoutResponse> checkout(@Header("Authorization") String token, @Body CheckoutRequest request);

    @POST("/api/reviews/create_review/{id}")
    Observable<ReviewResponse> sendReview(@Header("Authorization") String token, @Path("id") String courseId, @Body ReviewRequest request);

    @DELETE("api/carts/clearCart")
    Completable clearCart(@Header("Authorization") String token);

    @DELETE("api/carts/removeFromCart/{cartId}/{courseId}")
    Completable removeFromCart(@Header("Authorization") String token, @Path("cartId") String cartId, @Path("courseId") String courseId);

    @GET("api/users")
    Observable<UserResponse> getUserDetails(@Header("Authorization") String token);

    @POST("api/carts/addToCart")
    Observable<CartItemResponse> addToCart(@Header("Authorization") String token, @Body AddToCartRequest request);

    @POST("api/lessons/get-course-lessons")
    Observable<LessonListResponse> getInstructorLessons(@Body CourseIdRequest courseId);
    @DELETE("api/courses/delete-course/{id}")
    Completable deleteCourse( @Header("Authorization") String token, @Path("id") String courseId);
    @GET("api/courses/get-courses-by-instructor/{id}")
    Observable<CourseListResponse> getInstructorCourses(@Header("Authorization") String token, @Path("id") String instructorId);
    @DELETE("api/lessons/delete-lesson/{id}")
    Completable deleteLesson( @Header("Authorization") String token, @Path("id") String lessonId);
    @DELETE("api/documents/delete-document/{id}")
    Completable deleteDocument( @Header("Authorization") String token, @Path("id") String documentId);

    @POST("api/documents/get-lesson-documents")
    Observable<DocumentResponse> getLessonDocuments(@Body DocumentRequest request);

    @POST("api/comments/get-lesson-comments")
    Observable<CommentResponse> getLessonComments(@Body CommentRequest request);

    @POST("api/courses/search-courses")
    Observable<CourseResponse> searchCourses(@Body com.example.projectfinaltth.data.model.request.RequestBody requestBody);

    @POST("api/lessons/get-course-lessons")
    Observable<LessonByCourseResponse> getLessonsByCourse(@Body CourseDetailRequest requestBody);

    @POST("api/lessons/create-lesson")
    Observable<LessonItem> createLesson(@Header("Authorization") String token,@Body LessonRequest lessonRequest);
    @PUT("api/lessons/update-lesson/{id}")
    Observable<LessonItem> updateLesson(@Header("Authorization") String token, @Path("id") String lessonId,@Body LessonRequest lessonRequest);
    @POST("api/documents/get-lesson-documents")
    Observable<DocumentResponse> getDocumentByLesson(@Body DocumentRequest documentRequest);
    @Multipart
    @POST("api/documents/create-document")
    Single<DocumentResponse> createDocument(
            @Header("Authorization") String token,
            @Part("lessonId") RequestBody lessonId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @PUT("/api/users/change-password-app-user")
    Completable changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    @Multipart
    @POST("api/courses/create-course")
    Single<CourseItem> createCourse(
            @Header("Authorization") String token,
            @Part("title") RequestBody title,
            @Part("price") RequestBody price,
            @Part("topic") RequestBody topic,
            @Part("description") RequestBody description,
            @Part("userId") RequestBody userId,
            @Part MultipartBody.Part picture
    );

}





    

