package com.example.patrimoin_dz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryItem> historyItems;

    public HistoryAdapter(List<HistoryItem> historyItems) {
        this.historyItems = historyItems;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyItems.get(position);
        holder.titleText.setText(item.getTitle());
        holder.previewText.setText(item.getPreview());
        holder.dateText.setText(item.getDate());

        // Gérer le clic sur un élément de l'historique
        holder.itemView.setOnClickListener(v -> {
            // Récupérer la conversation complète
            String conversation = item.getFullConversation();

            // Enregistrer la conversation comme une nouvelle entrée dans SharedPreferences
            SharedPreferences prefs = holder.itemView.getContext().getSharedPreferences("PatrAI_History", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("conversation_" + System.currentTimeMillis(), conversation);
            editor.apply();

            // Revenir à PatrAIActivity
            Intent intent = new Intent(holder.itemView.getContext(), PatrAIActivity.class);
            holder.itemView.getContext().startActivity(intent);

            // Terminer HistoryActivity pour éviter de revenir en arrière
            if (holder.itemView.getContext() instanceof HistoryActivity) {
                ((HistoryActivity) holder.itemView.getContext()).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView previewText;
        TextView dateText;

        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.history_title);
            previewText = itemView.findViewById(R.id.history_preview);
            dateText = itemView.findViewById(R.id.history_date);
        }
    }
}