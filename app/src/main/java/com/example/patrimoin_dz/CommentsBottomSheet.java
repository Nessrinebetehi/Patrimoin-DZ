package com.example.patrimoin_dz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = "CommentsBottomSheet";
    private static final String ARG_POST_ID = "post_id";

    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private ImageButton sendCommentButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String postId;
    private ListenerRegistration commentsListener;

    public static CommentsBottomSheet newInstance(String postId) {
        CommentsBottomSheet fragment = new CommentsBottomSheet();
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
            Log.d(TAG, "CommentsBottomSheet created with postId: " + postId);
        } else {
            Log.e(TAG, "No arguments provided to CommentsBottomSheet");
            showToastSafely("Erreur : ID de publication manquant");
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "CommentsBottomSheet view created");

        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);
        commentInput = view.findViewById(R.id.comment_input);
        sendCommentButton = view.findViewById(R.id.send_comment_button);

        if (commentsRecyclerView == null || commentInput == null || sendCommentButton == null) {
            Log.e(TAG, "One or more views are null in bottom_sheet_comments layout");
            showToastSafely("Erreur d'initialisation de l'interface");
            dismiss();
            return;
        }

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, this::likeComment, this::replyToComment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        commentsRecyclerView.setLayoutManager(layoutManager);
        commentsRecyclerView.setAdapter(commentAdapter);

        sendCommentButton.setOnClickListener(v -> sendComment(null));

        loadComments();
    }

    private void loadComments() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "loadComments: User not logged in");
            showToastSafely("Utilisateur non connecté");
            dismiss();
            return;
        }

        if (postId == null) {
            Log.e(TAG, "loadComments: postId is null");
            showToastSafely("Erreur : ID de publication manquant");
            dismiss();
            return;
        }

        commentsListener = db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, aborting");
                        return;
                    }
                    if (error != null) {
                        Log.e(TAG, "Error loading comments: " + error.getMessage(), error);
                        showToastSafely("Erreur lors du chargement des commentaires");
                        return;
                    }
                    commentList.clear();
                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentSnapshot doc : snapshots) {
                            Comment comment = doc.toObject(Comment.class);
                            if (comment != null) {
                                comment.setCommentId(doc.getId());
                                Map<String, List<String>> reactions = (Map<String, List<String>>) doc.get("reactions");
                                if (reactions != null) {
                                    comment.setReactions(reactions);
                                } else {
                                    comment.setReactions(new HashMap<>());
                                }
                                fetchUserProfileAndReplies(comment);
                            }
                        }
                    } else {
                        Log.d(TAG, "No comments found");
                        commentAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void fetchUserProfileAndReplies(Comment comment) {
        db.collection("users").document(comment.getUserId())
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (!isAdded()) return;
                    String profilePhotoUrl = userDoc.getString("profilePhotoUrl");
                    comment.setProfilePhotoUrl(profilePhotoUrl);
                    fetchReplies(comment);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Log.e(TAG, "Failed to fetch profile for user " + comment.getUserId(), e);
                    fetchReplies(comment);
                });
    }

    private void fetchReplies(Comment comment) {
        db.collection("posts")
                .document(postId)
                .collection("comments")
                .whereEqualTo("parentCommentId", comment.getCommentId())
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!isAdded()) return;
                    List<Comment> replies = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot) {
                        Comment reply = doc.toObject(Comment.class);
                        if (reply != null) {
                            reply.setCommentId(doc.getId());
                            Map<String, List<String>> reactions = (Map<String, List<String>>) doc.get("reactions");
                            if (reactions != null) {
                                reply.setReactions(reactions);
                            } else {
                                reply.setReactions(new HashMap<>());
                            }
                            fetchUserProfileAndReplies(reply);
                            replies.add(reply);
                        }
                    }
                    comment.setReplies(replies);
                    commentList.add(comment);
                    commentAdapter.notifyDataSetChanged();
                    Log.d(TAG, "Loaded " + replies.size() + " replies for comment " + comment.getCommentId());
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Log.e(TAG, "Failed to fetch replies for comment " + comment.getCommentId() + ": " + e.getMessage(), e);
                    comment.setReplies(new ArrayList<>());
                    commentList.add(comment);
                    commentAdapter.notifyDataSetChanged();
                    showToastSafely("Erreur lors du chargement des réponses");
                });
    }

    private void sendComment(String parentCommentId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "sendComment: User not logged in");
            showToastSafely("Utilisateur non connecté");
            return;
        }
        String content = commentInput.getText().toString().trim();
        if (content.isEmpty()) {
            showToastSafely("Veuillez entrer un commentaire");
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, aborting");
                        return;
                    }
                    String username = documentSnapshot.getString("username");
                    String profilePhotoUrl = documentSnapshot.getString("profilePhotoUrl");
                    if (username == null || username.isEmpty()) {
                        username = "Utilisateur inconnu";
                    }

                    Map<String, Object> commentData = new HashMap<>();
                    commentData.put("userId", currentUser.getUid());
                    commentData.put("username", username);
                    commentData.put("content", content);
                    commentData.put("timestamp", System.currentTimeMillis());
                    commentData.put("profilePhotoUrl", profilePhotoUrl != null ? profilePhotoUrl : "");
                    commentData.put("reactions", new HashMap<String, List<String>>());
                    if (parentCommentId != null) {
                        commentData.put("parentCommentId", parentCommentId);
                    }

                    String finalUsername = username;
                    db.collection("posts")
                            .document(postId)
                            .collection("comments")
                            .add(commentData)
                            .addOnSuccessListener(documentReference -> {
                                if (!isAdded()) {
                                    Log.w(TAG, "Fragment not attached, skipping success handling");
                                    return;
                                }
                                Log.d(TAG, "Comment added successfully");
                                commentInput.setText("");
                                showToastSafely("Commentaire ajouté");
                                createCommentNotification(finalUsername, currentUser.getUid());
                            })
                            .addOnFailureListener(e -> {
                                if (!isAdded()) {
                                    Log.w(TAG, "Fragment not attached, skipping error handling");
                                    return;
                                }
                                Log.e(TAG, "Failed to add comment: " + e.getMessage(), e);
                                showToastSafely("Erreur lors de l'ajout du commentaire");
                            });
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, skipping error handling");
                        return;
                    }
                    Log.e(TAG, "Failed to fetch username: " + e.getMessage(), e);
                    showToastSafely("Erreur lors de la récupération du profil");
                });
    }

    private void createCommentNotification(String senderUsername, String senderId) {
        db.collection("posts").document(postId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded()) return;
                    String postOwnerId = doc.getString("userId");
                    if (postOwnerId != null && !postOwnerId.equals(senderId)) {
                        Map<String, Object> notificationData = new HashMap<>();
                        notificationData.put("type", "comment");
                        notificationData.put("message", senderUsername + " a commenté votre publication");
                        notificationData.put("timestamp", System.currentTimeMillis());
                        notificationData.put("recipientId", postOwnerId);
                        notificationData.put("senderId", senderId);
                        notificationData.put("postId", postId);
                        notificationData.put("emoji", null);

                        db.collection("notifications")
                                .add(notificationData)
                                .addOnSuccessListener(ref -> Log.d(TAG, "Comment notification created for post " + postId))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to create comment notification: " + e.getMessage(), e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch post for notification: " + e.getMessage(), e));
    }

    private void likeComment(Comment comment, String reactionType) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.e(TAG, "likeComment: User not logged in");
            showToastSafely("Utilisateur non connecté");
            return;
        }
        String currentUserId = currentUser.getUid();
        String commentId = comment.getCommentId();

        if (commentId == null) {
            Log.e(TAG, "likeComment: commentId is null");
            showToastSafely("Erreur : Commentaire invalide");
            return;
        }

        int commentPosition = commentList.indexOf(comment);
        if (commentPosition == -1) {
            Log.e(TAG, "likeComment: Comment not found in commentList");
            return;
        }

        db.collection("posts")
                .document(postId)
                .collection("comments")
                .document(commentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, aborting");
                        return;
                    }
                    Map<String, List<String>> reactions = (Map<String, List<String>>) documentSnapshot.get("reactions");
                    if (reactions == null) {
                        reactions = new HashMap<>();
                    }
                    List<String> reactionUsers = reactions.getOrDefault(reactionType, new ArrayList<>());
                    boolean isReacted = reactionUsers.contains(currentUserId);
                    if (isReacted) {
                        reactionUsers.remove(currentUserId);
                        showToastSafely("Réaction supprimée");
                    } else {
                        for (List<String> users : reactions.values()) {
                            users.remove(currentUserId);
                        }
                        reactionUsers.add(currentUserId);
                        showToastSafely("Réaction ajoutée: " + reactionType);
                    }
                    reactions.put(reactionType, reactionUsers);

                    Map<String, List<String>> finalReactions = reactions;
                    db.collection("posts")
                            .document(postId)
                            .collection("comments")
                            .document(commentId)
                            .update("reactions", reactions)
                            .addOnSuccessListener(aVoid -> {
                                if (!isAdded()) {
                                    Log.w(TAG, "Fragment not attached, skipping success handling");
                                    return;
                                }
                                Log.d(TAG, "Comment reaction updated for comment " + commentId);
                                comment.setReactions(finalReactions);
                                commentAdapter.notifyItemChanged(commentPosition);
                            })
                            .addOnFailureListener(e -> {
                                if (!isAdded()) {
                                    Log.w(TAG, "Fragment not attached, skipping error handling");
                                    return;
                                }
                                Log.e(TAG, "Failed to update reaction: " + e.getMessage(), e);
                                showToastSafely("Erreur lors de la mise à jour de la réaction");
                            });
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) {
                        Log.w(TAG, "Fragment not attached, skipping error handling");
                        return;
                    }
                    Log.e(TAG, "Failed to fetch comment: " + e.getMessage(), e);
                    showToastSafely("Erreur lors de la récupération du commentaire");
                });
    }

    private void replyToComment(Comment comment) {
        String commentId = comment.getCommentId();
        if (commentId == null) {
            Log.e(TAG, "replyToComment: commentId is null");
            showToastSafely("Erreur : Commentaire invalide");
            return;
        }
        String replyPrompt = "Réponse à " + (comment.getUsername() != null ? comment.getUsername() : "un utilisateur") + ": ";
        commentInput.setText(replyPrompt);
        commentInput.requestFocus();
        sendCommentButton.setOnClickListener(v -> {
            sendComment(commentId);
            sendCommentButton.setOnClickListener(v2 -> sendComment(null));
            commentInput.setText("");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (commentsListener != null) {
            commentsListener.remove();
        }
    }
}