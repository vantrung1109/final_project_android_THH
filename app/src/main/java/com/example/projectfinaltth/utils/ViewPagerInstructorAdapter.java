package com.example.projectfinaltth.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectfinaltth.ui.fragment.AccountInstructorFragment;
import com.example.projectfinaltth.ui.fragment.CreateInstructorFragment;
import com.example.projectfinaltth.ui.fragment.HomeInstructorFragment;

public class ViewPagerInstructorAdapter extends FragmentStateAdapter {

    public ViewPagerInstructorAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Tạo các fragment tương ứng với vị trí
        switch (position) {
            case 0:
                return new HomeInstructorFragment();  // HomeInstructorFragment cho trang chủ
            case 1:
                return new CreateInstructorFragment();  // CreateInstructorFragment cho khóa học
            case 2:
                return new AccountInstructorFragment();  // AccountInstructorFragment cho tài khoản
            default:
                return new HomeInstructorFragment();  // Mặc định trả về HomeInstructorFragment
        }
    }

    @Override
    public int getItemCount() {
        // Trả về tổng số trang (fragment)
        return 3;
    }
}
