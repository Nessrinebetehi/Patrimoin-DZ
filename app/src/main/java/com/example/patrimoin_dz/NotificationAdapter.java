package com.example.patrimoin_dz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<Notification> notificationList;
    private OnAcceptClickListener acceptClickListener;
    private OnRejectClickListener rejectClickListener;

    public interface OnAcceptClickListener {
        void onAcceptClicked(String requestId);
    }

    public interface OnRejectClickListener {
        void onRejectClicked(String requestId);
    }

    public NotificationAdapter(List<Notification> notificationList,
                               OnAcceptClickListener acceptClickListener,
                               OnRejectClickListener rejectClickListener) {
        this.notificationList = notificationList;
        this.acceptClickListener = acceptClickListener;
        this.rejectClickListener = rejectClickListener;
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
        holder.notificationIcon.setImageResource(R.drawable.ic_notification);

        holder.acceptButton.setOnClickListener(v -> {
            if (acceptClickListener != null) {
                acceptClickListener.onAcceptClicked(notification.getRequestId());
            }
        });

        holder.rejectButton.setOnClickListener(v -> {
            if (rejectClickListener != null) {
                rejectClickListener.onRejectClicked(notification.getRequestId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView notificationIcon;
        TextView notificationMessage;
        TextView notificationTime;
        Button acceptButton;
        Button rejectButton;

        NotificationViewHolder(View itemView) {
            super(itemView);
            notificationIcon = itemView.findViewById(R.id.notification_icon);
            notificationMessage = itemView.findViewById(R.id.notification_message);
            notificationTime = itemView.findViewById(R.id.notification_time);
            acceptButton = itemView.findViewById(R.id.accept_button);
            rejectButton = itemView.findViewById(R.id.reject_button);
        }
    }
}