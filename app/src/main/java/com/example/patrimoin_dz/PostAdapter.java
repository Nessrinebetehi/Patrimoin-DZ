package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private OnPostClickListener postClickListener;

    public interface OnPostClickListener {
        void onPostClick(String postId);
    }

    public PostAdapter(List<Post> postList, OnPostClickListener postClickListener) {
        this.postList = postList;
        this.postClickListener = postClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.postTime.setText(String.valueOf(post.getTimestamp()));
        Glide.with(holder.itemView.getContext())
                .load(post.getImageUrl())
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.postImage);

        holder.itemView.setOnClickListener(v -> postClickListener.onPostClick(post.getPostId()));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView postTime;

        PostViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.post_image);
            postTime = itemView.findViewById(R.id.post_time);
        }
    }
}