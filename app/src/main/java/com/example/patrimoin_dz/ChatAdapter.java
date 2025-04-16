package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Chat> chatList;

    public ChatAdapter(List<Chat> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.userName.setText(chat.getUserName());
        holder.lastMessage.setText(chat.getLastMessage());
        holder.profileImage.setImageResource(chat.getProfileImage());

        if (chat.getUnreadCount() > 0) {
            holder.unreadCount.setText("+ de " + chat.getUnreadCount() + " nouveaux ...");
            holder.unreadCount.setVisibility(View.VISIBLE);
        } else {
            holder.unreadCount.setVisibility(View.GONE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            if (!chat.getTimestamp().isEmpty()) {
                holder.timestamp.setText(chat.getTimestamp());
                holder.timestamp.setVisibility(View.VISIBLE);
            } else {
                holder.timestamp.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), ChatConversationActivity.class);
            intent.putExtra("USER_NAME", chat.getUserName());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateList(List<Chat> newList) {
        chatList = newList;
        notifyDataSetChanged();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView userName, lastMessage, unreadCount, timestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unreadCount = itemView.findViewById(R.id.unreadCount);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }
}