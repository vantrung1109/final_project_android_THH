package com.example.projectfinaltth.ui.instructor;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;

import java.util.List;

public class CourseLessonAdapter extends RecyclerView.Adapter<CourseLessonAdapter.CourseLessonViewHolder> {
    private List<CourseLesson> courseLessonList;

    public CourseLessonAdapter(List<CourseLesson> courseLessonList) {
        this.courseLessonList = courseLessonList;
    }

    @NonNull
    @Override
    public CourseLessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_lesson, parent, false);
        return new CourseLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseLessonViewHolder holder, int position) {
        CourseLesson courseLesson  = courseLessonList.get(position);
        holder.imageView.setImageResource(courseLesson.getImageResource());
        holder.titleTextView.setText(courseLesson.getTitle());
        holder.descriptionTextView.setText(courseLesson.getDescription());
    }

    @Override
    public int getItemCount() {
        return courseLessonList.size();
    }

    public static class CourseLessonViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, descriptionTextView;
        ImageButton editButton, deleteButton;

        public CourseLessonViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
