package com.example.projectfinaltth.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.model.response.courseIntro.Course;

import java.util.List;

public class MyCourseAdapter extends RecyclerView.Adapter<MyCourseAdapter.CourseViewHolder> {
    private Context context;
    private List<Course> courseList;

    public MyCourseAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.titleTxt.setText(course.getTitle());
        holder.authorTxt.setText(course.getInstructorName());

        // Sử dụng Glide để tải ảnh từ URL
        Glide.with(context)
                .load(course.getPicture())
//                .placeholder(R.drawable.placeholder) // Bạn có thể đặt một hình ảnh placeholder trong khi chờ tải ảnh
//                .error(R.drawable.error) // Bạn có thể đặt một hình ảnh hiển thị khi có lỗi tải ảnh
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        TextView titleTxt, authorTxt;
        Button viewDetailBtn;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.pic);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            authorTxt = itemView.findViewById(R.id.authorTxt);
            viewDetailBtn = itemView.findViewById(R.id.viewDetailBtn);
        }
    }
}
