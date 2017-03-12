package info.vteam.vmangaandroid;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import info.vteam.vmangaandroid.databinding.MangaReadItemBinding;

/**
 * Created by lednh on 3/11/2017.
 */

public class MangaReadAdapter extends RecyclerView.Adapter<MangaReadAdapter.MangaReadAdapterViewHolder> {
    private Context context;
    private String[] mMangaPagesList;

    private static final String LOG_TAG = MangaReadAdapter.class.getSimpleName();

    @Override
    public MangaReadAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.manga_read_item, parent, false);

        return new MangaReadAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MangaReadAdapterViewHolder holder, int position) {
        MangaReadItemBinding readItemBinding = holder.getBinding();
        String mangaPageContent = mMangaPagesList[position];

        Picasso picasso = Picasso.with(context);
        //picasso.setIndicatorsEnabled(true);

        picasso.load(mangaPageContent)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .fit()
                .into(readItemBinding.mangaReadItemImageView);
    }

    public void setMangaPagesList(String[] pagesList) {
        mMangaPagesList = pagesList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMangaPagesList != null ? mMangaPagesList.length : 0;
    }

    class MangaReadAdapterViewHolder extends RecyclerView.ViewHolder {
        MangaReadItemBinding mangaReadItemBinding;

        MangaReadAdapterViewHolder(View view) {
            super(view);

            mangaReadItemBinding = DataBindingUtil.bind(view);
        }

        MangaReadItemBinding getBinding() {
            return mangaReadItemBinding;
        }
    }

}
