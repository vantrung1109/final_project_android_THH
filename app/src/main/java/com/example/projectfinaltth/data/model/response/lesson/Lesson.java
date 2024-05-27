package com.example.projectfinaltth.data.model.response.lesson;

import android.view.View;
import android.widget.TextView;

import com.example.projectfinaltth.R;

import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson extends AbstractFlexibleItem<Lesson.LessonViewHolder> {
    String _id;
    String courseId;
    String title;
    String description;
    Boolean status;
    String createdAt;
    String updatedAt;
    Integer __v;

    @Override
    public int getLayoutRes() {
        return R.layout.rcv_item_lesson;
    }

    @Override
    public LessonViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new LessonViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, LessonViewHolder holder, int position, List<Object> payloads) {
        holder.tvTitle.setText(title);
        holder.tvDescription.setText(description);
        holder.tvDate.setText(createdAt);
    }

    public static class LessonViewHolder extends FlexibleViewHolder {
        TextView tvTitle, tvDescription, tvDate;
        TextView btnLessonDetail;

        public LessonViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            tvTitle = view.findViewById(R.id.tv_title);
            tvDescription = view.findViewById(R.id.tv_lesson_description);
            tvDate = view.findViewById(R.id.tv_lesson_date);
            btnLessonDetail = view.findViewById(R.id.btn_lesson_detail);

        }
    }
}
