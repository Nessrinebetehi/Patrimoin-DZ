package com.example.patrimoin_dz;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private static final String TAG = "CommentAdapter";
    private List<Comment> commentList;
    private OnLikeClickListener likeClickListener;
    private OnReplyClickListener replyClickListener;
    private Map<String, String> pendingReactions; // Cache for optimistic reaction updates

    public interface OnLikeClickListener {
        void onLikeClick(Comment comment, String reactionType);
    }

    public interface OnReplyClickListener {
        void onReplyClick(Comment comment);
    }

    public CommentAdapter(List<Comment> commentList, OnLikeClickListener likeClickListener, OnReplyClickListener replyClickListener) {
        this.commentList = commentList != null ? commentList : new ArrayList<>();
        this.likeClickListener = likeClickListener;
        this.replyClickListener = replyClickListener;
        this.pendingReactions = new HashMap<>();
    }

    // Method to update comment list with DiffUtil
    public void updateComments(List<Comment> newComments) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CommentDiffCallback(commentList, newComments));
        commentList.clear();
        commentList.addAll(newComments);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        Log.d(TAG, "Binding comment at position " + position + ": content=" + comment.getContent());

        // Indent replies
        if (comment.getParentCommentId() != null) {
            holder.itemView.setPadding(holder.itemView.getPaddingLeft() + 20, holder.itemView.getPaddingTop(),
                    holder.itemView.getPaddingRight(), holder.itemView.getPaddingBottom());
        } else {
            holder.itemView.setPadding(0, holder.itemView.getPaddingTop(),
                    holder.itemView.getPaddingRight(), holder.itemView.getPaddingBottom());
        }

        // Set username
        if (holder.username != null) {
            holder.username.setText(comment.getUsername() != null ? comment.getUsername() : "Utilisateur inconnu");
        } else {
            Log.e(TAG, "comment_user_name TextView is null at position " + position);
        }

        // Set content
        if (holder.content != null) {
            holder.content.setText(comment.getContent() != null ? comment.getContent() : "");
        } else {
            Log.e(TAG, "comment_text TextView is null at position " + position);
        }

        // Set timestamp
        if (holder.timestamp != null) {
            String timeAgo = DateUtils.getRelativeTimeSpanString(
                    comment.getTimestamp(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
            ).toString();
            holder.timestamp.setText(timeAgo);
        } else {
            Log.e(TAG, "comment_time TextView is null at position " + position);
        }

        // Load profile photo with Glide optimization
        if (holder.profilePhoto != null) {
            if (comment.getProfilePhotoUrl() != null && !comment.getProfilePhotoUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(comment.getProfilePhotoUrl())
                        .thumbnail(0.1f) // Smaller thumbnail for faster loading
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(holder.profilePhoto);
            } else {
                holder.profilePhoto.setImageResource(R.drawable.ic_profile_placeholder);
            }
        } else {
            Log.e(TAG, "comment_user_image ImageView is null at position " + position);
        }

        // Setup reply button
        if (holder.replyButton != null) {
            holder.replyButton.setOnClickListener(v -> {
                if (replyClickListener != null) {
                    replyClickListener.onReplyClick(comment);
                }
            });
        } else {
            Log.e(TAG, "reply_button is null at position " + position);
        }

        // Setup reaction button and reaction bar
        if (holder.reactButton != null && holder.reactionBar != null) {
            Log.d(TAG, "Reaction bar initialized at position " + position);
            // Update react button text based on user's reaction
            updateReactButtonText(holder, comment);

            holder.reactButton.setOnClickListener(v -> {
                if (holder.reactionBar != null) {
                    int visibility = holder.reactionBar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    holder.reactionBar.setVisibility(visibility);
                    Log.d(TAG, "Reaction bar toggled at position " + position + ": visibility=" + (visibility == View.VISIBLE ? "VISIBLE" : "GONE"));
                } else {
                    Log.e(TAG, "Reaction bar is null when toggling at position " + position);
                }
            });

            // Update reaction display
            updateReactionDisplay(holder, comment);

            // Setup reaction buttons
            setupReactionButton(holder.reactionLike, holder.reactionBar, holder.reactButton, comment, "like", position);
            setupReactionButton(holder.reactionLove, holder.reactionBar, holder.reactButton, comment, "love", position);
            setupReactionButton(holder.reactionHaha, holder.reactionBar, holder.reactButton, comment, "haha", position);
            setupReactionButton(holder.reactionWow, holder.reactionBar, holder.reactButton, comment, "wow", position);
            setupReactionButton(holder.reactionSad, holder.reactionBar, holder.reactButton, comment, "sad", position);
            setupReactionButton(holder.reactionAngry, holder.reactionBar, holder.reactButton, comment, "angry", position);
        } else {
            Log.e(TAG, "react_button or reaction_bar is null at position " + position);
        }

        // Setup replies RecyclerView
        if (holder.repliesRecyclerView != null) {
            if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
                holder.repliesRecyclerView.setVisibility(View.VISIBLE);
                CommentAdapter repliesAdapter = new CommentAdapter(
                        comment.getReplies(), likeClickListener, replyClickListener
                );
                holder.repliesRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                holder.repliesRecyclerView.setAdapter(repliesAdapter);
            } else {
                holder.repliesRecyclerView.setVisibility(View.GONE);
            }
        } else {
            Log.e(TAG, "replies_recycler_view is null at position " + position);
        }
    }

    private void updateReactButtonText(CommentViewHolder holder, Comment comment) {
        if (holder.reactButton == null) {
            Log.e(TAG, "react_button is null in updateReactButtonText");
            return;
        }

        // Map des emojis pour chaque type de réaction
        Map<String, String> reactionEmojis = new HashMap<>();
        reactionEmojis.put("like", "👍");
        reactionEmojis.put("love", "❤️");
        reactionEmojis.put("haha", "😂");
        reactionEmojis.put("wow", "😮");
        reactionEmojis.put("sad", "😢");
        reactionEmojis.put("angry", "😣");

        // Get current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = currentUser != null ? currentUser.getUid() : null;

        // Check pending reactions first (optimistic updates)
        String commentId = comment.getCommentId();
        String pendingReaction = pendingReactions.get(commentId);

        if (pendingReaction != null) {
            holder.reactButton.setText(reactionEmojis.get(pendingReaction));
            Log.d(TAG, "Set react_button text to pending emoji: " + reactionEmojis.get(pendingReaction) + " for comment " + commentId);
            return;
        }

        // Check Firestore reactions
        Map<String, List<String>> reactions = comment.getReactions();
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

        // Update react button text
        if (hasReacted && selectedEmoji != null) {
            holder.reactButton.setText(selectedEmoji);
            Log.d(TAG, "Set react_button text to emoji: " + selectedEmoji + " for comment " + commentId);
        } else {
            holder.reactButton.setText(R.string.react);
            Log.d(TAG, "Set react_button text to 'Réagir' for comment " + commentId);
        }
    }

    private void updateReactionDisplay(CommentViewHolder holder, Comment comment) {
        // Map des emojis pour chaque type de réaction
        Map<String, String> reactionEmojis = new HashMap<>();
        reactionEmojis.put("like", "👍");
        reactionEmojis.put("love", "❤️");
        reactionEmojis.put("haha", "😂");
        reactionEmojis.put("wow", "😮");
        reactionEmojis.put("sad", "😢");
        reactionEmojis.put("angry", "😣");

        // Récupérer les réactions du commentaire
        Map<String, List<String>> reactions = comment.getReactions();
        if (reactions == null) {
            reactions = new HashMap<>();
        }

        // Mettre à jour chaque bouton de réaction
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
                reactionView.setAlpha(1.0f); // Pleine opacité si sélectionné
            } else {
                reactionView.setText(emoji);
                reactionView.setAlpha(0.5f); // Semi-transparent si non sélectionné
            }
        }
    }

    private void setupReactionButton(TextView reactionView, LinearLayout reactionBar, Button reactButton, Comment comment, String reactionType, int position) {
        if (reactionView != null && reactButton != null && reactionBar != null) {
            reactionView.setOnClickListener(v -> {
                Log.d(TAG, "Reaction clicked: " + reactionType + " for comment " + comment.getCommentId() + " at position " + position);
                if (likeClickListener != null) {
                    likeClickListener.onLikeClick(comment, reactionType);
                }

                // Close the reaction bar
                reactionBar.setVisibility(View.GONE);
                Log.d(TAG, "Reaction bar closed for comment " + comment.getCommentId());

                // Optimistic update: Cache the reaction
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = currentUser != null ? currentUser.getUid() : null;
                Map<String, List<String>> reactions = comment.getReactions();
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

                String commentId = comment.getCommentId();
                if (isReacted) {
                    // User is removing the reaction
                    pendingReactions.remove(commentId);
                    reactButton.setText(R.string.react);
                    Log.d(TAG, "Optimistic update: Set react_button to 'Réagir' for comment " + commentId);
                } else {
                    // User is adding the reaction
                    pendingReactions.put(commentId, reactionType);
                    reactButton.setText(reactionEmojis.get(reactionType));
                    Log.d(TAG, "Optimistic update: Set react_button to emoji " + reactionEmojis.get(reactionType) + " for comment " + commentId);
                }
            });
        } else {
            Log.e(TAG, "Reaction view (" + reactionType + "), react_button, or reaction_bar is null at position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView content;
        TextView timestamp;
        ImageView profilePhoto;
        Button replyButton;
        Button reactButton;
        LinearLayout reactionBar;
        TextView reactionLike, reactionLove, reactionHaha, reactionWow, reactionSad, reactionAngry;
        RecyclerView repliesRecyclerView;

        CommentViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.comment_user_name);
            content = itemView.findViewById(R.id.comment_text);
            timestamp = itemView.findViewById(R.id.comment_time);
            profilePhoto = itemView.findViewById(R.id.comment_user_image);
            replyButton = itemView.findViewById(R.id.reply_button);
            reactButton = itemView.findViewById(R.id.react_button);
            reactionBar = itemView.findViewById(R.id.comment_reaction_bar);
            reactionLike = itemView.findViewById(R.id.comment_reaction_like);
            reactionLove = itemView.findViewById(R.id.comment_reaction_love);
            reactionHaha = itemView.findViewById(R.id.comment_reaction_haha);
            reactionWow = itemView.findViewById(R.id.comment_reaction_wow);
            reactionSad = itemView.findViewById(R.id.comment_reaction_sad);
            reactionAngry = itemView.findViewById(R.id.comment_reaction_angry);
            repliesRecyclerView = itemView.findViewById(R.id.replies_recycler_view);

            // Debug: Log if views are null
            if (username == null) Log.e(TAG, "ViewHolder: comment_user_name is null");
            if (content == null) Log.e(TAG, "ViewHolder: comment_text is null");
            if (timestamp == null) Log.e(TAG, "ViewHolder: comment_time is null");
            if (profilePhoto == null) Log.e(TAG, "ViewHolder: comment_user_image is null");
            if (replyButton == null) Log.e(TAG, "ViewHolder: reply_button is null");
            if (reactButton == null) Log.e(TAG, "ViewHolder: react_button is null");
            if (reactionBar == null) Log.e(TAG, "ViewHolder: comment_reaction_bar is null");
            if (reactionLike == null) Log.e(TAG, "ViewHolder: comment_reaction_like is null");
            if (reactionLove == null) Log.e(TAG, "ViewHolder: comment_reaction_love is null");
            if (reactionHaha == null) Log.e(TAG, "ViewHolder: comment_reaction_haha is null");
            if (reactionWow == null) Log.e(TAG, "ViewHolder: comment_reaction_wow is null");
            if (reactionSad == null) Log.e(TAG, "ViewHolder: comment_reaction_sad is null");
            if (reactionAngry == null) Log.e(TAG, "ViewHolder: comment_reaction_angry is null");
            if (repliesRecyclerView == null) Log.e(TAG, "ViewHolder: replies_recycler_view is null");
        }
    }

    // DiffUtil callback for efficient RecyclerView updates
    private static class CommentDiffCallback extends DiffUtil.Callback {
        private final List<Comment> oldList;
        private final List<Comment> newList;

        CommentDiffCallback(List<Comment> oldList, List<Comment> newList) {
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
            return oldList.get(oldItemPosition).getCommentId().equals(newList.get(newItemPosition).getCommentId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Comment oldComment = oldList.get(oldItemPosition);
            Comment newComment = newList.get(newItemPosition);
            return oldComment.getContent().equals(newComment.getContent()) &&
                    oldComment.getTimestamp() == newComment.getTimestamp() &&
                    oldComment.getReactions().equals(newComment.getReactions());
        }
    }
}