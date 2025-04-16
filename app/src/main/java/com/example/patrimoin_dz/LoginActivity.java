package com.example.patrimoin_dz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private EditText emailField;
    private EditText passwordField;
    private ImageButton signInButton;

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
            Toast.makeText(this, "Erreur d'initialisation Firebase", Toast.LENGTH_LONG).show();
            return;
        }

        // Vérifier si l'utilisateur est déjà connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "Utilisateur déjà connecté : " + currentUser.getEmail());
            navigateToProfile();
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

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        signInButton = findViewById(R.id.sign_in_button);
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

        // Pré-remplir le champ email si envoyé depuis AccountsListActivity
        String selectedEmail = getIntent().getStringExtra("selected_email");
        if (selectedEmail != null) {
            emailField.setText(selectedEmail);
            Toast.makeText(this, "Veuillez entrer le mot de passe pour " + selectedEmail, Toast.LENGTH_SHORT).show();
        }

        // Bouton de fermeture
        closeButton.setOnClickListener(v -> {
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            formContainer.startAnimation(slideUp);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        });

        // Bouton de connexion
        signInButton.setOnClickListener(v -> loginUser());

        // Réinitialisation du mot de passe
        forgotPassword.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Entrez votre email pour réinitialiser", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isNetworkAvailable()) {
                Toast.makeText(this, "Aucune connexion Internet. Veuillez vérifier votre réseau.", Toast.LENGTH_LONG).show();
                return;
            }

            Log.d(TAG, "Tentative de réinitialisation du mot de passe pour : " + email);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email de réinitialisation envoyé avec succès");
                            Toast.makeText(this, "Email de réinitialisation envoyé", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Échec de l'envoi de l'email de réinitialisation", task.getException());
                            handleAuthError(task.getException());
                        }
                    });
        });

        // Lien vers l'inscription
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        // Validation des champs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifier la connectivité réseau
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Aucune connexion Internet. Veuillez vérifier votre réseau.", Toast.LENGTH_LONG).show();
            return;
        }

        // Désactiver le bouton pour éviter les clics multiples
        signInButton.setEnabled(false);

        Log.d(TAG, "Tentative de connexion avec email : " + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    signInButton.setEnabled(true); // Réactiver le bouton
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Connexion réussie pour l'utilisateur : " + (user != null ? user.getEmail() : "null"));
                        Toast.makeText(this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                        navigateToProfile();
                    } else {
                        Log.e(TAG, "Échec de la connexion", task.getException());
                        handleAuthError(task.getException());
                    }
                });
    }

    private void handleAuthError(Exception e) {
        if (e instanceof FirebaseNetworkException) {
            Toast.makeText(this, "Erreur réseau : vérifiez votre connexion Internet", Toast.LENGTH_LONG).show();
        } else if (e instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) e).getErrorCode();
            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
                    break;
                case "ERROR_WRONG_PASSWORD":
                    Toast.makeText(this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
                    break;
                case "ERROR_USER_NOT_FOUND":
                    Toast.makeText(this, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show();
                    break;
                case "ERROR_TOO_MANY_REQUESTS":
                    Toast.makeText(this, "Trop de tentatives. Réessayez plus tard.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    Toast.makeText(this, "Erreur de connexion : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Erreur : " + (e != null ? e.getMessage() : "inconnue"), Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void navigateToProfile() {
        Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}