package com.example.patrimoin_dz;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GrokApiService {
    @Headers("Content-Type: application/json")
    @POST("chat/completions")
    Call<GrokResponse> getChatCompletion(@Body GrokRequest request);
}