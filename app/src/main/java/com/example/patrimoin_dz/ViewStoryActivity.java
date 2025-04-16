package com.example.patrimoin_dz;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ViewStoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView storyImage;
    private VideoView storyVideo;
    private TextView storyText;
    private List<Story> storyList;
    private int currentStoryIndex = 0;
    private Handler handler;
    private Runnable nextStoryRunnable;
    private MediaPlayer musicPlayer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storyImage = findViewById(R.id.story_image);
        storyVideo = findViewById(R.id.story_video);
        storyText = findViewById(R.id.story_text);
        storyList = new ArrayList<>();
        handler = new Handler();

        loadStories();
    }

    private void loadStories() {
        String userId = mAuth.getCurrentUser().getUid();
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);

        db.collection("stories")
                .whereEqualTo("userId", userId)
                .whereGreaterThan("timestamp", twentyFourHoursAgo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    storyList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Story story = doc.toObject(Story.class);
                        storyList.add(story);
                    }
                    if (!storyList.isEmpty()) {
                        displayStory(currentStoryIndex);
                    } else {
                        Toast.makeText(this, "Aucune story disponible", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erreur lors du chargement des stories", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void displayStory(int index) {
        if (index >= storyList.size()) {
            finish();
            return;
        }

        Story story = storyList.get(index);
        storyImage.setVisibility(View.GONE);
        storyVideo.setVisibility(View.GONE);

        if (story.getMediaUrl().endsWith(".mp4")) {
            storyVideo.setVisibility(View.VISIBLE);
            storyVideo.setVideoURI(Uri.parse(story.getMediaUrl()));
            storyVideo.start();
        } else {
            storyImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(story.getMediaUrl()).into(storyImage);
        }

        storyText.setText(story.getText());

        if (story.getMusicUrl() != null) {
            playMusic(story.getMusicUrl());
        } else {
            stopMusic();
        }

        // Auto-advance after 5 seconds
        nextStoryRunnable = () -> {
            currentStoryIndex++;
            displayStory(currentStoryIndex);
        };
        handler.postDelayed(nextStoryRunnable, 5000);
    }

    private void playMusic(String musicUrl) {
        try {
            if (musicPlayer != null) {
                musicPlayer.stop();
                musicPlayer.release();
            }
            musicPlayer = new MediaPlayer();
            musicPlayer.setDataSource(musicUrl);
            musicPlayer.prepare();
            musicPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
            musicPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && nextStoryRunnable != null) {
            handler.removeCallbacks(nextStoryRunnable);
        }
        stopMusic();
    }
}