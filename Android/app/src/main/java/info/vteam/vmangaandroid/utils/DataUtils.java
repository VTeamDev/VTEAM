package info.vteam.vmangaandroid.utils;

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
import info.vteam.vmangaandroid.models.Manga;
import info.vteam.vmangaandroid.models.MangaInfo;

/**
 * Created by YukiNoHara on 3/10/2017.
 */

public class DataUtils {
    public static final String TOTAL_PARAMS = "total";
    public static final String DATA_PARAMS = "data";
    public static final String ID_PARAMS = "manga_id";
    public static final String THUMBNAIL_PARAMS = "thumbnail";
    public static final String TITLE_PARAMS = "title";
    public static final String CATEGORY_PARAMS = "category";
    public static final String DESCRIPTION_PARAMS = "content";
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

    public static void insertFakeData(Context context){

        ArrayList<ContentValues> list = new ArrayList<>();
        for (int i = 0; i < 10; i++){

            ContentValues cv = new ContentValues();
            cv.put(MangaContract.MangaEntry.COLUMN_MANGA_ID, "111");
            cv.put(MangaContract.MangaEntry.COLUMN_THUMBNAIL, "http://1.bp.blogspot.com/-83h_elrGRv4/V3lYr3bpOgI/AAAAAAAQAR8/9gJ0jmvL4Rs/s300-p/j002.jpg");
            cv.put(MangaContract.MangaEntry.COLUMN_TITLE, "Kanojo to Kanojo no Neko");;
            list.add(cv);
        }

        context.getContentResolver().bulkInsert(MangaContract.MangaEntry.CONTENT_URI, list.toArray(new ContentValues[10]));

    }

    public static String insertDataFromResponse(Context context){
        try {
            URL url= null;
            sharedPreferences = context.getSharedPreferences("sort_mode", Context.MODE_PRIVATE);
            int mode = sharedPreferences.getInt("sort_mode", 0);
            Log.e("MODE", String.valueOf(mode));
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
            Log.e("LIST SIZE", String.valueOf(mList.size()));
            Log.e("LIST TYPE", String.valueOf(mList.get(0) instanceof Manga));
            String totalChapter = null;
            if (mode == 0){
                totalChapter = getTotalChapterFromResponse(response);
            } else {
                totalChapter = null;
            }

            Log.e("ChAP", "success");
            ArrayList<ContentValues> mListValues = new ArrayList<>();

            for (Manga manga : mList){
                Log.e("INSERT", "true");
                ContentValues cv = new ContentValues();
                cv.put(MangaContract.MangaEntry.COLUMN_MANGA_ID, manga.getmId());
                cv.put(MangaContract.MangaEntry.COLUMN_THUMBNAIL, manga.getResAvatar());
                cv.put(MangaContract.MangaEntry.COLUMN_TITLE, manga.getmTitle());
                mListValues.add(cv);
            }

            if (!mListValues.isEmpty()){
                Log.e("DELETE", "true");
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
            Log.e("URL", url.toString());

            String response = NetworkUtils.getResponseFromUrl(context, url);
            Log.e("Response", response);

            ArrayList<Manga> mList = getMangaListFromResponse(response);

            Log.e("Size", String.valueOf(mList.size()));

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
                contentResolver.delete(MangaContract.MangaEntry.CONTENT_URI,null, null);

                contentResolver.bulkInsert(MangaContract.MangaSearchEntry.CONTENT_URI, mListValues.toArray(new ContentValues[mListValues.size()]));

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void insertMangaInfoFromResponse(Context context, String idManga){
        MangaInfo mangaInfo;
        URL url = null;
        try {
            url = NetworkUtils.getUrlWithConditionAndId(context, "info", idManga);
            String json = NetworkUtils.getResponseFromUrl(context, url);

            mangaInfo = DataUtils.getMangaInfoFromResponse(json);

            ContentValues contentValues = new ContentValues();
            Log.e("MANGA INFO", mangaInfo.toString());
            contentValues.put(MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID, mangaInfo.getmId());
            contentValues.put(MangaContract.MangaInfoEntry.COLUMN_TITLE, mangaInfo.getmTitle());
            contentValues.put(MangaContract.MangaInfoEntry.COLUMN_CATEROGY, mangaInfo.getmCategory());
            contentValues.put(MangaContract.MangaInfoEntry.COLUMN_THUMBNAIL, mangaInfo.getmResAvatar());
            contentValues.put(MangaContract.MangaInfoEntry.COLUMN_DESCRIPTION, mangaInfo.getmDescription());

            context.getContentResolver().insert(MangaContract.MangaInfoEntry.CONTENT_URI, contentValues);

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
        Log.e("CURSOR COUNT", String.valueOf(cursor.getCount()));

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
//        contentValues.put(MangaContract.MangaInfoEntry.COLUMN_TYPE, type);
        context.getContentResolver().insert(MangaContract.MangaInfoEntry.CONTENT_URI, contentValues);

    }

    public static void insertMangaInfoRecent(Context context, MangaInfo mangaInfo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_MANGAINFO_ID, mangaInfo.getmId());
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_TITLE, mangaInfo.getmTitle());
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_THUMBNAIL, mangaInfo.getmResAvatar());
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_CATEROGY, mangaInfo.getmCategory());
        contentValues.put(MangaContract.MangaInfoRecentEntry.COLUMN_DESCRIPTION, mangaInfo.getmDescription());
//        contentValues.put(MangaContract.MangaInfoEntry.COLUMN_TYPE, type);
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
