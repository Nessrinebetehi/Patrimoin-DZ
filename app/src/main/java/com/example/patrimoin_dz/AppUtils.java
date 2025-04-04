package com.example.patrimoin_dz;

import android.app.Activity;
import android.widget.Toast;

public class AppUtils {

    public static void exitApp(Activity activity) {
        Toast.makeText(activity, "Exiting the app", Toast.LENGTH_SHORT).show();
        activity.finishAffinity(); // Ferme toutes les activités et quitte l'application
    }

    // Option avec confirmation (si tu veux l'ajouter plus tard)
    public static void exitAppWithConfirmation(Activity activity) {
        new android.app.AlertDialog.Builder(activity)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(activity, "Exiting the app", Toast.LENGTH_SHORT).show();
                    activity.finishAffinity();
                })
                .setNegativeButton("No", null)
                .show();
    }
}