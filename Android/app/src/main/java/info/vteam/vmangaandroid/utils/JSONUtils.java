package info.vteam.vmangaandroid.utils;

/**
 * Created by lednh on 3/6/2017.
 */

import android.content.ContentValues;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.vteam.vmangaandroid.data.MangaContract;
import info.vteam.vmangaandroid.model.Manga;

/**
 * Handle JSON objects here!
 */
public class JSONUtils {
    public static ContentValues[] getMangaValuesFromJson(Context context, String string) throws JSONException{
        JSONObject jsonObject = new JSONObject(string);
        JSONArray jsonArray = jsonObject.getJSONArray(DataUtils.DATA_PARAMS);

        ContentValues[] mangaValues = new ContentValues[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject object = jsonArray.getJSONObject(i);
            mangaValues[i].put(MangaContract.MangaEntry.COLUMN_MANGA_ID, object.getString(DataUtils.ID_PARAMS));
            mangaValues[i].put(MangaContract.MangaEntry.COLUMN_THUMBNAIL, object.getString(DataUtils.THUMBNAIL_PARAMS));
            mangaValues[i].put(MangaContract.MangaEntry.COLUMN_TITLE, object.getString(DataUtils.TITLE_PARAMS));
        }

        return mangaValues;
    }

    public static ContentValues[] getMangaInfoValuesFromJson(Context context, String string) throws JSONException{
        JSONObject jsonObject = new JSONObject(string);
        JSONArray jsonArray = jsonObject.getJSONArray(DataUtils.DATA_PARAMS);

        ContentValues[] mangaInfoValues = new ContentValues[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++){
            JSONObject object = jsonArray.getJSONObject(i);
            mangaInfoValues[i].put(MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID, object.getString(DataUtils.ID_PARAMS));
            mangaInfoValues[i].put(MangaContract.MangaInfoEntry.COLUMN_THUMBNAIL, object.getString(DataUtils.THUMBNAIL_PARAMS));
            mangaInfoValues[i].put(MangaContract.MangaInfoEntry.COLUMN_TITLE, object.getString(DataUtils.TITLE_PARAMS));
            JSONArray array = object.getJSONArray(DataUtils.CATEGORY_PARAMS);
            String[] category = new String[array.length()];
            for (int j = 0; j < array.length(); i++){
                category[i] = array.getString(i);
            }
            mangaInfoValues[i].put(MangaContract.MangaInfoEntry.COLUMN_CATEROGY, DataUtils.convertStringArrayIntoString(category));
            mangaInfoValues[i].put(MangaContract.MangaInfoEntry.COLUMN_DESCRIPTION, object.getString(DataUtils.DESCRIPTION_PARAMS));
        }

        return mangaInfoValues;
    }
}
