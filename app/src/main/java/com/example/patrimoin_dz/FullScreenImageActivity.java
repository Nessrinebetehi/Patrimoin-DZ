package com.example.patrimoin_dz;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Récupérer l'ImageView et le bouton de fermeture
        ImageView fullScreenImage = findViewById(R.id.full_screen_image);
        ImageButton closeButton = findViewById(R.id.close_button);

        // Récupérer l'ID de l'image depuis l'intent
        int imageResId = getIntent().getIntExtra("imageResId", -1);
        if (imageResId != -1) {
            try {
                fullScreenImage.setImageResource(imageResId);
                Log.d("FullScreenImageActivity", "Image affichée avec imageResId: " + imageResId);
            } catch (Exception e) {
                Log.e("FullScreenImageActivity", "Erreur lors du chargement de l'image: " + e.getMessage());
                fullScreenImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            Log.e("FullScreenImageActivity", "Aucun imageResId fourni dans l'Intent");
            finish(); // Fermer l'activité si aucune image n'est fournie
        }

        // Fermer l'activité lorsque l'utilisateur clique sur le bouton de fermeture
        closeButton.setOnClickListener(v -> finish());

        // Fermer l'activité si l'utilisateur clique sur l'image
        fullScreenImage.setOnClickListener(v -> finish());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_out, R.anim.zoom_out);
    }
}