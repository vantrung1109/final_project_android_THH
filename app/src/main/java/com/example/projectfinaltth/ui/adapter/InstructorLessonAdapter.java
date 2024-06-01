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
import com.example.projectfinaltth.data.model.response.lesson.LessonItem;
import com.example.projectfinaltth.ui.instructor.InstructorDocumentActivity;
import com.example.projectfinaltth.ui.instructor.InstructorLessonActivity;
import com.example.projectfinaltth.ui.instructor.UpdateLessonActivity;

import java.util.List;

public class InstructorLessonAdapter extends RecyclerView.Adapter<InstructorLessonAdapter.InstructorLessonViewHolder> {

    private Context context;
    private List<LessonItem> lessonItemList;
    private OnItemInteractionListener onItemInteractionListener;

    public InstructorLessonAdapter(Context context, List<LessonItem> lessonItemList, OnItemInteractionListener onItemInteractionListener) {
        this.context = context;
        this.lessonItemList = lessonItemList;
        this.onItemInteractionListener = onItemInteractionListener;
    }

    @NonNull
    @Override
    public InstructorLessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course_lesson, parent, false);
        return new InstructorLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorLessonViewHolder holder, int position) {
        LessonItem lessonItem = lessonItemList.get(position);
        holder.editButton.setOnClickListener(v -> {
            onItemInteractionListener.onEditLesson(holder.getAdapterPosition(), lessonItem);
//            Intent intent = new Intent(context, UpdateLessonActivity.class);
//            intent.putExtra("lessonId", lessonItem.getId());
//            intent.putExtra("courseId", lessonItem.getCourseId());
//            intent.putExtra("lessonTitle", lessonItem.getTitle());
//            intent.putExtra("lessonDescription", lessonItem.getDescription());
//            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v -> {
            // Gửi dữ liệu cần thiết sang activity mới
            Intent intent = new Intent(context, InstructorDocumentActivity.class);
            intent.putExtra("lessonId",  lessonItem.getId()); // Chuyển ID của khóa học
            context.startActivity(intent);
        });
        holder.titleTextView.setText("Title: " + lessonItem.getTitle());
        holder.descriptionTextView.setText("Description: " + lessonItem.getDescription());
        holder.imageView.setImageResource(R.drawable.book);
        holder.deleteButton.setOnClickListener(v -> onItemInteractionListener.onDeleteLesson(holder.getAdapterPosition(), lessonItem));
    }

    @Override
    public int getItemCount() {
        return lessonItemList.size();
    }

    public static class InstructorLessonViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, descriptionTextView;
        ImageButton editButton, deleteButton;

        public InstructorLessonViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

        }
    }

    public interface OnItemInteractionListener {
        void onDeleteLesson(int position,LessonItem lessonItem);
        void onEditLesson(int position,LessonItem lessonItem);
    }
}
