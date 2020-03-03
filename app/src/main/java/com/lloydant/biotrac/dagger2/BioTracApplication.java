package com.lloydant.biotrac.dagger2;

import android.app.Application;

public class BioTracApplication extends Application {
    private BioTracApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        
        initDagger();
    }

    public BioTracApplicationComponent getAppComponent(){
        return mComponent;
    }

    private void initDagger() {
    mComponent = DaggerBioTracApplicationComponent.builder().appModule(new AppModule(this)).build();
    mComponent.inject(this);
    }
}
