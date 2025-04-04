package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class FestivalsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_festivals);

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

        // Gestion des clics sur les éléments du menu pour naviguer vers toutes les pages
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(FestivalsActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_musique) {
                startActivity(new Intent(FestivalsActivity.this, MusicActivity.class));
            } else if (id == R.id.nav_vetements) {
                startActivity(new Intent(FestivalsActivity.this, ClothingActivity.class));
            } else if (id == R.id.nav_traditions) {
                startActivity(new Intent(FestivalsActivity.this, TraditionsActivity.class));
            } else if (id == R.id.nav_cuisine) {
                startActivity(new Intent(FestivalsActivity.this, CuisineActivity.class));
            } else if (id == R.id.nav_architecture) {
                startActivity(new Intent(FestivalsActivity.this, ArchitectureActivity.class));
            } else if (id == R.id.nav_linguistique) {
                startActivity(new Intent(FestivalsActivity.this, LinguisticActivity.class));
            } else if (id == R.id.nav_fetes) {
                // Déjà sur FestivalsActivity (Statistique), pas d'action
            } else if (id == R.id.nav_patrAI) {
                startActivity(new Intent(FestivalsActivity.this, PatrAIActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Chargement et application de l'animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Appliquer l'animation aux éléments
        ImageView titleImage = findViewById(R.id.lwla);
        ImageView chartArrivals = findViewById(R.id.chart_international_arrivals);
        ImageView chartOrigins = findViewById(R.id.chart_countries_origin);
        ImageView chartDomestic = findViewById(R.id.chart_domestic_tourism);

        titleImage.startAnimation(fadeIn);
        chartArrivals.startAnimation(fadeIn);
        chartOrigins.startAnimation(fadeIn);
        chartDomestic.startAnimation(fadeIn);
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