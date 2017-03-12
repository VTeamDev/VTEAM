package info.vteam.vmangaandroid.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by YukiNoHara on 3/8/2017.
 */

public class MangaProvider extends ContentProvider{
    public static final int CODE_MANGA = 100;
    public static final int CODE_MANGAINFO = 200;
    public static final int CODE_MANGA_WITH_ID = 101;
    public static final int CODE_MANGAINFO_WITH_ID = 201;
    public static final int CODE_MANGA_SEARCH = 300;
    public static final int CODE_MANGA_INFO_RECENT = 400;
    public static final int CODE_MANGA_INFO_RECENT_WITH_ID = 401;

    MangaDbHelper mOpenHelper;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA, CODE_MANGA);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA + "/*", CODE_MANGA_WITH_ID);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA_INFO, CODE_MANGAINFO);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA_INFO + "/*", CODE_MANGAINFO_WITH_ID);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA_SEARCH, CODE_MANGA_SEARCH);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA_INFO_RECENT, CODE_MANGA_INFO_RECENT);
        uriMatcher.addURI(MangaContract.AUTHORITY, MangaContract.PATH_MANGA_INFO_RECENT + "/*", CODE_MANGA_INFO_RECENT_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MangaDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_MANGA:
                cursor = db.query(MangaContract.MangaEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MANGAINFO:
                cursor = db.query(MangaContract.MangaInfoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MANGA_WITH_ID:
                String idManga = uri.getLastPathSegment();
                String[] mSelectionArgsManga = new String[]{idManga};
                cursor = db.query(MangaContract.MangaEntry.TABLE_NAME,
                        projection,
                        MangaContract.MangaEntry.COLUMN_MANGA_ID + " =? ",
                        mSelectionArgsManga,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_MANGAINFO_WITH_ID:
                String idMangaInfo = uri.getLastPathSegment();
                String[] mSelectionArgsMangaInfo = new String[]{idMangaInfo};
                cursor = db.query(MangaContract.MangaInfoEntry.TABLE_NAME,
                        projection,
                        MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID + " =? ",
                        mSelectionArgsMangaInfo,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MANGA_SEARCH:
                cursor = db.query(MangaContract.MangaSearchEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case CODE_MANGA_INFO_RECENT:
                cursor = db.query(MangaContract.MangaInfoRecentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri = null;
        switch (match){
            case CODE_MANGA:
                long _id = db.insert(MangaContract.MangaEntry.TABLE_NAME, null, values);
                if (_id > 0){
                    returnUri = ContentUris.withAppendedId(MangaContract.MangaEntry.CONTENT_URI,_id);
                }
                break;

            case CODE_MANGA_SEARCH:
                long _idSearch = db.insert(MangaContract.MangaSearchEntry.TABLE_NAME, null, values);
                if (_idSearch > 0){
                    returnUri = ContentUris.withAppendedId(MangaContract.MangaSearchEntry.CONTENT_URI, _idSearch);
                }
                break;

            case CODE_MANGAINFO:
                long _idMangaInfo = db.insert(MangaContract.MangaInfoEntry.TABLE_NAME, null, values);
                if (_idMangaInfo > 0){
                    returnUri = ContentUris.withAppendedId(MangaContract.MangaInfoEntry.CONTENT_URI, _idMangaInfo);
                }
                break;

            case CODE_MANGA_INFO_RECENT:
                long _idMangaInfoRecent = db.insert(MangaContract.MangaInfoRecentEntry.TABLE_NAME, null, values);
                if (_idMangaInfoRecent > 0){
                    returnUri = ContentUris.withAppendedId(MangaContract.MangaInfoRecentEntry.CONTENT_URI, _idMangaInfoRecent);
                }
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match){
            case CODE_MANGA:
                db.beginTransaction();
                int rowMangaInserted = 0;
                try{
                    for (ContentValues cv : values){
                        long _id_manga = db.insert(MangaContract.MangaEntry.TABLE_NAME, null, cv);
                        if (_id_manga != -1) rowMangaInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowMangaInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowMangaInserted;

            case CODE_MANGAINFO:
                db.beginTransaction();
                int rowMangaInfoInserted = 0;
                try {
                    for (ContentValues cv : values){
                        long _id_manga_info = db.insert(MangaContract.MangaInfoEntry.TABLE_NAME, null, cv);
                        if (_id_manga_info != -1) rowMangaInfoInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowMangaInfoInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowMangaInfoInserted;
            case CODE_MANGA_SEARCH:
                db.beginTransaction();
                int rowMangaSearchInserted = 0;
                try{
                    for (ContentValues cv : values){
                        long _id_manga = db.insert(MangaContract.MangaSearchEntry.TABLE_NAME, null, cv);
                        if (_id_manga != -1) rowMangaSearchInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                if (rowMangaSearchInserted > 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowMangaSearchInserted;

            default:
                return super.bulkInsert(uri, values);

        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowDeleted;
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        if (selection == null) selection = "1";

        switch (match){
            case CODE_MANGA:
                rowDeleted = db.delete(MangaContract.MangaEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case CODE_MANGAINFO:
                rowDeleted = db.delete(MangaContract.MangaInfoEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case CODE_MANGAINFO_WITH_ID: {
                String mangaInfoId = uri.getLastPathSegment();
                String[] mangaInfoIdStr = new String[]{mangaInfoId};
                rowDeleted = db.delete(MangaContract.MangaInfoEntry.TABLE_NAME,
                        MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID + " =?",
                        mangaInfoIdStr);
                break;
            }

            case CODE_MANGA_SEARCH:
                rowDeleted = db.delete(MangaContract.MangaSearchEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case CODE_MANGA_INFO_RECENT:
                rowDeleted = db.delete(MangaContract.MangaInfoRecentEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case CODE_MANGA_INFO_RECENT_WITH_ID: {
                String id = uri.getLastPathSegment();
                String[] args = new String[]{id};
                rowDeleted = db.delete(MangaContract.MangaInfoRecentEntry.TABLE_NAME,
                        MangaContract.MangaInfoRecentEntry.COLUMN_MANGAINFO_ID + " =?",
                        args);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowDeleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return -1;
    }
}
