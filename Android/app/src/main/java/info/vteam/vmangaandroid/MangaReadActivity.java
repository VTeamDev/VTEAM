package info.vteam.vmangaandroid;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.Preference;
import android.support.annotation.IntegerRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import info.vteam.vmangaandroid.databinding.ActivityMangaReadBinding;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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

    private String mangaId;
    private int chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_manga_read);

        mangaReadBinding = DataBindingUtil.setContentView(this, R.layout.activity_manga_read);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(VIEW_MODE, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

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

        LoaderCallbacks<String[]> callbacks = MangaReadActivity.this;

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra("manga_id")) {
            mangaId = intent.getStringExtra("manga_id");
            chapter = Integer.parseInt(intent.getStringExtra("chapter"));

            if(mangaId == null || chapter - 1 < 0) {
                startActivity(new Intent(this, MainActivity.class));
            }
        }

        SharedPreferences userIsReadingPrefs = getSharedPreferences("USER_MODE", MODE_PRIVATE);
        final String userId = userIsReadingPrefs.getString("user_id", null);

        if(userId != null) {

            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    OkHttpClient client = new OkHttpClient();
                    Uri userReadingUri = new Uri.Builder()
                            .scheme("http")
                            .authority("wannashare.info")
                            .appendPath("api")
                            .appendPath("v1")
                            .appendPath("list")
                            .appendPath(mangaId)
                            .build();
                    try {
                        URL userReadingURL = new URL(userReadingUri.toString());
                        Request request = new Request.Builder()
                                .url(userReadingURL)
                                .build();
                        Response response = client.newCall(request).execute();
                        JSONObject result = new JSONObject(response.body().string());
                       return result.getString("_id");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(final String s) {
                    if(s!= null) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                Log.e("GET CALLED", "DONE");
                                OkHttpClient client = new OkHttpClient();
                                Uri userReadingUri = new Uri.Builder()
                                        .scheme("http")
                                        .authority("wannashare.info")
                                        .appendPath("api")
                                        .appendPath("v1")
                                        .appendPath("realtime")
                                        .build();
                                try {
                                    RequestBody fromBody = new FormBody.Builder()
                                            .add("user", userId)
                                            .add("manga", s)
                                            .build();
                                    URL userReadingURL = new URL(userReadingUri.toString());
                                    Request request = new Request.Builder()
                                            .url(userReadingURL)
                                            .post(fromBody)
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    Log.e("RESPONSE", response.body().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        }.execute();
                    }
                }
            }.execute();
        }

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
                        .appendPath(mangaId)
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
                    JSONObject chapterJsonObject = chaptersJsonArrays.getJSONObject(chapter - 1);
                    contentJsonArrays = chapterJsonObject.getJSONArray("content");

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
        } else if(id == android.R.id.home) {
            super.onBackPressed();
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
