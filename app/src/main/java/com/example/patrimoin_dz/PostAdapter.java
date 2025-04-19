package com.example.patrimoin_dz;

import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private static final String TAG = "PostAdapter";
    private List<Post> postList;
    private OnPostDeleteListener deleteListener;
    private OnAddFriendListener addFriendListener;
    private OnCloseClickListener closeClickListener;
    private OnSoundClickListener soundClickListener;
    private OnLikeClickListener likeClickListener;
    private OnCommentClickListener commentClickListener;
    private OnShareClickListener shareClickListener;
    private boolean isProfileActivity;
    private Map<String, String> pendingReactions;

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
        void onLikeClicked(Post post, String reactionType);
    }

    public interface OnCommentClickListener {
        void onCommentClicked(Post post);
    }

    public interface OnShareClickListener {
        void onShareClicked(Post post);
    }

    public PostAdapter(List<Post> postList, OnPostDeleteListener deleteListener, boolean isProfileActivity) {
        this.postList = postList != null ? postList : new ArrayList<>();
        this.deleteListener = deleteListener;
        this.isProfileActivity = isProfileActivity;
        this.pendingReactions = new HashMap<>();
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

    public void updatePosts(List<Post> newPosts) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostDiffCallback(postList, newPosts));
        postList.clear();
        postList.addAll(newPosts);
        diffResult.dispatchUpdatesTo(this);
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
        Log.d(TAG, "Binding post at position " + position + ": content=" + post.getContent());

        // Common Views (for both layouts)
        if (holder.postContent != null) {
            holder.postContent.setText(post.getContent() != null ? post.getContent() : "");
        } else {
            Log.e(TAG, "postContent is null at position: " + position);
        }

        if (holder.postImage != null) {
            if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(post.getImageUrl())
                        .thumbnail(0.1f)
                        .override(400, 400)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e(TAG, "Failed to load post image for post " + post.getPostId() + ": " + (e != null ? e.getMessage() : "Unknown error"));
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d(TAG, "Post image loaded for post " + post.getPostId());
                                return false;
                            }
                        })
                        .into(holder.postImage);
            } else {
                holder.postImage.setImageResource(R.drawable.ic_profile_placeholder);
                Log.w(TAG, "No image URL for post " + post.getPostId());
            }
        } else {
            Log.e(TAG, "postImage is null at position: " + position);
        }

        if (holder.postTime != null) {
            holder.postTime.setText(formatTimestamp(post.getTimestamp()));
        } else {
            Log.e(TAG, "postTime is null at position: " + position);
        }

        // Delete Button (for EditProfileActivity's item_post.xml)
        if (!isProfileActivity && holder.deleteButton != null) {
            holder.deleteButton.setOnClickListener(v -> {
                Log.d(TAG, "Delete button clicked for post: " + post.getPostId());
                if (deleteListener != null) {
                    deleteListener.onPostDeleted(post);
                } else {
                    Log.e(TAG, "OnPostDeleteListener is null for post: " + post.getPostId());
                }
            });
        }

        // ProfileActivity-specific Views
        if (isProfileActivity) {
            if (holder.userName != null) {
                holder.userName.setText(post.getUserName() != null ? post.getUserName() : "Utilisateur inconnu");
            } else {
                Log.e(TAG, "userName is null at position: " + position);
            }

            if (holder.userProfileImage != null) {
                if (post.getUserProfileImageUrl() != null && !post.getUserProfileImageUrl().isEmpty()) {
                    Log.d(TAG, "Loading profile photo for post " + post.getPostId() + ": " + post.getUserProfileImageUrl());
                    Glide.with(holder.itemView.getContext())
                            .load(post.getUserProfileImageUrl())
                            .thumbnail(0.1f)
                            .override(100, 100)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .circleCrop()
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.e(TAG, "Failed to load profile photo for post " + post.getPostId() + ": " + (e != null ? e.getMessage() : "Unknown error"));
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Log.d(TAG, "Profile photo loaded for post " + post.getPostId());
                                    return false;
                                }
                            })
                            .into(holder.userProfileImage);
                } else {
                    Log.w(TAG, "No profile photo URL for post " + post.getPostId());
                    holder.userProfileImage.setImageResource(R.drawable.ic_profile_placeholder);
                }
            } else {
                Log.e(TAG, "userProfileImage is null at position: " + position);
            }

            if (holder.addFriendButton != null) {
                holder.addFriendButton.setOnClickListener(v -> {
                    Log.d(TAG, "Add friend button clicked for post: " + post.getPostId());
                    if (addFriendListener != null) {
                        addFriendListener.onAddFriend(post);
                    } else {
                        Log.e(TAG, "AddFriendListener is null for post: " + post.getPostId());
                    }
                });
            }

            if (holder.closeButton != null) {
                holder.closeButton.setOnClickListener(v -> {
                    Log.d(TAG, "Close button clicked for post: " + post.getPostId());
                    if (closeClickListener != null) {
                        closeClickListener.onCloseClicked(post);
                    } else {
                        Log.e(TAG, "CloseClickListener is null for post: " + post.getPostId());
                    }
                });
            }

            if (holder.soundButton != null) {
                holder.soundButton.setOnClickListener(v -> {
                    Log.d(TAG, "Sound button clicked for post: " + post.getPostId());
                    if (soundClickListener != null) {
                        soundClickListener.onSoundClicked(post);
                    } else {
                        Log.e(TAG, "SoundClickListener is null for post: " + post.getPostId());
                    }
                });
            }

            if (holder.likeButton != null && holder.reactionBar != null) {
                updateLikeButtonText(holder, post);
                updateReactionDisplay(holder, post);

                holder.likeButton.setOnClickListener(v -> {
                    int visibility = holder.reactionBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    holder.reactionBar.setVisibility(visibility);
                    Log.d(TAG, "Reaction bar toggled at position " + position + ": visibility=" + (visibility == View.VISIBLE ? "VISIBLE" : "GONE"));
                });

                setupReactionButton(holder, holder.reactionLike, holder.reactionBar, holder, post, "like", position);
                setupReactionButton(holder, holder.reactionLove, holder.reactionBar, holder, post, "love", position);
                setupReactionButton(holder, holder.reactionHaha, holder.reactionBar, holder, post, "haha", position);
                setupReactionButton(holder, holder.reactionWow, holder.reactionBar, holder, post, "wow", position);
                setupReactionButton(holder, holder.reactionSad, holder.reactionBar, holder, post, "sad", position);
                setupReactionButton(holder, holder.reactionAngry, holder.reactionBar, holder, post, "angry", position);
            } else {
                Log.e(TAG, "likeButton or reactionBar is null at position: " + position);
            }

            if (holder.commentButton != null) {
                holder.commentButton.setOnClickListener(v -> {
                    Log.d(TAG, "Comment button clicked for post: " + post.getPostId());
                    if (commentClickListener != null) {
                        commentClickListener.onCommentClicked(post);
                    } else {
                        Log.e(TAG, "CommentClickListener is null for post: " + post.getPostId());
                    }
                });
            }

            if (holder.shareButton != null) {
                holder.shareButton.setOnClickListener(v -> {
                    Log.d(TAG, "Share button clicked for post: " + post.getPostId());
                    if (shareClickListener != null) {
                        shareClickListener.onShareClicked(post);
                    } else {
                        Log.e(TAG, "ShareClickListener is null for post: " + post.getPostId());
                    }
                });
            }
        }
    }

    private void updateLikeButtonText(PostViewHolder holder, Post post) {
        if (holder.likeButton == null) {
            Log.e(TAG, "likeButton is null in updateLikeButtonText for post: " + post.getPostId());
            return;
        }

        Map<String, String> reactionEmojis = new HashMap<>();
        reactionEmojis.put("like", "👍");
        reactionEmojis.put("love", "❤️");
        reactionEmojis.put("haha", "😂");
        reactionEmojis.put("wow", "😮");
        reactionEmojis.put("sad", "😢");
        reactionEmojis.put("angry", "😣");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        String postId = post.getPostId();
        String pendingReaction = pendingReactions.get(postId);

        if (pendingReaction != null) {
            holder.likeButton.setText(reactionEmojis.get(pendingReaction));
            Log.d(TAG, "Set likeButton to pending emoji: " + reactionEmojis.get(pendingReaction) + " for post: " + postId);
            return;
        }

        Map<String, List<String>> reactions = post.getReactions();
        if (reactions == null) {
            reactions = new HashMap<>();
        }

        boolean hasReacted = false;
        String selectedEmoji = null;

        for (Map.Entry<String, String> entry : reactionEmojis.entrySet()) {
            String reactionType = entry.getKey();
            String emoji = entry.getValue();
            List<String> userIds = reactions.getOrDefault(reactionType, new ArrayList<>());
            if (currentUserId != null && userIds.contains(currentUserId)) {
                hasReacted = true;
                selectedEmoji = emoji;
                break;
            }
        }

        if (hasReacted && selectedEmoji != null) {
            holder.likeButton.setText(selectedEmoji);
            Log.d(TAG, "Set likeButton to emoji: " + selectedEmoji + " for post: " + postId);
        } else {
            holder.likeButton.setText(R.string.like);
            Log.d(TAG, "Set likeButton to 'J'aime' for post: " + postId);
        }
    }

    private void updateReactionDisplay(PostViewHolder holder, Post post) {
        Map<String, String> reactionEmojis = new HashMap<>();
        reactionEmojis.put("like", "👍");
        reactionEmojis.put("love", "❤️");
        reactionEmojis.put("haha", "😂");
        reactionEmojis.put("wow", "😮");
        reactionEmojis.put("sad", "😢");
        reactionEmojis.put("angry", "😣");

        Map<String, List<String>> reactions = post.getReactions();
        if (reactions == null) {
            reactions = new HashMap<>();
        }

        updateSingleReaction(holder.reactionLike, reactions.getOrDefault("like", new ArrayList<>()), reactionEmojis.get("like"));
        updateSingleReaction(holder.reactionLove, reactions.getOrDefault("love", new ArrayList<>()), reactionEmojis.get("love"));
        updateSingleReaction(holder.reactionHaha, reactions.getOrDefault("haha", new ArrayList<>()), reactionEmojis.get("haha"));
        updateSingleReaction(holder.reactionWow, reactions.getOrDefault("wow", new ArrayList<>()), reactionEmojis.get("wow"));
        updateSingleReaction(holder.reactionSad, reactions.getOrDefault("sad", new ArrayList<>()), reactionEmojis.get("sad"));
        updateSingleReaction(holder.reactionAngry, reactions.getOrDefault("angry", new ArrayList<>()), reactionEmojis.get("angry"));
    }

    private void updateSingleReaction(TextView reactionView, List<String> userIds, String emoji) {
        if (reactionView != null && emoji != null) {
            if (userIds != null && !userIds.isEmpty()) {
                reactionView.setText(emoji + " " + userIds.size());
                reactionView.setAlpha(1.0f);
            } else {
                reactionView.setText(emoji);
                reactionView.setAlpha(0.5f);
            }
        }
    }

    private void setupReactionButton(PostViewHolder holder, TextView reactionView, LinearLayout reactionBar, PostViewHolder viewHolder, Post post, String reactionType, int position) {
        if (reactionView != null && viewHolder.likeButton != null && reactionBar != null) {
            reactionView.setOnClickListener(v -> {
                Log.d(TAG, "Reaction clicked: " + reactionType + " for post: " + post.getPostId() + " at position: " + position);
                if (likeClickListener != null) {
                    likeClickListener.onLikeClicked(post, reactionType);
                }

                reactionBar.setVisibility(View.GONE);
                Log.d(TAG, "Reaction bar closed for post: " + post.getPostId());

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = currentUser != null ? currentUser.getUid() : null;
                Map<String, List<String>> reactions = post.getReactions();
                if (reactions == null) {
                    reactions = new HashMap<>();
                }
                List<String> userIds = reactions.getOrDefault(reactionType, new ArrayList<>());
                boolean isReacted = currentUserId != null && userIds.contains(currentUserId);

                Map<String, String> reactionEmojis = new HashMap<>();
                reactionEmojis.put("like", "👍");
                reactionEmojis.put("love", "❤️");
                reactionEmojis.put("haha", "😂");
                reactionEmojis.put("wow", "😮");
                reactionEmojis.put("sad", "😢");
                reactionEmojis.put("angry", "😣");

                String postId = post.getPostId();
                if (isReacted) {
                    pendingReactions.remove(postId);
                    viewHolder.likeButton.setText(R.string.like);
                    Log.d(TAG, "Optimistic update: Set likeButton to 'J'aime' for post: " + postId);
                } else {
                    pendingReactions.put(postId, reactionType);
                    viewHolder.likeButton.setText(reactionEmojis.get(reactionType));
                    Log.d(TAG, "Optimistic update: Set likeButton to emoji: " + reactionEmojis.get(reactionType) + " for post: " + postId);
                }

                updateReactionDisplay(viewHolder, post);
            });
        } else {
            Log.e(TAG, "Reaction view (" + reactionType + "), likeButton, or reactionBar is null at position: " + position);
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

    public void clearPendingReaction(String postId) {
        pendingReactions.remove(postId);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userProfileImage;
        Button addFriendButton;
        ImageButton closeButton;
        ImageButton soundButton;
        Button likeButton;
        Button commentButton;
        Button shareButton;
        ImageView postImage;
        TextView postContent;
        TextView postTime;
        LinearLayout reactionBar;
        TextView reactionLike, reactionLove, reactionHaha, reactionWow, reactionSad, reactionAngry;
        ImageButton deleteButton;

        PostViewHolder(@NonNull View itemView, boolean isProfileActivity) {
            super(itemView);
            if (isProfileActivity) {
                userName = itemView.findViewById(R.id.user_name);
                userProfileImage = itemView.findViewById(R.id.user_profile_image);
                addFriendButton = itemView.findViewById(R.id.add_friend_button);
                closeButton = itemView.findViewById(R.id.close_button);
                soundButton = itemView.findViewById(R.id.sound_button);
                likeButton = itemView.findViewById(R.id.like_text);
                commentButton = itemView.findViewById(R.id.comment_button);
                shareButton = itemView.findViewById(R.id.share_button);
                reactionBar = itemView.findViewById(R.id.post_reaction_bar);
                reactionLike = itemView.findViewById(R.id.post_reaction_like);
                reactionLove = itemView.findViewById(R.id.post_reaction_love);
                reactionHaha = itemView.findViewById(R.id.post_reaction_haha);
                reactionWow = itemView.findViewById(R.id.post_reaction_wow);
                reactionSad = itemView.findViewById(R.id.post_reaction_sad);
                reactionAngry = itemView.findViewById(R.id.post_reaction_angry);
            } else {
                userName = itemView.findViewById(R.id.post_user_name);
                userProfileImage = itemView.findViewById(R.id.post_user_image);
                likeButton = itemView.findViewById(R.id.like_text);
                commentButton = itemView.findViewById(R.id.comment_button);
                shareButton = itemView.findViewById(R.id.share_button);
                reactionBar = itemView.findViewById(R.id.post_reaction_bar);
                reactionLike = itemView.findViewById(R.id.post_reaction_like);
                reactionLove = itemView.findViewById(R.id.post_reaction_love);
                reactionHaha = itemView.findViewById(R.id.post_reaction_haha);
                reactionWow = itemView.findViewById(R.id.post_reaction_wow);
                reactionSad = itemView.findViewById(R.id.post_reaction_sad);
                reactionAngry = itemView.findViewById(R.id.post_reaction_angry);
                deleteButton = itemView.findViewById(R.id.delete_button);
            }
            postImage = itemView.findViewById(R.id.post_image);
            postContent = itemView.findViewById(R.id.post_content);
            postTime = itemView.findViewById(R.id.post_time);

            if (userName == null && isProfileActivity) Log.e(TAG, "ViewHolder: userName is null in ProfileActivity");
            if (userProfileImage == null && isProfileActivity) Log.e(TAG, "ViewHolder: userProfileImage is null in ProfileActivity");
            if (likeButton == null && isProfileActivity) Log.e(TAG, "ViewHolder: likeButton is null in ProfileActivity");
            if (commentButton == null && isProfileActivity) Log.e(TAG, "ViewHolder: commentButton is null in ProfileActivity");
            if (shareButton == null && isProfileActivity) Log.e(TAG, "ViewHolder: shareButton is null in ProfileActivity");
            if (reactionBar == null && isProfileActivity) Log.e(TAG, "ViewHolder: reactionBar is null in ProfileActivity");
            if (postImage == null) Log.e(TAG, "ViewHolder: postImage is null");
            if (postContent == null) Log.e(TAG, "ViewHolder: postContent is null");
            if (postTime == null) Log.e(TAG, "ViewHolder: postTime is null");
            if (deleteButton == null && !isProfileActivity) Log.e(TAG, "ViewHolder: deleteButton is null in EditProfileActivity");
        }
    }

    private static class PostDiffCallback extends DiffUtil.Callback {
        private final List<Post> oldList;
        private final List<Post> newList;

        PostDiffCallback(List<Post> oldList, List<Post> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getPostId().equals(newList.get(newItemPosition).getPostId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Post oldPost = oldList.get(oldItemPosition);
            Post newPost = newList.get(newItemPosition);
            return oldPost.getContent().equals(newPost.getContent()) &&
                    oldPost.getTimestamp() == newPost.getTimestamp() &&
                    oldPost.getReactions().equals(newPost.getReactions());
        }
    }
}