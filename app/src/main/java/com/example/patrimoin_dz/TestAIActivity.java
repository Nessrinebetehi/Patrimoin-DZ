package com.example.patrimoin_dz;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestAIActivity extends AppCompatActivity {
    private static final String TAG = "TestAIActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_ai);

        GrokApiService apiService = RetrofitClient.getService();
        GrokRequest request = new GrokRequest("What is the Casbah?");
        Call<GrokResponse> call = apiService.getChatCompletion(request);

        call.enqueue(new Callback<GrokResponse>() {
            @Override
            public void onResponse(Call<GrokResponse> call, Response<GrokResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Response: " + response.body().getResponseText());
                } else {
                    Log.e(TAG, "Error code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<GrokResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage());
            }
        });
    }
}