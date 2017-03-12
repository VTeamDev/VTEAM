package info.vteam.vmangaandroid;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SnapHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import info.vteam.vmangaandroid.data.MangaContract;
import info.vteam.vmangaandroid.databinding.ActivityMainBinding;
import info.vteam.vmangaandroid.model.Manga;
import info.vteam.vmangaandroid.utils.DataUtils;
import info.vteam.vmangaandroid.utils.NetworkUtils;
import io.socket.client.IO;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Socket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements MangaAdapter.MangaOnClickHandle,
        LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, SharedPreferences.OnSharedPreferenceChangeListener{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    MangaAdapter mAdapter;
    Context mContext;

    String totalChapter = null;

    private static final int MANGA_LOADER_ID = 1011;
    private static final int MANGA_SEARCH_LOADER_ID = 1102;

    private int mPosition = RecyclerView.NO_POSITION;
    private ActivityMainBinding mBinding;
    private final String SORT_MODE = "sort_mode";
    private SharedPreferences sharedPreferences;

    public static final String[] MAIN_MANGA_PROJECTION = {
            MangaContract.MangaEntry._ID,
            MangaContract.MangaEntry.COLUMN_MANGA_ID,
            MangaContract.MangaEntry.COLUMN_THUMBNAIL,
            MangaContract.MangaEntry.COLUMN_TITLE
    };

    public static final String[] MAIN_MANGA_SEARCH_PROJECTION = {
            MangaContract.MangaEntry._ID,
            MangaContract.MangaEntry.COLUMN_MANGA_ID,
            MangaContract.MangaEntry.COLUMN_THUMBNAIL,
            MangaContract.MangaEntry.COLUMN_TITLE
    };

    public static final int INDEX_MANGA_ID = 0;
    public static final int INDEX_MANGA_IDMANGA = 1;
    public static final int INDEX_MANGA_THUMBNAIL = 2;
    public static final int INDEX_MANGA_TITLE = 3;

    public static final int INDEX_MANGA_SEARCH_ID = 0;
    public static final int INDEX_MANGA_SEARCH_IDMANGA = 1;
    public static final int INDEX_MANGA_SEARCH_THUMBNAIL = 2;
    public static final int INDEX_MANGA_SEARCH_TITLE = 3;

    private LoaderManager.LoaderCallbacks callbacks;

    private boolean isCreate = false;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        mContext = MainActivity.this;
        mAdapter = new MangaAdapter(this, this);

        LinearLayoutManager adsLinearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBinding.adsRv.setLayoutManager(adsLinearLayoutManager);
        mBinding.adsRv.setAdapter(new AdsAdapter());

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(mBinding.adsRv);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mBinding.mangaListRv.setHasFixedSize(true);
        mBinding.mangaListRv.setLayoutManager(gridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_element_grid);
        mBinding.mangaListRv.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, true, 0));

        mBinding.mangaListRv.setAdapter(mAdapter);
        mBinding.refreshSwl.setOnRefreshListener(this);
        mBinding.sortTv.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(SORT_MODE, Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                // TODO Done getting token
                if(currentAccessToken != null) {
                    Log.i("ACCESS_TOKEN", currentAccessToken.getToken());
                }
                //currentAccessToken.getPermissions()
                //currentAccessToken.getUserId()
                //currentAccessToken.isExpired()
            }
        };

        // TODO Get token everywhere
        // If the access token is available already assign it.
        // accessToken = AccessToken.getCurrentAccessToken();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                //currentProfile.getFirstName()
                // TODO Get whatever information you want
            }
        };

        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings_big));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setBackgroundDrawable(R.drawable.my_floating_button)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        final ImageView loginImageView = new ImageView(this);
        loginImageView.setBackground(getResources().getDrawable(R.drawable.my_floating_button));
        ImageView favoriteImageView = new ImageView(this);
        favoriteImageView.setBackground(getResources().getDrawable(R.drawable.my_floating_button));
        ImageView recentImageView = new ImageView(this);
        recentImageView.setBackground(getResources().getDrawable(R.drawable.my_floating_button));
        ImageView mapImageView = new ImageView(this);
        mapImageView.setBackground(getResources().getDrawable(R.drawable.my_floating_button));

        if(AccessToken.getCurrentAccessToken() != null) {
            loginImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_exit_to_app_black_24dp));
        } else {
            loginImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add));
        }
        favoriteImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
        recentImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_access_time));
        mapImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_location_on));

        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(loginImageView).build())
                .addSubActionView(rLSubBuilder.setContentView(favoriteImageView).build())
                .addSubActionView(rLSubBuilder.setContentView(recentImageView).build())
                .addSubActionView(rLSubBuilder.setContentView(mapImageView).build())
                .attachTo(rightLowerButton)
                .build();

        loginImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AccessToken.getCurrentAccessToken() != null) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Logout")
                            .setMessage("Are you sure you want to logout?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LoginManager.getInstance().logOut();
                                    loginImageView.setImageDrawable(getResources()
                                            .getDrawable(R.drawable.ic_person_add));
                                }
                            }).setNegativeButton("Cancel", null)
                            .create()
                            .show();
                } else {
                    accessTokenTracker.startTracking();
                    profileTracker.startTracking();
                    LoginManager.getInstance().logInWithReadPermissions(MainActivity.this,
                            Arrays.asList("email"));
                    LoginManager.getInstance().registerCallback(callbackManager,
                            new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    final Uri loginUri = new Uri.Builder()
                                            .scheme("http")
                                            .authority("wannashare.info")
                                            .appendPath("auth")
                                            .appendPath("facebook")
                                            .appendPath("token")
                                            .appendQueryParameter("access_token",
                                                    loginResult.getAccessToken().getToken())
                                            .build();

                                    new AsyncTask<Void, Void, Boolean>() {
                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                        }

                                        @Override
                                        protected Boolean doInBackground(Void... params) {

                                            try {
                                                URL loginURL = new URL(loginUri.toString());

                                                OkHttpClient client = new OkHttpClient();
                                                Request request = new Request.Builder()
                                                        .url(loginURL)
                                                        .build();
                                                Response response = client.newCall(request)
                                                        .execute();
                                                JSONObject responseResult =
                                                        new JSONObject(response.body().string());
                                                SharedPreferences sharedPreferences =
                                                        getApplicationContext()
                                                                .getSharedPreferences("USER_MODE", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                Log.d("RESULT", responseResult.getString("_id"));
                                                editor.putString("user_id", responseResult.getString("_id"));
                                                editor.apply();
                                                //Log.d("RESULT", response.body().string());
                                                return true;
                                            } catch (java.io.IOException e) {
                                                e.printStackTrace();
                                                return false;
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                return false;
                                            }
                                        }

                                        @Override
                                        protected void onPostExecute(Boolean isSuccess) {
                                            super.onPostExecute(isSuccess);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putBoolean("login", isSuccess);
                                            editor.apply();

                                            if(!isSuccess) {
                                                LoginManager.getInstance().logOut();
                                            } else {
                                                loginImageView.setImageDrawable(getResources()
                                                        .getDrawable(R.drawable.ic_exit_to_app_black_24dp));

                                            }
                                        }
                                    }.execute();
                                }

                                @Override
                                public void onCancel() {
                                    loginImageView.setImageDrawable(getResources()
                                            .getDrawable(R.drawable.ic_person_add));
                                }

                                @Override
                                public void onError(FacebookException error) {
                                    loginImageView.setImageDrawable(getResources()
                                            .getDrawable(R.drawable.ic_person_add));
                                }
                            });
                }
            }
        });

        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MangaFavoriteActivity.class);
                startActivity(intent);
            }
        });

        recentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MangaRecentActivity.class);
                startActivity(intent);
            }
        });

        mapImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MangaLocationActivity.class);
                startActivity(intent);
            }
        });

        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });

        showLoadingBar();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        callbacks = MainActivity.this;
        new MangaTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    public void showLoadingBar(){
        mBinding.mangaListRv.setVisibility(View.INVISIBLE);
        mBinding.loadingPb.setVisibility(View.VISIBLE);
    }

    public void hideLoadingBar(){
        mBinding.mangaListRv.setVisibility(View.VISIBLE);
        mBinding.loadingPb.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(long id) {
        Intent intent = new Intent(this, MangaDetailActivity.class);
        String mangaId = mAdapter.getMangaId(id);
        intent.putExtra("manga_id", mangaId);
        startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId){
            case MANGA_LOADER_ID:
                Uri queryUri = MangaContract.MangaEntry.CONTENT_URI;

                return new CursorLoader(this,
                        queryUri,
                        MAIN_MANGA_PROJECTION,
                        null,
                        null,
                        null);

            case MANGA_SEARCH_LOADER_ID:
                Uri queryUriSearch = MangaContract.MangaSearchEntry.CONTENT_URI;

                return new CursorLoader(this,
                        queryUriSearch,
                        MAIN_MANGA_SEARCH_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader not implemented: " + loaderId);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mBinding.mangaListRv.smoothScrollToPosition(mPosition);

        if (data.getCount() != 0) hideLoadingBar();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        new MangaTask().execute();
    }

    private class MangaTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            totalChapter = DataUtils.insertDataFromResponse(mContext);
            return totalChapter;
        }

        @Override
        protected void onPostExecute(String string) {
            if (string == null && sharedPreferences.getInt(SORT_MODE, 0) == 1){
                mBinding.infoListTv.setText(getString(R.string.pref_sort_top));
            } else if(string == null && sharedPreferences.getInt(SORT_MODE, 0) == 2) {
                mBinding.infoListTv.setText(getString(R.string.pref_sort_latest));
            } else if (string == null && sharedPreferences.getInt(SORT_MODE, 0) == 3){
                mBinding.infoListTv.setText(getString(R.string.pref_sort_recommend));
            } else {
                mBinding.infoListTv.setText(string + " manga");
            }
            if (!isCreate){
                getSupportLoaderManager().initLoader(MANGA_LOADER_ID, null, callbacks);
                isCreate = true;
            } else {
                getSupportLoaderManager().destroyLoader(MANGA_LOADER_ID);
                getSupportLoaderManager().restartLoader(MANGA_LOADER_ID, null, callbacks);
            }
        }
    }

    private class MangaSearchTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            Log.e(LOG_TAG, params[0]);
            DataUtils.insertDataFromResponseSearchByKey(mContext, params[0]);
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sort_tv){
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.action_sort))
                    .setSingleChoiceItems(R.array.pref_sort_mode,
                            sharedPreferences.getInt(SORT_MODE, 0),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:{
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt(SORT_MODE, 0);
                                            editor.apply();
                                            break;
                                        }
                                        case 1: {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt(SORT_MODE, 1);
                                            editor.apply();
                                            break;
                                        }
                                        case 2:{
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt(SORT_MODE, 2);
                                            editor.apply();
                                            break;
                                        }
                                        case 3: {
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt(SORT_MODE, 3);
                                            editor.apply();
                                            break;
                                        }
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).create().show();
        }
    }

    @Override
    public void onRefresh() {
        new MangaTask().execute();
        getSupportLoaderManager().destroyLoader(MANGA_LOADER_ID);
        getSupportLoaderManager().restartLoader(MANGA_LOADER_ID, null, this);
        mBinding.refreshSwl.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search),
                new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                LoaderManager.LoaderCallbacks callbacks = MainActivity.this;
                getSupportLoaderManager().destroyLoader(MANGA_SEARCH_LOADER_ID);
                getSupportLoaderManager().restartLoader(MANGA_LOADER_ID, null, callbacks);
                return true;
            }
        });

        return true;
    }

    /**
     * Will be called when user press the submit button (Currently just let it empty)
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e(LOG_TAG, "onQueryTextSubmit");
        return false;
    }

    /**
     * Changing the searchview's text
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)){
            new MangaSearchTask().execute(newText);
            Log.e(LOG_TAG, newText);
            getSupportLoaderManager().restartLoader(MANGA_SEARCH_LOADER_ID, null, this);
        }
        return true;
    }

}
