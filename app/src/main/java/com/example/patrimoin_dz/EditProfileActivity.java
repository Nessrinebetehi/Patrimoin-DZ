package com.example.patrimoin_dz;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity implements PostAdapter.OnPostDeleteListener {

    private static final String TAG = "EditProfileActivity";
    private TextView usernameTextView, displayNameTextView, postsCountTextView, followersCountTextView, followingCountTextView;
    private ImageView profilePictureImageView;
    private RecyclerView postsRecyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private User user;
    private Uri selectedPostImageUri;
    private ListenerRegistration postsListener;
    private ListenerRegistration userListener;

    private final ActivityResultLauncher<Intent> pickProfileImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (profilePictureImageView != null) {
                        Glide.with(this)
                                .load(imageUri)
                                .apply(new RequestOptions().circleCrop())
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .into(profilePictureImageView);
                    }
                    if (mAuth.getCurrentUser() != null) {
                        String userId = mAuth.getCurrentUser().getUid();
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                                .child("profile_pictures/" + userId + ".jpg");

                        // Uploader l'image vers Firebase Storage
                        storageRef.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Récupérer l'URL publique
                                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                        // Mettre à jour Firestore
                                        db.collection("users").document(userId)
                                                .update("profilePictureUrl", uri.toString())
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d(TAG, "Photo de profil mise à jour: " + uri.toString());
                                                    Toast.makeText(this, "Photo de profil mise à jour", Toast.LENGTH_SHORT).show();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e(TAG, "Erreur mise à jour Firestore", e);
                                                    Toast.makeText(this, "Erreur mise à jour photo", Toast.LENGTH_SHORT).show();
                                                });
                                    }).addOnFailureListener(e -> {
                                        Log.e(TAG, "Erreur récupération URL", e);
                                        Toast.makeText(this, "Erreur récupération URL", Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Erreur upload image", e);
                                    Toast.makeText(this, "Erreur upload photo", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    Toast.makeText(this, "Aucune image sélectionnée", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> pickPostImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedPostImageUri = result.getData().getData();
                    BottomSheetDialog dialog = (BottomSheetDialog) postsRecyclerView.getTag();
                    if (dialog != null && dialog.isShowing()) {
                        ImageView postImage = dialog.findViewById(R.id.post_image);
                        Button sharePostButton = dialog.findViewById(R.id.share_post_button);
                        if (postImage != null) {
                            Glide.with(this)
                                    .load(selectedPostImageUri)
                                    .placeholder(R.drawable.ic_profile_placeholder)
                                    .into(postImage);
                        }
                        if (sharePostButton != null) {
                            sharePostButton.setEnabled(true);
                        }
                    }
                } else {
                    Toast.makeText(this, "Aucune image sélectionnée", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            Log.w(TAG, "Utilisateur non authentifié");
            Toast.makeText(this, "Utilisateur non authentifié", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialisation des vues
        Toolbar toolbar = findViewById(R.id.toolbar);
        usernameTextView = findViewById(R.id.username);
        displayNameTextView = findViewById(R.id.display_name);
        postsCountTextView = findViewById(R.id.posts_count);
        followersCountTextView = findViewById(R.id.followers_count);
        followingCountTextView = findViewById(R.id.following_count);
        profilePictureImageView = findViewById(R.id.profile_picture);
        postsRecyclerView = findViewById(R.id.posts_recycler_view);

        // Vérifications des vues
        if (toolbar == null || usernameTextView == null || displayNameTextView == null ||
                postsCountTextView == null || followersCountTextView == null ||
                followingCountTextView == null || profilePictureImageView == null ||
                postsRecyclerView == null) {
            Log.e(TAG, "Une ou plusieurs vues non trouvées");
            Toast.makeText(this, "Erreur de configuration de l'interface", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialisation du RecyclerView
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, this, false); // false pour EditProfileActivity
        postsRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        postsRecyclerView.setAdapter(postAdapter);

        // Écouteur pour la photo de profil
        profilePictureImageView.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Log.w(TAG, "Utilisateur non authentifié lors du clic sur la photo");
                Toast.makeText(this, "Utilisateur non authentifié", Toast.LENGTH_SHORT).show();
                return;
            }
            openProfileImagePicker();
        });

        // Bouton de changement de compte
        ImageButton switchAccountButton = findViewById(R.id.switch_account_button);
        if (switchAccountButton != null) {
            switchAccountButton.setOnClickListener(v -> {
                Intent intent = new Intent(EditProfileActivity.this, AccountsListActivity.class);
                startActivity(intent);
            });
        } else {
            Log.w(TAG, "switch_account_button non trouvé");
        }

        // Bouton pour ajouter un post
        Button postButton = findViewById(R.id.post_button);
        if (postButton != null) {
            postButton.setOnClickListener(v -> showPostOptionsBottomSheet());
        } else {
            Log.w(TAG, "post_button non trouvé");
        }

        loadUserData();
        loadPosts();
    }

    private void openProfileImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            pickProfileImageLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Erreur ouverture sélecteur image profil", e);
            Toast.makeText(this, "Erreur accès galerie", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPostOptionsBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        try {
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_post_options);
        } catch (Exception e) {
            Log.e(TAG, "Erreur chargement layout bottom_sheet_post_options", e);
            Toast.makeText(this, "Erreur affichage options", Toast.LENGTH_SHORT).show();
            return;
        }

        Button newPostButton = bottomSheetDialog.findViewById(R.id.new_post_button);
        if (newPostButton != null) {
            newPostButton.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                showNewPostDialog();
            });
        } else {
            Log.w(TAG, "new_post_button non trouvé");
        }

        try {
            bottomSheetDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Erreur affichage BottomSheetDialog", e);
            Toast.makeText(this, "Erreur affichage options", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNewPostDialog() {
        BottomSheetDialog newPostDialog = new BottomSheetDialog(this);
        try {
            newPostDialog.setContentView(R.layout.new_post_layout);
        } catch (Exception e) {
            Log.e(TAG, "Erreur chargement layout new_post_layout", e);
            Toast.makeText(this, "Erreur création post", Toast.LENGTH_SHORT).show();
            return;
        }

        ImageView postImage = newPostDialog.findViewById(R.id.post_image);
        Button choosePhotoButton = newPostDialog.findViewById(R.id.choose_photo_button);
        Button sharePostButton = newPostDialog.findViewById(R.id.share_post_button);
        TextView postContent = newPostDialog.findViewById(R.id.post_content);

        if (sharePostButton != null) {
            sharePostButton.setEnabled(false);
        }

        postsRecyclerView.setTag(newPostDialog);

        if (postImage != null) {
            postImage.setOnClickListener(v -> openPostImagePicker());
        }

        if (choosePhotoButton != null) {
            choosePhotoButton.setOnClickListener(v -> openPostImagePicker());
        }

        if (sharePostButton != null) {
            sharePostButton.setOnClickListener(v -> {
                if (selectedPostImageUri == null) {
                    Toast.makeText(this, "Veuillez choisir une image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mAuth.getCurrentUser() == null) {
                    Toast.makeText(this, "Utilisateur non authentifié", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = mAuth.getCurrentUser().getUid();
                String content = postContent != null ? postContent.getText().toString().trim() : "";
                long timestamp = System.currentTimeMillis();
                String postId = db.collection("posts").document().getId();

                Log.d(TAG, "Création post: postId=" + postId + ", userId=" + userId);

                db.collection("users").document(userId).get()
                        .addOnSuccessListener(userDoc -> {
                            String userName = userDoc.getString("username");
                            String profileImageUrl = userDoc.getString("profilePictureUrl");

                            if (userName == null) {
                                userName = "Utilisateur inconnu";
                            }
                            if (profileImageUrl == null) {
                                profileImageUrl = "";
                            }

                            Post newPost = new Post(postId, userId, userName, profileImageUrl, selectedPostImageUri.toString(), content, timestamp);
                            newPost.setIsPublic(false);

                            Log.d(TAG, "Enregistrement post dans Firestore: postId=" + postId);

                            db.collection("posts").document(postId).set(newPost)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Post ajouté avec succès: postId=" + postId);
                                        Toast.makeText(this, "Publication ajoutée", Toast.LENGTH_SHORT).show();
                                        newPostDialog.dismiss();
                                        selectedPostImageUri = null;
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Erreur ajout publication: " + e.getMessage(), e);
                                        Toast.makeText(this, "Erreur ajout publication", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Erreur récupération utilisateur: " + e.getMessage(), e);
                            Toast.makeText(this, "Erreur création publication", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            Log.w(TAG, "share_post_button non trouvé");
            Toast.makeText(this, "Erreur configuration bouton publier", Toast.LENGTH_SHORT).show();
        }

        try {
            newPostDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Erreur affichage NewPostDialog", e);
            Toast.makeText(this, "Erreur création post", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPostImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            pickPostImageLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Erreur ouverture sélecteur image post", e);
            Toast.makeText(this, "Erreur accès galerie", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() == null) {
            Log.w(TAG, "Utilisateur non authentifié");
            Toast.makeText(this, "Utilisateur non authentifié", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        userListener = db.collection("users").document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Erreur chargement utilisateur", e);
                        Toast.makeText(this, "Erreur chargement données utilisateur", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        try {
                            user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                Log.d(TAG, "Utilisateur chargé: username=" + user.getUsername() + ", profilePictureUrl=" + user.getProfilePictureUrl());
                                populateUserData();
                            } else {
                                Log.w(TAG, "Objet User null après conversion");
                                Toast.makeText(this, "Erreur données utilisateur", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Erreur désérialisation User: " + ex.getMessage(), ex);
                            Toast.makeText(this, "Erreur chargement profil", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Document utilisateur introuvable: " + userId);
                        Toast.makeText(this, "Utilisateur introuvable", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadPosts() {
        if (mAuth.getCurrentUser() == null) {
            Log.w(TAG, "Utilisateur non authentifié pour chargement posts");
            Toast.makeText(this, "Utilisateur non authentifié", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Log.d(TAG, "Chargement des posts pour userId: " + userId);
        postsListener = db.collection("posts")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Erreur chargement posts: " + error.getMessage(), error);
                        Toast.makeText(this, "Erreur chargement publications", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    postList.clear();
                    if (value != null && !value.isEmpty()) {
                        Log.d(TAG, "Nombre de documents trouvés: " + value.size());
                        for (DocumentSnapshot doc : value) {
                            try {
                                Post post = doc.toObject(Post.class);
                                if (post != null) {
                                    post.setPostId(doc.getId());
                                    Log.d(TAG, "Post chargé: postId=" + post.getPostId() + ", userId=" + post.getUserId() + ", content=" + post.getContent());
                                    postList.add(post);
                                } else {
                                    Log.w(TAG, "Post null pour document: " + doc.getId());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Erreur désérialisation Post pour document " + doc.getId() + ": " + e.getMessage(), e);
                            }
                        }
                    } else {
                        Log.d(TAG, "Aucune publication trouvée pour userId: " + userId);
                        Toast.makeText(this, "Aucune publication disponible", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(TAG, "Posts chargés: " + postList.size());
                    postAdapter.notifyDataSetChanged();
                    if (postsCountTextView != null) {
                        postsCountTextView.setText(String.valueOf(postList.size()));
                    }
                });
    }

    private void populateUserData() {
        if (user == null) {
            Log.w(TAG, "User null dans populateUserData");
            return;
        }
        usernameTextView.setText(user.getUsername() != null ? user.getUsername() : "Inconnu");
        displayNameTextView.setText(user.getDisplayName() != null ? user.getDisplayName() : "Inconnu");
        followersCountTextView.setText(String.valueOf(user.getFollowersCount()));
        followingCountTextView.setText(String.valueOf(user.getFollowingCount()));
        if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getProfilePictureUrl())
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(profilePictureImageView);
        } else {
            profilePictureImageView.setImageResource(R.drawable.ic_profile_placeholder);
            Log.d(TAG, "Aucune URL de photo de profil disponible");
        }
    }

    @Override
    public void onPostDeleted(Post post) {
        if (post == null || post.getPostId() == null) {
            Log.w(TAG, "Post ou postId null lors de suppression");
            Toast.makeText(this, "Erreur suppression", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("posts").document(post.getPostId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    postList.remove(post);
                    postAdapter.notifyDataSetChanged();
                    postsCountTextView.setText(String.valueOf(postList.size()));
                    Toast.makeText(this, "Publication supprimée", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Post supprimé: " + post.getPostId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur suppression post: " + e.getMessage(), e);
                    Toast.makeText(this, "Erreur suppression publication", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onPostToggleVisibility(Post post) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postsListener != null) {
            postsListener.remove();
        }
        if (userListener != null) {
            userListener.remove();
        }
    }
}