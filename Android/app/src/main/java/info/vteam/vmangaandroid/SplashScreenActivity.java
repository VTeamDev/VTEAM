package info.vteam.vmangaandroid;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import info.vteam.vmangaandroid.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    ActivitySplashScreenBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        getSupportActionBar().hide();

        Picasso.with(this).load(R.drawable.splash).fetch(new Callback() {
            @Override
            public void onSuccess() {
                Picasso.with(SplashScreenActivity.this).load(R.drawable.splash)
                        .fit()
                        .noFade()
                        .noPlaceholder()
                        .centerCrop()
                        .into(mBinding.splashImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                startActivity(new Intent(SplashScreenActivity.this,
                                        MainActivity.class));
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }

            @Override
            public void onError() {

            }
        });
    }
}
