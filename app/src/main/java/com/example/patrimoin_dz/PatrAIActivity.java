package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatrAIActivity extends AppCompatActivity {

    private static final String TAG = "PatrAIActivity";
    private DrawerLayout drawerLayout;
    private EditText questionInput;
    private Button submitButton;
    private TextView responseText;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_patr_ai);

            // Configuration de la Toolbar
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            // Configuration du DrawerLayout et NavigationView
            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(PatrAIActivity.this, HomeActivity.class));
                } else if (id == R.id.nav_musique) {
                    startActivity(new Intent(PatrAIActivity.this, MusicActivity.class));
                } else if (id == R.id.nav_vetements) {
                    startActivity(new Intent(PatrAIActivity.this, ClothingActivity.class));
                } else if (id == R.id.nav_traditions) {
                    startActivity(new Intent(PatrAIActivity.this, TraditionsActivity.class));
                } else if (id == R.id.nav_cuisine) {
                    startActivity(new Intent(PatrAIActivity.this, CuisineActivity.class));
                } else if (id == R.id.nav_architecture) {
                    startActivity(new Intent(PatrAIActivity.this, ArchitectureActivity.class));
                } else if (id == R.id.nav_linguistique) {
                    startActivity(new Intent(PatrAIActivity.this, LinguisticActivity.class));
                } else if (id == R.id.nav_fetes) {
                    startActivity(new Intent(PatrAIActivity.this, FestivalsActivity.class));
                } else if (id == R.id.nav_patrAI) {
                    // Déjà ici
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });

            // Initialisation des éléments UI
            questionInput = findViewById(R.id.question_input);
            submitButton = findViewById(R.id.submit_button);
            responseText = findViewById(R.id.response_text);
            loadingIndicator = findViewById(R.id.loading_indicator);

            // Gestion du clic sur le bouton "Ask PatrAI"
            submitButton.setOnClickListener(v -> {
                String question = questionInput.getText().toString().trim();
                if (!question.isEmpty()) {
                    fetchAIResponse(question);
                } else {
                    responseText.setText("Please enter a question!");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Crash in onCreate: " + e.getMessage(), e);
            responseText.setText("Application crashed: " + e.getMessage());
        }
    }

    private void fetchAIResponse(String question) {
        try {
            loadingIndicator.setVisibility(View.VISIBLE);
            responseText.setText("Waiting for PatrAI's response...");
            Log.d(TAG, "Sending question: " + question);

            GrokApiService apiService = RetrofitClient.getService();
            GrokRequest request = new GrokRequest(question);
            Call<GrokResponse> call = apiService.getChatCompletion(request);

            call.enqueue(new Callback<GrokResponse>() {
                @Override
                public void onResponse(Call<GrokResponse> call, Response<GrokResponse> response) {
                    loadingIndicator.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        String answer = response.body().getResponseText();
                        responseText.setText(answer);
                        Log.d(TAG, "Response received: " + answer);
                    } else {
                        String errorMsg = "Error: Unable to get a response from PatrAI. HTTP Code: " + response.code();
                        if (response.errorBody() != null) {
                            try {
                                errorMsg += " - " + response.errorBody().string();
                            } catch (Exception e) {
                                Log.e(TAG, "Error reading error body: " + e.getMessage());
                            }
                        }
                        responseText.setText(errorMsg);
                        Log.e(TAG, errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<GrokResponse> call, Throwable t) {
                    loadingIndicator.setVisibility(View.GONE);
                    String errorMsg = "Network error: " + t.getMessage();
                    responseText.setText(errorMsg);
                    Log.e(TAG, errorMsg);
                }
            });
        } catch (Exception e) {
            loadingIndicator.setVisibility(View.GONE);
            Log.e(TAG, "Crash in fetchAIResponse: " + e.getMessage(), e);
            responseText.setText("Error during API call: " + e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}