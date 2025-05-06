package com.example.admin.helper.firebase;

import android.util.Log;

import com.example.admin.model.Category;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository extends BaseRepository {
    private static final String COLLECTION = "categories";

    public CategoryRepository() {
        super("CategoryRepository");
    }

    public void getAllCategories(FirestoreCallback<List<Category>> callback) {
        db.collection(COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Category> categories = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Category category = doc.toObject(Category.class);

                        assert category != null;
                        category.setId(doc.getId());
                        categories.add(category);
                    }
                    callback.onSuccess(categories);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Gagal mengambil kategori", e));
    }
    public DocumentReference getCategoryRefById(String categoryId) {
        return db
                .collection("categories")
                .document(categoryId);

    }


}