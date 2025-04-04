package com.example.patrimoin_dz;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

    private int[] images;
    private String[] captions;
    private Context context;

    // Constructeur avec images et légendes (utilisé par exemple dans ArchitectureActivity)
    public SliderAdapter(Context context, int[] images, String[] captions) {
        this.context = context;
        this.images = images != null ? images : new int[0]; // Évite NullPointerException si images est null
        this.captions = captions != null ? captions : new String[0]; // Évite NullPointerException si captions est null
        // Ajuste la taille de captions si elle ne correspond pas à images
        if (this.captions.length != this.images.length) {
            this.captions = new String[this.images.length];
            for (int i = 0; i < this.images.length; i++) {
                this.captions[i] = ""; // Légende vide par défaut
            }
        }
    }

    // Constructeur sans légendes (pour pages où seules les images sont nécessaires)
    public SliderAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images != null ? images : new int[0]; // Évite NullPointerException
        this.captions = new String[this.images.length];
        for (int i = 0; i < this.images.length; i++) {
            this.captions[i] = ""; // Légende vide par défaut
        }
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate le layout pour chaque élément du slider
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        try {
            // Charge l'image et la légende si disponibles
            if (position < images.length) {
                holder.imageView.setImageResource(images[position]);
            } else {
                holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery); // Image par défaut
            }
            holder.captionText.setText(position < captions.length ? captions[position] : "");

            // Ajoute un OnClickListener pour ouvrir l'image en plein écran
            holder.imageView.setOnClickListener(v -> {
                Log.d("SliderAdapter", "Image cliquée à la position: " + position);
                try {
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.putExtra("imageResId", images[position]);
                    context.startActivity(intent);
                    Log.d("SliderAdapter", "Intent lancé pour FullScreenImageActivity avec imageResId: " + images[position]);
                    // Applique une animation de zoom si le contexte est une activité compatible
                    if (context instanceof AppCompatActivity) {
                        ((AppCompatActivity) context).overridePendingTransition(R.anim.zoom_in, R.anim.zoom_in);
                    }
                } catch (Exception e) {
                    Log.e("SliderAdapter", "Erreur lors du lancement de FullScreenImageActivity: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("SliderAdapter", "Erreur lors du chargement de l'image à la position " + position + ": " + e.getMessage());
            holder.imageView.setImageResource(android.R.drawable.ic_menu_gallery); // Image par défaut en cas d'erreur
            holder.captionText.setText("Image unavailable");
        }
    }

    @Override
    public int getItemCount() {
        return images.length; // Retourne la taille du tableau d'images
    }

    // Classe interne pour gérer les vues de chaque élément du slider
    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView captionText;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_image);
            captionText = itemView.findViewById(R.id.slider_caption);
            // Vérifie que les vues existent pour éviter NullPointerException
            if (imageView == null || captionText == null) {
                Log.e("SliderAdapter", "Erreur: slider_image ou slider_caption non trouvé dans item_slider.xml");
            }
        }
    }
}