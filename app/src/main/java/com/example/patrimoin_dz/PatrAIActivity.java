package com.example.patrimoin_dz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class PatrAIActivity extends AppCompatActivity {
    private static final String TAG = "PatrAIActivity";
    private EditText userInput;
    private MaterialButton askButton;
    private ProgressBar loadingIndicator;
    private RecyclerView chatRecyclerView;
    private AdapterAI adapterAI;
    private List<ChatMessage> chatMessages;
    private GrokApiService apiService;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patr_ai);

        // Configurer la Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurer les boutons de la Toolbar
        ImageButton newPageButton = findViewById(R.id.new_page_button);
        ImageButton historyButton = findViewById(R.id.history_button);

        newPageButton.setOnClickListener(v -> {
            Log.d(TAG, "Clic sur le bouton Nouvelle Page");
            // Ouvrir une nouvelle instance de PatrAIActivity
            Intent intent = new Intent(PatrAIActivity.this, PatrAIActivity.class);
            // Ajouter FLAG_ACTIVITY_NEW_TASK pour créer une nouvelle tâche
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        historyButton.setOnClickListener(v -> {
            Log.d(TAG, "Clic sur le bouton Historique");
            // Sauvegarder la conversation avant d'ouvrir l'historique
            saveConversation();
            // Ouvrir l'activité HistoryActivity
            Intent intent = new Intent(PatrAIActivity.this, HistoryActivity.class);
            try {
                startActivity(intent);
                Log.d(TAG, "HistoryActivity démarrée avec succès");
            } catch (Exception e) {
                Log.e(TAG, "Erreur lors du démarrage de HistoryActivity : " + e.getMessage(), e);
            }
        });

        // Initialisation des vues
        userInput = findViewById(R.id.question_input);
        askButton = findViewById(R.id.submit_button);
        loadingIndicator = findViewById(R.id.loading_indicator);
        chatRecyclerView = findViewById(R.id.chat_recycler_view);

        // Configurer le RecyclerView
        chatMessages = new ArrayList<>();
        adapterAI = new AdapterAI(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(adapterAI);

        // Ajouter un message de bienvenue
        chatMessages.add(new ChatMessage("Bienvenue ! Posez-moi une question sur le patrimoine algérien.", false));
        adapterAI.notifyDataSetChanged();

        // Initialisation de l'API service en arrière-plan
        new Thread(() -> {
            apiService = RetrofitClient.getService();
            runOnUiThread(() -> askButton.setEnabled(true));
        }).start();

        askButton.setEnabled(false);

        askButton.setOnClickListener(v -> {
            String question = userInput.getText().toString().trim();
            if (!question.isEmpty()) {
                chatMessages.add(new ChatMessage(question, true));
                adapterAI.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                userInput.setText("");
                sendRequest(question, 0);
            } else {
                chatMessages.add(new ChatMessage("Veuillez entrer une question.", false));
                adapterAI.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        });
    }

    private void sendRequest(String question, int retryCount) {
        loadingIndicator.setVisibility(View.VISIBLE);
        askButton.setEnabled(false);

        SharedPreferences prefs = getSharedPreferences("PatrAI_Cache", MODE_PRIVATE);
        String cachedResponse = prefs.getString(question, null);
        if (cachedResponse != null) {
            loadingIndicator.setVisibility(View.GONE);
            askButton.setEnabled(true);
            chatMessages.add(new ChatMessage(cachedResponse, false));
            adapterAI.notifyDataSetChanged();
            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
            Log.d(TAG, "Réponse récupérée du cache: " + cachedResponse);
            return;
        }

        GrokRequest request = new GrokRequest();
        String prompt = "Vous êtes un expert en patrimoine algérien. Répondez uniquement en français sur des sujets liés à la culture, l'histoire et le patrimoine algérien. Fournissez des détails précis et des exemples. Si la question n'est pas liée à l'Algérie ou à son patrimoine, répondez : 'Désolé, je ne peux répondre qu'aux questions sur le patrimoine algérien.' Question : " + question;
        request.setPrompt(prompt);

        Call<GrokResponse> call = apiService.getChatCompletion(request);
        call.enqueue(new Callback<GrokResponse>() {
            @Override
            public void onResponse(Call<GrokResponse> call, Response<GrokResponse> response) {
                runOnUiThread(() -> {
                    loadingIndicator.setVisibility(View.GONE);
                    askButton.setEnabled(true);

                    if (response.isSuccessful() && response.body() != null) {
                        String answer = response.body().getResponseText();
                        chatMessages.add(new ChatMessage(answer, false));
                        adapterAI.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(question, answer);
                        editor.apply();
                        Log.d(TAG, "Réponse reçue: " + answer);
                    } else {
                        String errorMsg;
                        if (response.code() == 400) {
                            errorMsg = "Erreur : Problème avec la requête. Vérifiez votre question.";
                        } else if (response.code() == 401) {
                            errorMsg = "Erreur : Clé API non autorisée. Vérifiez votre clé API Gemini.";
                        } else if (response.code() == 403) {
                            errorMsg = "Erreur : Accès refusé. Vérifiez votre clé API ou les permissions.";
                        } else if (response.code() == 429) {
                            errorMsg = "Erreur : Trop de requêtes. Veuillez réessayer plus tard.";
                        } else if (response.code() == 503) {
                            if (retryCount < MAX_RETRIES) {
                                errorMsg = "Service temporairement indisponible. Nouvelle tentative dans " + (RETRY_DELAY_MS / 1000) + " secondes...";
                                chatMessages.add(new ChatMessage(errorMsg, false));
                                adapterAI.notifyDataSetChanged();
                                chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                                Log.w(TAG, errorMsg);
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    sendRequest(question, retryCount + 1);
                                }, RETRY_DELAY_MS);
                                return;
                            } else {
                                errorMsg = "Erreur : Service indisponible après " + MAX_RETRIES + " tentatives. Veuillez réessayer plus tard.";
                            }
                        } else {
                            errorMsg = "Erreur inattendue : Code HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Erreur lecture erreur: " + e.getMessage());
                            }
                        }
                        chatMessages.add(new ChatMessage(errorMsg, false));
                        adapterAI.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                        Log.e(TAG, errorMsg);
                    }
                });
            }

            @Override
            public void onFailure(Call<GrokResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    loadingIndicator.setVisibility(View.GONE);
                    askButton.setEnabled(true);
                    String errorMsg = "Erreur de connexion : " + t.getMessage();
                    chatMessages.add(new ChatMessage(errorMsg, false));
                    adapterAI.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                    Log.e(TAG, errorMsg, t);
                });
            }
        });
    }

    private void saveConversation() {
        SharedPreferences prefs = getSharedPreferences("PatrAI_History", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        StringBuilder conversation = new StringBuilder();
        for (ChatMessage message : chatMessages) {
            conversation.append(message.isUser() ? "Utilisateur: " : "PatrAI: ")
                    .append(message.getMessage())
                    .append("\n");
        }
        editor.putString("conversation_" + System.currentTimeMillis(), conversation.toString());
        editor.apply();
        Log.d(TAG, "Conversation sauvegardée dans SharedPreferences");
    }
}