package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText emailField, passwordField, displayNameField, confirmPasswordField;
    private ImageButton registerButton, closeButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Erreur initialisation Firebase : ", e);
            Toast.makeText(this, "Erreur Firebase : " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        displayNameField = findViewById(R.id.display_name);
        confirmPasswordField = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);
        closeButton = findViewById(R.id.close_button);
        progressBar = findViewById(R.id.progress_bar);

        registerButton.setOnClickListener(v -> registerUser());
        closeButton.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();
        String displayName = displayNameField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    Post post = null;
                    User user = new User(post.getUserName(), post.getUserName(), post.getUserProfileImageUrl(), post.getUserId());
                    user.setUsername(displayName); // Utiliser displayName comme username
                    user.setDisplayName(displayName);
                    user.setProfilePictureUrl("");
                    user.setPostsCount(0);
                    user.setFollowersCount(0);
                    user.setFollowingCount(0);

                    db.collection("users").document(userId).set(user)
                            .addOnSuccessListener(aVoid -> {
                                progressBar.setVisibility(View.GONE);
                                Log.d(TAG, "Compte créé : " + userId);
                                Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Log.e(TAG, "Erreur Firestore : ", e);
                                Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Erreur inscription : ", e);
                    Toast.makeText(this, "Erreur d'inscription : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}