package com.example.patrimoin_dz;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";

    private RecyclerView notificationsRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting NotificationsActivity");
        setContentView(R.layout.activity_notifications);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Handle back button click
        toolbar.setNavigationOnClickListener(v -> {
            Log.d(TAG, "onNavigationClick: Back button clicked");
            finish();
        });

        // Initialize RecyclerView
        notificationsRecyclerView = findViewById(R.id.notifications_recycler_view);
        if (notificationsRecyclerView == null) {
            Log.e(TAG, "notificationsRecyclerView is null! Check your layout for R.id.notifications_recycler_view");
            return;
        }

        // Setup Notifications RecyclerView
        notificationList = new ArrayList<>();
        // Ajouter plusieurs notifications fictives pour tester
        notificationList.add(new Notification("Amina a aimé votre publication", "Il y a 1 minute"));
        notificationList.add(new Notification("Karim a commenté votre photo", "Il y a 15 minutes"));
        notificationList.add(new Notification("Sofia vous a envoyé une demande d'ami", "Il y a 1 heure"));
        notificationList.add(new Notification("Yanis a partagé votre publication", "Il y a 2 heures"));
        notificationList.add(new Notification("Nadia a aimé votre commentaire", "Il y a 3 heures"));
        notificationList.add(new Notification("Samir vous a mentionné dans un post", "Il y a 5 heures"));
        notificationList.add(new Notification("Lina a réagi à votre story", "Il y a 1 jour"));
        notificationList.add(new Notification("Omar a rejoint votre groupe", "Il y a 2 jours"));
        notificationList.add(new Notification("Fatima a aimé votre publication", "Il y a 3 jours"));
        notificationList.add(new Notification("Rayan vous a envoyé un message", "Il y a 4 jours"));

        notificationAdapter = new NotificationAdapter(notificationList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        notificationsRecyclerView.setLayoutManager(layoutManager);
        notificationsRecyclerView.setAdapter(notificationAdapter);

        // Ajouter un DividerItemDecoration pour séparer les notifications
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(notificationsRecyclerView.getContext(),
                layoutManager.getOrientation());
        notificationsRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}