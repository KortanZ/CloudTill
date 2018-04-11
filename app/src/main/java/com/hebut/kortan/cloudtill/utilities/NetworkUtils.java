package com.hebut.kortan.cloudtill.utilities;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtils {

    private static final String baseUrl = "http://211.159.165.169";

    private static OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();

    public String Http_Get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + url)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    public String Http_Post(HashMap<String, String> map, String url) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        if (map != null) {
             for (HashMap.Entry<String, String> entry : map.entrySet()) {
                     builder.add(entry.getKey(), entry.getValue());
                 }
         }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(baseUrl + url)
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }
}