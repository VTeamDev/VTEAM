package info.vteam.vmangaandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import info.vteam.vmangaandroid.R;

/**
 * Created by YukiNoHara on 3/10/2017.
 */

public class PreferencesUtils {

    public static boolean isFavoriteManga(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isFavorite = sharedPreferences.getBoolean(context.getString(R.string.pref_favorite_key), false);

        return isFavorite;
    }

    public static void setPreferences(Context context, String key, boolean value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

}
