package com.example.projectfinaltth.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;
import com.example.projectfinaltth.ui.courseIntro.CourseIntroActivity;
import com.example.projectfinaltth.ui.fragment.HomeFragment;

import java.util.List;
//MSSV:21110826 Họ Và Tên: Từ Thanh Hoài
public class HomeCourseAdapter extends RecyclerView.Adapter<HomeCourseAdapter.ViewHolder> {

    private List<Course> items; // Danh sách các khóa học
    private HomeFragment homeFragment; // Fragment chứa adapter này

    public HomeCourseAdapter(List<Course> items, HomeFragment homeFragment) {
        this.items = items; // Khởi tạo danh sách khóa học
        this.homeFragment = homeFragment; // Khởi tạo fragment chứa adapter
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho mỗi item trong RecyclerView
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_course, parent, false);
        return new ViewHolder(inflate); // Trả về ViewHolder mới
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Lấy khóa học từ danh sách tại vị trí position
        Course course = items.get(position);
        // Thiết lập các giá trị cho các view trong ViewHolder
        holder.titleTxt.setText(course.getTitle());
        holder.ownerTxt.setText(course.getInstructorName());
        holder.priceTxt.setText("$" + course.getPrice());

        // Sử dụng Glide để tải hình ảnh của khóa học vào ImageView
        Glide.with(holder.itemView.getContext())
                .load(course.getPicture())
                .into(holder.pic);

        // Thiết lập sự kiện click cho nút "Add to Cart"
        holder.addToCartButton.setOnClickListener(v -> {
            homeFragment.addToCart(course.get_id()); // Gọi phương thức addToCart của fragment để thêm khóa học vào giỏ hàng
        });

        // Thiết lập sự kiện click cho nút "View Intro"
        holder.viewIntroButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CourseIntroActivity.class); // Tạo intent để mở CourseIntroActivity
            intent.putExtra("course_id", course.get_id()); // Truyền course_id vào intent
            holder.itemView.getContext().startActivity(intent); // Bắt đầu activity
        });
    }

    @Override
    public int getItemCount() {
        return items.size(); // Trả về số lượng khóa học trong danh sách
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, ownerTxt, priceTxt; // TextView để hiển thị tiêu đề, tên người dạy và giá khóa học
        ImageView pic; // ImageView để hiển thị hình ảnh khóa học
        ImageButton addToCartButton; // ImageButton để thêm khóa học vào giỏ hàng
        Button viewIntroButton; // Button để xem giới thiệu khóa học

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Liên kết các view với ID trong layout
            titleTxt = itemView.findViewById(R.id.titleTxt);
            ownerTxt = itemView.findViewById(R.id.ownerTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            pic = itemView.findViewById(R.id.pic);
            addToCartButton = itemView.findViewById(R.id.buttonAddToCart);
            viewIntroButton = itemView.findViewById(R.id.buttonViewIntro);
        }
    }
}
