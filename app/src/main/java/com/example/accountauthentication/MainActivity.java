package com.example.accountauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.exceptions.ClearCredentialException;

import com.bumptech.glide.Glide;

import java.util.concurrent.Executors;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class MainActivity extends AppCompatActivity {
    private TextView emailTextView;
    private TextView nameTextView;
    private ImageView avatarImageView;
    private Button logoutButton;
    private CredentialManager credentialManager;
    private String TAG = "LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        credentialManager = CredentialManager.create(this);
        emailTextView = findViewById(R.id.emailTextView);
        nameTextView = findViewById(R.id.nameTextView);
        avatarImageView = findViewById(R.id.avatarImageView);
        logoutButton = findViewById(R.id.logoutButton);

        // Get user data from intent
        String email = getIntent().getStringExtra("user_email");
        String name = getIntent().getStringExtra("user_name");
        String avatarUrl = getIntent().getStringExtra("user_avatar");

        // Display user information
        emailTextView.setText(email);
        nameTextView.setText(name);

        // Load avatar image using Glide
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .circleCrop()
                    .into(avatarImageView);
        }

        // Set up logout button
        logoutButton.setOnClickListener(v -> performLogout());
    }

    private void performLogout() {
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<Void, ClearCredentialException>() {
                    @Override
                    public void onResult(@NonNull Void result) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(@NonNull ClearCredentialException e) {
                        Log.e(TAG, "Couldn't clear user credentials: " + e.getLocalizedMessage());
                    }
                });
    }
}