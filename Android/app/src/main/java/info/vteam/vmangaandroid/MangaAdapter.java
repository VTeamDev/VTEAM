package info.vteam.vmangaandroid;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.vteam.vmangaandroid.databinding.MangaListItemBinding;
import info.vteam.vmangaandroid.model.Manga;

/**
 * Created by YukiNoHara on 3/7/2017.
 */

public class MangaAdapter
        extends RecyclerView.Adapter<MangaAdapter.MangaViewHolder>{
    private static final String LOG_TAG = MangaAdapter.class.getSimpleName();
    private Context mContext;

    public MangaOnClickHandle mOnClickHandle;
    public MangaOnLongClickHandle mOnLongClickHandle;

    private Cursor mCursor;

    ArrayList<Manga> mList;

    public MangaAdapter(Context context, MangaOnClickHandle mangaOnClickHandle){
        mContext = context;
        this.mOnClickHandle = mangaOnClickHandle;
        mList = new ArrayList<>();
    }

    public interface MangaOnClickHandle{
        void onClick(long id);
    }

    public interface MangaOnLongClickHandle{
        void onLongClick(long id);
    }

    public void setmOnLongClickHandle(MangaOnLongClickHandle mOnLongClickHandle) {
        this.mOnLongClickHandle = mOnLongClickHandle;
    }

    @Override
    public MangaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.manga_list_item, null);
        view.setFocusable(true);
        return new MangaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MangaViewHolder holder, int position) {
        MangaListItemBinding mBinding = holder.getBinding();
        mCursor.moveToPosition(position);
        String mangaId = mCursor.getString(MainActivity.INDEX_MANGA_IDMANGA);
        String thumbnail = mCursor.getString(MainActivity.INDEX_MANGA_THUMBNAIL);
        String title = mCursor.getString(MainActivity.INDEX_MANGA_TITLE);

        mList.add(new Manga(mangaId, thumbnail, title));
        mBinding.mangaTitleTv.setText(title);
        Picasso.with(mContext)
                .load(thumbnail)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .error(R.drawable.ic_close_black_24dp)
                .placeholder(R.drawable.loading)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .fit()
                .into(mBinding.mangaAvatarImv);
        Picasso.with(mContext).invalidate(thumbnail);
    }

    @Override
    public int getItemCount() {
        if(mCursor == null){
            return 0;

        } else {
            return mCursor.getCount();
        }
    }

    public void swapCursor(Cursor data){
        mCursor = data;
        notifyDataSetChanged();
    }

    class MangaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        MangaListItemBinding mBinding;

        public MangaViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);

            mBinding.mangaAvatarImv.setOnClickListener(this);
            mBinding.mangaAvatarImv.setOnLongClickListener(this);
        }

        public MangaListItemBinding getBinding(){
            return mBinding;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnClickHandle.onClick(position);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            mOnLongClickHandle.onLongClick(position);
            Log.e("LONG CLICK", String.valueOf(position));
            return true;
        }
    }

    public String getMangaId(long id){
        mCursor.moveToPosition((int) id);
        return mCursor.getString(MainActivity.INDEX_MANGA_IDMANGA);
    }

    public ArrayList<Manga> getMangaByKey(String key){
        ArrayList<Manga> listManga = new ArrayList<>();
        for (Manga m : mList){
            if (m.getmTitle().toLowerCase().contains(key.toLowerCase())){
                listManga.add(m);
            }
        }
        return listManga;
    }

}
