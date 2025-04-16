package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class AccountsListActivity extends AppCompatActivity {

    private static final String TAG = "AccountsListActivity";
    private RecyclerView accountsRecyclerView;
    private Button createAccountButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<User> accountsList;
    private AccountsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_list);

        try {
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
        } catch (Exception e) {
            Log.e(TAG, "Erreur initialisation Firebase : ", e);
            Toast.makeText(this, "Erreur Firebase : " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        accountsRecyclerView = findViewById(R.id.accounts_recycler_view);
        createAccountButton = findViewById(R.id.create_account_button);
        accountsList = new ArrayList<>();
        adapter = new AccountsAdapter(accountsList, this::switchAccount);

        accountsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        accountsRecyclerView.setAdapter(adapter);

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Utilisateur non authentifié. Veuillez vous connecter.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        createAccountButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountsListActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loadAccounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            loadAccounts();
        }
    }

    private void loadAccounts() {
        db.collection("users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    accountsList.clear();
                    for (var doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        accountsList.add(user);
                    }
                    adapter.notifyDataSetChanged();
                    if (accountsList.isEmpty()) {
                        Toast.makeText(this, "Aucun compte trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur chargement comptes : ", e);
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void switchAccount(User selectedUser) {
        mAuth.signOut();
        Toast.makeText(this, "Déconnexion de l'utilisateur actuel", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("selected_username", selectedUser.getUsername());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}