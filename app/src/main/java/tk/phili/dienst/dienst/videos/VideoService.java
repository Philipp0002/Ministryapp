package tk.phili.dienst.dienst.videos;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tk.phili.dienst.dienst.utils.JWCallback;
import tk.phili.dienst.dienst.utils.JWVideoCategoryReponse;

public class VideoService {

    private static final String SP_VIDEOS_KEY = "downloadedVideos";
    private static final String CATEGORY_INFO_URL = "https://b.jw-cdn.org/apis/mediator/v1/categories/%s/%s?detailed=1&mediaLimit=0&clientType=www";
    public static final String CATEGORY_VOD = "VideoOnDemand";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor sharedPreferencesEditor;

    public VideoService(Context context) {
        sharedPreferences = context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    public void getVideoCategory(JWCallback<JWVideoCategory> callback, String jwLangCode, String categoryKey) {
        OkHttpClient client = new OkHttpClient();

        String url = String.format(CATEGORY_INFO_URL, jwLangCode, categoryKey);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if(callback != null) callback.onError(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    if(callback != null) callback.onError(new IOException("Unexpected code " + response));
                    return;
                }

                Gson gson = new Gson();
                Type listType = new TypeToken<JWVideoCategoryReponse>() {}.getType();
                JWVideoCategoryReponse categoryResponse = gson.fromJson(response.body().string(), listType);
                if(callback != null) callback.onSuccess(categoryResponse.getCategory());
            }
        });
    }


    public void saveDownloadedVideos(List<JWVideo> videos) {
        Gson gson = new Gson();
        String json = gson.toJson(videos);
        sharedPreferencesEditor.putString(SP_VIDEOS_KEY, json);
        sharedPreferencesEditor.apply();
    }

    public List<JWVideo> getDownloadedVideos() {
        String json = sharedPreferences.getString(SP_VIDEOS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<JWVideo>>() {}.getType();
        return gson.fromJson(json, listType);
    }

    public void addDownloadedVideo(JWVideo video) {
        List<JWVideo> videos = getDownloadedVideos();
        if (videos == null) {
            videos = new ArrayList<>();
        }
        videos.add(video);
        saveDownloadedVideos(videos);
    }

    public void removeDownloadedVideo(JWVideo video) {
        List<JWVideo> videos = getDownloadedVideos();
        if (videos != null) {
            videos.remove(video);
            saveDownloadedVideos(videos);
        }
    }

}
