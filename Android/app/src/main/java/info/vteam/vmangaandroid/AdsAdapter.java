package info.vteam.vmangaandroid;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.vteam.vmangaandroid.databinding.AdsListItemBinding;

/**
 * Created by lednh on 3/12/2017.
 */

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdsViewHolder> {
    private ArrayList<String> adsList;
    private Context context;

    AdsAdapter() {
        adsList = new ArrayList<>();

        adsList.add("image_1");
        adsList.add("image_2");
        adsList.add("image_3");
        adsList.add("image_4");
    }

    @Override
    public AdsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.ads_list_item, parent, false);
        return new AdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AdsViewHolder holder, int position) {
        AdsListItemBinding listItemBinding = holder.getBinding();
        String imageAtPosition = adsList.get(position);

        int drawableId = context.getResources()
                .getIdentifier(imageAtPosition, "drawable",
                        context.getPackageName());

        Log.e("DRAWABLE", Integer.toString(drawableId));

        Picasso.with(context)
                .load(drawableId)
                .into(listItemBinding.adsListItemImageView);
    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }

    class AdsViewHolder extends RecyclerView.ViewHolder {
        AdsListItemBinding adsListItemBinding;

        AdsViewHolder(View view) {
            super(view);

            adsListItemBinding = DataBindingUtil.bind(view);
        }

        AdsListItemBinding getBinding() {
            return adsListItemBinding;
        }
    }
}
