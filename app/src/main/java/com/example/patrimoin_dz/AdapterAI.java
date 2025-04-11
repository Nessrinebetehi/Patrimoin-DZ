package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdapterAI extends RecyclerView.Adapter<AdapterAI.MessageViewHolder> {
    private List<ChatMessage> messages;

    public AdapterAI(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (message.isUser()) {
            holder.userMessage.setText(message.getMessage());
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.aiMessage.setVisibility(View.GONE);
        } else {
            holder.aiMessage.setText(message.getMessage());
            holder.aiMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage;
        TextView aiMessage;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.user_message);
            aiMessage = itemView.findViewById(R.id.ai_message);
        }
    }
}