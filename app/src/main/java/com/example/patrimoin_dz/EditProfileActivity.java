package com.example.patrimoin_dz;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfileActivity extends AppCompatActivity {

    private TextView usernameTextView, displayNameTextView, postsCountTextView, followersCountTextView, followingCountTextView;
    private ImageView profilePictureImageView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        usernameTextView = findViewById(R.id.username);
        displayNameTextView = findViewById(R.id.display_name);
        postsCountTextView = findViewById(R.id.posts_count);
        followersCountTextView = findViewById(R.id.followers_count);
        followingCountTextView = findViewById(R.id.following_count);
        profilePictureImageView = findViewById(R.id.profile_picture);

        loadUserData();
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        populateUserData();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur de chargement : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void populateUserData() {
        usernameTextView.setText(user.getUsername());
        displayNameTextView.setText(user.getDisplayName());
        postsCountTextView.setText(String.valueOf(user.getPostsCount()));
        followersCountTextView.setText(String.valueOf(user.getFollowersCount()));
        followingCountTextView.setText(String.valueOf(user.getFollowingCount()));
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            Glide.with(this).load(user.getProfilePictureUrl()).placeholder(R.drawable.ic_profile_placeholder).into(profilePictureImageView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}