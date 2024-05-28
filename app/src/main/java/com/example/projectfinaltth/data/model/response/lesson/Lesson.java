package com.example.projectfinaltth.data.model.response.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.ui.lesson_detail.LessonDetailActivity;
import com.example.projectfinaltth.utils.DateConvertUtils;

import java.io.Serializable;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
import eu.davidea.flexibleadapter.items.IFlexible;
import eu.davidea.viewholders.FlexibleViewHolder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// MSSV: 21110335, Họ và tên: Nguyễn Trần Văn Trung
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lesson extends AbstractFlexibleItem<Lesson.LessonViewHolder> implements Serializable {
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
        holder.tvDate.setText(DateConvertUtils.convertDateTimeToDate(createdAt));
        holder.btnLessonDetail.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), LessonDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("lesson", this);
            intent.putExtras(bundle);
            v.getContext().startActivity(intent);
        });

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
