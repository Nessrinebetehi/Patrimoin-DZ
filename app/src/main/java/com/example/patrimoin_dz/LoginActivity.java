package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialiser Firebase Auth
        try {
            mAuth = FirebaseAuth.getInstance();
            Log.d(TAG, "FirebaseAuth initialisé avec succès");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation de FirebaseAuth", e);
            Toast.makeText(this, "Erreur d'initialisation Firebase : " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Vérifier si l'utilisateur est déjà connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Utilisateur déjà connecté : " + currentUser.getEmail());
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
            return;
        }

        // Initialiser les vues
        LinearLayout formContainer = findViewById(R.id.form_container);
        if (formContainer == null) {
            Log.e(TAG, "form_container est null");
            Toast.makeText(this, "Erreur : form_container introuvable", Toast.LENGTH_LONG).show();
            return;
        }
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        formContainer.startAnimation(slideDown);

        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        ImageButton signInButton = findViewById(R.id.sign_in_button);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        ImageButton closeButton = findViewById(R.id.close_button);
        TextView registerLink = findViewById(R.id.register_link);

        // Vérifier que les vues ne sont pas null
        if (emailField == null || passwordField == null || signInButton == null ||
                forgotPassword == null || closeButton == null || registerLink == null) {
            Log.e(TAG, "Une ou plusieurs vues sont null");
            Toast.makeText(this, "Erreur : Une ou plusieurs vues sont introuvables", Toast.LENGTH_LONG).show();
            return;
        }

        // Bouton de fermeture
        closeButton.setOnClickListener(v -> {
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            formContainer.startAnimation(slideUp);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) { finish(); }
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        });

        // Bouton de connexion
        signInButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Validation des champs
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Connexion avec Firebase
            Log.d(TAG, "Tentative de connexion avec email : " + email);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG, "Connexion réussie pour l'utilisateur : " + (user != null ? user.getEmail() : "null"));
                            Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                            // Navigate to ProfileActivity
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Échec de la connexion", task.getException());
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Erreur inconnue";
                            Toast.makeText(LoginActivity.this, "Échec de la connexion : " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Réinitialisation du mot de passe
        forgotPassword.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Entrez votre email pour réinitialiser", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d(TAG, "Tentative de réinitialisation du mot de passe pour : " + email);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email de réinitialisation envoyé avec succès");
                            Toast.makeText(LoginActivity.this, "Email de réinitialisation envoyé", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Échec de l'envoi de l'email de réinitialisation", task.getException());
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Erreur inconnue";
                            Toast.makeText(LoginActivity.this, "Erreur : " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Lien vers l'inscription
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}