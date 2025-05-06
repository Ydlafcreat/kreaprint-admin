package com.example.admin.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

public class ImagekitHelper {
    public static final String DEFAULT_PROFILE_FOLDER = "kreaprint/user_profiles";
    public static final String DEFAULT_PRODUCT_FOLDER = "kreaprint/products";

    public static final String TAG = "ImagekitHelper";


    private static final String UPLOAD_BASE_URL = "https://upload.imagekit.io/api/v2/files/";
    private static final String TOKEN_BASE_URL = "https://v0-next-js-token-server.vercel.app/api/";

    public interface UploadCallback {
        void onSuccess(UploadResponse response);
        void onError(String error);
    }

    public interface UploadService {
        @Multipart
        @POST("upload")
        @Headers({
                "Accept: application/json",
                "Authorization: Basic cHJpdmF0ZV9pb28zUEFPRFUzbmtnSVNxc216VTlxa2pMajA9Og=="
        })
        Call<UploadResponse> uploadFile(
                @Part MultipartBody.Part file,
                @PartMap Map<String, RequestBody> parameters

        );

    }

    public interface DeleteCallback {
        void onSuccess(DeleteResponse response);
        void onError(String error);
    }

    public interface DeleteService {
        @DELETE("{fileId}")
        @Headers({
                "Accept: application/json",
                "Authorization: Basic cHJpdmF0ZV9pb28zUEFPRFUzbmtnSVNxc216VTlxa2pMajA9Og=="
        })
        Call<DeleteResponse> deleteFile(@Path("fileId") String fileId);
    }

    public interface TokenService {
        @POST("auth")
        @Headers({
                "Accept: application/json",
                "Authorization: Basic cHJpdmF0ZV9pb28zUEFPRFUzbmtnSVNxc216VTlxa2pMajA9Og=="
        })
        Call<AuthTokenResponse> getToken(@Body AuthTokenRequest request);
    }

    public static void deleteFile(String fileId, DeleteCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.imagekit.io/v1/files/") // base URL ends with /
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DeleteService deleteService = retrofit.create(DeleteService.class);

        Call<DeleteResponse> call = deleteService.deleteFile(fileId);

        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteResponse> call, @NonNull Response<DeleteResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(response.errorBody() != null ? response.errorBody().string() : "Unknown error");
                    } catch (IOException e) {
                        callback.onError("Error parsing error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteResponse> call, @NonNull Throwable t) {
                callback.onError("Request failed: " + t.getMessage());
            }
        });
    }


    /**
    *  @param file: File to be uploaded
    *  @param params: Map of parameters to be sent with the request
    */
    public static void uploadFile(File file, Map<String, String> params, UploadCallback callback) {

        Log.d(TAG, "Initializing Upload File");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(UPLOAD_BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UploadService uploadService = retrofit.create(UploadService.class);

        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestFile);



        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            requestBodyMap.put(entry.getKey(),
                    RequestBody.create(entry.getValue(), MediaType.parse("text/plain")));
        }

        Log.d(TAG, "Starting Upload File");

        Call<UploadResponse> call = uploadService.uploadFile(filePart, requestBodyMap);

        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(@NonNull Call<UploadResponse> call, @NonNull Response<UploadResponse> response) {
                Log.d(TAG, "Upload response: " + response.toString());

                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    try {
                        callback.onError(response.errorBody() != null ? response.errorBody().string() : "Unknown error");
                    } catch (IOException e) {
                        callback.onError("Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Upload failed: " + t.getMessage());
                callback.onError("Upload failed: " + t.getMessage());
            }
        });
    }



    // The getUploadToken function mimics your JavaScript getToken function.
    private static void getUploadToken(TokenCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TOKEN_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TokenService service = retrofit.create(TokenService.class);
        Call<AuthTokenResponse> call = service.getToken(new AuthTokenRequest());

        call.enqueue(new Callback<AuthTokenResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthTokenResponse> call, @NonNull Response<AuthTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onTokenReceived(response.body().token);
                } else {
                    callback.onError("Token fetch failed: Code " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthTokenResponse> call, @NonNull Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // TokenCallback used internally by getUploadToken
    private interface TokenCallback {
        void onTokenReceived(String token);
        void onError(String error);
    }

    // Request payload model (same as JS: { uploadPayload: {}, expire: 60 })
    public static class AuthTokenRequest {
        public Map<String, Object> uploadPayload = new HashMap<>();
        public int expire = 60;
    }

    // Token response model
    public static class AuthTokenResponse {
        public String token;
    }

    // UploadResponse data model — adjust these fields as needed to match the response from ImageKit.
    public static class UploadResponse {
        public String fileId;
        public String name;
        public String url;
        public String thumbnailUrl;
    }

    public  static class DeleteResponse {
        public String message;
    }
}
