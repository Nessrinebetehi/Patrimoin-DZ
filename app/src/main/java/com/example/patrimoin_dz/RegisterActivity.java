package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar; // Add a ProgressBar to your layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        LinearLayout formContainer = findViewById(R.id.form_container);
        Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        formContainer.startAnimation(slideDown);

        EditText firstNameField = findViewById(R.id.first_name);
        EditText emailField = findViewById(R.id.email);
        EditText passwordField = findViewById(R.id.password);
        EditText confirmPasswordField = findViewById(R.id.confirm_password);
        ImageButton registerButton = findViewById(R.id.register_button);
        ImageButton closeButton = findViewById(R.id.close_button);
        progressBar = findViewById(R.id.progress_bar); // Add this to your layout

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

        registerButton.setOnClickListener(v -> {
            String firstName = firstNameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (firstName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show loading indicator
            progressBar.setVisibility(android.view.View.VISIBLE);
            registerButton.setEnabled(false); // Prevent multiple clicks

            Log.d(TAG, "Tentative d'inscription avec email: " + email);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            User user = new User(email.split("@")[0], firstName, "", 0, 0, 0);
                            db.collection("users").document(userId).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Utilisateur ajouté à Firestore avec UID: " + userId);
                                        Toast.makeText(this, "Inscription réussie !", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(this, ProfileActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Erreur Firestore : " + e.getMessage());
                                        Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } else {
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthUserCollisionException) {
                                Log.e(TAG, "Collision détectée : " + exception.getMessage());
                                Toast.makeText(this, "Cet email est déjà utilisé avec un autre mot de passe. Essayez un mot de passe différent ou connectez-vous.", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "Échec de l'inscription : " + (exception != null ? exception.getMessage() : "Erreur inconnue"), exception);
                                String errorMessage = exception != null ? exception.getMessage() : "Erreur inconnue";
                                Toast.makeText(this, "Échec de l'inscription : " + errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                        // Hide loading indicator
                        progressBar.setVisibility(android.view.View.GONE);
                        registerButton.setEnabled(true);
                    });
        });
    }
}