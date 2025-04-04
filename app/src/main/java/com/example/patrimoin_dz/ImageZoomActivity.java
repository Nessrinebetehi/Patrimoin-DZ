package com.example.patrimoin_dz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ImageZoomActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_zoom);

        ZoomableImageView imageView = findViewById(R.id.zoom_image);
        imageView.setImageResource(R.drawable.map);  // Assure-toi que l'image est correcte
    }
}