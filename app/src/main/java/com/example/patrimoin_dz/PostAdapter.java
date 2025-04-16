package com.example.patrimoin_dz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private OnPostDeleteListener deleteListener;
    private OnAddFriendListener addFriendListener;
    private OnCloseClickListener closeClickListener;
    private OnSoundClickListener soundClickListener;
    private OnLikeClickListener likeClickListener;
    private OnCommentClickListener commentClickListener;
    private OnShareClickListener shareClickListener;
    private boolean isProfileActivity;

    public interface OnPostDeleteListener {
        void onPostDeleted(Post post);

        void onPostToggleVisibility(Post post);
    }

    public interface OnAddFriendListener {
        void onAddFriend(Post post);
    }

    public interface OnCloseClickListener {
        void onCloseClicked(Post post);
    }

    public interface OnSoundClickListener {
        void onSoundClicked(Post post);
    }

    public interface OnLikeClickListener {
        void onLikeClicked(Post post);
    }

    public interface OnCommentClickListener {
        void onCommentClicked(Post post);
    }

    public interface OnShareClickListener {
        void onShareClicked(Post post);
    }

    public PostAdapter(List<Post> postList, OnPostDeleteListener deleteListener, boolean isProfileActivity) {
        this.postList = postList;
        this.deleteListener = deleteListener;
        this.isProfileActivity = isProfileActivity;
    }

    public void setAddFriendListener(OnAddFriendListener addFriendListener) {
        this.addFriendListener = addFriendListener;
    }

    public void setCloseClickListener(OnCloseClickListener closeClickListener) {
        this.closeClickListener = closeClickListener;
    }

    public void setSoundClickListener(OnSoundClickListener soundClickListener) {
        this.soundClickListener = soundClickListener;
    }

    public void setLikeClickListener(OnLikeClickListener likeClickListener) {
        this.likeClickListener = likeClickListener;
    }

    public void setCommentClickListener(OnCommentClickListener commentClickListener) {
        this.commentClickListener = commentClickListener;
    }

    public void setShareClickListener(OnShareClickListener shareClickListener) {
        this.shareClickListener = shareClickListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = isProfileActivity ? R.layout.item_profile_post : R.layout.item_post;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new PostViewHolder(view, isProfileActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        if (isProfileActivity) {
            if (holder.userName != null) {
                holder.userName.setText(post.getUserName() != null ? post.getUserName() : "Utilisateur inconnu");
            }
            if (holder.userProfileImage != null) {
                Glide.with(holder.itemView.getContext())
                        .load(post.getUserProfileImageUrl())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(holder.userProfileImage);
            }
            if (holder.addFriendButton != null) {
                holder.addFriendButton.setOnClickListener(v -> {
                    if (addFriendListener != null) {
                        addFriendListener.onAddFriend(post);
                    }
                });
            }
            if (holder.closeButton != null) {
                holder.closeButton.setOnClickListener(v -> {
                    if (closeClickListener != null) {
                        closeClickListener.onCloseClicked(post);
                    }
                });
            }
            if (holder.soundButton != null) {
                holder.soundButton.setOnClickListener(v -> {
                    if (soundClickListener != null) {
                        soundClickListener.onSoundClicked(post);
                    }
                });
            }
            if (holder.likeButton != null) {
                holder.likeButton.setOnClickListener(v -> {
                    if (likeClickListener != null) {
                        likeClickListener.onLikeClicked(post);
                    }
                });
            }
            if (holder.commentButton != null) {
                holder.commentButton.setOnClickListener(v -> {
                    if (commentClickListener != null) {
                        commentClickListener.onCommentClicked(post);
                    }
                });
            }
            if (holder.shareButton != null) {
                holder.shareButton.setOnClickListener(v -> {
                    if (shareClickListener != null) {
                        shareClickListener.onShareClicked(post);
                    }
                });
            }
        }

        Glide.with(holder.itemView.getContext())
                .load(post.getImageUrl())
                .placeholder(R.drawable.ic_profile_placeholder)
                .into(holder.postImage);

        if (holder.postContent != null) {
            holder.postContent.setText(post.getContent() != null ? post.getContent() : "");
        } else {
            Log.e("PostAdapter", "postContent is null at position: " + position);
        }

        if (holder.postTime != null) {
            holder.postTime.setText(formatTimestamp(post.getTimestamp()));
        } else {
            Log.e("PostAdapter", "postTime is null at position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public interface OnPostToggleVisibilityListener {
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userProfileImage;
        Button addFriendButton;
        ImageButton closeButton;
        ImageButton soundButton;
        LinearLayout likeButton;
        LinearLayout commentButton;
        LinearLayout shareButton;
        ImageView postImage;
        TextView postContent;
        TextView postTime;

        PostViewHolder(@NonNull View itemView, boolean isProfileActivity) {
            super(itemView);
            if (isProfileActivity) {
                userName = itemView.findViewById(R.id.user_name);
                userProfileImage = itemView.findViewById(R.id.user_profile_image);
                addFriendButton = itemView.findViewById(R.id.add_friend_button);
                closeButton = itemView.findViewById(R.id.close_button);
                soundButton = itemView.findViewById(R.id.sound_button);
                likeButton = itemView.findViewById(R.id.like_button);
                commentButton = itemView.findViewById(R.id.comment_button);
                shareButton = itemView.findViewById(R.id.share_button);
            }
            postImage = itemView.findViewById(R.id.post_image);
            postContent = itemView.findViewById(R.id.post_content);
            postTime = itemView.findViewById(R.id.post_time);
        }
    }
}