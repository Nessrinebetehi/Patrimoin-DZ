package com.example.patrimoin_dz;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "https://api.x.ai/v1/";
    private static final String API_KEY = "your_new_api_key_here"; // Replace with the actual key

    public static GrokApiService getService() {
        // Create a logging interceptor
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Log request and response body

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + API_KEY)
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(logging) // Add the logging interceptor
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(GrokApiService.class);
    }
}