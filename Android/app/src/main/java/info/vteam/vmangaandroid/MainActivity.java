package info.vteam.vmangaandroid;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import info.vteam.vmangaandroid.data.MangaContract;
import info.vteam.vmangaandroid.databinding.ActivityMainBinding;
import info.vteam.vmangaandroid.utils.DataUtils;

public class MainActivity extends AppCompatActivity implements MangaAdapter.MangaOnClickHandle,
        LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,
        View.OnClickListener, SearchView.OnCloseListener, SwipeRefreshLayout.OnRefreshListener{
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    MangaAdapter mAdapter;
    Context mContext;

    String totalChapter = null;

    private static final int MANGA_LOADER_ID = 1011;
    private static final int MANGA_SEARCH_LOADER_ID = 1102;

    private int mPosition = RecyclerView.NO_POSITION;
    private ActivityMainBinding mBinding;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mContext = MainActivity.this;
        mAdapter = new MangaAdapter(this, this);

        new MangaTask().execute();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mBinding.mangaListRv.setHasFixedSize(true);
        mBinding.mangaListRv.setLayoutManager(gridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_element_grid);
        mBinding.mangaListRv.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, true, 0));

        mBinding.mangaListRv.setAdapter(mAdapter);
        mBinding.refreshSwl.setOnRefreshListener(this);

        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.setting_icon));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView loginImageView = new ImageView(this);
        ImageView favoriteImageView = new ImageView(this);
        ImageView recentImageView = new ImageView(this);

        loginImageView.setImageDrawable(getResources().getDrawable(R.drawable.login_icon));
        favoriteImageView.setImageDrawable(getResources().getDrawable(R.drawable.favorite_icon));
        recentImageView.setImageDrawable(getResources().getDrawable(R.drawable.recent_icon));

        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(loginImageView).build())
                .addSubActionView(rLSubBuilder.setContentView(favoriteImageView).build())
                .addSubActionView(rLSubBuilder.setContentView(recentImageView).build())
                .attachTo(rightLowerButton)
                .build();

        loginImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().initLoader(MANGA_LOADER_ID, null, this);
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

    private class MangaTask extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            totalChapter = DataUtils.insertDataFromResponse(mContext);
            return totalChapter;
        }

        @Override
        protected void onPostExecute(String string) {
            mBinding.infoListTv.setText("Blogtruyen, " + string + " manga");
        }
    }

    private class MangaSearchTask extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            Log.e(LOG_TAG, params[0]);
            DataUtils.insertDataFromResponseSearchByKey(mContext, params[0]);
            Log.e(LOG_TAG, "end called");
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    @Override
    public void onRefresh() {
        new MangaTask().execute();
        getSupportLoaderManager().initLoader(MANGA_LOADER_ID, null, this);
        mBinding.refreshSwl.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);

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
            getSupportLoaderManager().initLoader(MANGA_SEARCH_LOADER_ID, null, this);
        }
        return true;
    }

    @Override
    public boolean onClose() {
        Log.e(LOG_TAG, "OnClose");
//        getSupportLoaderManager().initLoader(MANGA_LOADER_ID, null, this);
        return false;
    }
}
