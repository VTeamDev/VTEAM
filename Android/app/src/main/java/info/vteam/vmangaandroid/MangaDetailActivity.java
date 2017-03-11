package info.vteam.vmangaandroid;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

import info.vteam.vmangaandroid.data.MangaContract;
import info.vteam.vmangaandroid.databinding.ActivityMangaDetailBinding;
import info.vteam.vmangaandroid.models.MangaInfo;
import info.vteam.vmangaandroid.utils.DataUtils;
import info.vteam.vmangaandroid.utils.NetworkUtils;

public class MangaDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MangaInfo>, View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String LOG_TAG = MangaDetailActivity.class.getSimpleName();

    ActivityMangaDetailBinding mDatabinding;

    String idManga;

    Context mContext;

    MangaInfo mangaInfo;

    private boolean isFavorite = false;

    private Cursor mCursor;

    private String favoriteStatus;

    private static final int MANGA_INFO_LOADER_ID = 1111;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;

    private static final String[] MANGA_INFO_PROJECTION = {
            MangaContract.MangaInfoEntry._ID,
            MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID,
            MangaContract.MangaInfoEntry.COLUMN_THUMBNAIL,
            MangaContract.MangaInfoEntry.COLUMN_TITLE,
            MangaContract.MangaInfoEntry.COLUMN_CATEROGY,
            MangaContract.MangaInfoEntry.COLUMN_DESCRIPTION
    };

    private static final int INDEX_MANGA_INFO_ID = 0;
    private static final int INDEX_MANGA_INFO_IDMANGA = 1;
    private static final int INDEX_MANGA_INFO_THUMBNAIL = 2;
    private static final int INDEX_MANGA_INFO_TITLE = 3;
    private static final int INDEX_MANGA_INFO_CATEGORY = 4;
    private static final int INDEX_MANGA_INFO_DESCRIPTION = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = MangaDetailActivity.this;
        mDatabinding = DataBindingUtil.setContentView(this, R.layout.activity_manga_detail);

        Intent intent = getIntent();
        idManga = intent.getStringExtra("manga_id");
        mDatabinding.favoriteBtn.setOnClickListener(this);
        getSupportLoaderManager().initLoader(MANGA_INFO_LOADER_ID, null, this);
        PreferenceManager.getDefaultSharedPreferences(mContext).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            getSupportLoaderManager().restartLoader(MANGA_INFO_LOADER_ID, null, this);
//            isFavorite = PreferencesUtils.isFavoriteManga(mContext);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }


        //  Log.e(LOG_TAG, String.valueOf(isFavorite));
        mCursor = DataUtils.getFavoriteMangaListById(mContext, idManga);
        if (mCursor.getCount() == 0){
            isFavorite = false;
        } else {
            isFavorite = true;
        }

        if (isFavorite){
            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.start_checked);
        } else {
            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.star);
        }

//        if (mCursor.getCount() == 0){
//            favoriteStatus = null;
//        } else {
//            favoriteStatus = mCursor.getString(INDEX_MANGA_INFO_FAVORITE);
//        }
//        if (favoriteStatus.equals("FALSE")){
//            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.star);
//        } else {
//            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.start_checked);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(mContext).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<MangaInfo> onCreateLoader(int id, Bundle args) {
//        switch (id){
//            case MANGA_INFO_LOADER_ID:
//                Uri uri = Uri.withAppendedPath(MangaContract.MangaInfoEntry.CONTENT_URI, idManga);
//
//                return new CursorLoader(this,
//                        uri,
//                        MANGA_INFO_PROJECTION,
//                        null,
//                        null,
//                        null);
//            default:
//                throw new RuntimeException("Loader not implemented: " + id);
//        }
        return new AsyncTaskLoader<MangaInfo>(this) {
            MangaInfo mangaInfo;

            @Override
            protected void onStartLoading() {
                if (mangaInfo != null){
                    deliverResult(mangaInfo);
                } else {
                    forceLoad();
                }
            }

            @Override
            public MangaInfo loadInBackground() {
                try {
                    URL url = NetworkUtils.getUrlWithConditionAndId(mContext, "info", idManga);

                    String json = NetworkUtils.getResponseFromUrl(mContext, url);

                    mangaInfo = DataUtils.getMangaInfoFromResponse(json);

                    return mangaInfo;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void deliverResult(MangaInfo data) {
                mangaInfo = data;
                super.deliverResult(data);
            }
        };
    }

    public void onFavoriteButtonClick(boolean isFavorite){
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFavorite){
            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.star);
            this.isFavorite = false;
        } else {
            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.start_checked);
            this.isFavorite = true;
        }
//        editor.putBoolean(mContext.getString(R.string.pref_favorite_key), isFavorite);
//        editor.commit();
    }

    //    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//
//        Picasso.with(this)
//                .load(data.getString(INDEX_MANGA_INFO_THUMBNAIL))
//                .fit()
//                .centerCrop()
//                .into(mDatabinding.mangaImageView);
//        mDatabinding.mangaTitleTextView.setText(data.getString(INDEX_MANGA_INFO_TITLE));
//        mDatabinding.mangaCategoryTextView.setText(data.getString(INDEX_MANGA_INFO_CATEGORY));
//        mDatabinding.mangeDescriptionTextView.setText(data.get);
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }
//
    @Override
    public void onLoadFinished(Loader<MangaInfo> loader, MangaInfo data) {
        mangaInfo = data;

        Picasso.with(this)
                .load(data.getmResAvatar())
                .fit()
                .centerCrop()
                .into(mDatabinding.mangaImageView);
        mDatabinding.mangaTitleTextView.setText(data.getmTitle());
        mDatabinding.mangaCategoryTextView.setText(data.getmCategory());
        mDatabinding.mangeDescriptionTextView.setText(data.getmDescription());
    }

    @Override
    public void onLoaderReset(Loader<MangaInfo> loader) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.favorite_btn:
                Log.e(LOG_TAG, String.valueOf(mCursor.getCount()));
                if (mCursor.getCount() == 0){
                    Log.e("FAVORITE", "added");
                    onFavoriteButtonClick(isFavorite);

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID, mangaInfo.getmId());
                    contentValues.put(MangaContract.MangaInfoEntry.COLUMN_TITLE, mangaInfo.getmTitle());
                    contentValues.put(MangaContract.MangaInfoEntry.COLUMN_THUMBNAIL, mangaInfo.getmResAvatar());
                    contentValues.put(MangaContract.MangaInfoEntry.COLUMN_CATEROGY, mangaInfo.getmCategory());
                    contentValues.put(MangaContract.MangaInfoEntry.COLUMN_DESCRIPTION, mangaInfo.getmDescription());

                    mContext.getContentResolver().insert(MangaContract.MangaInfoEntry.CONTENT_URI, contentValues);

                } else {
                    Log.e("FAVORITE", "deleted");
                    onFavoriteButtonClick(isFavorite);
                    Uri uri = Uri.withAppendedPath(MangaContract.MangaInfoEntry.CONTENT_URI, idManga);
                    mContext.getContentResolver().delete(uri, null, null);

                }
                // PreferencesUtils.setPreferences(mContext, mContext.getString(R.string.pref_favorite_key), isFavorite);
                mCursor = DataUtils.getFavoriteMangaListById(mContext, idManga);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }
}
