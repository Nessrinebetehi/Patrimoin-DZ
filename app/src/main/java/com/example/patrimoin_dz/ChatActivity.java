package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private RecyclerView recyclerViewChats;
    private SearchView searchView;
    private ChatAdapter chatAdapter;
    private List<Chat> chatList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration chatsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewChats = findViewById(R.id.recyclerViewChats);
        searchView = findViewById(R.id.searchView);

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatList);
        recyclerViewChats.setAdapter(chatAdapter);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));

        setupSearchView();
        loadChats();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterChats(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterChats(newText);
                return true;
            }
        });
    }

    private void filterChats(String query) {
        List<Chat> filteredList = new ArrayList<>();
        for (Chat chat : chatList) {
            if (chat.getUserName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(chat);
            }
        }
        chatAdapter.updateList(filteredList);
    }

    private void loadChats() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Utilisateur non connecté");
            Toast.makeText(this, "Veuillez vous connecter", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userId = currentUser.getUid();

        chatsListener = db.collection("friendships")
                .document(userId)
                .collection("friends")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Erreur chargement amis: " + error.getMessage(), error);
                        Toast.makeText(this, "Erreur chargement amis", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    chatList.clear();
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            String friendId = doc.getString("friendId");
                            String username = doc.getString("username");
                            if (friendId != null && username != null) {
                                db.collection("users").document(friendId)
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            String profileImageUrl = userDoc.getString("profilePictureUrl");
                                            String conversationId = generateConversationId(userId, friendId);
                                            db.collection("conversations").document(conversationId)
                                                    .get()
                                                    .addOnSuccessListener(conversationDoc -> {
                                                        String lastMessage = "";
                                                        String timestamp = "";
                                                        if (conversationDoc.exists()) {
                                                            lastMessage = conversationDoc.getString("lastMessage") != null ?
                                                                    conversationDoc.getString("lastMessage") : "";
                                                            Long conversationTimestamp = conversationDoc.getLong("timestamp");
                                                            timestamp = conversationTimestamp != null ?
                                                                    formatTimestamp(conversationTimestamp) : "";
                                                        }
                                                        Chat chat = new Chat(
                                                                username,
                                                                lastMessage,
                                                                0, // Unread count TBD
                                                                timestamp,
                                                                profileImageUrl != null ? profileImageUrl : ""
                                                        );
                                                        chatList.add(chat);
                                                        chatAdapter.notifyDataSetChanged();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Log.e(TAG, "Erreur récupération conversation: " + e.getMessage(), e);
                                                        Toast.makeText(this, "Erreur récupération conversation", Toast.LENGTH_SHORT).show();
                                                    });
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "Erreur récupération profil utilisateur: " + e.getMessage(), e);
                                            Toast.makeText(this, "Erreur récupération profil", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        }
                        Log.d(TAG, "Amis chargés : " + chatList.size());
                    } else {
                        Toast.makeText(this, "Aucun ami trouvé", Toast.LENGTH_SHORT).show();
                    }
                    chatAdapter.notifyDataSetChanged();
                });
    }

    private String generateConversationId(String user1Id, String user2Id) {
        return user1Id.compareTo(user2Id) < 0 ? user1Id + "_" + user2Id : user2Id + "_" + user1Id;
    }

    private String formatTimestamp(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        if (diff < 60_000) return "Il y a quelques secondes";
        if (diff < 3_600_000) return "Il y a " + (diff / 60_000) + " min";
        if (diff < 86_400_000) return "Il y a " + (diff / 3_600_000) + " h";
        return "Il y a " + (diff / 86_400_000) + " j";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatsListener != null) {
            chatsListener.remove();
        }
    }
}