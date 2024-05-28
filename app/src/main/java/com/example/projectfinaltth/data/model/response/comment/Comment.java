package com.example.projectfinaltth.data.model.response.comment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.utils.DateConvertUtils;

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
public class Comment  extends AbstractFlexibleItem<Comment.CommentViewHolder> {
    String _id;
    String lessonId;
    String userId;
    String content;
    String createdAt;
    String updatedAt;
    String userName;
    String userPicture;

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rcv_item_comment;
    }

    @Override
    public CommentViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new CommentViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, CommentViewHolder holder, int position, List<Object> payloads) {
        holder.tvNameUser.setText(userName);
        holder.tvDateCreated.setText(DateConvertUtils.convertDateTimeToDate(createdAt));
        holder.tvCommentContent.setText(content);
        Glide.with(holder.itemView.getContext()).load(userPicture).into(holder.imgComment);
    }


    public static class CommentViewHolder extends FlexibleViewHolder{

        ImageView imgComment;

        TextView tvNameUser, tvDateCreated, tvCommentContent;
        public CommentViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            imgComment = view.findViewById(R.id.img_comment);
            tvNameUser = view.findViewById(R.id.tv_name_user);
            tvDateCreated = view.findViewById(R.id.tv_date_created);
            tvCommentContent = view.findViewById(R.id.tv_comment_content);
        }
    }
}
