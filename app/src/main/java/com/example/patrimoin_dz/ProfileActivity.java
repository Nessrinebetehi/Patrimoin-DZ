package com.example.patrimoin_dz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem lastSelectedItem;
    private ImageButton chatButton;
    private ImageButton profileButton;
    private ImageButton notificationButton;
    private RecyclerView storiesRecyclerView;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private Button likeButton, commentButton, shareButton, addFriendButton;
    private SearchView searchView;
    private LinearLayout reactionBar;
    private TextView reactionLike, reactionLove, reactionHaha, reactionWow, reactionSad, reactionAngry;
    private RecyclerView postImagesRecyclerView;
    private PostImageAdapter postImageAdapter;
    private List<String> postImageList;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starting ProfileActivity");
        try {
            setContentView(R.layout.activity_profile);
        } catch (Exception e) {
            Log.e(TAG, "Error inflating layout: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading layout: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialiser Firebase Auth et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Vérifier si l'utilisateur est connecté
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "No user logged in, redirecting to LoginActivity");
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Récupérer postId depuis l'Intent
        postId = getIntent().getStringExtra("post_id");

        // Initialiser Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialiser DrawerLayout et NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Ajouter le bouton hamburger pour ouvrir la sidebar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Gérer les clics sur les éléments du menu
        navigationView.setNavigationItemSelectedListener(item -> {
            if (lastSelectedItem != null) {
                lastSelectedItem.setChecked(false);
            }
            item.setChecked(true);
            lastSelectedItem = item;

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(ProfileActivity.this, "Home selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_musique) {
                Toast.makeText(ProfileActivity.this, "Algerian Music selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, MusicActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_vetements) {
                Toast.makeText(ProfileActivity.this, "Algerian Clothing selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, ClothingActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_traditions) {
                Toast.makeText(ProfileActivity.this, "Algerian Traditions selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, TraditionsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_cuisine) {
                Toast.makeText(ProfileActivity.this, "Algerian Cuisine selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, CuisineActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_architecture) {
                Toast.makeText(ProfileActivity.this, "Architecture and Historical Sites selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, ArchitectureActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_linguistique) {
                Toast.makeText(ProfileActivity.this, "Linguistic and Literary Heritage selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LinguisticActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_fetes) {
                Toast.makeText(ProfileActivity.this, "Festivals and Celebrations selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, FestivalsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_patrAI) {
                Toast.makeText(ProfileActivity.this, "PatrAI selected", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, PatrAIActivity.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Définir "Home" comme élément sélectionné par défaut
        navigationView.setCheckedItem(R.id.nav_home);
        lastSelectedItem = navigationView.getMenu().findItem(R.id.nav_home);

        // Initialiser les vues
        chatButton = findViewById(R.id.chat_button);
        profileButton = findViewById(R.id.profile_button);
        notificationButton = findViewById(R.id.notification_button);
        storiesRecyclerView = findViewById(R.id.stories_recycler_view);
        likeButton = findViewById(R.id.like_button);
        commentButton = findViewById(R.id.comment_button);
        shareButton = findViewById(R.id.share_button);
        addFriendButton = findViewById(R.id.add_friend_button);
        searchView = findViewById(R.id.search_view);
        reactionBar = findViewById(R.id.reaction_bar);
        reactionLike = findViewById(R.id.reaction_like);
        reactionLove = findViewById(R.id.reaction_love);
        reactionHaha = findViewById(R.id.reaction_haha);
        reactionWow = findViewById(R.id.reaction_wow);
        reactionSad = findViewById(R.id.reaction_sad);
        reactionAngry = findViewById(R.id.reaction_angry);
        postImagesRecyclerView = findViewById(R.id.post_images_recycler_view);


// Configurer les listeners pour les boutons
        chatButton.setOnClickListener(v -> {
            Log.d(TAG, "Chat button clicked!");
            Toast.makeText(ProfileActivity.this, "Chat clicked!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Log.d(TAG, "Profile button clicked!");
            Toast.makeText(ProfileActivity.this, "Profile clicked!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        notificationButton.setOnClickListener(v -> {
            Log.d(TAG, "Notification button clicked!");
            Toast.makeText(ProfileActivity.this, "Opening Notifications!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        // Charger les stories depuis Firestore
        storyList = new ArrayList<>();
        loadStories();

        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        storyAdapter = new StoryAdapter(storyList);
        storiesRecyclerView.setAdapter(storyAdapter);

        // Charger les images de la publication
        postImageList = new ArrayList<>();
        loadPostImages();

        postImagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        postImageAdapter = new PostImageAdapter(postImageList);
        postImagesRecyclerView.setAdapter(postImageAdapter);

        // Gérer les interactions avec la publication
        likeButton.setOnClickListener(v -> {
            if (reactionBar.getVisibility() == View.VISIBLE) {
                reactionBar.animate()
                        .translationY(50f)
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> reactionBar.setVisibility(View.GONE))
                        .start();
            } else {
                reactionBar.setVisibility(View.VISIBLE);
                reactionBar.setTranslationY(50f);
                reactionBar.setAlpha(0f);
                reactionBar.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(200)
                        .start();

                TextView[] emojis = {reactionLike, reactionLove, reactionHaha, reactionWow, reactionSad, reactionAngry};
                for (int i = 0; i < emojis.length; i++) {
                    emojis[i].setScaleX(0f);
                    emojis[i].setScaleY(0f);
                    emojis[i].animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(300)
                            .setStartDelay(i * 50L)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                }
            }
        });

        likeButton.setOnLongClickListener(v -> {
            Toast.makeText(ProfileActivity.this, "Liked!", Toast.LENGTH_SHORT).show();
            reactionBar.animate()
                    .translationY(50f)
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction(() -> reactionBar.setVisibility(View.GONE))
                    .start();
            likeButton.setText("👍");
            likeButton.setTextSize(16);
            return true;
        });

        reactionLike.setOnClickListener(v -> handleReaction("👍"));
        reactionLove.setOnClickListener(v -> handleReaction("❤️"));
        reactionHaha.setOnClickListener(v -> handleReaction("😂"));
        reactionWow.setOnClickListener(v -> handleReaction("😮"));
        reactionSad.setOnClickListener(v -> handleReaction("😢"));
        reactionAngry.setOnClickListener(v -> handleReaction("😡"));


        commentButton.setOnClickListener(v -> {
            CommentsBottomSheet commentsBottomSheet = CommentsBottomSheet.newInstance(postId);
            commentsBottomSheet.show(getSupportFragmentManager(), commentsBottomSheet.getTag());
        });

        shareButton.setOnClickListener(v -> {
            ShareBottomSheet shareBottomSheet = ShareBottomSheet.newInstance(postId);
            shareBottomSheet.show(getSupportFragmentManager(), shareBottomSheet.getTag());
        });

        addFriendButton.setOnClickListener(v -> {
            String targetUserId = "exampleUserId"; // Remplace par l'ID réel de l'utilisateur cible
            addFriend(currentUser.getUid(), targetUserId);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void handleReaction(String emoji) {
        Toast.makeText(ProfileActivity.this, "Reaction: " + emoji, Toast.LENGTH_SHORT).show();
        reactionBar.animate()
                .translationY(50f)
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> reactionBar.setVisibility(View.GONE))
                .start();
        likeButton.setText(emoji);
        likeButton.setTextSize(16);
    }

    private void loadStories() {
        // Charger les stories depuis Firestore (par exemple, les amis de l'utilisateur)
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("friends")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Friend friend = doc.toObject(Friend.class);
                        storyList.add(new Story(friend.getUserName()));
                    }
                    storyAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur lors du chargement des stories : " + e.getMessage());
                    Toast.makeText(ProfileActivity.this, "Erreur lors du chargement des stories", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadPostImages() {
        if (postId != null) {
            db.collection("posts").document(postId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Post post = documentSnapshot.toObject(Post.class);
                        if (post != null && post.getImageUrl() != null) {
                            postImageList.add(post.getImageUrl());
                            postImageAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erreur lors du chargement des images de la publication : " + e.getMessage());
                    });
        }
    }

    private void addFriend(String currentUserId, String targetUserId) {
        db.collection("users").document(currentUserId)
                .collection("friends").document(targetUserId)
                .set(new Friend(targetUserId, "Utilisateur")) // Remplace "Utilisateur" par le vrai nom
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Ami ajouté !", Toast.LENGTH_SHORT).show();
                    addFriendButton.setText("Ami ajouté");
                    addFriendButton.setEnabled(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Erreur lors de l'ajout de l'ami : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void searchUsers(String query) {
        db.collection("users")
                .whereGreaterThanOrEqualTo("username", query)
                .whereLessThanOrEqualTo("username", query + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> userNames = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("username");
                        if (name != null) {
                            userNames.add(name);
                        }
                    }
                    Toast.makeText(ProfileActivity.this, "Utilisateurs trouvés : " + userNames.toString(), Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Recherche échouée : " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
}