package com.example.patrimoin_dz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import me.relex.circleindicator.CircleIndicator3;

public class TraditionsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem lastSelectedItem;
    private ViewPager2 traditionsSlider;
    private CircleIndicator3 traditionsCircleIndicator;
    private ViewPager2 religiousFestivalsSlider;
    private CircleIndicator3 religiousFestivalsCircleIndicator;
    private ViewPager2 yennayerSlider;
    private CircleIndicator3 yennayerCircleIndicator;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    private Runnable religiousSliderRunnable;
    private Runnable yennayerSliderRunnable;
    private LinearLayout weddingTraditionsCard;
    private LinearLayout weddingVideoCard;
    private LinearLayout religiousFestivalsCard;
    private LinearLayout eidFitrCard;
    private LinearLayout eidAdhaCard;
    private LinearLayout mawlidCard;
    private LinearLayout ramadanCard;
    private LinearLayout fridayCouscousCard;
    private LinearLayout yennayerCard;
    private TextView religiousFestivalsTitle;
    private TextView fridayCouscousTitle;
    private TextView yennayerTitle;
    private VideoView weddingVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traditions);

        // Configurer la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurer le DrawerLayout et le NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Ajouter le bouton hamburger pour ouvrir la sidebar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Gérer les clics sur les éléments du menu
        navigationView.setNavigationItemSelectedListener(item -> {
            if (lastSelectedItem != null) {
                lastSelectedItem.setChecked(false);
            }
            item.setChecked(true);
            lastSelectedItem = item;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(TraditionsActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TraditionsActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_musique) {
                Toast.makeText(TraditionsActivity.this, "Algerian Music selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TraditionsActivity.this, MusicActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_vetements) {
                Toast.makeText(TraditionsActivity.this, "Algerian Clothing selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TraditionsActivity.this, ClothingActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_traditions) {
                Toast.makeText(TraditionsActivity.this, "Algerian Traditions selected", Toast.LENGTH_SHORT).show();
                // Déjà sur TraditionsActivity, pas besoin de lancer une nouvelle activité
            } else if (id == R.id.nav_cuisine) {
                Toast.makeText(TraditionsActivity.this, "Algerian Cuisine selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TraditionsActivity.this, CuisineActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_architecture) {
                Toast.makeText(TraditionsActivity.this, "Architecture and Historical Sites selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TraditionsActivity.this, ArchitectureActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_linguistique) {
                Toast.makeText(TraditionsActivity.this, "Linguistic and Literary Heritage selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TraditionsActivity.this, LinguisticActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_fetes) {
                Toast.makeText(TraditionsActivity.this, "Festivals and Celebrations selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TraditionsActivity.this, FestivalsActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_patrAI) {
                Toast.makeText(TraditionsActivity.this, "PatrAI selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TraditionsActivity.this, PatrAIActivity.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Sélectionner l'élément "Algerian Traditions" par défaut
        navigationView.setCheckedItem(R.id.nav_traditions);
        lastSelectedItem = navigationView.getMenu().findItem(R.id.nav_traditions);

        // Ajouter un écouteur de clic sur le bouton Login
        ImageButton loginButton = findViewById(R.id.login_button);
        if (loginButton != null) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                loginButton.setOnClickListener(v -> {
                    Toast.makeText(TraditionsActivity.this, "You are already logged in!", Toast.LENGTH_SHORT).show();
                });
            } else {
                loginButton.setOnClickListener(v -> {
                    Toast.makeText(TraditionsActivity.this, "Login button clicked!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(TraditionsActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
            }
        }

        // Ajouter un écouteur de clic sur le bouton Exit
        ImageButton exitButton = findViewById(R.id.exit_button);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> AppUtils.exitApp(this));
        }

        // Configurer le slider des mariages
        traditionsSlider = findViewById(R.id.traditions_slider);
        traditionsCircleIndicator = findViewById(R.id.traditions_circle_indicator);

        // Définir les images et les légendes pour le slider des mariages
        int[] weddingImages = new int[]{
                R.drawable.mariage,
                R.drawable.mariagee,
                R.drawable.mer,
                R.drawable.meri
        };

        String[] weddingCaptions = new String[]{
                "Traditional Henna Ceremony",
                "Bride in Chedda Dress",
                "Groom's Burnous Procession",
                "Wedding Feast Celebration"
        };

        // Créer l'adaptateur pour le slider des mariages
        SliderAdapter weddingSliderAdapter = new SliderAdapter(this, weddingImages, weddingCaptions);
        traditionsSlider.setAdapter(weddingSliderAdapter);
        traditionsCircleIndicator.setViewPager(traditionsSlider);

        // Configurer le défilement automatique pour le slider des mariages
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = traditionsSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % weddingImages.length;
                traditionsSlider.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // Change toutes les 3 secondes
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);

        // Configurer le slider des fêtes religieuses
        religiousFestivalsSlider = findViewById(R.id.religious_festivals_slider);
        religiousFestivalsCircleIndicator = findViewById(R.id.religious_festivals_circle_indicator);

        // Définir les images et les légendes pour le slider des fêtes religieuses
        int[] festivalImages = new int[]{
                R.drawable.qide,
                R.drawable.kbir,
                R.drawable.nayer,
                R.drawable.moulad,
                R.drawable.waada,
                R.drawable.nayer
        };

        String[] festivalCaptions = new String[]{
                "Eid al-Fitr Celebration",
                "Eid al-Adha Sacrifice",
                "Mawlid Procession",
                "Ramadan Iftar Gathering",
                "Ashura Commemoration",
                "Laylat al-Qadr Prayer"
        };

        // Créer l'adaptateur pour le slider des fêtes religieuses
        SliderAdapter festivalSliderAdapter = new SliderAdapter(this, festivalImages, festivalCaptions);
        religiousFestivalsSlider.setAdapter(festivalSliderAdapter);
        religiousFestivalsCircleIndicator.setViewPager(religiousFestivalsSlider);

        // Configurer le défilement automatique pour le slider des fêtes religieuses
        religiousSliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = religiousFestivalsSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % festivalImages.length;
                religiousFestivalsSlider.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // Change toutes les 3 secondes
            }
        };
        sliderHandler.postDelayed(religiousSliderRunnable, 3000);

        // Configurer le slider de Yennayer
        yennayerSlider = findViewById(R.id.yennayer_slider);
        yennayerCircleIndicator = findViewById(R.id.yennayer_circle_indicator);

        // Définir les images et les légendes pour le slider de Yennayer
        int[] yennayerImages = new int[]{
                R.drawable.nayer,  // Remplace par une vraie image
                R.drawable.nn,  // Remplace par une vraie image
                R.drawable.nnnn,  // Remplace par une vraie image
                R.drawable.nnnnnnn,  // Remplace par une vraie image
                R.drawable.nnnnn  // Remplace par une vraie image
        };

        String[] yennayerCaptions = new String[]{
                "Yennayer Festive Meal",
                "Traditional Amazigh Clothing",
                "Bonfire Celebration",
                "Amazigh Songs and Dance",
                "Family Gathering for Yennayer"
        };

        // Créer l'adaptateur pour le slider de Yennayer
        SliderAdapter yennayerSliderAdapter = new SliderAdapter(this, yennayerImages, yennayerCaptions);
        yennayerSlider.setAdapter(yennayerSliderAdapter);
        yennayerCircleIndicator.setViewPager(yennayerSlider);

        // Configurer le défilement automatique pour le slider de Yennayer
        yennayerSliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = yennayerSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % yennayerImages.length;
                yennayerSlider.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // Change toutes les 3 secondes
            }
        };
        sliderHandler.postDelayed(yennayerSliderRunnable, 3000);

        // Initialiser les cartes et les titres
        weddingTraditionsCard = findViewById(R.id.wedding_traditions_card);
        weddingVideoCard = findViewById(R.id.wedding_video_card);
        religiousFestivalsCard = findViewById(R.id.religious_festivals_card);
        eidFitrCard = findViewById(R.id.eid_fitr_card);
        eidAdhaCard = findViewById(R.id.eid_adha_card);
        mawlidCard = findViewById(R.id.mawlid_card);
        ramadanCard = findViewById(R.id.ramadan_card);
        fridayCouscousCard = findViewById(R.id.friday_couscous_card);
        yennayerCard = findViewById(R.id.yennayer_card);
        religiousFestivalsTitle = findViewById(R.id.religious_festivals_title);
        fridayCouscousTitle = findViewById(R.id.friday_couscous_title);
        yennayerTitle = findViewById(R.id.yennayer_title);

        // Configurer la vidéo
        weddingVideo = findViewById(R.id.wedding_video);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wedding_video); // Remplace par ta vraie vidéo
        weddingVideo.setVideoURI(videoUri);

        // Ajouter un MediaController pour contrôler la lecture
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(weddingVideo);
        weddingVideo.setMediaController(mediaController);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
        sliderHandler.removeCallbacks(religiousSliderRunnable);
        sliderHandler.removeCallbacks(yennayerSliderRunnable);
        if (weddingVideo != null && weddingVideo.isPlaying()) {
            weddingVideo.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
        sliderHandler.postDelayed(religiousSliderRunnable, 3000);
        sliderHandler.postDelayed(yennayerSliderRunnable, 3000);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}