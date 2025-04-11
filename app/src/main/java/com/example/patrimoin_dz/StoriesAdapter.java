package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoryViewHolder> {

    private List<ChatStory> storiesList;

    public StoriesAdapter(List<ChatStory> storiesList) {
        this.storiesList = storiesList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        ChatStory story = storiesList.get(position);
        holder.profileImage.setImageResource(story.getProfileImage());
        holder.userName.setText(story.getUserName());
    }

    @Override
    public int getItemCount() {
        return storiesList.size();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView userName;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.storyImage);
            userName = itemView.findViewById(R.id.storyUserName);
        }
    }
}