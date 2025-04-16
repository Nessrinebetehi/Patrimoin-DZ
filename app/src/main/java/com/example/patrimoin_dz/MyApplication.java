package com.example.patrimoin_dz;

import android.app.Application;
import android.util.Log;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            // Log package name
            Log.d(TAG, "Package name: " + getPackageName());
            // Check Google Play Services
            int playServicesStatus = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
            Log.d(TAG, "Google Play Services status: " + playServicesStatus);
            Log.d(TAG, "Google Play Services version: " + GoogleApiAvailability.getInstance().getClientVersion(this));
            // Initialize Firebase
            FirebaseApp.initializeApp(this);
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Initialization error: " + e.getMessage(), e);
        }
    }
}