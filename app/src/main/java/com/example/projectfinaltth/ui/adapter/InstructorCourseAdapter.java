package com.example.projectfinaltth.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.model.response.course.CourseItem;
import com.example.projectfinaltth.ui.instructor.InstructorLessonActivity;
import com.example.projectfinaltth.ui.instructor.UpdateCourseActivity;
import com.example.projectfinaltth.ui.instructor.UpdateLessonActivity;

import java.util.List;

public class InstructorCourseAdapter extends RecyclerView.Adapter<InstructorCourseAdapter.InstructorCourseViewHolder> {

    private Context context;
    private List<CourseItem> courseItemList;
    private OnItemInteractionListener onItemInteractionListener;

    public InstructorCourseAdapter(Context context, List<CourseItem> courseItemList, OnItemInteractionListener onItemInteractionListener) {
        this.context = context;
        this.courseItemList = courseItemList;
        this.onItemInteractionListener = onItemInteractionListener;
    }

    @NonNull
    @Override
    public InstructorCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_instructor_course, parent, false);
        return new InstructorCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorCourseViewHolder holder, int position) {
        CourseItem courseItem = courseItemList.get(position);

        holder.titleTextView.setText(courseItem.getTitle());
        holder.typeTextView.setText(courseItem.getTopic());
        holder.priceTextView.setText("$" + courseItem.getPrice());
        //holder.descriptionTextView.setText(courseItem.getDescription());
        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateCourseActivity.class);
            intent.putExtra("courseId", courseItem.getId());
            intent.putExtra("title", courseItem.getTitle());
            intent.putExtra("description", courseItem.getDescription());
            intent.putExtra("price", String.valueOf(courseItem.getPrice()));
            intent.putExtra("topic", courseItem.getTopic());
            intent.putExtra("picture", courseItem.getPicture());
            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v -> {
            // Gửi dữ liệu cần thiết sang activity mới
            Intent intent = new Intent(context, InstructorLessonActivity.class);
            intent.putExtra("courseId", courseItem.getId()); // Chuyển ID của khóa học
            context.startActivity(intent);
        });
        // Load image using Glide
        Glide.with(context)
                .load(courseItem.getPicture())
                .into(holder.imageView);

        // Set click listeners for edit and delete buttons
        holder.deleteButton.setOnClickListener(v -> onItemInteractionListener.onDeleteCourse(holder.getAdapterPosition(), courseItem));

    }

    @Override
    public int getItemCount() {
        return courseItemList.size();
    }

    public static class InstructorCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, typeTextView, priceTextView, descriptionTextView;
        ImageButton editButton, deleteButton;

        public InstructorCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            //descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }

    public interface OnItemInteractionListener {
        void onDeleteCourse(int position, CourseItem courseItem);

    }
}
