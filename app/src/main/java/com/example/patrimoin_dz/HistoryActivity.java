package com.example.patrimoin_dz;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";
    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private List<HistoryItem> historyItems;
    private TextView emptyHistoryText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_history);

            // Configurer la Toolbar
            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());

            // Initialiser les vues
            historyRecyclerView = findViewById(R.id.history_recycler_view);
            emptyHistoryText = findViewById(R.id.empty_history_text);

            // Configurer le RecyclerView
            historyItems = new ArrayList<>();
            historyAdapter = new HistoryAdapter(historyItems);
            historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            historyRecyclerView.setAdapter(historyAdapter);

            // Charger l'historique depuis SharedPreferences
            loadHistory();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation de HistoryActivity : " + e.getMessage(), e);
            finish(); // Fermer l'activité en cas d'erreur
        }
    }

    private void loadHistory() {
        try {
            SharedPreferences prefs = getSharedPreferences("PatrAI_History", MODE_PRIVATE);
            Map<String, ?> allEntries = prefs.getAll();

            if (allEntries == null || allEntries.isEmpty()) {
                emptyHistoryText.setVisibility(View.VISIBLE);
                historyRecyclerView.setVisibility(View.GONE);
                return;
            }

            emptyHistoryText.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);

            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // Vérifier que la valeur est une String
                if (!(value instanceof String)) {
                    Log.w(TAG, "Entrée invalide dans SharedPreferences, clé : " + key);
                    continue;
                }

                String conversation = (String) value;

                // Vérifier que la conversation n'est pas vide
                if (conversation == null || conversation.trim().isEmpty()) {
                    Log.w(TAG, "Conversation vide pour la clé : " + key);
                    continue;
                }

                // Extraire un titre
                String title;
                String[] lines = conversation.split("\n");
                if (lines.length > 0 && !lines[0].trim().isEmpty()) {
                    title = lines[0];
                } else {
                    title = "Conversation sans titre";
                }
                if (title.length() > 50) {
                    title = title.substring(0, 47) + "...";
                }

                // Extraire un aperçu
                String preview = conversation.length() > 100 ? conversation.substring(0, 97) + "..." : conversation;

                // Extraire la date à partir de la clé (timestamp)
                String timestamp = key.replace("conversation_", "");
                String date;
                try {
                    date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                            .format(new java.util.Date(Long.parseLong(timestamp)));
                } catch (NumberFormatException e) {
                    Log.w(TAG, "Erreur lors du parsing du timestamp pour la clé : " + key, e);
                    date = "Date inconnue";
                }

                historyItems.add(new HistoryItem(title, preview, date, conversation));
            }

            historyAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du chargement de l'historique : " + e.getMessage(), e);
            emptyHistoryText.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
            emptyHistoryText.setText("Erreur lors du chargement de l'historique.");
        }
    }
}