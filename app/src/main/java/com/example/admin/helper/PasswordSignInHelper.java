package com.example.admin.helper;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class PasswordSignInHelper {
    private final AuthHelper authHelper;

    public PasswordSignInHelper(AuthHelper authHelper) {
        this.authHelper = authHelper;
    }

    public void loginWithPassword(String email, String password, AuthCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onLoginFailure(new IllegalArgumentException("Email dan Password harus diisi!"));
            return;
        }

        callback.onLoginStart();

        authHelper.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = authHelper.auth.getCurrentUser();
                        if (user != null) {
                            authHelper.saveUserSession(user.getUid());
                            callback.onLoginSuccess(user);
                        }
                    } else {
                        callback.onLoginFailure(task.getException());
                    }
                });
    }

    public void registerWithPassword(String email, String password, String displayName, AuthCallback callback) {
        if (email.isEmpty() || password.isEmpty() || displayName.isEmpty()) {
            callback.onLoginFailure(new IllegalArgumentException("Email, Password, dan Nama harus diisi!"));
            return;
        }

        callback.onLoginStart();

        authHelper.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

//                    When task is success, update the firestore with the display name
                    if (task.isSuccessful()) {
                        FirebaseUser user = authHelper.auth.getCurrentUser();
                        if (user != null) {

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(displayName)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            callback.onLoginSuccess(user);
                                        } else {
                                            callback.onLoginFailure(updateTask.getException());
                                        }
                                    });
                        } else {
                            callback.onLoginFailure(new Exception("User tidak ditemukan setelah registrasi."));
                        }
                    } else {
                        callback.onLoginFailure(task.getException());
                    }
                });
    }





    public interface AuthCallback {
        void onLoginStart();
        void onLoginSuccess(FirebaseUser user);
        void onLoginFailure(Exception e);
    }
}
