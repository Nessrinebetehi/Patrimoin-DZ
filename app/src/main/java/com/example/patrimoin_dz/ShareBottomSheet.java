package com.example.patrimoin_dz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "ShareBottomSheet";
    private static final String ARG_POST_ID = "post_id";

    private RecyclerView friendsRecyclerView;
    private FriendAdapter friendAdapter;
    private List<Friend> friendList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String postId;

    public static ShareBottomSheet newInstance(String postId) {
        ShareBottomSheet fragment = new ShareBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            postId = getArguments().getString(ARG_POST_ID);
            Log.d(TAG, "ShareBottomSheet created with postId: " + postId);
        } else {
            Log.e(TAG, "No arguments provided to ShareBottomSheet");
            showToastSafely("Erreur : ID de publication manquant");
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "ShareBottomSheet view created");

        // Initialize views
        friendsRecyclerView = view.findViewById(R.id.friends_recycler_view);

        if (friendsRecyclerView == null) {
            Log.e(TAG, "friendsRecyclerView is null in bottom_sheet_share layout");
            showToastSafely("Erreur d'initialisation de l'interface");
            dismiss();
            return;
        }

        // Setup RecyclerView
        friendList = new ArrayList<>();
        friendAdapter = FriendAdapter.createFriendAdapter(friendList, this::sharePostWithFriend);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        friendsRecyclerView.setLayoutManager(layoutManager);
        friendsRecyclerView.setAdapter(friendAdapter);

        // Load friends from conversations
        loadFriendsFromConversations();
    }

    private void loadFriendsFromConversations() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "loadFriendsFromConversations: User not logged in");
            showToastSafely("Utilisateur non connecté");
            dismiss();
            return;
        }
        String currentUserId = currentUser.getUid();

        db.collection("conversations")
                .whereArrayContains("userIds", currentUserId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, aborting");
                        return;
                    }
                    friendList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        List<String> userIds = (List<String>) doc.get("userIds");
                        if (userIds != null && userIds.size() == 2) {
                            String friendId = userIds.get(0).equals(currentUserId) ? userIds.get(1) : userIds.get(0);
                            // Fetch friend details
                            db.collection("users").document(friendId)
                                    .get()
                                    .addOnSuccessListener(userDoc -> {
                                        if (!isAdded()) {
                                            Log.w(TAG, "Fragment not attached, skipping friend add");
                                            return;
                                        }
                                        if (userDoc.exists()) {
                                            String username = userDoc.getString("username");
                                            if (username != null && !username.isEmpty()) {
                                                friendList.add(new Friend(friendId, username));
                                                friendAdapter.notifyDataSetChanged();
                                                Log.d(TAG, "Added friend: " + username + ", ID: " + friendId);
                                            } else {
                                                Log.w(TAG, "Friend username is null or empty for ID: " + friendId);
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error fetching friend details: " + e.getMessage(), e);
                                    });
                        }
                    }
                    if (friendList.isEmpty()) {
                        Log.w(TAG, "No friends found in conversations");
                        showToastSafely("Aucun ami trouvé dans les conversations");
                        dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, skipping error handling");
                        return;
                    }
                    Log.e(TAG, "Error loading conversations: " + e.getMessage(), e);
                    showToastSafely("Erreur lors du chargement des amis");
                    dismiss();
                });
    }

    private void sharePostWithFriend(Friend friend) {
        if (!isAdded()) {
            Log.w(TAG, "Fragment not attached, cannot share");
            return;
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "sharePostWithFriend: User not logged in");
            showToastSafely("Utilisateur non connecté");
            return;
        }
        String currentUserId = currentUser.getUid();

        if (postId == null) {
            Log.e(TAG, "sharePostWithFriend: postId is null");
            showToastSafely("Erreur : ID de la publication manquant");
            return;
        }

        String friendUserId = friend.getUserId();
        if (friendUserId == null) {
            Log.e(TAG, "sharePostWithFriend: friend userId is null");
            showToastSafely("Erreur : Ami non valide");
            return;
        }

        String friendName = friend.getUsername() != null ? friend.getUsername() : "un ami";
        Log.d(TAG, "Sharing post " + postId + " with friend: " + friendName);

        // Find or create conversation
        String conversationId = getConversationId(currentUserId, friendUserId);
        Map<String, Object> message = new HashMap<>();
        message.put("senderId", currentUserId);
        message.put("postId", postId);
        message.put("type", "shared_post");
        message.put("timestamp", System.currentTimeMillis());

        db.collection("conversations")
                .document(conversationId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, skipping success handling");
                        return;
                    }
                    Log.d(TAG, "Post shared successfully with " + friendName + " in conversation: " + conversationId);
                    showToastSafely("Publication partagée avec " + friendName);
                    // Update conversation metadata
                    updateConversationMetadata(conversationId, currentUserId, friendUserId);
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, skipping error handling");
                        return;
                    }
                    Log.e(TAG, "Failed to share post: " + e.getMessage(), e);
                    showToastSafely("Erreur lors du partage");
                });
    }

    private String getConversationId(String userId1, String userId2) {
        // Generate a consistent conversation ID by sorting user IDs
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    private void updateConversationMetadata(String conversationId, String currentUserId, String friendUserId) {
        Map<String, Object> conversationData = new HashMap<>();
        conversationData.put("userIds", List.of(currentUserId, friendUserId));
        conversationData.put("lastMessageTimestamp", System.currentTimeMillis());

        db.collection("conversations")
                .document(conversationId)
                .set(conversationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Conversation metadata updated for ID: " + conversationId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update conversation metadata: " + e.getMessage(), e);
                });
    }

    private void showToastSafely(String message) {
        Context context = getContext();
        if (context != null && isAdded()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        } else {
            Log.w(TAG, "Cannot show toast: context is null or fragment not attached");
        }
    }
}