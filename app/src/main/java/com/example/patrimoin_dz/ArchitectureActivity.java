package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import me.relex.circleindicator.CircleIndicator3;

public class ArchitectureActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_architecture);

        // Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configuration du DrawerLayout et NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Ajout du bouton de menu (hamburger) dans la Toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Gestion des clics sur les éléments du menu
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ArchitectureActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_musique) {
                startActivity(new Intent(ArchitectureActivity.this, MusicActivity.class));
            } else if (id == R.id.nav_vetements) {
                startActivity(new Intent(ArchitectureActivity.this, ClothingActivity.class));
            } else if (id == R.id.nav_traditions) {
                startActivity(new Intent(ArchitectureActivity.this, TraditionsActivity.class));
            } else if (id == R.id.nav_cuisine) {
                startActivity(new Intent(ArchitectureActivity.this, CuisineActivity.class));
            } else if (id == R.id.nav_architecture) {
                // Déjà sur ArchitectureActivity, pas d'action nécessaire
            } else if (id == R.id.nav_linguistique) {
                startActivity(new Intent(ArchitectureActivity.this, LinguisticActivity.class));
            } else if (id == R.id.nav_fetes) {
                startActivity(new Intent(ArchitectureActivity.this, FestivalsActivity.class));
            } else if (id == R.id.nav_patrAI) {
                startActivity(new Intent(ArchitectureActivity.this, PatrAIActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Configuration du slider
        viewPager = findViewById(R.id.architecture_slider);
        int[] images = {
                R.drawable.costonti,
                R.drawable.kasba,
                R.drawable.mansora,
                R.drawable.moski,
                R.drawable.tasili,
                R.drawable.kasor
        };
        String[] captions = {
                "Constantine Bridges",
                "Casbah of Algiers",
                "Mansourah Mosque",
                "Tlemcen Mosque",
                "Tassili n'Ajjer",
                "Ksar of Timgad"
        };

        SliderAdapter sliderAdapter = new SliderAdapter(this, images, captions);
        viewPager.setAdapter(sliderAdapter);

        // Liaison avec l'indicateur
        CircleIndicator3 indicator = findViewById(R.id.architecture_circle_indicator);
        if (indicator != null) {
            indicator.setViewPager(viewPager);
        }

        // Configuration du défilement automatique
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager.getCurrentItem();
                int totalItems = sliderAdapter.getItemCount();
                if (currentItem < totalItems - 1) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else {
                    viewPager.setCurrentItem(0); // Retour au début
                }
                sliderHandler.postDelayed(this, 3000); // Défile toutes les 3 secondes
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000); // Démarre après 3 secondes

        // Configuration des images cliquables et leurs paragraphes
        setupImageClickListener(R.id.image_casbah, R.id.text_casbah);
        setupImageClickListener(R.id.image_roman_ruins, R.id.text_roman_ruins);
        setupImageClickListener(R.id.image_ksour, R.id.text_ksour);
        setupImageClickListener(R.id.image_mosques, R.id.text_mosques);
        setupImageClickListener(R.id.image_tassili, R.id.text_tassili);
        setupImageClickListener(R.id.image_constantine_bridges, R.id.text_constantine_bridges);
        setupImageClickListener(R.id.image_mansourah, R.id.text_mansourah);
    }

    // Méthode pour gérer le clic sur une image et toggle le texte
    private void setupImageClickListener(int imageId, int textId) {
        ImageView imageView = findViewById(imageId);
        TextView textView = findViewById(textId);
        textView.setVisibility(View.GONE); // Paragraphe caché par défaut

        imageView.setOnClickListener(v -> {
            if (textView.getVisibility() == View.GONE) {
                textView.setVisibility(View.VISIBLE); // Affiche le texte au premier clic
            } else {
                textView.setVisibility(View.GONE); // Cache le texte au deuxième clic
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable); // Arrête le défilement en pause
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000); // Reprend le défilement
    }

    // Gestion du bouton retour pour fermer le drawer s'il est ouvert
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}