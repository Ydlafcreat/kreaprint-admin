package com.example.admin.helper.firebase;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

public abstract class BaseRepository {
    protected final FirebaseFirestore db;
    protected final String TAG;

    public BaseRepository(String tag) {
        this.db = FirebaseFirestore.getInstance();
        this.TAG = tag;
    }

    protected void logSuccess(String operation) {
        Log.d(TAG, operation + " sukses");
    }

    protected void logError(String operation, Exception e) {
        Log.e(TAG, operation + " gagal: " + e.getMessage());
    }
}