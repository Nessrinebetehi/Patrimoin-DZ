package com.example.patrimoin_dz;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

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
    private Comment replyingToComment;

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

        // Initialize views
        commentsRecyclerView = view.findViewById(R.id.comments_recycler_view);
        commentInput = view.findViewById(R.id.comment_input);
        sendCommentButton = view.findViewById(R.id.send_comment_button);

        // Setup RecyclerView
        commentList = new ArrayList<>();
        // Charger les commentaires depuis Firestore ou une autre source
        // Pour l'instant, on utilise des données fictives
        commentList.add(new Comment("user1", "Amina", "Super publication !", "Il y a 5 minutes"));
        commentList.add(new Comment("user2", "Karim", "J'adore ça !", "Il y a 10 minutes"));
        commentList.add(new Comment("user3", "Sofia", "Magnifique !", "Il y a 15 minutes"));

        commentAdapter = new CommentAdapter(commentList, comment -> {
            replyingToComment = comment;
            commentInput.setHint("Répondre à " + comment.getUserName() + "...");
            commentInput.requestFocus();
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        commentsRecyclerView.setLayoutManager(layoutManager);
        commentsRecyclerView.setAdapter(commentAdapter);

        // Handle send comment
        sendCommentButton.setOnClickListener(v -> {
            String commentText = commentInput.getText().toString().trim();
            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(getContext(), "Veuillez entrer un commentaire", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
            if (currentUserId == null) {
                Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
                return;
            }

            String userName = "Utilisateur Actuel"; // À remplacer par le vrai nom d'utilisateur
            Comment newComment = new Comment(currentUserId, userName, commentText, "À l'instant");

            if (replyingToComment != null) {
                replyingToComment.getReplies().add(newComment);
                commentAdapter.notifyDataSetChanged();
                saveReplyToFirestore(currentUserId, userName, commentText, replyingToComment);
                replyingToComment = null;
                commentInput.setHint("Écrire un commentaire...");
            } else {
                commentList.add(newComment);
                commentAdapter.notifyItemInserted(commentList.size() - 1);
                commentsRecyclerView.scrollToPosition(commentList.size() - 1);
                saveCommentToFirestore(currentUserId, userName, commentText);
            }

            commentInput.setText("");
        });
    }

    private void saveCommentToFirestore(String userId, String userName, String commentText) {
        if (postId == null) {
            Log.e(TAG, "saveCommentToFirestore: postId is null");
            return;
        }

        Comment comment = new Comment(userId, userName, commentText, "À l'instant");
        db.collection("posts").document(postId)
                .collection("comments")
                .add(comment)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "saveCommentToFirestore: Comment added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "saveCommentToFirestore: Failed to add comment - " + e.getMessage());
                    Toast.makeText(getContext(), "Erreur lors de l'ajout du commentaire", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveReplyToFirestore(String userId, String userName, String replyText, Comment parentComment) {
        if (postId == null) {
            Log.e(TAG, "saveReplyToFirestore: postId is null");
            return;
        }

        Comment reply = new Comment(userId, userName, replyText, "À l'instant");
        db.collection("posts").document(postId)
                .collection("comments")
                .document(parentComment.getUserId())
                .collection("replies")
                .add(reply)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "saveReplyToFirestore: Reply added successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "saveReplyToFirestore: Failed to add reply - " + e.getMessage());
                    Toast.makeText(getContext(), "Erreur lors de l'ajout de la réponse", Toast.LENGTH_SHORT).show();
                });
    }
}