package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;

    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.notificationMessage.setText(notification.getMessage());
        holder.notificationTime.setText(notification.getTime());
        // Optionnel : tu peux personnaliser l'icône selon le type de notification
        holder.notificationIcon.setImageResource(R.drawable.ic_notification);
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView notificationIcon;
        TextView notificationMessage;
        TextView notificationTime;

        NotificationViewHolder(View itemView) {
            super(itemView);
            notificationIcon = itemView.findViewById(R.id.notification_icon);
            notificationMessage = itemView.findViewById(R.id.notification_message);
            notificationTime = itemView.findViewById(R.id.notification_time);
        }
    }
}