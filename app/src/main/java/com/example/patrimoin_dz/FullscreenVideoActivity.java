package com.example.patrimoin_dz;

import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class FullscreenVideoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_video);

        VideoView videoView = findViewById(R.id.fullscreen_video);
        String videoUrl = getIntent().getStringExtra("video_url");
        videoView.setVideoURI(android.net.Uri.parse(videoUrl));
        videoView.start();
    }
}