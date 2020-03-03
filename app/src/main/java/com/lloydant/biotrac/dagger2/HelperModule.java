package com.lloydant.biotrac.dagger2;

import android.content.Context;

import com.google.gson.Gson;
import com.lloydant.biotrac.helpers.FingerprintConverter;
import com.lloydant.biotrac.helpers.NetworkCheck;
import com.lloydant.biotrac.helpers.StorageHelper;

import dagger.Module;
import dagger.Provides;

@Module(includes = {AppModule.class})
public class HelperModule {

    @BioTracApplicationScope
    @Provides
    public StorageHelper mStorageHelper(Context context){
        return new StorageHelper(context);
    }

    @BioTracApplicationScope
    @Provides
    public NetworkCheck mNetworkCheck(Context context){
        return new NetworkCheck(context);
    }

    @BioTracApplicationScope
    @Provides
    public FingerprintConverter mFingerprintConverter(Gson gson){
        return new FingerprintConverter(gson);
    }
}
