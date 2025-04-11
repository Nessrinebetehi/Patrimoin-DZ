package com.example.patrimoin_dz;

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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

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

        // Initialize views
        friendsRecyclerView = view.findViewById(R.id.friends_recycler_view);

        // Setup RecyclerView
        friendList = new ArrayList<>();
        // Ajouter des amis fictifs pour tester (à remplacer par des données réelles depuis Firestore)
        friendList.add(new Friend("friend1", "Amina"));
        friendList.add(new Friend("friend2", "Karim"));
        friendList.add(new Friend("friend3", "Sofia"));
        friendList.add(new Friend("friend4", "Yanis"));

        friendAdapter = new FriendAdapter(friendList, friend -> {
            // Gérer le clic sur un ami pour partager
            sharePostWithFriend(friend);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        friendsRecyclerView.setLayoutManager(layoutManager);
        friendsRecyclerView.setAdapter(friendAdapter);
    }

    private void sharePostWithFriend(Friend friend) {
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId == null) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        if (postId == null) {
            Toast.makeText(getContext(), "Erreur : ID de la publication manquant", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simuler le partage (à remplacer par une vraie logique de partage, par exemple via Firestore)
        Toast.makeText(getContext(), "Publication partagée avec " + friend.getUserName(), Toast.LENGTH_SHORT).show();

        // (Optionnel) Sauvegarder le partage dans Firestore
        saveShareToFirestore(currentUserId, friend.getUserId());

        // Fermer la Bottom Sheet après le partage
        dismiss();
    }

    private void saveShareToFirestore(String currentUserId, String friendUserId) {
        // Simuler un partage dans Firestore (par exemple, ajouter une entrée dans une collection "shares")
        db.collection("users").document(friendUserId)
                .collection("shared_posts")
                .document(postId)
                .set(new Share(currentUserId, postId))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "saveShareToFirestore: Post shared successfully with " + friendUserId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "saveShareToFirestore: Failed to share post - " + e.getMessage());
                    Toast.makeText(getContext(), "Erreur lors du partage", Toast.LENGTH_SHORT).show();
                });
    }
}