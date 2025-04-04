package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Charger les vues
        ImageView imageView = findViewById(R.id.imagePatrimoine);
        TextView titre = findViewById(R.id.titre);
        TextView description = findViewById(R.id.description);
        Button boutonContinuer = findViewById(R.id.boutonContinuer);

        // Charger l'animation
        Animation slideUpAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Appliquer l'animation aux éléments
        imageView.startAnimation(slideUpAnimation);
        titre.startAnimation(slideUpAnimation);
        description.startAnimation(slideUpAnimation);
        boutonContinuer.startAnimation(slideUpAnimation);

        // Ajouter un écouteur de clic sur le bouton Continuer
        boutonContinuer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        });
    }
}