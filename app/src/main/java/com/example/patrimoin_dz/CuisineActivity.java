package com.example.patrimoin_dz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class CuisineActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem lastSelectedItem;
    private ImageView cuisineImage;
    private ViewPager2 cuisineSlider;
    private CircleIndicator3 cuisineCircleIndicator;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;
    private ViewPager2 gateauxSlider;
    private CircleIndicator3 gateauxCircleIndicator;
    private Handler gateauxSliderHandler = new Handler();
    private Runnable gateauxSliderRunnable;
    private VideoView couscousVideo;
    private Button playPauseButton;
    private boolean isPlayingCouscous = false;
    private VideoView chorbaFrikVideo;
    private Button chorbaFrikPlayPauseButton;
    private boolean isPlayingChorbaFrik = false;
    private VideoView mhajebVideo;
    private Button mhajebPlayPauseButton;
    private boolean isPlayingMhajeb = false;
    private VideoView bourakVideo;
    private Button bourakPlayPauseButton;
    private boolean isPlayingBourak = false;
    private VideoView zitouneVideo;
    private Button zitounePlayPauseButton;
    private boolean isPlayingZitoune = false;
    private VideoView rechtaVideo;
    private Button rechtaPlayPauseButton;
    private boolean isPlayingRechta = false;
    private VideoView tajineHlouVideo;
    private Button tajineHlouPlayPauseButton;
    private boolean isPlayingTajineHlou = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine);

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
                Toast.makeText(CuisineActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CuisineActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_musique) {
                Toast.makeText(CuisineActivity.this, "Algerian Music selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CuisineActivity.this, MusicActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_vetements) {
                Toast.makeText(CuisineActivity.this, "Algerian Clothing selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CuisineActivity.this, ClothingActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_traditions) {
                Toast.makeText(CuisineActivity.this, "Algerian Traditions selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CuisineActivity.this, TraditionsActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_cuisine) {
                Toast.makeText(CuisineActivity.this, "Algerian Cuisine selected", Toast.LENGTH_SHORT).show();
                // Déjà sur CuisineActivity, pas besoin de lancer une nouvelle activité
            } else if (id == R.id.nav_architecture) {
                Toast.makeText(CuisineActivity.this, "Architecture and Historical Sites selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CuisineActivity.this, ArchitectureActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_linguistique) {
                Toast.makeText(CuisineActivity.this, "Linguistic and Literary Heritage selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CuisineActivity.this, LinguisticActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_fetes) {
                Toast.makeText(CuisineActivity.this, "Festivals and Celebrations selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CuisineActivity.this, FestivalsActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_patrAI) {
                Toast.makeText(CuisineActivity.this, "PatrAI selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CuisineActivity.this, PatrAIActivity.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Sélectionner l'élément "Algerian Cuisine" par défaut
        navigationView.setCheckedItem(R.id.nav_cuisine);
        lastSelectedItem = navigationView.getMenu().findItem(R.id.nav_cuisine);

        // Ajouter un écouteur de clic sur le bouton Login
        ImageButton loginButton = findViewById(R.id.login_button);
        if (loginButton != null) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                loginButton.setOnClickListener(v -> {
                    Toast.makeText(CuisineActivity.this, "You are already logged in!", Toast.LENGTH_SHORT).show();
                });
            } else {
                loginButton.setOnClickListener(v -> {
                    Toast.makeText(CuisineActivity.this, "Login button clicked!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CuisineActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
            }
        }

        // Ajouter un écouteur de clic sur le bouton Exit
        ImageButton exitButton = findViewById(R.id.exit_button);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> AppUtils.exitApp(this));
        }

        // Initialiser l'image
        cuisineImage = findViewById(R.id.cuisine_image);

        // Appliquer l'animation "slide to left" à l'image automatiquement
        Animation slideToLeft = AnimationUtils.loadAnimation(this, R.anim.slide_to_left);
        cuisineImage.startAnimation(slideToLeft);

        // Configurer le slider principal
        cuisineSlider = findViewById(R.id.cuisine_slider);
        cuisineCircleIndicator = findViewById(R.id.cuisine_circle_indicator);

        // Définir les images et les légendes pour le slider principal
        int[] cuisineImages = new int[]{
                R.drawable.couscous,
                R.drawable.mhajab,
                R.drawable.chakchouka,
                R.drawable.ribiya,
                R.drawable.khlat,
                R.drawable.chrba,
                R.drawable.zitone,
                R.drawable.rechta,
                R.drawable.hlou
        };

        String[] cuisineCaptions = new String[]{
                "Couscous with Vegetables",
                "Shakshuka with Eggs",
                "Mechoui Roasted Lamb",
                "Baklava Dessert",
                "Makroud with Dates",
                "Chorba Frik Soup",
                "Tajine Zitoun",
                "Rechta Noodles",
                "Tajine Hlou"
        };

        // Créer l'adaptateur pour le slider principal
        SliderAdapter cuisineSliderAdapter = new SliderAdapter(this, cuisineImages, cuisineCaptions);
        cuisineSlider.setAdapter(cuisineSliderAdapter);
        cuisineCircleIndicator.setViewPager(cuisineSlider);

        // Configurer le défilement automatique pour le slider principal
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = cuisineSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % cuisineImages.length;
                cuisineSlider.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // Change toutes les 3 secondes
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);

        // Configurer le slider des gâteaux
        gateauxSlider = findViewById(R.id.gateaux_slider);
        gateauxCircleIndicator = findViewById(R.id.gateaux_circle_indicator);

        // Définir les images et les légendes pour le slider des gâteaux
        int[] gateauxImages = new int[]{
                R.drawable.makrout,
                R.drawable.kalbe,
                R.drawable.baklawa,
                R.drawable.griwach,
                R.drawable.tcharak,
                R.drawable.dyiriya,
                R.drawable.mchawak,
                R.drawable.samsa
        };

        String[] gateauxCaptions = new String[]{
                "Makroud with Dates",
                "Kalb El Louz with Orange Blossom",
                "Baklava with Nuts",
                "Griwech with Honey",
                "Tcharek with Almonds",
                "Dzeryet with Pistachios",
                "Mchewek with Almonds",
                "Samsa with Honey"
        };

        // Créer l'adaptateur pour le slider des gâteaux
        SliderAdapter gateauxSliderAdapter = new SliderAdapter(this, gateauxImages, gateauxCaptions);
        gateauxSlider.setAdapter(gateauxSliderAdapter);
        gateauxCircleIndicator.setViewPager(gateauxSlider);

        // Configurer le défilement automatique pour le slider des gâteaux
        gateauxSliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = gateauxSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % gateauxImages.length;
                gateauxSlider.setCurrentItem(nextItem, true);
                gateauxSliderHandler.postDelayed(this, 3000); // Change toutes les 3 secondes
            }
        };
        gateauxSliderHandler.postDelayed(gateauxSliderRunnable, 3000);

        // Initialiser la vidéo du Couscous
        couscousVideo = findViewById(R.id.couscous_video);
        playPauseButton = findViewById(R.id.play_pause_button);

        // Charger la vidéo du Couscous depuis res/raw
        String couscousVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.how_to_make_couscous;
        couscousVideo.setVideoURI(Uri.parse(couscousVideoPath));

        // Gérer le bouton Play/Pause pour le Couscous
        playPauseButton.setOnClickListener(v -> {
            if (isPlayingCouscous) {
                couscousVideo.pause();
                playPauseButton.setText("Play");
                isPlayingCouscous = false;
            } else {
                couscousVideo.start();
                playPauseButton.setText("Pause");
                isPlayingCouscous = true;
            }
        });

        // Ajouter un écouteur pour gérer la fin de la vidéo du Couscous
        couscousVideo.setOnCompletionListener(mp -> {
            playPauseButton.setText("Play");
            isPlayingCouscous = false;
        });

        // Initialiser la vidéo de Chorba Frik
        chorbaFrikVideo = findViewById(R.id.chorba_frik_video);
        chorbaFrikPlayPauseButton = findViewById(R.id.chorba_frik_play_pause_button);

        // Charger la vidéo de Chorba Frik depuis res/raw
        String chorbaFrikVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.how_to_make_chorba_frik;
        chorbaFrikVideo.setVideoURI(Uri.parse(chorbaFrikVideoPath));

        // Gérer le bouton Play/Pause pour Chorba Frik
        chorbaFrikPlayPauseButton.setOnClickListener(v -> {
            if (isPlayingChorbaFrik) {
                chorbaFrikVideo.pause();
                chorbaFrikPlayPauseButton.setText("Play");
                isPlayingChorbaFrik = false;
            } else {
                chorbaFrikVideo.start();
                chorbaFrikPlayPauseButton.setText("Pause");
                isPlayingChorbaFrik = true;
            }
        });

        // Ajouter un écouteur pour gérer la fin de la vidéo de Chorba Frik
        chorbaFrikVideo.setOnCompletionListener(mp -> {
            chorbaFrikPlayPauseButton.setText("Play");
            isPlayingChorbaFrik = false;
        });

        // Initialiser la vidéo de Mhajeb
        mhajebVideo = findViewById(R.id.mhajeb_video);
        mhajebPlayPauseButton = findViewById(R.id.mhajeb_play_pause_button);

        // Charger la vidéo de Mhajeb depuis res/raw
        String mhajebVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.how_to_make_mhajeb;
        mhajebVideo.setVideoURI(Uri.parse(mhajebVideoPath));

        // Gérer le bouton Play/Pause pour Mhajeb
        mhajebPlayPauseButton.setOnClickListener(v -> {
            if (isPlayingMhajeb) {
                mhajebVideo.pause();
                mhajebPlayPauseButton.setText("Play");
                isPlayingMhajeb = false;
            } else {
                mhajebVideo.start();
                mhajebPlayPauseButton.setText("Pause");
                isPlayingMhajeb = true;
            }
        });

        // Ajouter un écouteur pour gérer la fin de la vidéo de Mhajeb
        mhajebVideo.setOnCompletionListener(mp -> {
            mhajebPlayPauseButton.setText("Play");
            isPlayingMhajeb = false;
        });

        // Initialiser la vidéo de Bourak
        bourakVideo = findViewById(R.id.bourak_video);
        bourakPlayPauseButton = findViewById(R.id.bourak_play_pause_button);

        // Charger la vidéo de Bourak depuis res/raw
        String bourakVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.how_to_make_bourak;
        bourakVideo.setVideoURI(Uri.parse(bourakVideoPath));

        // Gérer le bouton Play/Pause pour Bourak
        bourakPlayPauseButton.setOnClickListener(v -> {
            if (isPlayingBourak) {
                bourakVideo.pause();
                bourakPlayPauseButton.setText("Play");
                isPlayingBourak = false;
            } else {
                bourakVideo.start();
                bourakPlayPauseButton.setText("Pause");
                isPlayingBourak = true;
            }
        });

        // Ajouter un écouteur pour gérer la fin de la vidéo de Bourak
        bourakVideo.setOnCompletionListener(mp -> {
            bourakPlayPauseButton.setText("Play");
            isPlayingBourak = false;
        });

        // Initialiser la vidéo de Zitoune
        zitouneVideo = findViewById(R.id.zitoune_video);
        zitounePlayPauseButton = findViewById(R.id.zitoune_play_pause_button);

        // Charger la vidéo de Zitoune depuis res/raw
        String zitouneVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.how_to_make_zitoune;
        zitouneVideo.setVideoURI(Uri.parse(zitouneVideoPath));

        // Gérer le bouton Play/Pause pour Zitoune
        zitounePlayPauseButton.setOnClickListener(v -> {
            if (isPlayingZitoune) {
                zitouneVideo.pause();
                zitounePlayPauseButton.setText("Play");
                isPlayingZitoune = false;
            } else {
                zitouneVideo.start();
                zitounePlayPauseButton.setText("Pause");
                isPlayingZitoune = true;
            }
        });

        // Ajouter un écouteur pour gérer la fin de la vidéo de Zitoune
        zitouneVideo.setOnCompletionListener(mp -> {
            zitounePlayPauseButton.setText("Play");
            isPlayingZitoune = false;
        });

        // Initialiser la vidéo de Rechta
        rechtaVideo = findViewById(R.id.rechta_video);
        rechtaPlayPauseButton = findViewById(R.id.rechta_play_pause_button);

        // Charger la vidéo de Rechta depuis res/raw
        String rechtaVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.how_to_make_rechta;
        rechtaVideo.setVideoURI(Uri.parse(rechtaVideoPath));

        // Gérer le bouton Play/Pause pour Rechta
        rechtaPlayPauseButton.setOnClickListener(v -> {
            if (isPlayingRechta) {
                rechtaVideo.pause();
                rechtaPlayPauseButton.setText("Play");
                isPlayingRechta = false;
            } else {
                rechtaVideo.start();
                rechtaPlayPauseButton.setText("Pause");
                isPlayingRechta = true;
            }
        });

        // Ajouter un écouteur pour gérer la fin de la vidéo de Rechta
        rechtaVideo.setOnCompletionListener(mp -> {
            rechtaPlayPauseButton.setText("Play");
            isPlayingRechta = false;
        });

        // Initialiser la vidéo de Tajine Hlou
        tajineHlouVideo = findViewById(R.id.tajine_hlou_video);
        tajineHlouPlayPauseButton = findViewById(R.id.tajine_hlou_play_pause_button);

        // Charger la vidéo de Tajine Hlou depuis res/raw
        String tajineHlouVideoPath = "android.resource://" + getPackageName() + "/" + R.raw.how_to_make_tajine_hlou;
        tajineHlouVideo.setVideoURI(Uri.parse(tajineHlouVideoPath));

        // Gérer le bouton Play/Pause pour Tajine Hlou
        tajineHlouPlayPauseButton.setOnClickListener(v -> {
            if (isPlayingTajineHlou) {
                tajineHlouVideo.pause();
                tajineHlouPlayPauseButton.setText("Play");
                isPlayingTajineHlou = false;
            } else {
                tajineHlouVideo.start();
                tajineHlouPlayPauseButton.setText("Pause");
                isPlayingTajineHlou = true;
            }
        });

        // Ajouter un écouteur pour gérer la fin de la vidéo de Tajine Hlou
        tajineHlouVideo.setOnCompletionListener(mp -> {
            tajineHlouPlayPauseButton.setText("Play");
            isPlayingTajineHlou = false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
        gateauxSliderHandler.removeCallbacks(gateauxSliderRunnable);
        // Mettre en pause la vidéo du Couscous si elle joue
        if (couscousVideo != null && couscousVideo.isPlaying()) {
            couscousVideo.pause();
            playPauseButton.setText("Play");
            isPlayingCouscous = false;
        }
        // Mettre en pause la vidéo de Chorba Frik si elle joue
        if (chorbaFrikVideo != null && chorbaFrikVideo.isPlaying()) {
            chorbaFrikVideo.pause();
            chorbaFrikPlayPauseButton.setText("Play");
            isPlayingChorbaFrik = false;
        }
        // Mettre en pause la vidéo de Mhajeb si elle joue
        if (mhajebVideo != null && mhajebVideo.isPlaying()) {
            mhajebVideo.pause();
            mhajebPlayPauseButton.setText("Play");
            isPlayingMhajeb = false;
        }
        // Mettre en pause la vidéo de Bourak si elle joue
        if (bourakVideo != null && bourakVideo.isPlaying()) {
            bourakVideo.pause();
            bourakPlayPauseButton.setText("Play");
            isPlayingBourak = false;
        }
        // Mettre en pause la vidéo de Zitoune si elle joue
        if (zitouneVideo != null && zitouneVideo.isPlaying()) {
            zitouneVideo.pause();
            zitounePlayPauseButton.setText("Play");
            isPlayingZitoune = false;
        }
        // Mettre en pause la vidéo de Rechta si elle joue
        if (rechtaVideo != null && rechtaVideo.isPlaying()) {
            rechtaVideo.pause();
            rechtaPlayPauseButton.setText("Play");
            isPlayingRechta = false;
        }
        // Mettre en pause la vidéo de Tajine Hlou si elle joue
        if (tajineHlouVideo != null && tajineHlouVideo.isPlaying()) {
            tajineHlouVideo.pause();
            tajineHlouPlayPauseButton.setText("Play");
            isPlayingTajineHlou = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
        gateauxSliderHandler.postDelayed(gateauxSliderRunnable, 3000);
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