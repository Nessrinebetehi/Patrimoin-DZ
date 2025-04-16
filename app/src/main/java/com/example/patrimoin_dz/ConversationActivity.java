package com.example.patrimoin_dz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConversationActivity extends AppCompatActivity {

    private static final String TAG = "ConversationActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText messageInput;
    private ImageButton sendButton, attachImageButton;
    private String friendId, friendName;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    if (imageUri != null) {
                        sendImageMessage();
                    } else {
                        Toast.makeText(this, "Aucune image sélectionnée", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "Utilisateur non connecté, redirection vers LoginActivity");
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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
        attachImageButton = findViewById(R.id.attachImageButton);

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
            }
        });

        attachImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        });
    }

    private void loadMessages() {
        String userId = mAuth.getCurrentUser().getUid();
        String chatId = getChatId(userId, friendId);

        db.collection("chats").document(chatId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Erreur chargement messages: " + error.getMessage(), error);
                        Toast.makeText(this, "Erreur chargement messages: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            Message message = dc.getDocument().toObject(Message.class);
                            switch (dc.getType()) {
                                case ADDED:
                                    messageList.add(message);
                                    break;
                                case MODIFIED:
                                    // Update existing message if needed
                                    for (int i = 0; i < messageList.size(); i++) {
                                        if (messageList.get(i).getId().equals(message.getId())) {
                                            messageList.set(i, message);
                                            break;
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    messageList.removeIf(m -> m.getId().equals(message.getId()));
                                    break;
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
            Log.e(TAG, "Utilisateur non connecté");
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        String chatId = getChatId(userId, friendId);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("content", text);
        messageData.put("senderId", userId);
        messageData.put("timestamp", System.currentTimeMillis());
        messageData.put("isImage", false);

        db.collection("chats").document(chatId).collection("messages")
                .add(messageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Message texte envoyé: " + documentReference.getId());
                    sendNotification("Nouveau message", "Vous avez reçu un message de " + friendName);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur envoi message: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur envoi message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendImageMessage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Utilisateur non connecté");
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        String chatId = getChatId(userId, friendId);
        StorageReference ref = storage.getReference().child("chat_images/" + System.currentTimeMillis() + ".jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    Map<String, Object> messageData = new HashMap<>();
                    messageData.put("content", uri.toString());
                    messageData.put("senderId", userId);
                    messageData.put("timestamp", System.currentTimeMillis());
                    messageData.put("isImage", true);

                    db.collection("chats").document(chatId).collection("messages")
                            .add(messageData)
                            .addOnSuccessListener(documentReference -> {
                                Log.d(TAG, "Message image envoyé: " + documentReference.getId());
                                sendNotification("Nouveau message image", "Vous avez reçu une image de " + friendName);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erreur envoi image: " + e.getMessage(), e);
                                Toast.makeText(this, "Erreur envoi image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur upload image: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotification(String title, String message) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("senderId", mAuth.getCurrentUser().getUid());

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