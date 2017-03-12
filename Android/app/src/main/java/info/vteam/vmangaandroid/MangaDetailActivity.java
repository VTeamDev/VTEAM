package info.vteam.vmangaandroid;

import android.content.ContentUris;
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
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import info.vteam.vmangaandroid.data.MangaContract;
import info.vteam.vmangaandroid.databinding.ActivityMangaDetailBinding;
import info.vteam.vmangaandroid.model.MangaInfo;
import info.vteam.vmangaandroid.utils.DataUtils;
import info.vteam.vmangaandroid.utils.NetworkUtils;
import info.vteam.vmangaandroid.utils.PreferencesUtils;

public class MangaDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<MangaInfo>, View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String LOG_TAG = MangaDetailActivity.class.getSimpleName();

    ActivityMangaDetailBinding mDatabinding;

    String idManga;

    Context mContext;

    MangaInfo mangaInfo;

    private boolean isFavorite = false;

    private Cursor mCursorFavorite;
    private Cursor mCursorRecent;

    private String favoriteStatus;

    ArrayAdapter<String> mAdapter;

    int chapterSize = 0;
    ArrayList<String> list = new ArrayList<>();

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        idManga = intent.getStringExtra("manga_id");
        mDatabinding.favoriteBtn.setOnClickListener(this);
        mAdapter = new ArrayAdapter<String>(this,
                R.layout.manga_chapter_item,
                R.id.chapter_tv,
                new ArrayList<String>());
//
        mDatabinding.chapterLv.setAdapter(mAdapter);
        mDatabinding.chapterLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String chapterRow = (String) mDatabinding.chapterLv.getItemAtPosition(position);
                String[] splitStr = chapterRow.split(" ");
                String chapter = splitStr[splitStr.length - 1];
                Intent intentRead = new Intent(MangaDetailActivity.this, MangaReadActivity.class);
                intentRead.putExtra("manga_id", idManga);
                intentRead.putExtra("chapter", chapter);
                Log.e("CHAPTER", chapter);
                Log.e("CHAPTER", idManga);
                startActivity(intentRead);
            }
        });
        mAdapter.clear();
        getSupportLoaderManager().initLoader(MANGA_INFO_LOADER_ID, null, this);
        PreferenceManager.getDefaultSharedPreferences(mContext).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            mAdapter.clear();
            getSupportLoaderManager().restartLoader(MANGA_INFO_LOADER_ID, null, this);
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }

        mCursorFavorite = DataUtils.getFavoriteMangaListById(mContext, idManga);
        if (mCursorFavorite.getCount() == 0){
            isFavorite = false;
        } else {
            isFavorite = true;
        }

        if (isFavorite){
            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.star_checked);
        } else {
            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.star);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(mContext).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Loader<MangaInfo> onCreateLoader(int id, Bundle args) {
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

                    chapterSize = DataUtils.getChapter(mContext, idManga);
                    if (!list.isEmpty()){
                        list.clear();
                    }

                    if (chapterSize > 0){
                        for (int i = 1; i <= chapterSize; i++){
                            list.add(mangaInfo.getmTitle() + " Chapter " + i);
                        }
                    }

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
        if (isFavorite){
            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.star);
            this.isFavorite = false;
        } else {
            mDatabinding.favoriteBtn.setBackgroundResource(R.drawable.star_checked);
            this.isFavorite = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(Loader<MangaInfo> loader, MangaInfo data) {
        mangaInfo = data;
        mAdapter.clear();
        mAdapter.addAll(list);

        DataUtils.insertMangaInfoRecent(mContext, mangaInfo);

        mCursorRecent = DataUtils.getRecentMangaList(mContext);
        Log.e(LOG_TAG, String.valueOf(mCursorRecent.getCount()));

        Picasso.with(this)
                .load(data.getmResAvatar())
                .fit()
                .placeholder(R.drawable.loading)
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
                Log.e(LOG_TAG, String.valueOf(mCursorFavorite.getCount()));
                if (mCursorFavorite.getCount() == 0){
                    onFavoriteButtonClick(isFavorite);
                    DataUtils.insertMangaInfo(mContext, mangaInfo);

                    Toast.makeText(this, "Add successfully", Toast.LENGTH_SHORT).show();

                } else {
                    onFavoriteButtonClick(isFavorite);
                    Uri uri = Uri.withAppendedPath(MangaContract.MangaInfoEntry.CONTENT_URI, idManga);
                    mContext.getContentResolver().delete(uri,
                            null,
                            null);
                }
                mCursorFavorite = DataUtils.getFavoriteMangaListById(mContext, idManga);
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }
}
