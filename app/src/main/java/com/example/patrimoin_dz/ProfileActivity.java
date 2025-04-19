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
import java.util.concurrent.atomic.AtomicReference;

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

        AtomicReference<FirebaseUser> currentUser = new AtomicReference<>(mAuth.getCurrentUser());
        if (currentUser.get() == null) {
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

        postAdapter.setCommentClickListener(post -> {
            if (post.getPostId() == null) {
                Log.e(TAG, "Comment button clicked but postId is null");
                Toast.makeText(this, "Erreur : ID de publication manquant", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Comment button clicked for post: " + post.getPostId());
            CommentsBottomSheet commentsBottomSheet = CommentsBottomSheet.newInstance(post.getPostId());
            try {
                commentsBottomSheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
                Log.d(TAG, "CommentsBottomSheet opened successfully for post: " + post.getPostId());
            } catch (Exception e) {
                Log.e(TAG, "Error opening CommentsBottomSheet: " + e.getMessage(), e);
                Toast.makeText(this, "Erreur lors de l'ouverture des commentaires", Toast.LENGTH_SHORT).show();
            }
        });

        postAdapter.setShareClickListener(post -> {
            if (post.getPostId() == null) {
                Log.e(TAG, "Share button clicked but postId is null");
                Toast.makeText(this, "Erreur : ID de publication manquant", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Share button clicked for post: " + post.getPostId());
            ShareBottomSheet shareBottomSheet = ShareBottomSheet.newInstance(post.getPostId());
            try {
                shareBottomSheet.show(getSupportFragmentManager(), "ShareBottomSheet");
                Log.d(TAG, "ShareBottomSheet opened successfully for post: " + post.getPostId());
            } catch (Exception e) {
                Log.e(TAG, "Error opening ShareBottomSheet: " + e.getMessage(), e);
                Toast.makeText(this, "Erreur lors de l'ouverture du partage", Toast.LENGTH_SHORT).show();
            }
        });

        postAdapter.setAddFriendListener(post -> {
            if (post.getUserId() == null || post.getUserName() == null) {
                Log.e(TAG, "Add friend button clicked but userId or userName is null");
                Toast.makeText(this, "Erreur : Informations utilisateur manquantes", Toast.LENGTH_SHORT).show();
                return;
            }
            sendFriendRequest(new User(post.getUserName(), post.getUserName(), post.getUserProfileImageUrl(), post.getUserId()));
        });

        postAdapter.setLikeClickListener((post, reactionType) -> {
            if (post.getPostId() == null) {
                Log.e(TAG, "Like button clicked but postId is null");
                Toast.makeText(this, "Erreur : ID de publication manquant", Toast.LENGTH_SHORT).show();
                return;
            }
            currentUser.set(mAuth.getCurrentUser());
            if (currentUser.get() == null) {
                Log.e(TAG, "likePost: User not logged in");
                Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
                return;
            }
            String currentUserId = currentUser.get().getUid();

            int postPosition = postList.indexOf(post);
            if (postPosition == -1) {
                Log.e(TAG, "likePost: Post not found in postList");
                return;
            }

            db.collection("posts")
                    .document(post.getPostId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Map<String, List<String>> reactions = (Map<String, List<String>>) documentSnapshot.get("reactions");
                        if (reactions == null) {
                            reactions = new HashMap<>();
                        }
                        List<String> reactionUsers = reactions.getOrDefault(reactionType, new ArrayList<>());
                        boolean isReacted = reactionUsers.contains(currentUserId);
                        if (isReacted) {
                            reactionUsers.remove(currentUserId);
                            Toast.makeText(this, "Réaction supprimée", Toast.LENGTH_SHORT).show();
                        } else {
                            for (List<String> users : reactions.values()) {
                                users.remove(currentUserId);
                            }
                            reactionUsers.add(currentUserId);
                            Toast.makeText(this, "Réaction ajoutée: " + reactionType, Toast.LENGTH_SHORT).show();
                            createReactionNotification(currentUserId, post, reactionType);
                        }
                        reactions.put(reactionType, reactionUsers);

                        Map<String, List<String>> finalReactions = reactions;
                        db.collection("posts")
                                .document(post.getPostId())
                                .update("reactions", reactions)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Post reaction updated for post " + post.getPostId());
                                    post.setReactions(finalReactions);
                                    postAdapter.notifyItemChanged(postPosition);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to update reaction: " + e.getMessage(), e);
                                    Toast.makeText(this, "Erreur lors de la mise à jour de la réaction", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to fetch post: " + e.getMessage(), e);
                        Toast.makeText(this, "Erreur lors de la récupération de la publication", Toast.LENGTH_SHORT).show();
                    });
        });

        setupSearchView();
        loadAllPosts();
    }

    private void createReactionNotification(String senderId, Post post, String reactionType) {
        Map<String, String> reactionEmojis = new HashMap<>();
        reactionEmojis.put("like", "👍");
        reactionEmojis.put("love", "❤️");
        reactionEmojis.put("haha", "😂");
        reactionEmojis.put("wow", "😮");
        reactionEmojis.put("sad", "😢");
        reactionEmojis.put("angry", "😣");

        String emoji = reactionEmojis.get(reactionType);
        if (emoji == null) {
            Log.e(TAG, "Invalid reaction type: " + reactionType);
            return;
        }

        db.collection("users").document(senderId)
                .get()
                .addOnSuccessListener(doc -> {
                    String senderUsername = doc.getString("username");
                    if (senderUsername == null) {
                        senderUsername = "Utilisateur inconnu";
                    }
                    String postOwnerId = post.getUserId();
                    if (postOwnerId != null && !postOwnerId.equals(senderId)) {
                        Map<String, Object> notificationData = new HashMap<>();
                        notificationData.put("type", "reaction");
                        notificationData.put("message", senderUsername + " a réagi à votre publication avec " + emoji);
                        notificationData.put("timestamp", System.currentTimeMillis());
                        notificationData.put("recipientId", postOwnerId);
                        notificationData.put("senderId", senderId);
                        notificationData.put("postId", post.getPostId());
                        notificationData.put("emoji", emoji);

                        db.collection("notifications")
                                .add(notificationData)
                                .addOnSuccessListener(ref -> Log.d(TAG, "Reaction notification created for post " + post.getPostId()))
                                .addOnFailureListener(e -> Log.e(TAG, "Failed to create reaction notification: " + e.getMessage(), e));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch sender username: " + e.getMessage(), e));
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

                    db.collection("users").document(currentUserId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    String senderUsername = documentSnapshot.getString("username");
                                    if (senderUsername == null || senderUsername.isEmpty()) {
                                        senderUsername = "Utilisateur inconnu";
                                    }

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
                                Map<String, List<String>> reactions = (Map<String, List<String>>) doc.get("reactions");
                                if (reactions != null) {
                                    post.setReactions(reactions);
                                } else {
                                    post.setReactions(new HashMap<>());
                                }
                                db.collection("users").document(post.getUserId())
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            if (userDoc.exists()) {
                                                String userName = userDoc.getString("username");
                                                String profileImageUrl = userDoc.getString("profilePictureUrl");
                                                post.setUserName(userName != null ? userName : "Utilisateur inconnu");
                                                post.setUserProfileImageUrl(profileImageUrl);
                                                post.setUserId(post.getUserId());
                                                int postPosition = postList.indexOf(post);
                                                if (postPosition != -1) {
                                                    postAdapter.notifyItemChanged(postPosition);
                                                }
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e(TAG, "Erreur récupération nom utilisateur: " + e.getMessage(), e));
                                postList.add(post);
                            }
                        }
                        Log.d(TAG, "Posts chargés : " + postList.size());
                        postAdapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Aucune publication trouvée");
                        Toast.makeText(this, "Aucune publication disponible", Toast.LENGTH_SHORT).show();
                    }
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