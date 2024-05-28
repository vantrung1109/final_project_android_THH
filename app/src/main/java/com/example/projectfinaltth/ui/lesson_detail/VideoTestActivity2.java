package com.example.projectfinaltth.ui.lesson_detail;

import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.projectfinaltth.databinding.ActivityTestVideo2Binding;
import com.example.projectfinaltth.databinding.ActivityTestVideoBinding;

public class VideoTestActivity2 extends AppCompatActivity implements ExoPlayer.Listener{
    private ExoPlayer player;
    private String videoUrl = "https://res.cloudinary.com/dinyrr5ad/video/upload/v1715535605/zbm2zewq4djkqkmdk8cb.mp4";

    ActivityTestVideo2Binding mActivityTestVideo2Binding;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityTestVideo2Binding = ActivityTestVideo2Binding.inflate(getLayoutInflater());
        setContentView(mActivityTestVideo2Binding.getRoot());

        // Tạo player và set video đã lấy ở link lên player
        player = new ExoPlayer.Builder(this).build();
        player.addListener(this);
        mActivityTestVideo2Binding.videoDocument.setPlayer(player);

//        DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(this);
//        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));

        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
        player.setMediaItem(mediaItem);
//        player.setMediaSource(mediaSource);
        player.prepare();
    }


}
