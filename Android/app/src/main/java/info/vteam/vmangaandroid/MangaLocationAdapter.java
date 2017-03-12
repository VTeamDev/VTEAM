package info.vteam.vmangaandroid;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.vteam.vmangaandroid.databinding.ActivityMangaLocationBinding;
import info.vteam.vmangaandroid.databinding.MangaListItemBinding;
import info.vteam.vmangaandroid.databinding.MangaUserItemBinding;
import info.vteam.vmangaandroid.model.User;

/**
 * Created by YukiNoHara on 3/12/2017.
 */

public class MangaLocationAdapter extends RecyclerView.Adapter<MangaLocationAdapter.MangaLocationViewHolder>{
    Context mContext;
    ArrayList<User> mList;
    LocationOnClickHandle mListener;

    public MangaLocationAdapter(Context context, LocationOnClickHandle mListener) {
        mContext = context;
        this.mListener = mListener;
        mList = new ArrayList<>();
    }

    public interface LocationOnClickHandle{
        void onClick(long id);
    }

    @Override
    public MangaLocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.manga_user_item, parent, false);
        view.setFocusable(true);
        return new MangaLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MangaLocationViewHolder holder, int position) {
        MangaUserItemBinding mBinding = holder.getBinding();

        User user = mList.get(position);

        Picasso.with(mContext)
                .load(user.getmAvatar())
                .fit()
                .centerCrop()
                .into(mBinding.avatarUserImv);

        mBinding.nameUserTv.setText(user.getmUserName());
        mBinding.actionUserTv.setText("is reading " + user.getmTitleReading());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MangaLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        MangaUserItemBinding mBinding;

        public MangaLocationViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            mBinding.getRoot().setOnClickListener(this);
        }

        public MangaUserItemBinding getBinding(){
            return mBinding;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mListener.onClick(position);
        }
    }

    public void setData(ArrayList<User> list){
        mList = list;
        notifyDataSetChanged();
    }

    public User getUserByPosition(long id){
        return mList.get((int) id);
    }
}
