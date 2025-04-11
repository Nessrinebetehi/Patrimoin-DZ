package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private OnReplyClickListener replyClickListener;

    public interface OnReplyClickListener {
        void onReplyClick(Comment comment);
    }

    public CommentAdapter(List<Comment> commentList, OnReplyClickListener replyClickListener) {
        this.commentList = commentList;
        this.replyClickListener = replyClickListener;
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
        holder.userName.setText(comment.getUserName());
        holder.commentText.setText(comment.getText());
        holder.commentTime.setText(comment.getTime());

        // Assurez-vous que la barre de réactions est initialement cachée
        holder.reactionBar.setVisibility(View.GONE);

        // Afficher la réaction si elle existe
        if (comment.getReaction() != null) {
            holder.reactButton.setText(comment.getReaction());
            holder.reactButton.setTextSize(16);
            holder.reactButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // Retirer l'icône
        } else {
            holder.reactButton.setText("Réagir");
            holder.reactButton.setTextSize(12);
            holder.reactButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
        }

        // Gérer le clic sur le bouton "Réagir"
        holder.reactButton.setOnClickListener(v -> {
            if (holder.reactionBar.getVisibility() == View.VISIBLE) {
                // Cacher la barre de réactions
                holder.reactionBar.animate()
                        .translationY(50f)
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> holder.reactionBar.setVisibility(View.GONE))
                        .start();
            } else {
                // Afficher la barre de réactions
                holder.reactionBar.setVisibility(View.VISIBLE);
                holder.reactionBar.setTranslationY(50f);
                holder.reactionBar.setAlpha(0f);
                holder.reactionBar.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(200)
                        .start();

                // Animer chaque émoji
                TextView[] emojis = {
                        holder.reactionLike, holder.reactionLove, holder.reactionHaha,
                        holder.reactionWow, holder.reactionSad, holder.reactionAngry
                };
                for (int i = 0; i < emojis.length; i++) {
                    emojis[i].setScaleX(0f);
                    emojis[i].setScaleY(0f);
                    emojis[i].animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(300)
                            .setStartDelay(i * 50L)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                }
            }
        });

        // Gérer les clics sur les émojis de réaction
        holder.reactionLike.setOnClickListener(v -> handleReaction(holder, comment, "👍", position));
        holder.reactionLove.setOnClickListener(v -> handleReaction(holder, comment, "❤️", position));
        holder.reactionHaha.setOnClickListener(v -> handleReaction(holder, comment, "😂", position));
        holder.reactionWow.setOnClickListener(v -> handleReaction(holder, comment, "😮", position));
        holder.reactionSad.setOnClickListener(v -> handleReaction(holder, comment, "😢", position));
        holder.reactionAngry.setOnClickListener(v -> handleReaction(holder, comment, "😡", position));

        // Gérer le clic sur le bouton "Répondre"
        holder.replyButton.setOnClickListener(v -> {
            if (replyClickListener != null) {
                replyClickListener.onReplyClick(comment);
            }
        });

        // Gérer l'affichage des réponses
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            holder.repliesRecyclerView.setVisibility(View.VISIBLE);
            CommentAdapter repliesAdapter = new CommentAdapter(comment.getReplies(), replyClickListener);
            holder.repliesRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.repliesRecyclerView.setAdapter(repliesAdapter);
        } else {
            holder.repliesRecyclerView.setVisibility(View.GONE);
        }
    }

    private void handleReaction(CommentViewHolder holder, Comment comment, String emoji, int position) {
        comment.setReaction(emoji);
        holder.reactButton.setText(emoji);
        holder.reactButton.setTextSize(16);
        holder.reactButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // Retirer l'icône après réaction
        holder.reactionBar.animate()
                .translationY(50f)
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> holder.reactionBar.setVisibility(View.GONE))
                .start();
        Toast.makeText(holder.itemView.getContext(), "Réaction ajoutée : " + emoji, Toast.LENGTH_SHORT).show();
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return commentList != null ? commentList.size() : 0;
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;
        TextView commentText;
        TextView commentTime;
        Button replyButton;
        Button reactButton;
        LinearLayout reactionBar;
        TextView reactionLike, reactionLove, reactionHaha, reactionWow, reactionSad, reactionAngry;
        RecyclerView repliesRecyclerView;

        CommentViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.comment_user_image);
            userName = itemView.findViewById(R.id.comment_user_name);
            commentText = itemView.findViewById(R.id.comment_text);
            commentTime = itemView.findViewById(R.id.comment_time);
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
        }
    }
}