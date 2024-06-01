package com.example.projectfinaltth.data;

import com.example.projectfinaltth.data.model.request.*;
import com.example.projectfinaltth.data.model.request.checkout.CheckoutRequest;
import com.example.projectfinaltth.data.model.request.comment.CommentRequest;
import com.example.projectfinaltth.data.model.request.comment.CreateCommentRequest;
import com.example.projectfinaltth.data.model.request.course_detail.CourseDetailRequest;
import com.example.projectfinaltth.data.model.request.document.CreateDocumentRequest;
import com.example.projectfinaltth.data.model.request.document.DocumentRequest;
import com.example.projectfinaltth.data.model.request.password.ChangePasswordRequest;
import com.example.projectfinaltth.data.model.request.profile.ChangeNameRequest;
import com.example.projectfinaltth.data.model.request.review.ReviewRequest;
import com.example.projectfinaltth.data.model.request.signup.OtpRequest;
import com.example.projectfinaltth.data.model.request.signup.SignUpRequest;
import com.example.projectfinaltth.data.model.response.*;
import com.example.projectfinaltth.data.model.response.change_course_visibility.ChangeCourseVisibilityResponse;
import com.example.projectfinaltth.data.model.response.checkout.CartItemResponse;
import com.example.projectfinaltth.data.model.response.comment.CommentResponse;
import com.example.projectfinaltth.data.model.response.comment.CreateCommentResponse;
import com.example.projectfinaltth.data.model.response.course.CourseItem;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseSearchResponse;
import com.example.projectfinaltth.data.model.response.document.DocumentResponse;
import com.example.projectfinaltth.data.model.response.lesson.LessonByCourseResponse;
import com.example.projectfinaltth.data.model.response.lesson.LessonItem;
import com.example.projectfinaltth.data.model.response.cart.CartListItemResponse;
import com.example.projectfinaltth.data.model.response.checkout.CartResponse;
import com.example.projectfinaltth.data.model.response.checkout.CheckoutResponse;
import com.example.projectfinaltth.data.model.response.course.CourseListResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.CoursehomeResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.MyCoursesResponse;
import com.example.projectfinaltth.data.model.response.lesson.LessonListResponse;
import com.example.projectfinaltth.data.model.response.profile.UserPictureResponse;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

public interface ApiService {
    // Tạo interceptor để log chi tiết request và response
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);

    // Cấu hình OkHttpClient với timeout và interceptor
    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .build();

    // Tạo đối tượng Retrofit với base URL và converter
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://online-courses-web-v2.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService.class);

    // Endpoint để đăng nhập người dùng
    @POST("api/users/login")
    Observable<SignInResponse> signIn(@Body SignInRequest request);

    // Endpoint để đăng ký người dùng mới
    @POST("api/users/register")
    Observable<SignUpResponse> signUp(@Body SignUpRequest request);

    // Endpoint để xác thực người dùng với OTP
    @POST("api/users/otp-authentication")
    Observable<OtpResponse> verifyOtp(@Body OtpRequest request);

    // Endpoint để thay đổi trạng thái hiển thị của khóa học (cần xác thực)
    @PUT("api/courses/change-course-visibility/{id}")
    Observable<ChangeCourseVisibilityResponse> changeCourseVisibility(@Header("Authorization") String token, @Path("id") String courseId);

    // Endpoint để cập nhật ảnh đại diện người dùng (cần xác thực, upload file)
    @Multipart
    @PUT("api/users/update-profile-picture")
    Observable<UserPictureResponse> updateProfilePicture(@Header("Authorization") String token, @Part MultipartBody.Part picture);

    // Endpoint để lấy thông tin giỏ hàng của người dùng (cần xác thực)
    @GET("api/carts/getCart")
    Observable<CartResponse> getCart(@Header("Authorization") String token);

    // Endpoint để lấy thông tin chi tiết khóa học theo ID
    @GET("api/courses/get_course/{id}")
    Observable<CourseIntroResponse> getCourseIntroById(@Path("id") String courseId);

    // Endpoint để lấy tất cả các khóa học
    @GET("api/courses/all")
    Observable<CoursehomeResponse> getAllCourses();

    // Endpoint để lấy danh sách khóa học của người dùng (cần xác thực)
    @GET("api/invoices/my_course")
    Observable<MyCoursesResponse> getMyCourses(@Header("Authorization") String token);

    // Endpoint để lấy các mục trong giỏ hàng của người dùng (cần xác thực)
    @GET("api/carts/getCart")
    Observable<CartListItemResponse> getCartItem(@Header("Authorization") String token);

    // Endpoint để thực hiện thanh toán và tạo hóa đơn (cần xác thực)
    @POST("api/invoices/checkout")
    Observable<CheckoutResponse> checkout(@Header("Authorization") String token, @Body CheckoutRequest request);

    // Endpoint để tạo đánh giá cho khóa học (cần xác thực)
    @POST("/api/reviews/create_review/{id}")
    Observable<ReviewResponse> sendReview(@Header("Authorization") String token, @Path("id") String courseId, @Body ReviewRequest request);

    // Endpoint để xóa toàn bộ giỏ hàng (cần xác thực)
    @DELETE("api/carts/clearCart")
    Completable clearCart(@Header("Authorization") String token);

    // Endpoint để xóa một mục trong giỏ hàng (cần xác thực)
    @DELETE("api/carts/removeFromCart/{cartId}/{courseId}")
    Completable removeFromCart(@Header("Authorization") String token, @Path("cartId") String cartId, @Path("courseId") String courseId);

    // Endpoint để lấy thông tin người dùng hiện tại (cần xác thực)
    @GET("api/users")
    Observable<UserResponse> getUserDetails(@Header("Authorization") String token);

    // Endpoint để thêm khóa học vào giỏ hàng (cần xác thực)
    @POST("api/carts/addToCart")
    Observable<CartItemResponse> addToCart(@Header("Authorization") String token, @Body AddToCartRequest request);

    // Endpoint để lấy danh sách bài học của giảng viên theo khóa học
    @POST("api/lessons/get-course-lessons")
    Observable<LessonListResponse> getInstructorLessons(@Body CourseIdRequest courseId);

    // Endpoint để xóa khóa học (cần xác thực)
    @DELETE("api/courses/delete-course/{id}")
    Completable deleteCourse(@Header("Authorization") String token, @Path("id") String courseId);

    // Endpoint để lấy danh sách khóa học của giảng viên theo ID (cần xác thực)
    @GET("api/courses/get-courses-by-instructor/{id}")
    Observable<CourseListResponse> getInstructorCourses(@Header("Authorization") String token, @Path("id") String instructorId);

    // Endpoint để xóa bài học (cần xác thực)
    @DELETE("api/lessons/delete-lesson/{id}")
    Completable deleteLesson(@Header("Authorization") String token, @Path("id") String lessonId);

    // Endpoint để xóa tài liệu (cần xác thực)
    @DELETE("api/documents/delete-document/{id}")
    Completable deleteDocument(@Header("Authorization") String token, @Path("id") String documentId);

    // Endpoint để lấy danh sách tài liệu của bài học
    @POST("api/documents/get-lesson-documents")
    Observable<DocumentResponse> getLessonDocuments(@Body DocumentRequest request);

    // Endpoint để lấy danh sách bình luận của bài học
    @POST("api/comments/get-lesson-comments")
    Observable<CommentResponse> getLessonComments(@Body CommentRequest request);

    // Endpoint để tạo bình luận (cần xác thực)
    @POST("api/comments/create-comment")
    Observable<CreateCommentResponse> createComment(@Header("Authorization") String token, @Body CreateCommentRequest request);

    // Endpoint để tìm kiếm các khóa học
    @POST("api/courses/search-courses")
    Observable<CourseResponse> searchCourses(@Body com.example.projectfinaltth.data.model.request.RequestBody requestBody);

    // Endpoint để lấy danh sách bài học theo khóa học
    @POST("api/lessons/get-course-lessons")
    Observable<LessonByCourseResponse> getLessonsByCourse(@Body CourseDetailRequest requestBody);

    // Endpoint để tạo bài học mới (cần xác thực)
    @POST("api/lessons/create-lesson")
    Observable<LessonItem> createLesson(@Header("Authorization") String token, @Body LessonRequest lessonRequest);

    // Endpoint để cập nhật bài học (cần xác thực)
    @PUT("api/lessons/update-lesson/{id}")
    Observable<LessonItem> updateLesson(@Header("Authorization") String token, @Path("id") String lessonId, @Body LessonRequest lessonRequest);

    // Endpoint để lấy tài liệu của bài học
    @POST("api/documents/get-lesson-documents")
    Observable<DocumentResponse> getDocumentByLesson(@Body DocumentRequest documentRequest);

    // Endpoint để tạo tài liệu mới (cần xác thực, upload file)
    @Multipart
    @POST("api/documents/create-document")
    Single<DocumentResponse> createDocument(
            @Header("Authorization") String token,
            @Part("lessonId") RequestBody lessonId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    // Endpoint để thay đổi mật khẩu người dùng (cần xác thực)
    @PUT("/api/users/change-password-app-user")
    Completable changePassword(@Header("Authorization") String token, @Body ChangePasswordRequest request);

    // Endpoint để cập nhật tên người dùng (cần xác thực)
    @PUT("/api/users/update-profile")
    Completable changeName(@Header("Authorization") String token, @Body ChangeNameRequest request);

    // Endpoint để cập nhật ảnh đại diện người dùng (cần xác thực, upload file)
    @Multipart
    @PUT("/api/users/update-profile-picture")
    Completable changePicture(@Header("Authorization") String token, @Part MultipartBody.Part picture);

    // Endpoint để tìm kiếm khóa học theo chuỗi
    @GET("api/courses/find_course/{str}")
    Observable<CourseSearchResponse> findCourse(@Path("str") String query);

    // Endpoint để tạo khóa học mới (cần xác thực, upload file)
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

    // Endpoint để cập nhật thông tin giới thiệu khóa học (cần xác thực, upload file)
    @Multipart
    @PUT("api/courses/update-course-intro/{id}")
    Single<CourseItem> updateCourse(
            @Header("Authorization") String token,
            @Path("id") String courseId,
            @Part("title") RequestBody title,
            @Part("price") RequestBody price,
            @Part("topic") RequestBody topic,
            @Part("description") RequestBody description,
            @Part("userId") RequestBody userId,
            @Part MultipartBody.Part picture
    );
}
