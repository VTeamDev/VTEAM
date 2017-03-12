package info.vteam.vmangaandroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by YukiNoHara on 3/8/2017.
 */

public class MangaDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "manga.db";
    public static final int DATABASE_VERSION = 14;

    public MangaDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MANGA_TABLE = "CREATE TABLE " + MangaContract.MangaEntry.TABLE_NAME + " (" +
                MangaContract.MangaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MangaContract.MangaEntry.COLUMN_MANGA_ID + " TEXT NOT NULL UNIQUE, " +
                MangaContract.MangaEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                MangaContract.MangaEntry.COLUMN_TITLE + " TEXT NOT NULL" + ");";

        final String SQL_CREATE_MANGA_INFO_FAVORITE_TABLE = "CREATE TABLE " + MangaContract.MangaInfoEntry.TABLE_NAME + " (" +
                MangaContract.MangaInfoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID + " TEXT NOT NULL UNIQUE, " +
                MangaContract.MangaInfoEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                MangaContract.MangaInfoEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MangaContract.MangaInfoEntry.COLUMN_CATEROGY + " TEXT NOT NULL, " +
                MangaContract.MangaInfoEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL" + ");";

        final String SQL_CREATE_MANGA_SEARCH_TABLE = "CREATE TABLE " + MangaContract.MangaSearchEntry.TABLE_NAME + " (" +
                MangaContract.MangaEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MangaContract.MangaEntry.COLUMN_MANGA_ID + " TEXT NOT NULL UNIQUE, " +
                MangaContract.MangaEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                MangaContract.MangaEntry.COLUMN_TITLE + " TEXT NOT NULL" + ");";

        final String SQL_CREATE_MANGA_INFO_RECENT_TABLE = "CREATE TABLE " + MangaContract.MangaInfoRecentEntry.TABLE_NAME + " (" +
                MangaContract.MangaInfoRecentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MangaContract.MangaInfoRecentEntry.COLUMN_MANGAINFO_ID + " TEXT NOT NULL UNIQUE, " +
                MangaContract.MangaInfoRecentEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                MangaContract.MangaInfoRecentEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MangaContract.MangaInfoRecentEntry.COLUMN_CATEROGY + " TEXT NOT NULL, " +
                MangaContract.MangaInfoRecentEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL" + ");";

        db.execSQL(SQL_CREATE_MANGA_TABLE);
        Log.e("SQL_CREATE_MANGA_TABLE", SQL_CREATE_MANGA_TABLE);
        db.execSQL(SQL_CREATE_MANGA_INFO_FAVORITE_TABLE);
        Log.e("SQL_CREATE_MANGAINFO", SQL_CREATE_MANGA_INFO_FAVORITE_TABLE);
        db.execSQL(SQL_CREATE_MANGA_SEARCH_TABLE);
        Log.e("SQL_CREATE_MANGA_SEARCH", SQL_CREATE_MANGA_SEARCH_TABLE);
        db.execSQL(SQL_CREATE_MANGA_INFO_RECENT_TABLE);
        Log.e("SQL_CREATE_MANGA_RECENT", SQL_CREATE_MANGA_INFO_RECENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MangaContract.MangaEntry.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + MangaContract.MangaInfoEntry.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + MangaContract.MangaSearchEntry.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + MangaContract.MangaInfoRecentEntry.TABLE_NAME + ";");
        onCreate(db);

    }
}
