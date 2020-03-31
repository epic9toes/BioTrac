package com.lloydant.biotrac.dagger2;


import android.content.Context;

import java.io.File;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

@Module(includes = {AppModule.class})
public class NetworkModule {

    @Provides
    @BioTracApplicationScope
    public File cacheFile(Context context) {
        return new File(context.getCacheDir(), "okhttp_cache");
    }

    @Provides
    @BioTracApplicationScope
    public Cache cache(File cacheFile) {
        return new Cache(cacheFile, 10 * 1000 * 1000);//10mb Cache
    }

    @BioTracApplicationScope
    @Provides
    public OkHttpClient provideOkHttpClient(Cache cache) {
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .cache(cache).build();
    }

}
