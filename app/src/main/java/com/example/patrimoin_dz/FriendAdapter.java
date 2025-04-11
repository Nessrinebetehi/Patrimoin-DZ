package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private List<Friend> friendList;
    private OnFriendClickListener friendClickListener;

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }

    public FriendAdapter(List<Friend> friendList, OnFriendClickListener friendClickListener) {
        this.friendList = friendList;
        this.friendClickListener = friendClickListener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);
        holder.friendName.setText(friend.getUserName());

        holder.itemView.setOnClickListener(v -> {
            if (friendClickListener != null) {
                friendClickListener.onFriendClick(friend);
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