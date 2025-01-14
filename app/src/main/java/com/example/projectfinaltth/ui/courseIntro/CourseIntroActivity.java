package com.example.projectfinaltth.ui.courseIntro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.AddToCartRequest;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.data.model.response.courseIntro.MyCoursesResponse;
import com.example.projectfinaltth.databinding.ActivityCourseIntroBinding;
import com.example.projectfinaltth.ui.courseDetail.CourseDetailActivity;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
// Xử lý khi người dùng nhấn vào view intro của 1 khóa học
public class CourseIntroActivity extends AppCompatActivity {
    ActivityCourseIntroBinding mActivityCourseIntroBinding;
    FlexibleAdapter flexibleAdapterReviews;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    MutableLiveData<MyCoursesResponse> listMyCourses = new MutableLiveData<>();
    CourseIntroResponse courseIntent;
    boolean isPurchased = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        mActivityCourseIntroBinding = ActivityCourseIntroBinding.inflate(getLayoutInflater());
        setContentView(mActivityCourseIntroBinding.getRoot());

        // Lấy courseId từ Intent
        String courseId = getIntent().getStringExtra("course_id");

        // Lấy token từ DataLocalManager
        String token = "Bearer " + DataLocalManager.getToken();

        // Hiển thị ProgressBar khi tải dữ liệu
        mActivityCourseIntroBinding.progressBar.setVisibility(ProgressBar.VISIBLE);

        // Lấy danh sách khóa học của người dùng
        compositeDisposable.add(
                ApiService.apiService.getMyCourses(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            listMyCourses.setValue(response);
                            List<String> listCourseId = new ArrayList<>();
                            for (Course course_temp : response.getCourses()) {
                                listCourseId.add(course_temp.get_id());
                            }
                            if (listCourseId.contains(courseId)) {
                                isPurchased = true;
                                mActivityCourseIntroBinding.btnAddToCart.setBackground(getResources().getDrawable(R.drawable.background_custom_border_blue, null));
                                mActivityCourseIntroBinding.btnAddToCart.setText("View Detail");
                            }
                            mActivityCourseIntroBinding.progressBar.setVisibility(ProgressBar.GONE);
                        }, throwable -> {
                            Log.e("TAG", "Error: " + throwable.getMessage());
                            mActivityCourseIntroBinding.progressBar.setVisibility(ProgressBar.GONE);
                        })
        );

        // Gọi API lấy thông tin khóa học
        compositeDisposable.add(
                ApiService.apiService.getCourseIntroById(courseId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            // Set dữ liệu lên view
                            mActivityCourseIntroBinding.tvCourseName.setText(response.getCourse().getTitle());
                            mActivityCourseIntroBinding.tvCourseAuthor.setText(response.getCourse().getInstructorName());
                            mActivityCourseIntroBinding.tvDescriptionContent.setText(response.getCourse().getDescription());
                            mActivityCourseIntroBinding.tvCoursePrice.setText(String.valueOf(response.getCourse().getPrice()));
                            mActivityCourseIntroBinding.ratingBar.setRating(response.getAverageStars().floatValue());
                            Glide.with(this).load(response.getCourse().getPicture()).into(mActivityCourseIntroBinding.imgCourseIntro);
                            courseIntent = response;

                            flexibleAdapterReviews = new FlexibleAdapter(response.getReviews());
                            mActivityCourseIntroBinding.rcvReviews.setAdapter(flexibleAdapterReviews);
                            mActivityCourseIntroBinding.rcvReviews.setLayoutManager(new LinearLayoutManager(this));
                            mActivityCourseIntroBinding.progressBar.setVisibility(ProgressBar.GONE);
                        }, throwable -> {
                            Log.e("TAG", "Error: " + throwable.getMessage());
                            mActivityCourseIntroBinding.progressBar.setVisibility(ProgressBar.GONE);
                        })
        );

        // Xử lý sự kiện bấm nút "Add to Cart" hoặc "Detail"
        mActivityCourseIntroBinding.btnAddToCart.setOnClickListener(v -> {
            if (isPurchased) {
                Intent intent = new Intent(this, CourseDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("courseIntro", courseIntent);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                addToCart(courseId);
            }
        });
        mActivityCourseIntroBinding.buttonBack.setOnClickListener(v -> finish());
    }

    //MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
    // Gọi API thêm khóa học vào giỏ hàng
    private void addToCart(String courseId) { // Phương thức thêm vào giỏ hàng với courseId là mã khóa học
        String token = DataLocalManager.getToken(); // Lấy token từ DataLocalManager
        String cartId = DataLocalManager.getCartId(); // Lấy cartId từ DataLocalManager

        // Kiểm tra nếu cartId rỗng, thông báo lỗi
        if (cartId == null || cartId.isEmpty()) {
            Toast.makeText(this, "Cart ID is missing", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi
            return; // Kết thúc phương thức
        }

        AddToCartRequest request = new AddToCartRequest(courseId, cartId); // Tạo đối tượng request để thêm vào giỏ hàng với courseId và cartId

        compositeDisposable.add( // Thêm disposable vào CompositeDisposable
                ApiService.apiService.addToCart("Bearer " + token, request) // Gọi API để thêm vào giỏ hàng với token và request
                        .subscribeOn(Schedulers.io()) // Đăng ký trên luồng IO
                        .observeOn(AndroidSchedulers.mainThread()) // Quan sát trên luồng chính
                        .subscribe(cartItemResponse -> { // Đăng ký để nhận phản hồi
                            Toast.makeText(this, "Add to cart", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo thành công
                        }, throwable -> { // Xử lý lỗi
                            String errorMessage = throwable.getMessage(); // Lấy thông báo lỗi
                            if (errorMessage != null && errorMessage.contains("already in your cart")) { // Kiểm tra nếu thông báo lỗi chứa "already in your cart"
                                Toast.makeText(this, "This course is already in your cart.", Toast.LENGTH_SHORT).show(); // Hiển thị thông báo khóa học đã có trong giỏ hàng
                            } else { // Nếu không
                                Toast.makeText(this, "Failed to add to cart: " + errorMessage, Toast.LENGTH_SHORT).show(); // Hiển thị thông báo lỗi khi thêm vào giỏ hàng
                            }
                        })
        );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
