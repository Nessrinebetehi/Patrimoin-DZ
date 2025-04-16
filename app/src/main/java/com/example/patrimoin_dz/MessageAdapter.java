package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";

        if (message.getSenderId().equals(currentUserId)) {
            holder.userMessage.setText(message.getContent());
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.aiMessage.setVisibility(View.GONE);
        } else {
            holder.aiMessage.setText(message.getContent());
            holder.aiMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage, aiMessage;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.user_message);
            aiMessage = itemView.findViewById(R.id.ai_message);
        }
    }
}