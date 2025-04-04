package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialiser Firebase Auth
        try {
            mAuth = FirebaseAuth.getInstance();
            Log.d(TAG, "FirebaseAuth initialisé avec succès");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors de l'initialisation de FirebaseAuth", e);
            Toast.makeText(this, "Erreur d'initialisation Firebase : " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Initialiser les vues
        LinearLayout formContainer = findViewById(R.id.form_container);
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        formContainer.startAnimation(slideDown);

        EditText firstNameField = findViewById(R.id.first_name);
        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        EditText confirmPasswordField = findViewById(R.id.confirm_password);
        ImageButton registerButton = findViewById(R.id.register_button);
        ImageButton closeButton = findViewById(R.id.close_button);

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

        // Bouton d'inscription
        registerButton.setOnClickListener(v -> {
            String firstName = firstNameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            // Validation des champs
            if (firstName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inscription avec Firebase
            Log.d(TAG, "Tentative d'inscription avec email : " + email);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Inscription réussie pour l'utilisateur : " + email);
                            Toast.makeText(RegisterActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Échec de l'inscription", task.getException());
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Erreur inconnue";
                            Toast.makeText(RegisterActivity.this, "Échec de l'inscription : " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}