package com.example.patrimoin_dz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.IOException;
import java.util.UUID;
import android.hardware.Camera;

public class CreateStoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private SurfaceView cameraPreview;
    private Camera camera;
    private ImageView capturedImage;
    private EditText storyText;
    private Button captureButton, addMusicButton, shareButton;
    private Uri mediaUri, musicUri;
    private boolean isVideoMode = false;

    private final ActivityResultLauncher<Intent> pickMediaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    mediaUri = result.getData().getData();
                    Glide.with(this).load(mediaUri).into(capturedImage);
                    capturedImage.setVisibility(View.VISIBLE);
                    cameraPreview.setVisibility(View.GONE);
                    shareButton.setEnabled(true);
                }
            });

    private final ActivityResultLauncher<Intent> pickMusicLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    musicUri = result.getData().getData();
                    Toast.makeText(this, "Musique sélectionnée", Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        cameraPreview = findViewById(R.id.camera_preview);
        capturedImage = findViewById(R.id.captured_image);
        storyText = findViewById(R.id.story_text);
        captureButton = findViewById(R.id.capture_button);
        addMusicButton = findViewById(R.id.add_music_button);
        shareButton = findViewById(R.id.share_button);

        shareButton.setEnabled(false);

        if (checkPermissions()) {
            setupCamera();
        } else {
            requestPermissions();
        }

        captureButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType(isVideoMode ? "video/*" : "image/*");
            pickMediaLauncher.launch(intent);
        });

        addMusicButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            pickMusicLauncher.launch(intent);
        });

        shareButton.setOnClickListener(v -> uploadStory());
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, 100);
    }

    private void setupCamera() {
        try {
            camera = Camera.open();
            SurfaceHolder holder = cameraPreview.getHolder();
            holder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        camera.setPreviewDisplay(holder);
                        camera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (camera != null) {
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur d'accès à la caméra", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadStory() {
        if (mediaUri == null) {
            Toast.makeText(this, "Veuillez sélectionner une image ou une vidéo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Utilisateur non authentifié", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String storyId = UUID.randomUUID().toString();
        StorageReference mediaRef = storage.getReference().child("stories/" + storyId);

        mediaRef.putFile(mediaUri)
                .addOnSuccessListener(taskSnapshot -> mediaRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String mediaUrl = uri.toString();
                    String text = storyText.getText().toString().trim();

                    if (musicUri != null) {
                        StorageReference musicRef = storage.getReference().child("music/" + storyId);
                        musicRef.putFile(musicUri).addOnSuccessListener(task -> musicRef.getDownloadUrl().addOnSuccessListener(musicUri -> {
                            saveStory(storyId, userId, mediaUrl, text, musicUri.toString());
                        }));
                    } else {
                        saveStory(storyId, userId, mediaUrl, text, null);
                    }
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur lors du téléchargement", Toast.LENGTH_SHORT).show());
    }

    private void saveStory(String storyId, String userId, String mediaUrl, String text, String musicUrl) {
        Story story = new Story(storyId, userId, mediaUrl, text, musicUrl, System.currentTimeMillis());
        db.collection("stories").document(storyId).set(story)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Story publiée avec succès", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur lors de la publication", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (camera != null) {
            camera.release();
        }
    }
}