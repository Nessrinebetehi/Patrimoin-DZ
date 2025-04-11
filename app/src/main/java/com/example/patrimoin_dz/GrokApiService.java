package com.example.patrimoin_dz;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GrokApiService {
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    Call<GrokResponse> getChatCompletion(@Body GrokRequest request);
}