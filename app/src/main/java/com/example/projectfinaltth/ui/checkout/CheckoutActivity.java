package com.example.projectfinaltth.ui.checkout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.ApiService;
import com.example.projectfinaltth.data.ShareRefences.DataLocalManager;
import com.example.projectfinaltth.data.model.request.checkout.CheckoutRequest;
import com.example.projectfinaltth.data.model.response.checkout.CartItemResponse;
import com.example.projectfinaltth.data.model.response.checkout.CartResponse;
import com.example.projectfinaltth.data.model.response.checkout.CourseOrder;
import com.example.projectfinaltth.data.model.response.courseIntro.CourseIntroResponse;
import com.example.projectfinaltth.databinding.ActivityCheckoutBinding;
import com.example.projectfinaltth.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
public class CheckoutActivity extends AppCompatActivity {
    ActivityCheckoutBinding mActivityCheckoutBinding;
    Boolean isMomo = false, isPaypal = false;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    FlexibleAdapter flexibleAdapterCourses;
    MutableLiveData<CartResponse> cart = new MutableLiveData<>();
    MutableLiveData<List<CourseOrder>> listCourseOrders = new MutableLiveData<>();
    Double total = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCheckoutBinding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(mActivityCheckoutBinding.getRoot());

        String token = "Bearer " + DataLocalManager.getToken();
        // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        // Xử lý sự kiện khi người dùng click vào nút thanh toán momo, paypal
        mActivityCheckoutBinding.rb1.setOnClickListener(v -> {
            mActivityCheckoutBinding.rb1.setChecked(true);
            mActivityCheckoutBinding.rb2.setChecked(false);
            isMomo = true;
            isPaypal = false;
        });
        mActivityCheckoutBinding.rb2.setOnClickListener(v -> {
            mActivityCheckoutBinding.rb1.setChecked(false);
            mActivityCheckoutBinding.rb2.setChecked(true);
            isMomo = false;
            isPaypal = true;
        });

        // Thực hiện gọi API lấy cart
        compositeDisposable.add(
                ApiService.apiService.getCart(token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {
                            // Thực hiện set dữ liệu lên view
                            cart.setValue(response);
                            Log.e("cart", response.toString());

                        }, throwable -> {
                            Toast.makeText(this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        })
        );


        // Lấy danh sách các khóa học trong giỏ hàng sau khi lấy cart
        cart.observe(this, cartResponse -> {
            List<CourseOrder> list = new ArrayList<>();
            for (CartItemResponse cartItemResponse : cartResponse.getCartItems()) {
                compositeDisposable.add(
                        // Thực hiện gọi API lấy course by id
                        ApiService.apiService.getCourseIntroById(cartItemResponse.getCourseId())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(response -> {

                                    CourseOrder courseOrder = new CourseOrder(response.getCourse());
                                    list.add(courseOrder);
                                    listCourseOrders.setValue(list);
                                }, throwable -> {
                                    Log.e("TAG", "Error: " + throwable.getMessage());
                                })
                );
            }
        });

        // Hiển thị danh sách các khóa học trong giỏ hàng
        flexibleAdapterCourses = new FlexibleAdapter(new ArrayList<>());
        mActivityCheckoutBinding.rcvListOrder.setAdapter(flexibleAdapterCourses);
        mActivityCheckoutBinding.rcvListOrder.setLayoutManager(new LinearLayoutManager(this));
        listCourseOrders.observe(this, courseOrders -> {
            for (CourseOrder courseOrder : courseOrders) {
                total += courseOrder.getPrice();
            }
            mActivityCheckoutBinding.tvTotalPrice.setText(total.toString());
            flexibleAdapterCourses.updateDataSet(courseOrders);
        });

        String paymentMethod = "";
        if (isPaypal) {
            paymentMethod = "PAYPAL";
        } else {
            paymentMethod = "MOMO";
        }
        CheckoutRequest checkoutRequest = new CheckoutRequest(paymentMethod);

        // MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
        // Xử lý sự kiện khi người dùng click vào nút thanh toán
        mActivityCheckoutBinding.btnCheckout.setOnClickListener(v -> {
            compositeDisposable.add(
                    // Thực hiện gọi API checkout
                    ApiService.apiService.checkout(token,checkoutRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                Toast.makeText(this, "Checkout success", Toast.LENGTH_SHORT).show();
                                // Chuyển sang màn hình main sau khi checkout thành công
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                            }, throwable -> {
                                Toast.makeText(this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            ));
        });

        // Xử lý sự kiện khi người dùng click vào nút back
        mActivityCheckoutBinding.buttonBack.setOnClickListener(v -> {
            finish();
        });
    }
}
