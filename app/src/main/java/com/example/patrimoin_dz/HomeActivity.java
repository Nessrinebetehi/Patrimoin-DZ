package com.example.patrimoin_dz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import me.relex.circleindicator.CircleIndicator3;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem lastSelectedItem;
    private ViewPager2 imageSlider;
    private CircleIndicator3 circleIndicator;
    private ViewPager2 clothingSlider;
    private CircleIndicator3 clothingCircleIndicator;
    private ViewPager2 cuisineSlider;
    private CircleIndicator3 cuisineCircleIndicator;
    private ViewPager2 architectureSlider;
    private CircleIndicator3 architectureCircleIndicator;
    private ViewPager2 musicSlider;
    private CircleIndicator3 musicCircleIndicator;
    private Handler handler;
    private Runnable runnable;
    private Handler clothingHandler;
    private Runnable clothingRunnable;
    private Handler cuisineHandler;
    private Runnable cuisineRunnable;
    private Handler architectureHandler;
    private Runnable architectureRunnable;
    private Handler musicHandler;
    private Runnable musicRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
                Toast.makeText(HomeActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                // Déjà sur HomeActivity, pas besoin de lancer une nouvelle activité
            } else if (id == R.id.nav_musique) {
                Toast.makeText(HomeActivity.this, "Algerian Music selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, MusicActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_vetements) {
                Toast.makeText(HomeActivity.this, "Algerian Clothing selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, ClothingActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_traditions) {
                Toast.makeText(HomeActivity.this, "Algerian Traditions selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, TraditionsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_cuisine) {
                Toast.makeText(HomeActivity.this, "Algerian Cuisine selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, CuisineActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_architecture) {
                Toast.makeText(HomeActivity.this, "Architecture and Historical Sites selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, ArchitectureActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_linguistique) {
                Toast.makeText(HomeActivity.this, "Linguistic and Literary Heritage selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, LinguisticActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_fetes) {
                Toast.makeText(HomeActivity.this, "Festivals and Celebrations selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, FestivalsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_patrAI) {
                Toast.makeText(HomeActivity.this, "PatrAI selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, PatrAIActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Sélectionner l'élément "Home" par défaut
        navigationView.setCheckedItem(R.id.nav_home);
        lastSelectedItem = navigationView.getMenu().findItem(R.id.nav_home);

        // Configurer le premier slider d'images
        imageSlider = findViewById(R.id.image_slider);
        circleIndicator = findViewById(R.id.circle_indicator);

        int[] images = new int[]{
                R.drawable.alg,
                R.drawable.hdid,
                R.drawable.musiq,
                R.drawable.harb,
                R.drawable.mansora,
                R.drawable.hayak,
                R.drawable.banyan,
                R.drawable.iksisoir
        };

        SliderAdapter sliderAdapter = new SliderAdapter(this, images);
        imageSlider.setAdapter(sliderAdapter);
        Log.d("HomeActivity", "First slider initialized with " + images.length + " images");

        circleIndicator.setViewPager(imageSlider);

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = imageSlider.getCurrentItem();
                int totalItems = imageSlider.getAdapter().getItemCount();
                imageSlider.setCurrentItem((currentItem + 1) % totalItems);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);

        // Configurer le deuxième slider d'images (pour les vêtements)
        clothingSlider = findViewById(R.id.clothing_slider);
        clothingCircleIndicator = findViewById(R.id.clothing_circle_indicator);

        int[] clothingImages = new int[]{
                R.drawable.f,
                R.drawable.g,
                R.drawable.y,
                R.drawable.d,
                R.drawable.n,
                R.drawable.h,
                R.drawable.c
        };

        SliderAdapter clothingSliderAdapter = new SliderAdapter(this, clothingImages);
        clothingSlider.setAdapter(clothingSliderAdapter);
        Log.d("HomeActivity", "Second slider initialized with " + clothingImages.length + " images");

        clothingCircleIndicator.setViewPager(clothingSlider);

        clothingHandler = new Handler(Looper.getMainLooper());
        clothingRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = clothingSlider.getCurrentItem();
                int totalItems = clothingSlider.getAdapter().getItemCount();
                clothingSlider.setCurrentItem((currentItem + 1) % totalItems);
                clothingHandler.postDelayed(this, 3000);
            }
        };
        clothingHandler.postDelayed(clothingRunnable, 3000);

        // Configurer le troisième slider d'images (pour la cuisine)
        cuisineSlider = findViewById(R.id.cuisine_slider);
        cuisineCircleIndicator = findViewById(R.id.cuisine_circle_indicator);

        int[] cuisineImages = new int[]{
                R.drawable.couscous,
                R.drawable.mtoume,
                R.drawable.hrira,
                R.drawable.rechta,
                R.drawable.gateaux,
                R.drawable.soupe,
                R.drawable.kraht
        };

        SliderAdapter cuisineSliderAdapter = new SliderAdapter(this, cuisineImages);
        cuisineSlider.setAdapter(cuisineSliderAdapter);
        Log.d("HomeActivity", "Third slider initialized with " + cuisineImages.length + " images");

        cuisineCircleIndicator.setViewPager(cuisineSlider);

        cuisineHandler = new Handler(Looper.getMainLooper());
        cuisineRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = cuisineSlider.getCurrentItem();
                int totalItems = cuisineSlider.getAdapter().getItemCount();
                cuisineSlider.setCurrentItem((currentItem + 1) % totalItems);
                cuisineHandler.postDelayed(this, 3000);
            }
        };
        cuisineHandler.postDelayed(cuisineRunnable, 3000);

        // Configurer le quatrième slider d'images (pour l'architecture)
        architectureSlider = findViewById(R.id.architecture_slider);
        architectureCircleIndicator = findViewById(R.id.architecture_circle_indicator);

        int[] architectureImages = new int[]{
                R.drawable.banyan,
                R.drawable.seti,
                R.drawable.maalabalich,
                R.drawable.babe,
                R.drawable.hotam,
                R.drawable.dar,
                R.drawable.zanka,
                R.drawable.sahra
        };

        SliderAdapter architectureSliderAdapter = new SliderAdapter(this, architectureImages);
        architectureSlider.setAdapter(architectureSliderAdapter);
        Log.d("HomeActivity", "Fourth slider initialized with " + architectureImages.length + " images");

        architectureCircleIndicator.setViewPager(architectureSlider);

        architectureHandler = new Handler(Looper.getMainLooper());
        architectureRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = architectureSlider.getCurrentItem();
                int totalItems = architectureSlider.getAdapter().getItemCount();
                architectureSlider.setCurrentItem((currentItem + 1) % totalItems);
                architectureHandler.postDelayed(this, 3000);
            }
        };
        architectureHandler.postDelayed(architectureRunnable, 3000);

        // Configurer le cinquième slider d'images (pour la musique)
        musicSlider = findViewById(R.id.music_slider);
        musicCircleIndicator = findViewById(R.id.music_circle_indicator);

        int[] musicImages = new int[]{
                R.drawable.tarabe,
                R.drawable.kabil,
                R.drawable.jmaa,
                R.drawable.khaled,
                R.drawable.ala,
                R.drawable.jsp,
                R.drawable.ide
        };

        SliderAdapter musicSliderAdapter = new SliderAdapter(this, musicImages);
        musicSlider.setAdapter(musicSliderAdapter);
        Log.d("HomeActivity", "Fifth slider initialized with " + musicImages.length + " images");

        musicCircleIndicator.setViewPager(musicSlider);

        musicHandler = new Handler(Looper.getMainLooper());
        musicRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = musicSlider.getCurrentItem();
                int totalItems = musicSlider.getAdapter().getItemCount();
                musicSlider.setCurrentItem((currentItem + 1) % totalItems);
                musicHandler.postDelayed(this, 3000);
            }
        };
        musicHandler.postDelayed(musicRunnable, 3000);

        // Ajouter un écouteur de clic sur le bouton Login
        ImageButton loginButton = findViewById(R.id.login_button);
        if (loginButton != null) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                loginButton.setOnClickListener(v -> {
                    Toast.makeText(HomeActivity.this, "You are already logged in!", Toast.LENGTH_SHORT).show();
                });
            } else {
                loginButton.setOnClickListener(v -> {
                    Log.d("HomeActivity", "Login button clicked!");
                    Toast.makeText(HomeActivity.this, "Login button clicked!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
            }
        } else {
            Log.e("HomeActivity", "Login button not found!");
        }

        // Ajouter une animation de zoom sur l'image de la carte
        ImageView algeriaImage = findViewById(R.id.algeria_image);
        if (algeriaImage != null) {
            algeriaImage.setOnClickListener(v -> {
                ScaleAnimation zoomAnimation = new ScaleAnimation(
                        1.0f, 1.5f,
                        1.0f, 1.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                zoomAnimation.setDuration(300);
                zoomAnimation.setFillAfter(false);

                ScaleAnimation shrinkAnimation = new ScaleAnimation(
                        1.5f, 1.0f,
                        1.5f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                shrinkAnimation.setDuration(300);
                shrinkAnimation.setStartOffset(300);
                shrinkAnimation.setFillAfter(false);

                algeriaImage.startAnimation(zoomAnimation);
                zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        algeriaImage.startAnimation(shrinkAnimation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(HomeActivity.this, ImageZoomActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            });
        } else {
            Log.e("HomeActivity", "Algeria image not found!");
        }

        // Ajouter une animation de zoom sur l'image du drapeau algérien
        ImageView algerianFlagImage = findViewById(R.id.algerian_flag_image);
        if (algerianFlagImage != null) {
            algerianFlagImage.setOnClickListener(v -> {
                ScaleAnimation zoomAnimation = new ScaleAnimation(
                        1.0f, 1.5f,
                        1.0f, 1.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                zoomAnimation.setDuration(300);
                zoomAnimation.setFillAfter(false);

                ScaleAnimation shrinkAnimation = new ScaleAnimation(
                        1.5f, 1.0f,
                        1.5f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                shrinkAnimation.setDuration(300);
                shrinkAnimation.setStartOffset(300);
                shrinkAnimation.setFillAfter(false);

                algerianFlagImage.startAnimation(zoomAnimation);
                zoomAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        algerianFlagImage.startAnimation(shrinkAnimation);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });

                shrinkAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(HomeActivity.this, ImageZoomActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            });
        } else {
            Log.e("HomeActivity", "Algerian flag image not found!");
        }

        // Ajouter des animations de survol aux cartes
        LinearLayout introCard = findViewById(R.id.intro_card);
        LinearLayout distanceCard = findViewById(R.id.distance_card);
        LinearLayout clothingCard = findViewById(R.id.clothing_card);
        LinearLayout cuisineCard = findViewById(R.id.cuisine_card);
        LinearLayout architectureCard = findViewById(R.id.architecture_card);
        LinearLayout musicCard = findViewById(R.id.music_card);
        LinearLayout flagElementsCard = findViewById(R.id.flag_elements_card);

        // Charger les animations
        Animation hoverAnimation = AnimationUtils.loadAnimation(this, R.anim.hover);
        Animation hoverExitAnimation = AnimationUtils.loadAnimation(this, R.anim.hover_exit);

        // Appliquer l'animation de survol à la première carte (Introduction)
        if (introCard != null) {
            introCard.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(hoverAnimation);
                        v.setElevation(8f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.startAnimation(hoverExitAnimation);
                        v.setElevation(4f);
                        break;
                }
                return false;
            });
        } else {
            Log.e("HomeActivity", "Intro card not found!");
        }

        // Appliquer l'animation de survol à la deuxième carte (Geographical Diversity)
        if (distanceCard != null) {
            distanceCard.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(hoverAnimation);
                        v.setElevation(8f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.startAnimation(hoverExitAnimation);
                        v.setElevation(4f);
                        break;
                }
                return false;
            });
        } else {
            Log.e("HomeActivity", "Distance card not found!");
        }

        // Appliquer l'animation de survol à la troisième carte (Traditional Clothing)
        if (clothingCard != null) {
            clothingCard.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(hoverAnimation);
                        v.setElevation(8f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.startAnimation(hoverExitAnimation);
                        v.setElevation(4f);
                        break;
                }
                return false;
            });
        } else {
            Log.e("HomeActivity", "Clothing card not found!");
        }

        // Appliquer l'animation de survol à la quatrième carte (Algerian Culinary Heritage)
        if (cuisineCard != null) {
            cuisineCard.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(hoverAnimation);
                        v.setElevation(8f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.startAnimation(hoverExitAnimation);
                        v.setElevation(4f);
                        break;
                }
                return false;
            });
        } else {
            Log.e("HomeActivity", "Cuisine card not found!");
        }

        // Appliquer l'animation de survol à la cinquième carte (Algerian Architectural Heritage)
        if (architectureCard != null) {
            architectureCard.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(hoverAnimation);
                        v.setElevation(8f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.startAnimation(hoverExitAnimation);
                        v.setElevation(4f);
                        break;
                }
                return false;
            });
        } else {
            Log.e("HomeActivity", "Architecture card not found!");
        }

        // Appliquer l'animation de survol à la sixième carte (Algerian Musical Heritage)
        if (musicCard != null) {
            musicCard.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(hoverAnimation);
                        v.setElevation(8f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.startAnimation(hoverExitAnimation);
                        v.setElevation(4f);
                        break;
                }
                return false;
            });
        } else {
            Log.e("HomeActivity", "Music card not found!");
        }

        // Appliquer l'animation de survol à la nouvelle carte (Elements of the Algerian Flag)
        if (flagElementsCard != null) {
            flagElementsCard.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.startAnimation(hoverAnimation);
                        v.setElevation(8f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.startAnimation(hoverExitAnimation);
                        v.setElevation(4f);
                        break;
                }
                return false;
            });
        } else {
            Log.e("HomeActivity", "Flag elements card not found!");
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Nettoyer le premier slider
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
        // Nettoyer le deuxième slider
        if (clothingHandler != null && clothingRunnable != null) {
            clothingHandler.removeCallbacks(clothingRunnable);
        }
        // Nettoyer le troisième slider
        if (cuisineHandler != null && cuisineRunnable != null) {
            cuisineHandler.removeCallbacks(cuisineRunnable);
        }
        // Nettoyer le quatrième slider
        if (architectureHandler != null && architectureRunnable != null) {
            architectureHandler.removeCallbacks(architectureRunnable);
        }
        // Nettoyer le cinquième slider
        if (musicHandler != null && musicRunnable != null) {
            musicHandler.removeCallbacks(musicRunnable);
        }
    }
}