package com.example.patrimoin_dz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MusicActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem lastSelectedItem;
    private ViewPager2 imageSlider;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout et NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Navigation menu
        navigationView.setNavigationItemSelectedListener(item -> {
            if (lastSelectedItem != null) lastSelectedItem.setChecked(false);
            item.setChecked(true);
            lastSelectedItem = item;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else if (id == R.id.nav_musique) {
                Toast.makeText(this, "Algerian Music selected", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_vetements) {
                startActivity(new Intent(this, ClothingActivity.class));
                finish();
            } else if (id == R.id.nav_traditions) {
                startActivity(new Intent(this, TraditionsActivity.class));
                finish();
            } else if (id == R.id.nav_cuisine) {
                startActivity(new Intent(this, CuisineActivity.class));
                finish();
            } else if (id == R.id.nav_architecture) {
                startActivity(new Intent(this, ArchitectureActivity.class));
                finish();
            } else if (id == R.id.nav_linguistique) {
                startActivity(new Intent(this, LinguisticActivity.class));
                finish();
            } else if (id == R.id.nav_fetes) {
                startActivity(new Intent(this, FestivalsActivity.class));
                finish();
            } else if (id == R.id.nav_patrAI) {
                startActivity(new Intent(this, PatrAIActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        navigationView.setCheckedItem(R.id.nav_musique);
        lastSelectedItem = navigationView.getMenu().findItem(R.id.nav_musique);

        // Bouton Login
        ImageButton loginButton = findViewById(R.id.login_button);
        if (loginButton != null) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
            loginButton.setOnClickListener(v -> {
                if (isLoggedIn) {
                    Toast.makeText(this, "You are already logged in!", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, LoginActivity.class));
                }
            });
        } else {
            Log.e("MusicActivity", "loginButton is null");
        }

        // Bouton Exit
        ImageButton exitButton = findViewById(R.id.exit_button);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> finish()); // Remplace AppUtils.exitApp par finish()
        } else {
            Log.e("MusicActivity", "exitButton is null");
        }

        // Slider avec indicateurs
        imageSlider = findViewById(R.id.image_slider);
        if (imageSlider != null) {
            int[] images = {  R.drawable.tarabe,R.drawable.khaled, R.drawable.musk, R.drawable.malouf,
                    R.drawable.alef, R.drawable.ala, R.drawable.kabil, R.drawable.jsp };
            SliderAdapter adapter = new SliderAdapter(this, images);
            imageSlider.setAdapter(adapter);

            TabLayout sliderDots = findViewById(R.id.slider_dots);
            if (sliderDots != null) {
                new TabLayoutMediator(sliderDots, imageSlider, (tab, position) -> {}).attach();
            }

            sliderRunnable = new Runnable() {
                @Override
                public void run() {
                    int currentItem = imageSlider.getCurrentItem();
                    int nextItem = (currentItem + 1) % adapter.getItemCount();
                    imageSlider.setCurrentItem(nextItem);
                    sliderHandler.postDelayed(this, 3000);
                }
            };
            sliderHandler.postDelayed(sliderRunnable, 3000);
        } else {
            Log.e("MusicActivity", "imageSlider is null");
        }

        // Sections collapsables pour musique
        setupCollapsibleSection(R.id.card_rai, R.id.image_rai, R.id.text_rai);
        setupCollapsibleSection(R.id.card_chaabi, R.id.image_chaabi, R.id.text_chaabi);
        setupCollapsibleSection(R.id.card_malouf, R.id.image_malouf, R.id.text_malouf);
        setupCollapsibleSection(R.id.card_kabyle, R.id.image_kabyle, R.id.text_kabyle);
        setupCollapsibleSection(R.id.card_ahellil, R.id.image_ahellil, R.id.text_ahellil);

        // Vidéos avec contrôles avancés (utiliser une vidéo locale pour éviter les crashs)
        setupDanceVideo(R.id.video_kabyle_dance, "android.resource://" + getPackageName() + "/" + R.raw.video_kabyle_dance,
                R.id.play_kabyle_button, R.id.pause_kabyle_button, R.id.stop_kabyle_button, R.id.fullscreen_kabyle_button);
        setupDanceVideo(R.id.video_sahraoui_dance, "android.resource://" + getPackageName() + "/" + R.raw.video_sahraoui_dance,
                R.id.play_sahraoui_button, R.id.pause_sahraoui_button, R.id.stop_sahraoui_button, R.id.fullscreen_sahraoui_button);
        setupDanceVideo(R.id.video_ouled_nail_dance, "android.resource://" + getPackageName() + "/" + R.raw.video_ouled_nail_dance,
                R.id.play_ouled_nail_button, R.id.pause_ouled_nail_button, R.id.stop_ouled_nail_button, R.id.fullscreen_ouled_nail_button);
    }

    private void setupCollapsibleSection(int cardId, int imageId, int textId) {
        CardView card = findViewById(cardId);
        TextView text = findViewById(textId);
        if (card != null && text != null) {
            card.setOnClickListener(v -> toggleTextVisibility(text));
        } else {
            Log.e("MusicActivity", "Collapsible section failed: cardId=" + cardId + ", textId=" + textId);
        }
    }

    private void toggleTextVisibility(TextView textView) {
        textView.setVisibility(textView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
    }

    private void setupDanceVideo(int videoId, String url, int playId, int pauseId, int stopId, int fullscreenId) {
        VideoView videoView = findViewById(videoId);
        if (videoView != null) {
            try {
                videoView.setVideoURI(Uri.parse(url));
                videoView.setOnPreparedListener(mp -> mp.setLooping(true));
            } catch (Exception e) {
                Log.e("MusicActivity", "Error setting video URI: " + e.getMessage());
            }

            ImageButton playButton = findViewById(playId);
            ImageButton pauseButton = findViewById(pauseId);
            ImageButton stopButton = findViewById(stopId);
            ImageButton fullscreenButton = findViewById(fullscreenId);

            if (playButton != null) playButton.setOnClickListener(v -> videoView.start());
            if (pauseButton != null) pauseButton.setOnClickListener(v -> videoView.pause());
            if (stopButton != null) stopButton.setOnClickListener(v -> {
                videoView.pause();
                videoView.seekTo(0);
            });
            if (fullscreenButton != null) fullscreenButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, FullscreenVideoActivity.class);
                intent.putExtra("video_url", url);
                startActivity(intent);
            });
        } else {
            Log.e("MusicActivity", "VideoView is null for ID: " + videoId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
        pauseAllVideos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sliderHandler != null && sliderRunnable != null) {
            sliderHandler.removeCallbacks(sliderRunnable);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void pauseAllVideos() {
        int[] videoIds = { R.id.video_kabyle_dance, R.id.video_sahraoui_dance, R.id.video_ouled_nail_dance };
        for (int id : videoIds) {
            VideoView video = findViewById(id);
            if (video != null && video.isPlaying()) {
                video.pause();
            }
        }
    }
}