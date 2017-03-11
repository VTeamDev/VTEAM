package info.vteam.vmangaandroid;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;

import info.vteam.vmangaandroid.data.MangaContract;
import info.vteam.vmangaandroid.databinding.ActivityMainBinding;
import info.vteam.vmangaandroid.models.Manga;
import info.vteam.vmangaandroid.utils.DataUtils;

public class MangaRecentActivity extends AppCompatActivity implements MangaAdapter.MangaOnClickHandle,
        LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,
        View.OnClickListener, SearchView.OnCloseListener, SwipeRefreshLayout.OnRefreshListener{
    ActivityMainBinding mBinding;

    private static final int MANGA_RECENT_LOADER_ID = 2111;
    private static final int MANGA_SEARCH_RECENT_LOADER_ID = 2112;

    private int mPosition = RecyclerView.NO_POSITION;

    private MangaAdapter mAdapter;

    private static final String[] MANGA_INFO_PROJECTION = {
            MangaContract.MangaInfoRecentEntry._ID,
            MangaContract.MangaInfoRecentEntry.COLUMN_MANGAINFO_ID,
            MangaContract.MangaInfoRecentEntry.COLUMN_THUMBNAIL,
            MangaContract.MangaInfoRecentEntry.COLUMN_TITLE,
            MangaContract.MangaInfoRecentEntry.COLUMN_CATEROGY,
            MangaContract.MangaInfoRecentEntry.COLUMN_DESCRIPTION
    };

    public static final String[] MAIN_MANGA_SEARCH_PROJECTION = {
            MangaContract.MangaSearchEntry._ID,
            MangaContract.MangaSearchEntry.COLUMN_MANGA_ID,
            MangaContract.MangaSearchEntry.COLUMN_THUMBNAIL,
            MangaContract.MangaSearchEntry.COLUMN_TITLE
    };

    private static final int INDEX_MANGA_INFO_ID = 0;
    private static final int INDEX_MANGA_INFO_IDMANGA = 1;
    private static final int INDEX_MANGA_INFO_THUMBNAIL = 2;
    private static final int INDEX_MANGA_INFO_TITLE = 3;
    private static final int INDEX_MANGA_INFO_CATEGORY = 0;
    private static final int INDEX_MANGA_INFO_DESCRIPTION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mAdapter = new MangaAdapter(this, this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mBinding.mangaListRv.setHasFixedSize(true);
        mBinding.mangaListRv.setLayoutManager(gridLayoutManager);
        mBinding.mangaListRv.setAdapter(mAdapter);

        mBinding.refreshSwl.setOnRefreshListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().initLoader(MANGA_RECENT_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id){
            case MANGA_RECENT_LOADER_ID:
                Uri uriQuery = MangaContract.MangaInfoRecentEntry.CONTENT_URI;

                return new CursorLoader(this,
                        uriQuery,
                        MANGA_INFO_PROJECTION,
                        null,
                        null,
                        null);

            case MANGA_SEARCH_RECENT_LOADER_ID:
                Uri uri = MangaContract.MangaSearchEntry.CONTENT_URI;

                return new CursorLoader(this,
                        uri,
                        MAIN_MANGA_SEARCH_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mBinding.mangaListRv.smoothScrollToPosition(mPosition);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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

    @Override
    public void onRefresh() {
        getSupportLoaderManager().initLoader(MANGA_RECENT_LOADER_ID, null, this);
        mBinding.refreshSwl.setRefreshing(false);
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)){
//            Log.e("CHECK NULL", "not null");
            ArrayList<Manga> list = mAdapter.getMangaByKey(newText);
//            Log.e("CHECK", "1");
            DataUtils.insertMangaFromMangaList(this, list);
            getSupportLoaderManager().initLoader(MANGA_SEARCH_RECENT_LOADER_ID, null, this);
        }
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(long id) {
        String mangId = mAdapter.getMangaId(id);
        Intent intent = new Intent(this, MangaDetailActivity.class);
        intent.putExtra("manga_id", mangId);
        startActivity(intent);
    }
}
