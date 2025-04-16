package com.example.patrimoin_dz;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";
    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ListenerRegistration notificationsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        if (notificationsRecyclerView == null) {
            Log.e(TAG, "notificationsRecyclerView is null!");
            return;
        }

        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(notificationList, this::acceptFriendRequest, this::rejectFriendRequest);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notificationsRecyclerView.setLayoutManager(layoutManager);
        notificationsRecyclerView.setAdapter(notificationAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                notificationsRecyclerView.getContext(), layoutManager.getOrientation());
        notificationsRecyclerView.addItemDecoration(dividerItemDecoration);

        loadFriendRequests();
    }

    private void loadFriendRequests() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Utilisateur non connecté");
            finish();
            return;
        }
        String userId = currentUser.getUid();

        notificationsListener = db.collection("friend_requests")
                .whereEqualTo("recipientId", userId)
                .whereEqualTo("status", "pending")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Erreur chargement demandes: " + error.getMessage(), error);
                        return;
                    }
                    notificationList.clear();
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            String senderUsername = doc.getString("senderUsername");
                            String requestId = doc.getId();
                            if (senderUsername != null) {
                                Notification notification = new Notification(
                                        senderUsername + " vous a envoyé une demande d'ami",
                                        formatTimestamp(doc.getLong("timestamp")),
                                        requestId
                                );
                                notificationList.add(notification);
                            }
                        }
                        Log.d(TAG, "Demandes chargées : " + notificationList.size());
                    }
                    notificationAdapter.notifyDataSetChanged();
                });
    }

    private void acceptFriendRequest(String requestId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "Utilisateur non connecté");
            return;
        }
        String currentUserId = currentUser.getUid();

        db.collection("friend_requests").document(requestId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String senderId = doc.getString("senderId");
                        String senderUsername = doc.getString("senderUsername");
                        String recipientId = doc.getString("recipientId");
                        String recipientUsername = doc.getString("recipientUsername");

                        if (senderId == null || recipientId == null || senderUsername == null || recipientUsername == null) {
                            Log.e(TAG, "Données de demande invalides");
                            return;
                        }

                        // Ajouter à la liste des amis de l'expéditeur
                        Map<String, Object> senderFriend = new HashMap<>();
                        senderFriend.put("friendId", currentUserId);
                        senderFriend.put("username", recipientUsername);

                        // Ajouter à la liste des amis du destinataire
                        Map<String, Object> recipientFriend = new HashMap<>();
                        recipientFriend.put("friendId", senderId);
                        recipientFriend.put("username", senderUsername);

                        // Créer une conversation vide
                        Map<String, Object> conversation = new HashMap<>();
                        conversation.put("user1Id", currentUserId);
                        conversation.put("user2Id", senderId);
                        conversation.put("lastMessage", "");
                        conversation.put("timestamp", System.currentTimeMillis());

                        // Mettre à jour Firestore
                        db.runBatch(batch -> {
                            batch.set(
                                    db.collection("friendships")
                                            .document(senderId)
                                            .collection("friends")
                                            .document(currentUserId),
                                    senderFriend
                            );
                            batch.set(
                                    db.collection("friendships")
                                            .document(currentUserId)
                                            .collection("friends")
                                            .document(senderId),
                                    recipientFriend
                            );
                            batch.set(
                                    db.collection("conversations")
                                            .document(generateConversationId(currentUserId, senderId)),
                                    conversation
                            );
                            batch.update(
                                    db.collection("friend_requests").document(requestId),
                                    "status", "accepted"
                            );
                        }).addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "Ami ajouté et conversation créée avec succès");
                            notificationAdapter.notifyDataSetChanged();
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Erreur ajout ami: " + e.getMessage(), e);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur récupération demande: " + e.getMessage(), e);
                });
    }

    private void rejectFriendRequest(String requestId) {
        db.collection("friend_requests").document(requestId)
                .update("status", "rejected")
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Demande rejetée");
                    notificationAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur rejet demande: " + e.getMessage(), e);
                });
    }

    private String generateConversationId(String user1Id, String user2Id) {
        return user1Id.compareTo(user2Id) < 0 ? user1Id + "_" + user2Id : user2Id + "_" + user1Id;
    }

    private String formatTimestamp(Long timestamp) {
        if (timestamp == null) return "";
        long now = System.currentTimeMillis();
        long diff = now - timestamp;
        if (diff < 60_000) return "Il y a quelques secondes";
        if (diff < 3_600_000) return "Il y a " + (diff / 60_000) + " minutes";
        if (diff < 86_400_000) return "Il y a " + (diff / 3_600_000) + " heures";
        return "Il y a " + (diff / 86_400_000) + " jours";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (notificationsListener != null) {
            notificationsListener.remove();
        }
    }
}