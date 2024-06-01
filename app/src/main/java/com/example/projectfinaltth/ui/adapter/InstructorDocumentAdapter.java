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

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectfinaltth.R;
import com.example.projectfinaltth.data.model.request.document.DocumentRequest;
import com.example.projectfinaltth.data.model.response.document.Document;
import com.example.projectfinaltth.data.model.response.document.DocumentResponse;
import com.example.projectfinaltth.data.model.response.lesson.LessonItem;
import com.example.projectfinaltth.ui.instructor.UpdateLessonActivity;

import java.util.List;

public class InstructorDocumentAdapter extends RecyclerView.Adapter<InstructorDocumentAdapter.InstructorDocumentViewHolder> {

    private Context context;
    private List<Document> documentList;
    private OnItemInteractionListener onItemInteractionListener;
    // Đặng Xuân Hùng - 21110194

    public InstructorDocumentAdapter(Context context, List<Document> documentList, OnItemInteractionListener onItemInteractionListener) {
        this.context = context;
        this.documentList = documentList;
        this.onItemInteractionListener = onItemInteractionListener;
    }

    @NonNull
    @Override
    public InstructorDocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new InstructorDocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructorDocumentViewHolder holder, int position) {
        Document document = documentList.get(position);
        holder.titleTextView.setText("Title: " + document.getTitle());
        holder.descriptionTextView.setText("Description: " + document.getDescription());
        ExoPlayer player = new ExoPlayer.Builder(holder.itemView.getContext()).build();
        holder.videoDocument.setPlayer(player);

        // Set media item to play
        MediaItem mediaItem = MediaItem.fromUri(document.getContent());
        player.setMediaItem(mediaItem);
        player.prepare();
        holder.deleteButton.setOnClickListener(v -> onItemInteractionListener.onDeleteLesson(holder.getAdapterPosition(), document));
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public static class InstructorDocumentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageButton deleteButton;
        PlayerView videoDocument;

        public InstructorDocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            videoDocument = itemView.findViewById(R.id.videoDocument);
        }
    }

    public interface OnItemInteractionListener {
        void onDeleteLesson(int position,Document document);
    }
}
