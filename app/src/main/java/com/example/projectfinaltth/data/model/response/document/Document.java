package com.example.projectfinaltth.data.model.response.document;

import android.net.Uri;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

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


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document extends AbstractFlexibleItem<Document.DocumentViewHolder>  {
    String _id;
    String lessonId;
    String cloudinary;
    String title;
    String description;
    String content;
    String createdAt;
    String updatedAt;
    Integer __v;

    private ExoPlayer player;

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.rcv_item_document;
    }

    @Override
    public DocumentViewHolder createViewHolder(View view, FlexibleAdapter<IFlexible> adapter) {
        return new DocumentViewHolder(view, adapter);
    }

    @Override
    public void bindViewHolder(FlexibleAdapter<IFlexible> adapter, DocumentViewHolder holder, int position, List<Object> payloads) {

        // Tạo player và set video đã lấy ở api lên player
        player = new ExoPlayer.Builder(holder.itemView.getContext()).build();
        player.addListener(holder);
        holder.playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(content));
        player.setMediaItem(mediaItem);
        player.prepare();

        holder.tvTitle.setText(title);
        holder.tvDescription.setText(description);
        holder.tvDate.setText(DateConvertUtils.convertDateTimeToDate(createdAt));
    }

    public static class DocumentViewHolder extends FlexibleViewHolder implements Player.Listener{
        PlayerView playerView;
        TextView tvTitle, tvDescription, tvDate;
        public DocumentViewHolder(View view, FlexibleAdapter adapter) {
            super(view, adapter);
            playerView = view.findViewById(R.id.video_document);
            tvTitle = view.findViewById(R.id.tv_document_title);
            tvDescription = view.findViewById(R.id.tv_document_description);
            tvDate = view.findViewById(R.id.tv_date_created);
        }
    }

}
