package com.example.patrimoin_dz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChats;
    private RecyclerView storiesRecyclerView;
    private SearchView searchView;
    private ChatAdapter chatAdapter;
    private StoriesAdapter storiesAdapter;
    private List<Chat> chatList;
    private List<ChatStory> storiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize RecyclerViews and SearchView
        recyclerViewChats = findViewById(R.id.recyclerViewChats);
        storiesRecyclerView = findViewById(R.id.storiesRecyclerView);
        searchView = findViewById(R.id.searchView);

        // Setup Stories RecyclerView
        setupStoriesRecyclerView();

        // Setup Chats RecyclerView
        setupChatsRecyclerView();

        // Configure SearchView
        setupSearchView();
    }

    private void setupStoriesRecyclerView() {
        storiesList = new ArrayList<>();
        // Ajoutez des données de stories si elles existent
        // Exemple : storiesList.add(new ChatStory("User1", R.drawable.profile1));
        storiesAdapter = new StoriesAdapter(storiesList);
        storiesRecyclerView.setAdapter(storiesAdapter);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Si aucune story n'existe, vous pouvez masquer le RecyclerView
        if (storiesList.isEmpty()) {
            storiesRecyclerView.setVisibility(RecyclerView.GONE);
        }
    }

    private void setupChatsRecyclerView() {
        chatList = new ArrayList<>();
        // Ajoutez des données de conversations (exemple)
        chatList.add(new Chat("Blondinelle", "Qu'y a-t-il sur votre playlist ?", 4, "4j", R.drawable.ic_profile_placeholder));
        chatList.add(new Chat("cutty girls", "Blondinelle - ph...", 0, "serr", R.drawable.ic_profile_placeholder));
        chatList.add(new Chat("chaimae", "+ de 4 nouveaux ...", 4, "3 sem", R.drawable.ic_profile_placeholder));
        chatList.add(new Chat("zahra", "+ de 4 nouveaux ...", 4, "3 sem", R.drawable.ic_profile_placeholder));
        chatList.add(new Chat("عزيزة عين", "+ de 4 nouveaux ...", 4, "4 sem", R.drawable.ic_profile_placeholder));
        chatList.add(new Chat("Bouchra Nh", "", 0, "", R.drawable.ic_profile_placeholder));

        chatAdapter = new ChatAdapter(chatList);
        recyclerViewChats.setAdapter(chatAdapter);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterChats(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterChats(newText);
                return true;
            }
        });
    }

    private void filterChats(String query) {
        List<Chat> filteredList = new ArrayList<>();
        for (Chat chat : chatList) {
            if (chat.getUserName().toLowerCase().contains(query.toLowerCase()) ||
                    chat.getLastMessage().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(chat);
            }
        }
        chatAdapter.updateList(filteredList);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}