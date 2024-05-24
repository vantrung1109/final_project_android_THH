package com.example.projectfinaltth.ui.checkout;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.databinding.ActivityCheckoutBinding;

public class CheckoutActivity extends AppCompatActivity {
    ActivityCheckoutBinding mActivityCheckoutBinding;
    Boolean isMomo = false, isPaypal = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityCheckoutBinding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(mActivityCheckoutBinding.getRoot());

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
    }
}
