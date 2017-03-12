package info.vteam.vmangaandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import info.vteam.vmangaandroid.databinding.ActivityMangaLocationBinding;
import info.vteam.vmangaandroid.model.Manga;
import info.vteam.vmangaandroid.model.User;
import info.vteam.vmangaandroid.utils.NetworkUtils;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MangaLocationActivity extends AppCompatActivity implements View.OnClickListener, MangaLocationAdapter.LocationOnClickHandle{
    ActivityMangaLocationBinding mBinding;
    SharedPreferences sharedPreferences;
    private boolean isLogin = false;
    Context mContext;

    ArrayList<User> mListUser;

    MangaLocationAdapter mAdapter;

    private static int POSITION = RecyclerView.NO_POSITION;

    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manga_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = MangaLocationActivity.this;
        mListUser = new ArrayList<>();
        mAdapter = new MangaLocationAdapter(this, this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mBinding.userRv.setLayoutManager(linearLayoutManager);

        mBinding.userRv.setAdapter(mAdapter);

        sharedPreferences = getSharedPreferences("sort_mode", Context.MODE_PRIVATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (sharedPreferences.contains("login")){
            isLogin = sharedPreferences.getBoolean("login", false);
        }
        new UserTask().execute();
        Log.e("LOGIN", String.valueOf(isLogin));

        if (isLogin){
            try {
                socket = IO.socket("http://wannashare.info");
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.emit("foo", "hi");
                }

            }).on("api/v1/realtime created", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    new UserTask().execute();
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            });
            socket.connect();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(long id) {
        User user = mAdapter.getUserByPosition(id);
        String info = "User Name: " + user.getmUserName() + "\n" +
                "Email: " + user.getmEmail() + "\n" +
                "Manga is being read: " + user.getmTitleReading();
        new AlertDialog.Builder(this)
                .setTitle("User")
                .setMessage(info)
                .create().show();
    }

    private class UserTask extends AsyncTask<Void, Void, ArrayList<User>>{
        @Override
        protected ArrayList<User> doInBackground(Void... params) {
            try {
                URL url = NetworkUtils.getUrlRealtimeUser(mContext);

                String response = NetworkUtils.getResponseFromUrl(mContext, url);

                JSONObject jsonObject = new JSONObject(response);

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (!mListUser.isEmpty()){
                    mListUser.clear();
                }

                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject dataOjbect = jsonArray.getJSONObject(i);
                    JSONObject mangaObject = dataOjbect.getJSONObject("manga");

                    JSONObject userObject = dataOjbect.getJSONObject("user");
                    User user = new User(userObject.getString("facebookId"),
                            userObject.getString("name"),
                            userObject.getString("email"),
                            userObject.getString("avatars"),
                            mangaObject.getString("title"));

                    mListUser.add(user);

                }
                return mListUser;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            super.onPostExecute(users);
            mAdapter.setData(mListUser);
            if (POSITION == RecyclerView.NO_POSITION) POSITION = 0;
            mBinding.userRv.smoothScrollToPosition(POSITION);

        }
    }

    @Override
    public void onClick(View v) {

    }
}
