package com.example.projectfinaltth.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.projectfinaltth.data.model.response.course.CourseItem;
import com.example.projectfinaltth.ui.instructor.CourseIntroInstructorActivity;
import com.example.projectfinaltth.ui.instructor.InstructorLessonActivity;
import com.example.projectfinaltth.ui.instructor.UpdateCourseActivity;

import java.util.List;

public class InstructorCourseAdapter extends RecyclerView.Adapter<InstructorCourseAdapter.InstructorCourseViewHolder> {

    private Context context;
    private List<CourseItem> courseItemList;
    private String instructorName;
    private OnItemInteractionListener onItemInteractionListener;
    // Đặng Xuân Hùng - 21110194


    public InstructorCourseAdapter(Context context, List<CourseItem> courseItemList,String instructorName, OnItemInteractionListener onItemInteractionListener) {
        this.context = context;
        this.courseItemList = courseItemList;
        this.instructorName = instructorName;
        this.onItemInteractionListener = onItemInteractionListener;
    }

    @NonNull
    @Override
    public InstructorCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_item_course_instructor, parent, false);
        return new InstructorCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorCourseViewHolder holder, int position) {
        CourseItem courseItem = courseItemList.get(position);

        holder.titleTextView.setText(courseItem.getTitle());
        holder.priceTextView.setText("" + courseItem.getPrice());
        holder.tvInstructorName.setText(instructorName);
        //holder.descriptionTextView.setText(courseItem.getDescription());
        holder.btnViewIntro.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseIntroInstructorActivity.class);
            intent.putExtra("courseId", courseItem.getId());
            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v -> {
            // Gửi dữ liệu cần thiết sang activity mới
            Intent intent = new Intent(context, CourseIntroInstructorActivity.class);
            intent.putExtra("courseId", courseItem.getId());
            context.startActivity(intent);
        });
        // Load image using Glide
        Glide.with(context)
                .load(courseItem.getPicture())
                .into(holder.imageView);

        if (courseItem.getVisibility()) {
            holder.itemView.setAlpha(1.0f);
            Log.e("visibility", "true");
        } else if (!courseItem.getVisibility()){
            holder.itemView.setAlpha(0.3f);
            Log.e("visibility", "false");
        }

        holder.btnChangeCourseVisibility.setOnClickListener(v -> {
            onItemInteractionListener.onChangeCourseVisibility(holder.getAdapterPosition(), courseItem);
        });

        // Set click listeners for edit and delete buttons
       // holder.deleteButton.setOnClickListener(v -> onItemInteractionListener.onDeleteCourse(holder.getAdapterPosition(), courseItem));

    }

    @Override
    public int getItemCount() {
        return courseItemList.size();
    }

    public static class InstructorCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, priceTextView, tvInstructorName;
        Button btnViewIntro;
        ImageButton btnChangeCourseVisibility;

        public InstructorCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_course);
            titleTextView = itemView.findViewById(R.id.tv_title);
            tvInstructorName = itemView.findViewById(R.id.tv_name_instructor);
            priceTextView = itemView.findViewById(R.id.tv_price_course);
            btnViewIntro = itemView.findViewById(R.id.btn_view_intro);
            btnChangeCourseVisibility = itemView.findViewById(R.id.btn_change_visibility);
        }
    }

    public interface OnItemInteractionListener {
        void onChangeCourseVisibility(int position, CourseItem courseItem);

    }
}
