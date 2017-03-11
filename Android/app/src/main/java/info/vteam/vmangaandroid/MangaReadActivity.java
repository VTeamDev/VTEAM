package info.vteam.vmangaandroid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import info.vteam.vmangaandroid.databinding.ActivityMangaReadBinding;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MangaReadActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String[]>,
        SharedPreferences.OnSharedPreferenceChangeListener {

    //http://wannashare.info/api/v1/manga/14183
    private static final String LOG_TAG = MangaReadActivity.class.getSimpleName();
    private static final int READ_MANGA_LOADER_ID = 232;
    private static final String VIEW_MODE = "VIEW_MODE";
    ActivityMangaReadBinding mangaReadBinding;
    private MangaReadAdapter mangaReadAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_manga_read);

        sharedPreferences = getSharedPreferences(VIEW_MODE, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        mangaReadBinding = DataBindingUtil.setContentView(this, R.layout.activity_manga_read);

        linearLayoutManager = new LinearLayoutManager(this,
                sharedPreferences.getInt(VIEW_MODE, 0) == 0 ? LinearLayoutManager.HORIZONTAL
                        : LinearLayoutManager.VERTICAL, false);

        mangaReadBinding.mangaReadRv.setLayoutManager(linearLayoutManager);
        mangaReadBinding.mangaReadRv.setHasFixedSize(false);
        mangaReadBinding.mangaReadRv.setItemViewCacheSize(20);
        mangaReadBinding.mangaReadRv.setDrawingCacheEnabled(true);
        mangaReadBinding.mangaReadRv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mangaReadBinding.mangaReadRv);

        mangaReadAdapter = new MangaReadAdapter();
        mangaReadBinding.mangaReadRv.setAdapter(mangaReadAdapter);

        LoaderManager.LoaderCallbacks<String[]> callbacks = MangaReadActivity.this;

        getSupportLoaderManager().initLoader(READ_MANGA_LOADER_ID, null, callbacks);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            String[] resultStrArray = null;

            @Override
            protected void onStartLoading() {
                if(resultStrArray != null) {
                    deliverResult(resultStrArray);
                } else {
                    mangaReadBinding.loadingPb.setVisibility(View.VISIBLE);
                    mangaReadBinding.mangaReadRv.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                OkHttpClient client = new OkHttpClient();

                Uri mReadMangaUri = new Uri.Builder()
                        .scheme("http")
                        .authority("wannashare.info")
                        .appendPath("api")
                        .appendPath("v1")
                        .appendPath("manga")
                        .appendPath("14183")
                        .build();

                try {
                    URL mReadMangaUrl = new URL(mReadMangaUri.toString());

                    Request request = new Request.Builder()
                            .url(mReadMangaUrl)
                            .build();

                    Response response = client
                            .newCall(request)
                            .execute();

                    JSONObject readMangaJsonObject = new JSONObject(response.body().string());
                    JSONArray chaptersJsonArrays = readMangaJsonObject.getJSONArray("chapters");
                    JSONArray contentJsonArrays = null;

                    // TODO Get param from the intent!
                    for(int i=0;i<chaptersJsonArrays.length();i++) {
                        JSONObject chapterJsonObject = chaptersJsonArrays.getJSONObject(i);
                        if (chapterJsonObject.getString("name").equals("chap 1")) {
                            contentJsonArrays = chapterJsonObject.getJSONArray("content");
                            break;
                        }
                    }

                    if(contentJsonArrays == null) {
                        return null;
                    }

                    String[] result = new String[contentJsonArrays.length()];
                    for(int i=0;i< contentJsonArrays.length();i++) {
                        result[i] = contentJsonArrays.getString(i);
                    }

                    return result;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                return new String[0];
            }

            @Override
            public void deliverResult(String[] data) {
                resultStrArray = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        //mangaReadBinding.loadingPb.setVisibility(View.INVISIBLE);
        mangaReadAdapter.setMangaPagesList(data);

        if(data != null && data.length > 0) {
            for(String url: data) {
                Picasso.with(this).load(url).fetch();
            }
            showResult();
        } else {
            hideResult();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int viewMode = sharedPreferences.getInt(VIEW_MODE, 0);
        linearLayoutManager.setOrientation(viewMode == 0 ? LinearLayoutManager.HORIZONTAL
                : LinearLayoutManager.VERTICAL);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_manga_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_change_orientation) {
            new AlertDialog.Builder(this)
                    .setTitle("View mode")
                    .setSingleChoiceItems(R.array.pref_view_mode,
                            sharedPreferences.getInt(VIEW_MODE, 0),
                            new Dialog.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt(VIEW_MODE, 0);
                                            editor.apply();
                                            break;
                                        }
                                        case 1: {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt(VIEW_MODE, 1);
                                            editor.apply();
                                            break;
                                        }
                                        default:
                                            break;
                                    }
                                }
                            }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResult() {
        mangaReadBinding.mangaReadRv.setVisibility(View.VISIBLE);
    }

    private void hideResult() {
        mangaReadBinding.mangaReadRv.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
