package com.example.patrimoin_dz;

import android.content.ContentValues;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private List<Post> postsList;
    private ContentValues postList;

    public PostsAdapter() {
        this.postsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postsList.get(position);

        // Charger l'image
        Glide.with(holder.itemView.getContext())
                .load(post.getImageUrl())
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.postImage);

        // Afficher le contenu
        if (holder.postContent != null) {
            holder.postContent.setText(post.getContent() != null ? post.getContent() : "");
        } else {
            Log.e("PostsAdapter", "postContent is null at position: " + position);
        }

        // Afficher le timestamp
        if (holder.postTime != null) {
            holder.postTime.setText(String.valueOf(post.getTimestamp()));
        } else {
            Log.e("PostsAdapter", "postTime is null at position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void addPost(Post post) {
        postsList.add(0, post);
        notifyItemInserted(0);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView postContent;
        TextView postTime;
        ImageButton deleteButton;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            postContent = itemView.findViewById(R.id.post_content);
            postTime = itemView.findViewById(R.id.post_time);
            deleteButton = itemView.findViewById(R.id.delete_button);

            // Logs pour débogage
            Log.d("PostViewHolder", "postContent: " + (postContent == null ? "null" : "not null"));
            Log.d("PostViewHolder", "postTime: " + (postTime == null ? "null" : "not null"));
        }
    }
}