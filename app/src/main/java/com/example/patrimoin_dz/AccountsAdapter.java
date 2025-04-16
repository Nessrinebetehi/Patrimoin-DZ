package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import java.util.List;

public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.AccountViewHolder> {

    private List<User> accountsList;
    private OnAccountClickListener listener;

    public interface OnAccountClickListener {
        void onAccountClick(User user);
    }

    public AccountsAdapter(List<User> accountsList, OnAccountClickListener listener) {
        this.accountsList = accountsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        User user = accountsList.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.displayNameTextView.setText(user.getDisplayName());

        // Load profile picture
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getProfilePictureUrl())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(holder.profilePictureImageView);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.ic_profile_placeholder)
                    .apply(new RequestOptions().circleCrop())
                    .into(holder.profilePictureImageView);
        }

        holder.itemView.setOnClickListener(v -> listener.onAccountClick(user));
    }

    @Override
    public int getItemCount() {
        return accountsList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, displayNameTextView;
        ImageView profilePictureImageView;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.account_username);
            displayNameTextView = itemView.findViewById(R.id.account_display_name);
            profilePictureImageView = itemView.findViewById(R.id.account_profile_picture);
        }
    }
}