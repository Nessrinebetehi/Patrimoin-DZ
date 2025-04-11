package com.example.patrimoin_dz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatConversationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText messageInput;
    private ImageButton sendButton;
    private ImageButton attachImageButton;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private String userName;

    // Launcher pour sélectionner une image
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Ajouter l'image sélectionnée à la conversation
                    messageList.add(new Message(uri, true));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);

        // Récupérer le nom de l'utilisateur depuis l'intent
        userName = getIntent().getStringExtra("USER_NAME");

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(userName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Views
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        attachImageButton = findViewById(R.id.attachImageButton);

        // Setup Messages RecyclerView
        setupMessagesRecyclerView();

        // Setup Send Button
        sendButton.setOnClickListener(v -> sendMessage());

        // Setup Attach Image Button
        attachImageButton.setOnClickListener(v -> pickImage());
    }

    private void setupMessagesRecyclerView() {
        messageList = new ArrayList<>();
        // Charger des messages différents selon l'utilisateur (exemple)
        if (userName.equals("Blondinelle")) {
            messageList.add(new Message("Qu'y a-t-il sur votre playlist ?", false));
            messageList.add(new Message("J'écoute du jazz en ce moment, et toi ?", true));
        } else if (userName.equals("cutty girls")) {
            messageList.add(new Message("Blondinelle - ph...", false));
            messageList.add(new Message("Haha, trop drôle !", true));
        } else if (userName.equals("chaimae")) {
            messageList.add(new Message("Salut, ça va ?", false));
            messageList.add(new Message("Oui, et toi ?", true));
        } else if (userName.equals("zahra")) {
            messageList.add(new Message("On se voit demain ?", false));
            messageList.add(new Message("Oui, à quelle heure ?", true));
        } else if (userName.equals("عزيزة عين")) {
            messageList.add(new Message("كيف حالك؟", false));
            messageList.add(new Message("بخير، وأنت؟", true));
        } else if (userName.equals("Bouchra Nh")) {
            messageList.add(new Message("Coucou !", false));
            messageList.add(new Message("Salut, quoi de neuf ?", true));
        }

        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            messageList.add(new Message(messageText, true));
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
            messageInput.setText("");
            // TODO: Ajouter la logique pour envoyer le message à un serveur ou une base de données
        }
    }

    private void pickImage() {
        // Lancer l'intent pour sélectionner une image depuis la galerie
        pickImageLauncher.launch("image/*");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}