package com.example.projectfinaltth.ui.main;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.utils.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
public class MainActivity extends AppCompatActivity {
    private ViewPager2 mViewPager; // ViewPager2 để quản lý các fragment
    private BottomNavigationView mBottomNavigationView; // BottomNavigationView để hiển thị các tab

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this); // Kích hoạt chế độ Edge-to-Edge cho ứng dụng
        setContentView(R.layout.activity_main); // Thiết lập layout cho Activity

        // Liên kết ViewPager2 từ layout
        mViewPager = findViewById(R.id.view_pager);
        // Liên kết BottomNavigationView từ layout
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        // Khởi tạo ViewPagerAdapter và thiết lập adapter cho ViewPager2
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3); // Thiết lập số lượng trang sẽ được giữ trong bộ nhớ

        // Thiết lập callback cho sự kiện thay đổi trang trên ViewPager2
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Đổi tab trên BottomNavigationView khi trang trên ViewPager2 thay đổi
                switch (position) {
                    case 0:
                        mBottomNavigationView.setSelectedItemId(R.id.home_tab);
                        break;
                    case 1:
                        mBottomNavigationView.setSelectedItemId(R.id.cart_tab);
                        break;
                    case 2:
                        mBottomNavigationView.setSelectedItemId(R.id.course_tab);
                        break;
                    case 3:
                        mBottomNavigationView.setSelectedItemId(R.id.account_tab);
                        break;
                }
            }
        });

        // Thiết lập listener cho sự kiện chọn tab trên BottomNavigationView
        mBottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            // Đổi trang trên ViewPager2 khi tab trên BottomNavigationView thay đổi
            if (id == R.id.home_tab) {
                mViewPager.setCurrentItem(0);
            } else if (id == R.id.cart_tab) {
                mViewPager.setCurrentItem(1);
            } else if (id == R.id.course_tab) {
                mViewPager.setCurrentItem(2);
            } else if (id == R.id.account_tab) {
                mViewPager.setCurrentItem(3);
            }
            return true; // Trả về true để hiển thị tab được chọn
        });
    }
}
