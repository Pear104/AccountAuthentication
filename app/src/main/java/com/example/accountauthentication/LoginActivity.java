package com.example.accountauthentication;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.PasswordCredential;
import androidx.credentials.PublicKeyCredential;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;

public class LoginActivity extends AppCompatActivity {
    private static final String WEB_CLIENT_ID = "236787614465-vuhddof7r6fphq5be2erfimcmjehneg3.apps.googleusercontent.com";
//    private static final String WEB_CLIENT_ID = "553874834892-51tqjh0j2m0nren6789bj02h6t2bnquh.apps.googleusercontent.com";

    private CredentialManager credentialManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        credentialManager = CredentialManager.create(this);

        Button signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setOnClickListener(v -> startGoogleSignIn());
    }

    private void startGoogleSignIn() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .setNonce("<nonce string>") // Make sure to generate a proper nonce
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                ContextCompat.getMainExecutor(this),
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignIn(result);
                    }

                    @Override
                    public void onError(GetCredentialException e) {
                        handleFailure(e);
                    }
                }
        );
    }

    private void handleSignIn(GetCredentialResponse result) {
        if (result != null && result.getCredential() != null) {
            Object credential = result.getCredential();

            if (credential instanceof PublicKeyCredential) {
                PublicKeyCredential publicKeyCredential = (PublicKeyCredential) credential;
                // Handle public key credential
            } else if (credential instanceof PasswordCredential) {
                PasswordCredential passwordCredential = (PasswordCredential) credential;
                // Handle password credential
            }

            if (credential instanceof CustomCredential) {
                CustomCredential customCredential = (CustomCredential) credential;

                if (customCredential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                    try {
                        GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(customCredential.getData());

                        // Get user information
                        String email = googleIdTokenCredential.getId();
                        String name = googleIdTokenCredential.getDisplayName();
                        String avatarUrl = String.valueOf(googleIdTokenCredential.getProfilePictureUri());

                        // Create User object
                        User user = new User(email, name, avatarUrl);

                        // Start MainActivity
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("user_email", email);
                        intent.putExtra("user_name", name);
                        intent.putExtra("user_avatar", avatarUrl);
                        startActivity(intent);
                        finish(); // Close LoginActivity

                    } catch (Exception e) {
                        Log.e("DKM", "Error parsing Google ID token", e);
                    }
                }
            }
        }
    }

    private void handleFailure(GetCredentialException e) {
        if (e instanceof NoCredentialException) {
            launchAddGoogleAccount();
        }
        // Handle failure scenario here
        Log.e("ZOZ", "Credential retrieval failed", e);
    }

    private void launchAddGoogleAccount() {
        Intent intent = new Intent(Settings.ACTION_ADD_ACCOUNT);
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[]{"com.google"});
        startActivity(intent);
    }
}