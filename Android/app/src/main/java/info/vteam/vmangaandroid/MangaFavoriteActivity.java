package info.vteam.vmangaandroid;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import info.vteam.vmangaandroid.data.MangaContract;
import info.vteam.vmangaandroid.databinding.ActivityMainBinding;
import info.vteam.vmangaandroid.databinding.ActivityMangaFavoriteBinding;
import info.vteam.vmangaandroid.model.Manga;
import info.vteam.vmangaandroid.utils.DataUtils;

public class MangaFavoriteActivity extends AppCompatActivity implements MangaAdapter.MangaOnClickHandle,
        LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,
        View.OnClickListener, SearchView.OnCloseListener, SwipeRefreshLayout.OnRefreshListener, MangaAdapter.MangaOnLongClickHandle{
    private static final String LOG_TAG = MangaFavoriteActivity.class.getSimpleName();
    ActivityMangaFavoriteBinding mBinding;
    private static final int MANGA_FAVORITE_LOADER_ID = 1111;
    private static final int MANGA_SEARCH_FAVORITE_LOADER_ID = 1112;

    private int mPosition = RecyclerView.NO_POSITION;

    private MangaAdapter mAdapter;
    private Context mContext;

    private static final String[] MANGA_INFO_PROJECTION = {
            MangaContract.MangaInfoEntry._ID,
            MangaContract.MangaInfoEntry.COLUMN_MANGAINFO_ID,
            MangaContract.MangaInfoEntry.COLUMN_THUMBNAIL,
            MangaContract.MangaInfoEntry.COLUMN_TITLE,
            MangaContract.MangaInfoEntry.COLUMN_CATEROGY,
            MangaContract.MangaInfoEntry.COLUMN_DESCRIPTION
    };

    public static final String[] MAIN_MANGA_SEARCH_PROJECTION = {
            MangaContract.MangaEntry._ID,
            MangaContract.MangaEntry.COLUMN_MANGA_ID,
            MangaContract.MangaEntry.COLUMN_THUMBNAIL,
            MangaContract.MangaEntry.COLUMN_TITLE
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manga_favorite);
        mAdapter = new MangaAdapter(this, this);
        mContext = MangaFavoriteActivity.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter.setmOnLongClickHandle(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        mBinding.mangaListRv.setHasFixedSize(true);
        mBinding.mangaListRv.setLayoutManager(gridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.margin_element_grid);
        mBinding.mangaListRv.addItemDecoration(new GridSpacingItemDecoration(3, spacingInPixels, true, 0));
        mBinding.mangaListRv.setAdapter(mAdapter);

        mBinding.refreshSwl.setOnRefreshListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().initLoader(MANGA_FAVORITE_LOADER_ID, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
       switch (id){
           case MANGA_FAVORITE_LOADER_ID:
               Uri uriQuery = MangaContract.MangaInfoEntry.CONTENT_URI;

               return new CursorLoader(this,
                       uriQuery,
                       MANGA_INFO_PROJECTION,
                       null,
                       null,
                       null);

           case MANGA_SEARCH_FAVORITE_LOADER_ID:
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

        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search),
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        LoaderManager.LoaderCallbacks callbacks = MangaFavoriteActivity.this;
                        getSupportLoaderManager().destroyLoader(MANGA_SEARCH_FAVORITE_LOADER_ID);
                        getSupportLoaderManager().restartLoader(MANGA_FAVORITE_LOADER_ID, null, callbacks);
                        return true;
                    }
                });

        return true;
    }

    @Override
    public void onRefresh() {
        getSupportLoaderManager().restartLoader(MANGA_FAVORITE_LOADER_ID, null, this);
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
            ArrayList<Manga> list = mAdapter.getMangaByKey(newText);
            DataUtils.insertMangaFromMangaList(this, list);
            getSupportLoaderManager().initLoader(MANGA_SEARCH_FAVORITE_LOADER_ID, null, this);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(long id) {
        String mangaId = mAdapter.getMangaId(id);
        Intent intent = new Intent(this, MangaDetailActivity.class);
        intent.putExtra("manga_id", mangaId);
        startActivity(intent);
    }

    @Override
    public void onLongClick(long id) {
        final String mangaId = mAdapter.getMangaId(id);
        Log.e("MANGA ID", mangaId);
        new AlertDialog.Builder(this)
                .setTitle("Do you want to remove")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.withAppendedPath(MangaContract.MangaInfoEntry.CONTENT_URI, mangaId);
                        mContext.getContentResolver().delete(uri, null, null);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
        getSupportLoaderManager().restartLoader(MANGA_FAVORITE_LOADER_ID, null, this);
    }
}
