package com.example.patrimoin_dz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imageUri = uri;
                    postImageView.setImageURI(uri);
                }
            }
    );

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

        postImageView.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        postButton.setOnClickListener(v -> createPost());
    }

    private void createPost() {
        String content = contentEditText.getText().toString().trim();
        String userId = mAuth.getCurrentUser().getUid();

        if (imageUri == null && content.isEmpty()) {
            Toast.makeText(this, "Ajoutez une image ou du texte", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            StorageReference ref = storage.getReference().child("posts/" + System.currentTimeMillis());
            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        Post post = new Post(userId, uri.toString(), System.currentTimeMillis(), content);
                        db.collection("posts").add(post)
                                .addOnSuccessListener(doc -> {
                                    updatePostsCount(userId);
                                    Toast.makeText(this, "Publication créée", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur d'upload : " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Post post = new Post(userId, "", System.currentTimeMillis(), content);
            db.collection("posts").add(post)
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