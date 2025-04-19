package com.example.patrimoin_dz;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private static final String TAG = "FriendAdapter";
    private List<Friend> friendList;
    private OnFriendClickListener friendClickListener;

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }

    public static FriendAdapter createFriendAdapter(List<Friend> friendList, OnFriendClickListener friendClickListener) {
        return new FriendAdapter(friendList, friendClickListener);
    }

    private FriendAdapter(List<Friend> friendList, OnFriendClickListener friendClickListener) {
        this.friendList = friendList;
        this.friendClickListener = friendClickListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        boolean false_ = false;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false_ );
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        Log.d(TAG, "Binding friend at position " + position + ": username=" + friend.getUsername() + ", ID=" + friend.getUserId());

        // Vérifier si username est valide
        String username = friend.getUsername() != null ? friend.getUsername() : "Ami inconnu";
        if (holder.friendName != null) {
            holder.friendName.setText(username);
        } else {
            Log.e(TAG, "friendName TextView is null at position " + position);
        }

        // Gérer l'image (optionnel, défini une image par défaut si disponible)
        if (holder.friendImage != null) {
            try {
                holder.friendImage.setImageResource(R.drawable.ic_profile_placeholder);
            } catch (Exception e) {
                Log.e(TAG, "Error setting friend image: " + e.getMessage(), e);
            }
        } else {
            Log.w(TAG, "friendImage ImageView is null at position " + position);
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Friend clicked: " + username);
            if (friendClickListener != null) {
                friendClickListener.onFriendClick(friend);
            } else {
                Log.e(TAG, "FriendClickListener is null");
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList != null ? friendList.size() : 0;
    }

    static class FriendViewHolder extends RecyclerView.ViewHolder {
        ImageView friendImage;
        TextView friendName;

        FriendViewHolder(View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.friend_image);
            friendName = itemView.findViewById(R.id.friend_name);
        }
    }
}