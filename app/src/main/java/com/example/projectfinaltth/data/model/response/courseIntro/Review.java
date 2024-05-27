package com.example.projectfinaltth.data.model.response.courseIntro;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class Review extends AbstractFlexibleItem<Review.ReviewViewHolder> implements Serializable {
    String _id;
    String userId;
    String courseId;
    Double ratingStar;
    String content;
    String createdAt;
    String updatedAt;
    Integer __v;
    String userName;
    String userPicture;

    @Override
    public int getLayoutRes() {
        return R.layout.rcv_item_review;
    }

    @Override
    public ReviewViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new ReviewViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, ReviewViewHolder holder, int position, List<Object> payloads) {
        holder.tvNameInstructor.setText(userName);
        holder.tvReviewContent.setText(content);

        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = inputFormat.parse(createdAt);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String formattedDate = outputFormat.format(date);
        holder.tvDateCreated.setText(formattedDate);
        Glide.with(holder.itemView.getContext()).load(userPicture).into(holder.imgReview);
    }

    public class ReviewViewHolder extends FlexibleViewHolder {
        ImageView imgReview;
        TextView tvNameInstructor, tvReviewContent, tvDateCreated;
        public ReviewViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);

            imgReview = view.findViewById(R.id.img_review);
            tvNameInstructor = view.findViewById(R.id.tv_name_instructor);
            tvReviewContent = view.findViewById(R.id.tv_review_content);
            tvDateCreated = view.findViewById(R.id.tv_date_created);
        }
    }
}
