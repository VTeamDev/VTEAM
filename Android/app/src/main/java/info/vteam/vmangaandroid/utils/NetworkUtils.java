package info.vteam.vmangaandroid.utils;

/**
 * Created by lednh on 3/6/2017.
 */

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * All network calls go here!
 */
public class NetworkUtils {
    static final String BASE_URL = "http://wannashare.info/api/v1";
    static final String LIMIT_PARAMS = "$limit";
    static final String SKIP_PARAMS = "$skip";
    static final String SEARCH_PARAMS = "search";
    static final String NAME_PARAMS = "name";
    static final String CHAPTER_PARAMS = "chapters";

    public static final URL getUrlWithContidition(Context context, String condition) throws MalformedURLException {
        final int NUM_MANGA = 30;
        final int NUM_SKIP = 0;

        Uri mangaUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(condition)
                .appendQueryParameter(LIMIT_PARAMS, String.valueOf(NUM_MANGA))
                .appendQueryParameter(SKIP_PARAMS, String.valueOf(NUM_SKIP))
                .build();

        return new URL(mangaUri.toString());
    }

    public static final URL getUrlRealtimeUser(Context context) throws MalformedURLException {
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("realtime")
                .build();

        return new URL(uri.toString());
    }

    public static final URL getUrlWithConditionAndId(Context context, String condition, String id) throws MalformedURLException {

        Uri mangaUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(condition)
                .appendPath(id)
                .build();

        return new URL(mangaUri.toString());
    }

    public static final URL getUrlWithConditionAndSearchKey(Context context, String condition, String key) throws MalformedURLException {

        Uri mangaUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(condition)
                .appendPath(SEARCH_PARAMS)
                .appendQueryParameter(NAME_PARAMS, key)
                .build();

        return new URL(mangaUri.toString());
    }

    public static final URL getUrlWithConditionAndParams(Context context, String condition, String params) throws MalformedURLException {
        Uri mangaUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(condition)
                .appendPath(params)
                .build();

        return new URL(mangaUri.toString());
    }

    public static String getResponseFromUrl(Context context, URL url){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e){
            e.printStackTrace();
            return null;
        }
    }

}
