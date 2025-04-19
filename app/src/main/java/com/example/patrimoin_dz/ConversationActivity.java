package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageButton sendButton;
    private String friendId, friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Initialiser Firebase
        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            Log.d(TAG, "Firebase initialisé avec succès");
        } catch (Exception e) {
            Log.e(TAG, "Erreur initialisation Firebase: " + e.getMessage(), e);
            Toast.makeText(this, "Erreur initialisation Firebase. Vérifiez Google Play Services.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Utilisateur non connecté, redirection vers LoginActivity");
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        Log.d(TAG, "Utilisateur connecté: " + currentUser.getUid());

        friendId = getIntent().getStringExtra("friendId");
        friendName = getIntent().getStringExtra("friendName");

        if (friendId == null || friendName == null) {
            Log.e(TAG, "friendId ou friendName manquant");
            Toast.makeText(this, "Erreur: utilisateur non spécifié", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(friendName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        loadMessages();

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendTextMessage(messageText);
                messageInput.setText("");
            } else {
                Toast.makeText(this, "Veuillez entrer un message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Utilisateur non connecté dans loadMessages");
            Toast.makeText(this, "Erreur: utilisateur non connecté", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = currentUser.getUid();
        String chatId = getChatId(userId, friendId);
        Log.d(TAG, "Chargement messages pour chatId: " + chatId);

        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Erreur chargement messages: " + error.getMessage(), error);
                        Toast.makeText(this, "Erreur chargement messages: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (value != null) {
                        messageList.clear(); // Effacer la liste pour éviter les doublons
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            try {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    Log.d(TAG, "Traitement document: " + dc.getDocument().getId() + ", données: " + dc.getDocument().getData());
                                    Message message = dc.getDocument().toObject(Message.class);
                                    message.setId(dc.getDocument().getId());
                                    messageList.add(message);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Erreur lors du traitement du document " + dc.getDocument().getId() + ": " + e.getMessage(), e);
                                Toast.makeText(this, "Erreur traitement message: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                        messageAdapter.notifyDataSetChanged();
                        if (!messageList.isEmpty()) {
                            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                        }
                    }
                });
    }

    private void sendTextMessage(String text) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Utilisateur non connecté dans sendTextMessage");
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = currentUser.getUid();
        String chatId = getChatId(userId, friendId);
        Log.d(TAG, "Envoi message: " + text + ", chatId: " + chatId + ", userId: " + userId);

        // Créer ou mettre à jour le document chat avec les participants
        Map<String, Object> chatData = new HashMap<>();
        List<String> participants = new ArrayList<>();
        participants.add(userId);
        participants.add(friendId);
        chatData.put("participants", participants);

        // Créer le document chat avant d'envoyer le message
        db.collection("chats").document(chatId).set(chatData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Document chat créé/mis à jour: " + chatId);

                    // Créer le message
                    Message message = new Message(text, userId, false);

                    // Envoyer le message
                    db.collection("chats").document(chatId).collection("messages")
                            .add(message)
                            .addOnSuccessListener(documentReference -> {
                                Log.d(TAG, "Message texte envoyé: " + documentReference.getId());
                                // Mettre à jour la conversation
                                updateConversation(chatId, text, false);
                                // Envoyer une notification
                                sendNotification("Nouveau message", "Vous avez reçu un message de " + friendName);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erreur envoi message: " + e.getMessage(), e);
                                Toast.makeText(this, "Erreur envoi message: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur création chat: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur création chat: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void updateConversation(String chatId, String lastMessage, boolean isImage) {
        Map<String, Object> conversationData = new HashMap<>();
        conversationData.put("lastMessage", lastMessage);
        conversationData.put("timestamp", System.currentTimeMillis());

        db.collection("conversations").document(chatId)
                .set(conversationData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Conversation mise à jour: " + chatId))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur mise à jour conversation: " + e.getMessage(), e));
    }

    private void sendNotification(String title, String message) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Utilisateur non connecté dans sendNotification");
            return;
        }

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("senderId", currentUser.getUid());
        notification.put("recipientId", friendId);

        db.collection("notifications")
                .add(notification)
                .addOnSuccessListener(doc -> Log.d(TAG, "Notification envoyée à " + friendId))
                .addOnFailureListener(e -> Log.e(TAG, "Erreur envoi notification: " + e.getMessage(), e));
    }

    private String getChatId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}