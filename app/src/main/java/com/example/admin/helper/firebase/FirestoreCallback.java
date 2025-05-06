package com.example.admin.helper.firebase;

public interface FirestoreCallback<T> {
    void onSuccess(T result);
    void onError(Exception e);
}