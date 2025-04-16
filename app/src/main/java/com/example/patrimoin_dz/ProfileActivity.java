package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem lastSelectedItem;
    private ImageButton chatButton, profileButton, notificationButton;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private UserAdapter userAdapter;
    private List<Post> postList;
    private List<User> userList;
    private ListenerRegistration postsListener;
    private SearchView searchView;
    private boolean isSearchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.posts_recycler_view);
        searchView = findViewById(R.id.search_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            if (lastSelectedItem != null) {
                lastSelectedItem.setChecked(false);
            }
            item.setChecked(true);
            lastSelectedItem = item;
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        navigationView.setCheckedItem(R.id.nav_home);
        lastSelectedItem = navigationView.getMenu().findItem(R.id.nav_home);

        chatButton = findViewById(R.id.chat_button);
        profileButton = findViewById(R.id.profile_button);
        notificationButton = findViewById(R.id.notification_button);

        chatButton.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));
        notificationButton.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));

        postList = new ArrayList<>();
        userList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, null, true);
        userAdapter = new UserAdapter(userList, this::sendFriendRequest);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(postAdapter);

        // Ajouter un écouteur pour le bouton "Add Friend" dans PostAdapter
        postAdapter.setAddFriendListener(post -> sendFriendRequest(new User(post.getUserName(), post.getUserName(), post.getUserProfileImageUrl(), post.getUserId())));

        setupSearchView();
        loadAllPosts();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText == null || newText.trim().isEmpty()) {
                    isSearchMode = false;
                    recyclerView.setAdapter(postAdapter);
                    loadAllPosts();
                } else {
                    isSearchMode = true;
                    searchUsers(newText);
                }
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            isSearchMode = false;
            recyclerView.setAdapter(postAdapter);
            loadAllPosts();
            return false;
        });
    }

    private void searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            isSearchMode = false;
            recyclerView.setAdapter(postAdapter);
            loadAllPosts();
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(userAdapter);
        userList.clear();
        userAdapter.notifyDataSetChanged();

        db.collection("users")
                .whereGreaterThanOrEqualTo("username", query)
                .whereLessThanOrEqualTo("username", query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    userList.clear();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    String currentUserId = currentUser != null ? currentUser.getUid() : "";
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        String userId = doc.getId();
                        user.setUid(userId);
                        if (userId != null && !userId.isEmpty() && !userId.equals(currentUserId) &&
                                user.getUsername() != null && !user.getUsername().isEmpty()) {
                            userList.add(user);
                        } else {
                            Log.d(TAG, "Skipped user: ID=" + userId + ", Username=" + user.getUsername());
                        }
                    }
                    userAdapter.notifyDataSetChanged();
                    if (userList.isEmpty()) {
                        Toast.makeText(ProfileActivity.this, "Aucun utilisateur trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Erreur recherche utilisateurs: " + e.getMessage(), e);
                    Toast.makeText(ProfileActivity.this, "Erreur recherche utilisateurs", Toast.LENGTH_SHORT).show();
                });
    }

    private void sendFriendRequest(User user) {
        if (user == null || user.getUid() == null || user.getUsername() == null) {
            Toast.makeText(this, "Utilisateur invalide", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }
        String currentUserId = currentUser.getUid();

        // Vérifier si une demande existe déjà
        db.collection("friend_requests")
                .whereEqualTo("senderId", currentUserId)
                .whereEqualTo("recipientId", user.getUid())
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "Demande déjà envoyée", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Récupérer le nom d'utilisateur de l'expéditeur
                    db.collection("users").document(currentUserId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String senderUsername = documentSnapshot.getString("username");
                                    if (senderUsername == null || senderUsername.isEmpty()) {
                                        senderUsername = "Utilisateur inconnu";
                                    }

                                    // Créer la demande d'ami
                                    Map<String, Object> request = new HashMap<>();
                                    request.put("senderId", currentUserId);
                                    request.put("senderUsername", senderUsername);
                                    request.put("recipientId", user.getUid());
                                    request.put("recipientUsername", user.getUsername());
                                    request.put("status", "pending");
                                    request.put("timestamp", System.currentTimeMillis());

                                    db.collection("friend_requests")
                                            .add(request)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(this, "Demande d'ami envoyée à " + user.getUsername(), Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e(TAG, "Erreur envoi demande d'ami: " + e.getMessage(), e);
                                                Toast.makeText(this, "Erreur envoi demande d'ami", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Erreur récupération nom utilisateur: " + e.getMessage(), e);
                                Toast.makeText(this, "Erreur récupération profil", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur vérification demande existante: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur vérification demande", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadAllPosts() {
        if (isSearchMode) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        postsListener = db.collection("posts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Log.e(TAG, "Erreur chargement posts: " + error.getMessage(), error);
                        Toast.makeText(this, "Impossible de charger les publications", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    postList.clear();
                    if (value != null && !value.isEmpty()) {
                        for (QueryDocumentSnapshot doc : value) {
                            Post post = doc.toObject(Post.class);
                            if (post != null && post.getUserId() != null) {
                                post.setPostId(doc.getId());
                                db.collection("users").document(post.getUserId())
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            if (userDoc.exists()) {
                                                String userName = userDoc.getString("username");
                                                String profileImageUrl = userDoc.getString("profilePictureUrl");
                                                post.setUserName(userName != null ? userName : "Utilisateur inconnu");
                                                post.setUserProfileImageUrl(profileImageUrl);
                                                post.setUserId(post.getUserId());
                                                postAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e(TAG, "Erreur récupération nom utilisateur: " + e.getMessage(), e));
                                postList.add(post);
                            }
                        }
                        Log.d(TAG, "Posts chargés : " + postList.size());
                    } else {
                        Log.d(TAG, "Aucune publication trouvée");
                        Toast.makeText(this, "Aucune publication disponible", Toast.LENGTH_SHORT).show();
                    }
                    postAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postsListener != null) {
            postsListener.remove();
        }
    }
}