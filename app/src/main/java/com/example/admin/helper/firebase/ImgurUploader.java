package com.example.admin.helper.firebase;

import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ImgurUploader {
    private static final String IMGUR_CLIENT_ID = "28aa446e7cf218e"; // Ganti dengan Client ID Anda
    private static final String IMGUR_API_URL = "https://api.imgur.com/3/";

    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public static void uploadImage(byte[] imageData, UploadCallback callback) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // Timeout 30 detik
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", IMGUR_CLIENT_ID)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "upload.jpg",
                        RequestBody.create(MediaType.parse("image/*"), imageData))
                .build();

        Request request = new Request.Builder()
                .url(IMGUR_API_URL + "image")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Gagal terhubung: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body().string();

                    // Cek jika response adalah XML
                    if (responseData.trim().startsWith("<?xml")) {
                        callback.onError("Server Imgur mengembalikan error. Coba lagi nanti.");
                        return;
                    }

                    JSONObject json = new JSONObject(responseData);
                    if (json.has("success") && json.getBoolean("success")) {
                        String imageUrl = json.getJSONObject("data").getString("link");
                        callback.onSuccess(imageUrl);
                    } else {
                        String errorMsg = json.optString("data", "Upload gagal");
                        callback.onError(errorMsg);
                    }
                } catch (Exception e) {
                    callback.onError("Error parsing response: " + e.getMessage());
                }
            }
        });
    }
}