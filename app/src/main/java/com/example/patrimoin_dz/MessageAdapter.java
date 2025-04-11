package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messageList;

    public MessageAdapter(List<Message> messageList) {
        this.messageList = messageList;
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

        // Réinitialiser les visibilités
        holder.sentMessage.setVisibility(View.GONE);
        holder.receivedMessage.setVisibility(View.GONE);
        holder.sentImage.setVisibility(View.GONE);
        holder.receivedImage.setVisibility(View.GONE);

        if (message.isImage()) {
            // Afficher une image
            if (message.isSentByUser()) {
                Glide.with(holder.itemView.getContext())
                        .load(message.getImageUri())
                        .into(holder.sentImage);
                holder.sentImage.setVisibility(View.VISIBLE);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(message.getImageUri())
                        .into(holder.receivedImage);
                holder.receivedImage.setVisibility(View.VISIBLE);
            }
        } else {
            // Afficher un message texte
            if (message.isSentByUser()) {
                holder.sentMessage.setText(message.getText());
                holder.sentMessage.setVisibility(View.VISIBLE);
            } else {
                holder.receivedMessage.setText(message.getText());
                holder.receivedMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView sentMessage, receivedMessage;
        ImageView sentImage, receivedImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessage = itemView.findViewById(R.id.sentMessage);
            receivedMessage = itemView.findViewById(R.id.receivedMessage);
            sentImage = itemView.findViewById(R.id.sentImage);
            receivedImage = itemView.findViewById(R.id.receivedImage);
        }
    }
}