package com.example.projectfinaltth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InstructorCourseAdapter extends RecyclerView.Adapter<InstructorCourseAdapter.InstructorCourseViewHolder> {
    private List<InstructorCourse> instructorCourseList;

    public InstructorCourseAdapter(List<InstructorCourse> instructorCourseList) {
        this.instructorCourseList = instructorCourseList;
    }

    @NonNull
    @Override
    public InstructorCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_instructor_course, parent, false);
        return new InstructorCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorCourseViewHolder holder, int position) {
        InstructorCourse instructorCourse = instructorCourseList.get(position);
        holder.imageView.setImageResource(instructorCourse.getImageResource());
        holder.titleTextView.setText(instructorCourse.getTitle());
        holder.typeTextView.setText(instructorCourse.getType());
        holder.priceTextView.setText(instructorCourse.getPrice());
        holder.descriptionTextView.setText(instructorCourse.getDescription());
    }

    @Override
    public int getItemCount() {
        return instructorCourseList.size();
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
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
