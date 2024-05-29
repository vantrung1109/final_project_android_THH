package com.example.projectfinaltth.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.model.response.comment.Comment;

import java.util.List;

import kr.co.prnd.readmore.ReadMoreTextView;

public class CommentDocumentAdapter extends RecyclerView.Adapter<CommentDocumentAdapter.CommentDocumentViewHolder> {

    private Context context;
    private List<Comment> commentList;

    public CommentDocumentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentDocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rcv_item_comment, parent, false);
        return new CommentDocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentDocumentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.tv_name_user.setText("Name: " + comment.getUserName());
        holder.tv_comment_content.setText("Content: " + comment.getContent());

        // Load user picture using Glide
        Glide.with(context)
                .load(comment.getUserPicture())
                .into(holder.img_comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentDocumentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name_user;
        ReadMoreTextView tv_comment_content;
        ImageView img_comment;
        ImageView iv_user_picture; // Add ImageView for user picture

        public CommentDocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_user = itemView.findViewById(R.id.tv_name_user);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            img_comment = itemView.findViewById(R.id.img_comment);
        }
    }
}
