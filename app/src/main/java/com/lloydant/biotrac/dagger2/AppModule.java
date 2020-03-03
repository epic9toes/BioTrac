package com.lloydant.biotrac.dagger2;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.lloydant.biotrac.helpers.AppConstants;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module(includes = {NetworkModule.class})
public class AppModule {

    private final Context mContext;

    public AppModule(Context context) {
        mContext = context;
    }

    @BioTracApplicationScope
    @Provides
    public Context getContext(){
        return mContext;
    }

    @BioTracApplicationScope
    @Provides
    public Gson getGson(){
        return new Gson();
    }

    @BioTracApplicationScope
    @Provides
    public Picasso getPicasso(Context context, OkHttp3Downloader okHttp3Downloader){
        return new Picasso.Builder(context)
                .downloader(okHttp3Downloader)
                .build();
    }

    @BioTracApplicationScope
    @Provides
    public OkHttp3Downloader mOkHttp3Downloader(OkHttpClient okHttpClient){
        return new OkHttp3Downloader(okHttpClient);
    }

    @BioTracApplicationScope
    @Provides
    public SharedPreferences mPreferences(Context app){
        return app.getSharedPreferences(AppConstants.USER_PREF, Context.MODE_PRIVATE);
    }
}
