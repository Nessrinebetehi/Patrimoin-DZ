package com.example.patrimoin_dz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class LinguisticActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linguistic);

        // Configuration de la Toolbar
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar);
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
                startActivity(new Intent(LinguisticActivity.this, HomeActivity.class));
            } else if (id == R.id.nav_musique) {
                startActivity(new Intent(LinguisticActivity.this, MusicActivity.class));
            } else if (id == R.id.nav_vetements) {
                startActivity(new Intent(LinguisticActivity.this, ClothingActivity.class));
            } else if (id == R.id.nav_traditions) {
                startActivity(new Intent(LinguisticActivity.this, TraditionsActivity.class));
            } else if (id == R.id.nav_cuisine) {
                startActivity(new Intent(LinguisticActivity.this, CuisineActivity.class));
            } else if (id == R.id.nav_architecture) {
                startActivity(new Intent(LinguisticActivity.this, ArchitectureActivity.class));
            } else if (id == R.id.nav_linguistique) {
                // Déjà sur LinguisticActivity, pas d'action
            } else if (id == R.id.nav_fetes) {
                startActivity(new Intent(LinguisticActivity.this, FestivalsActivity.class));
            } else if (id == R.id.nav_patrAI) {
                startActivity(new Intent(LinguisticActivity.this, PatrAIActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
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