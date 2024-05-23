package com.example.projectfinaltth.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectfinaltth.ui.fragment.AccountFragment;
import com.example.projectfinaltth.ui.fragment.CartFragment;
import com.example.projectfinaltth.ui.fragment.CourseFragment;
import com.example.projectfinaltth.ui.fragment.HomeFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Tạo các fragment tương ứng với vị trí
        switch (position) {
            case 0:
                return new HomeFragment();  // HomeFragment cho trang chủ
            case 1:
                return new CartFragment();  // CartFragment cho giỏ hàng
            case 2:
                return new CourseFragment();  // CourseFragment cho khóa học
            case 3:
                return new AccountFragment();  // AccountFragment cho tài khoản
            default:
                return new HomeFragment();  // Mặc định trả về HomeFragment
        }
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số trang (fragment)
        return 4;
    }
}
