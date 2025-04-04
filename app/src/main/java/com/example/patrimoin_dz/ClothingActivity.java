package com.example.patrimoin_dz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import me.relex.circleindicator.CircleIndicator3;

public class ClothingActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem lastSelectedItem;
    private ViewPager2 clothingSlider;
    private CircleIndicator3 clothingCircleIndicator;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing);

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
                Toast.makeText(ClothingActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClothingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_musique) {
                Toast.makeText(ClothingActivity.this, "Algerian Music selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClothingActivity.this, MusicActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_vetements) {
                Toast.makeText(ClothingActivity.this, "Algerian Clothing selected", Toast.LENGTH_SHORT).show();
                // Déjà sur ClothingActivity, pas besoin de lancer une nouvelle activité
            } else if (id == R.id.nav_traditions) {
                Toast.makeText(ClothingActivity.this, "Algerian Traditions selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClothingActivity.this, TraditionsActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_cuisine) {
                Toast.makeText(ClothingActivity.this, "Algerian Cuisine selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClothingActivity.this, CuisineActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_architecture) {
                Toast.makeText(ClothingActivity.this, "Architecture and Historical Sites selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClothingActivity.this, ArchitectureActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_linguistique) {
                Toast.makeText(ClothingActivity.this, "Linguistic and Literary Heritage selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClothingActivity.this, LinguisticActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_fetes) {
                Toast.makeText(ClothingActivity.this, "Festivals and Celebrations selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClothingActivity.this, FestivalsActivity.class);
                startActivity(intent);
                finish();
            } else if (id == R.id.nav_patrAI) {
                Toast.makeText(ClothingActivity.this, "PatrAI selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ClothingActivity.this, PatrAIActivity.class);
                startActivity(intent);
                finish();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Sélectionner l'élément "Algerian Clothing" par défaut
        navigationView.setCheckedItem(R.id.nav_vetements);
        lastSelectedItem = navigationView.getMenu().findItem(R.id.nav_vetements);

        // Ajouter un écouteur de clic sur le bouton Login
        ImageButton loginButton = findViewById(R.id.login_button);
        if (loginButton != null) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                loginButton.setOnClickListener(v -> {
                    Toast.makeText(ClothingActivity.this, "You are already logged in!", Toast.LENGTH_SHORT).show();
                });
            } else {
                loginButton.setOnClickListener(v -> {
                    Toast.makeText(ClothingActivity.this, "Login button clicked!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ClothingActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
            }
        }

        // Ajouter un écouteur de clic sur le bouton Exit
        ImageButton exitButton = findViewById(R.id.exit_button);
        if (exitButton != null) {
            exitButton.setOnClickListener(v -> AppUtils.exitApp(this));
        }

        // Configurer le slider
        clothingSlider = findViewById(R.id.clothing_slider);
        clothingCircleIndicator = findViewById(R.id.clothing_circle_indicator);

        // Définir les images et les légendes en anglais
        int[] images = new int[]{
                R.drawable.karako,
                R.drawable.chada,
                R.drawable.hayak,
                R.drawable.blouza,
                R.drawable.brnouse,
                R.drawable.ksantinya,
                R.drawable.coustim,
                R.drawable.kbayle
        };

        String[] captions = new String[]{
                "Karakou - Elegant velvet jacket",
                "Blouza - Vibrant Oran dress",
                "Burnous - Traditional men's cloak",
                "Kamja - Colorful Kabyle dress",
                "Hayek - Modest white veil",
                "Ksantiniya - Constantine's embroidered dress",
                "Chedda - Tlemcen's bridal attire",
                "Chaouia - Aurès region's patterned dress"
        };

        SliderAdapter sliderAdapter = new SliderAdapter(this, images);
        clothingSlider.setAdapter(sliderAdapter);
        clothingCircleIndicator.setViewPager(clothingSlider);

        // Configurer le défilement automatique
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = clothingSlider.getCurrentItem();
                int nextItem = (currentItem + 1) % images.length;
                clothingSlider.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000); // Change toutes les 3 secondes
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
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