package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<Story> storyList;

    public StoryAdapter(List<Story> storyList) {
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);
        holder.storyName.setText(story.getName());
        // Ici, tu peux charger une image avec une bibliothèque comme Glide ou Picasso si tu as des URLs
        // Pour l'instant, on utilise le placeholder
        holder.storyImage.setImageResource(R.drawable.ic_profile_placeholder);
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView storyImage;
        TextView storyName;

        StoryViewHolder(View itemView) {
            super(itemView);
            storyImage = itemView.findViewById(R.id.story_image);
            storyName = itemView.findViewById(R.id.story_name);
        }
    }
}