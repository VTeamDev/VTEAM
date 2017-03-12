package info.vteam.vmangaandroid.utils;

/**
 * Created by lednh on 3/6/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import info.vteam.vmangaandroid.R;

/**
 *  Retrieve app settings here
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
