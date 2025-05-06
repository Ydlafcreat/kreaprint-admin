package com.example.admin.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.admin.AdminLogin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthHelper {
    private static final String TAG = "AuthHelper";
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";

    public final FirebaseAuth auth;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;


    public AuthHelper(Context context) {
        this.context = context;
        this.auth = FirebaseAuth.getInstance();
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
    }

    public boolean isLoggedIn() {
        // Read from SharedPreferences
        boolean isSessionStored = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
                || (prefs.contains(KEY_USER_ID) && !prefs.getString(KEY_USER_ID, "").isEmpty());

        Log.d("SignIn", "Session Stored in Prefs: " + isSessionStored);

        // If no session found in prefs, check Firebase Authentication
        if (!isSessionStored) {
            Log.d("SignIn", "No session in prefs, checking Firebase Auth...");
            return isFirebaseAuthUserLoggedIn();
        }

        // If session is found in SharedPreferences, return true
        Log.d("SignIn", "User session found in prefs.");
        return true;
    }


    public boolean isFirebaseAuthUserLoggedIn() {
        FirebaseUser user = this.getCurrentUser();
        return user != null; // ✅ Returns true if user is logged in
    }


    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }


    public void saveUserSession(String userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.apply();
    }

    public void clearUserLogin() {
        Log.d("Authentication", "Cleared Session on shared preference");
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ID);
        editor.apply();
    }


    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }


    public void logoutUser() {
        this.clearUserLogin();
        auth.signOut();


        Intent intent = new Intent(context, AdminLogin.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }


    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }
}
