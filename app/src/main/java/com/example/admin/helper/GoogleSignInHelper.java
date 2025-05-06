package com.example.admin.helper;

import static com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL;

import android.content.Context;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.ClearCredentialException;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.admin.helper.firebase.FirestoreCallback;
import com.example.admin.helper.firebase.RepositoryFactory;
import com.example.admin.helper.firebase.UserRepository;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.concurrent.Executors;

public class GoogleSignInHelper {
    private static final String TAG = "GoogleSignInHelper";
    private final FirebaseAuth mAuth;
    private final CredentialManager credentialManager;
    private final Context context;
    private final String webClientId;
    private GoogleSignInCallback callback;

    public GoogleSignInHelper(Context context, String webClientId, GoogleSignInCallback callback) {
        this.context = context;
        this.webClientId = webClientId;
        this.callback = callback;
        this.mAuth = FirebaseAuth.getInstance();
        this.credentialManager = CredentialManager.create(context);
    }

    public void signIn() {
        try {
            GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(webClientId)
                    .build();

            GetCredentialRequest request = new GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build();

            credentialManager.getCredentialAsync(
                    context,
                    request,
                    new CancellationSignal(),
                    Executors.newSingleThreadExecutor(),
                    new CredentialManagerCallback<>() {
                        @Override
                        public void onResult(GetCredentialResponse result) {
                            try {
                                handleSignIn(result.getCredential());
                            }   catch (Exception e) {
                                Log.e(TAG, "Error handling sign-in result: " + e.getMessage(), e);
                                if (callback != null) callback.onSignInFailure(e);
                            }
                        }

                        @Override
                        public void onError(GetCredentialException e) {
                           callback.onError(e);
                        }
                    }
            );
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during sign-in: " + e.getMessage(), e);
            if (callback != null) callback.onSignInFailure(e);
        }
    }



    private void handleSignIn(Credential credential) {
        if (credential instanceof CustomCredential) {
            CustomCredential customCredential = (CustomCredential) credential;

            // Ensure credential type is Google ID Token
            if (customCredential.getType().equals(TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                try {
                    // Extract Google ID Token from credential data
                    GoogleIdTokenCredential googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(customCredential.getData());

                    if (googleIdTokenCredential != null) {
                        authenticateWithFirebase(googleIdTokenCredential.getIdToken());
                    } else {
                        Log.w(TAG, "GoogleIdTokenCredential is null!");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse Google ID Token Credential", e);
                    if (callback != null) callback.onSignInFailure(e);
                }
            } else {
                Log.w(TAG, "Credential type is not Google ID Token!");
            }
        } else {
            Log.w(TAG, "Credential is not an instance of CustomCredential!");
        }
    }

    private void authenticateWithFirebase(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {

                            UserRepository userRepo = RepositoryFactory.getUserRepository();
                            userRepo.createUser(firebaseUser, new FirestoreCallback<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    Log.d(TAG, "User created successfully");
                                }
                                @Override
                                public void onError(Exception e) {
                                    Log.e(TAG, "Error creating user", e);
                                }
                            });

                        }
                        if (callback != null) callback.onSignInSuccess(firebaseUser);
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (callback != null) callback.onSignInFailure(task.getException());
                    }
                });
    }

    public void  signOut(Runnable onSignOutComplete) {
        mAuth.signOut();
        ClearCredentialStateRequest clearRequest = new ClearCredentialStateRequest();
        credentialManager.clearCredentialStateAsync(
                clearRequest,
                new CancellationSignal(),
                Executors.newSingleThreadExecutor(),
                new CredentialManagerCallback<Void, ClearCredentialException>() {
                    @Override
                    public void onResult(Void result) {
                        Log.d(TAG, "Google Sign-Out successful and credentials cleared.");
                        if (callback != null) callback.onSignOut();
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (onSignOutComplete != null) {
                                onSignOutComplete.run();
                            }
                        });
                    }

                    @Override
                    public void onError(ClearCredentialException e) {
                        Log.e(TAG, "Failed to clear Google Sign-In credentials.", e);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            if (onSignOutComplete != null) {
                                onSignOutComplete.run();
                            }
                        });
                    }
                }
        );
    }



    public interface GoogleSignInCallback {
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailure(Exception e);

        void onSignOut();

        void onError(GetCredentialException e);
    }


}
