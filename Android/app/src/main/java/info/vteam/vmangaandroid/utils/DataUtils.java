package info.vteam.vmangaandroid.utils;

/**
 * Created by lednh on 3/6/2017.
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import info.vteam.vmangaandroid.R;
import info.vteam.vmangaandroid.data.MangaContract;
import info.vteam.vmangaandroid.model.Manga;
import info.vteam.vmangaandroid.model.MangaInfo;

/**
 * All the stuff life time, number,...
 */
public class DataUtils {
    public static final String TOTAL_PARAMS = "total";
    public static final String DATA_PARAMS = "data";
    public static final String ID_PARAMS = "manga_id";
    public static final String THUMBNAIL_PARAMS = "thumbnail";
    public static final String TITLE_PARAMS = "title";
    public static final String CATEGORY_PARAMS = "category";
    public static final String DESCRIPTION_PARAMS = "content";
    public static final String CHAPTER_PARAMS = "chapters";
    public static SharedPreferences sharedPreferences;

    public static ArrayList<Manga> getMangaListFromResponse(String string) throws JSONException {
        ArrayList<Manga> mangaList = new ArrayList<>();

        JSONObject mangaObject = new JSONObject(string);
        JSONArray mangaArray = mangaObject.getJSONArray(DATA_PARAMS);
        for (int i = 0; i < mangaArray.length(); i++){
            String id = mangaArray.getJSONObject(i).getString(ID_PARAMS);
            String thumbnail = mangaArray.getJSONObject(i).getString(THUMBNAIL_PARAMS);
            String title = mangaArray.getJSONObject(i).getString(TITLE_PARAMS);
            mangaList.add(new Manga(id, thumbnail, title));
        }
        return mangaList;
    }

    public static String getTotalChapterFromResponse(String string) throws JSONException{
        String totalChapter;
        JSONObject mangaObject = new JSONObject(string);
        totalChapter = mangaObject.getString(TOTAL_PARAMS);

        return totalChapter;
    }

    public static MangaInfo getMangaInfoFromResponse(String string) throws JSONException {
        JSONObject mangaInfoObject = new JSONObject(string);
        String id = mangaInfoObject.getString(ID_PARAMS);
        String thumbnail = mangaInfoObject.getString(THUMBNAIL_PARAMS);
        String title = mangaInfoObject.getString(TITLE_PARAMS);
        JSONArray categoryArray = mangaInfoObject.getJSONArray(CATEGORY_PARAMS);
        String[] category = new String[categoryArray.length()];
        for (int i = 0; i < categoryArray.length(); i++){
            category[i] = categoryArray.getString(i);
        }
        String description = mangaInfoObject.getString(DESCRIPTION_PARAMS);
        return new MangaInfo(id, thumbnail, title, convertStringArrayIntoString(category), description);
    }

    public static int getChapter(Context context, String id){
        URL url = null;
        try {
            url = NetworkUtils.getUrlWithConditionAndParams(context, "manga", id);

            String response = NetworkUtils.getResponseFromUrl(context, url);

            return getChapterFromResponse(response);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getChapterFromResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray(CHAPTER_PARAMS);

            return jsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String insertDataFromResponse(Context context){
        try {
            URL url= null;
            sharedPreferences = context.getSharedPreferences("sort_mode", Context.MODE_PRIVATE);
            int mode = sharedPreferences.getInt("sort_mode", 0);
            if (mode == 0){
                url = NetworkUtils.getUrlWithContidition(context, "list");
            } else if (mode == 1){
                url = NetworkUtils.getUrlWithConditionAndParams(context, "list", context.getString(R.string.pref_sort_top));
            } else if (mode == 2){
                url = NetworkUtils.getUrlWithConditionAndParams(context, "list", context.getString(R.string.pref_sort_latest));
            } else if (mode == 3){
                url = NetworkUtils.getUrlWithConditionAndParams(context, "list", context.getString(R.string.pref_sort_recommend));
            }

            String response = NetworkUtils.getResponseFromUrl(context, url);

            ArrayList<Manga> mList = getMangaListFromResponse(response);
            String totalChapter = null;
            if (mode == 0){
                totalChapter = getTotalChapterFromResponse(response);
            } else {
                totalChapter = null;
            }

            ArrayList<ContentValues> mListValues = new ArrayList<>();

            for (Manga manga : mList){
                ContentValues cv = new ContentValues();
                cv.put(MangaContract.MangaEntry.COLUMN_MANGA_ID, manga.getmId());
                cv.put(MangaContract.MangaEntry.COLUMN_THUMBNAIL, manga.getResAvatar());
                cv.put(MangaContract.MangaEntry.COLUMN_TITLE, manga.getmTitle());
                mListValues.add(cv);
            }

            if (!mListValues.isEmpty()){
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(MangaContract.MangaEntry.CONTENT_URI, null, null);

                contentResolver.bulkInsert(MangaContract.MangaEntry.CONTENT_URI, mListValues.toArray(new ContentValues[mListValues.size()]));

            }
            return totalChapter;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void insertDataFromResponseSearchByKey(Context context, String key){
        try {
            URL url = NetworkUtils.getUrlWithConditionAndSearchKey(context, "list", key);

            String response = NetworkUtils.getResponseFromUrl(context, url);

            ArrayList<Manga> mList = getMangaListFromResponse(response);

            ArrayList<ContentValues> mListValues = new ArrayList<>();

            for (Manga manga : mList){
                ContentValues cv = new ContentValues();
                cv.put(MangaContract.MangaSearchEntry.COLUMN_MANGA_ID, manga.getmId());
                cv.put(MangaContract.MangaSearchEntry.COLUMN_THUMBNAIL, manga.getResAvatar());
                cv.put(MangaContract.MangaSearchEntry.COLUMN_TITLE, manga.getmTitle());
                mListValues.add(cv);
            }

            if (!mListValues.isEmpty()){
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(MangaContract.MangaSearchEntry.CONTENT_URI,null, null);

                contentResolver.bulkInsert(MangaContract.MangaSearchEntry.CONTENT_URI, mListValues.toArray(new ContentValues[mListValues.size()]));

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Cursor getFavoriteMangaListById(Context context, String mangaId){
        Uri uri = Uri.withAppendedPath(MangaContract.MangaInfoEntry.CONTENT_URI, mangaId);
        Cursor cursor = context.getContentResolver().query(uri,
                null,
                null,
                null,
                null);

        return cursor;
    }

    public static Cursor getRecentMangaList(Context context){
        Uri uri = MangaContract.MangaInfoRecentEntry.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(uri,
                null,
                null,
                null,
                null);

        return cursor;
    }

    public static void insertMangaFromMangaList(Context context, ArrayList<Manga> mangas){
        ArrayList<ContentValues> mListValues = new ArrayList<>();

        for (Manga manga : mangas){
            ContentValues cv = new ContentValues();
            cv.put(MangaContract.MangaSearchEntry.COLUMN_MANGA_ID, manga.getmId());
            cv.put(MangaContract.MangaSearchEntry.COLUMN_THUMBNAIL, manga.getResAvatar());
            cv.put(MangaContract.MangaSearchEntry.COLUMN_TITLE, manga.getmTitle());
            mListValues.add(cv);
        }

        if (!mListValues.isEmpty()){
            ContentResolver contentResolver = context.getContentResolver();
            contentResolver.delete(MangaContract.MangaSearchEntry.CONTENT_URI, null, null);

            contentResolver.bulkInsert(MangaContract.MangaSearchEntry.CONTENT_URI, mListValues.toArray(new ContentValues[mListValues.size()]));

        }
    }

    public static void insertMangaInfo(Context context, MangaInfo mangaInfo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID, mangaInfo.getmId());
        contentValues.put(MangaContract.MangaInfoEntry.COLUMN_TITLE, mangaInfo.getmTitle());
        contentValues.put(MangaContract.MangaInfoEntry.COLUMN_THUMBNAIL, mangaInfo.getmResAvatar());
        contentValues.put(MangaContract.MangaInfoEntry.COLUMN_CATEROGY, mangaInfo.getmCategory());
        contentValues.put(MangaContract.MangaInfoEntry.COLUMN_DESCRIPTION, mangaInfo.getmDescription());
        context.getContentResolver().insert(MangaContract.MangaInfoEntry.CONTENT_URI, contentValues);

    }

    public static void insertMangaInfoRecent(Context context, MangaInfo mangaInfo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_MANGAINFO_ID, mangaInfo.getmId());
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_TITLE, mangaInfo.getmTitle());
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_THUMBNAIL, mangaInfo.getmResAvatar());
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_CATEROGY, mangaInfo.getmCategory());
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_DESCRIPTION, mangaInfo.getmDescription());
        context.getContentResolver().insert(MangaContract.MangaInfoRecentEntry.CONTENT_URI, contentValues);

    }

    public static String convertStringArrayIntoString(String[] str){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length - 1; i++){
            builder.append(str[i] + ", ");
        }
        builder.append(str[str.length - 1]);

        return builder.toString();
    }

}
