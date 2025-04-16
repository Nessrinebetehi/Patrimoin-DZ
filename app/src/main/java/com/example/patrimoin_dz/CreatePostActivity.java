package com.example.patrimoin_dz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreatePostActivity extends AppCompatActivity {

    private EditText contentEditText;
    private ImageView postImageView;
    private Button postButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private Uri imageUri;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    postImageView.setImageURI(imageUri);
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        contentEditText = findViewById(R.id.content_edit_text);
        postImageView = findViewById(R.id.post_image_view);
        postButton = findViewById(R.id.post_button);

        postImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        postButton.setOnClickListener(v -> createPost());
    }

    private void createPost() {
        String content = contentEditText.getText().toString().trim();
        String userId = mAuth.getCurrentUser().getUid();

        if (imageUri == null && content.isEmpty()) {
            Toast.makeText(this, "Ajoutez une image ou du texte", Toast.LENGTH_SHORT).show();
            return;
        }

        String postId = db.collection("posts").document().getId();
        long timestamp = System.currentTimeMillis();

        if (imageUri != null) {
            StorageReference ref = storage.getReference().child("posts/" + postId);
            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        Post post = new Post(postId, userId, postId, userId, uri.toString(), content, timestamp);
                        db.collection("posts").document(postId).set(post)
                                .addOnSuccessListener(doc -> {
                                    updatePostsCount(userId);
                                    Toast.makeText(this, "Publication créée", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur d'upload : " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Post post = new Post(postId, userId, postId, userId, "", content, timestamp);
            db.collection("posts").document(postId).set(post)
                    .addOnSuccessListener(doc -> {
                        updatePostsCount(userId);
                        Toast.makeText(this, "Publication créée", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void updatePostsCount(String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        user.setPostsCount(user.getPostsCount() + 1);
                        db.collection("users").document(userId).set(user);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}