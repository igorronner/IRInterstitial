package com.igorronner.irinterstitial.services;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.igorronner.irinterstitial.R;
import com.igorronner.irinterstitial.init.ConfigUtil;
import com.igorronner.irinterstitial.preferences.MainPreference;
import com.igorronner.irinterstitial.utils.DateUtils;

import java.util.Calendar;

public class RemoteConfigService {

    private Activity context;
    private static RemoteConfigService instance;
    public FirebaseRemoteConfig mFirebaseRemoteConfig;
    private ServiceListener serviceListener;
    private static final String DAYS_BEFORE_INSTERSTITIAL = ConfigUtil.APP_PREFIX+"days_before_interstitial";
    private static final String SHOW_SPLASH = ConfigUtil.APP_PREFIX+"_show_splash";
    private static final String FINISH_WITH_INTERSTITIAL = ConfigUtil.APP_PREFIX+"_finish_with_interstitial";
    
    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }


    public interface ServiceListener<T> {
        void onComplete(T result);
    }

    public RemoteConfigService.ServiceListener getServiceListener() {
        return serviceListener;
    }

    public RemoteConfigService setServiceListener(RemoteConfigService.ServiceListener serviceListener) {
        this.serviceListener = serviceListener;
        return this;
    }

    public RemoteConfigService() {

    }

    public static RemoteConfigService getInstance(Activity activity) {
        if (instance == null) {
            instance = new RemoteConfigService();

            FirebaseApp.initializeApp(activity);
            instance.mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings
                    .Builder()
                    .build();
            instance.mFirebaseRemoteConfig.setConfigSettings(configSettings);
            instance.mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        }

        instance.setContext(activity);

        return instance;
    }

    public void canShowSplash(final ServiceListener<Boolean> serviceListener){

        mFirebaseRemoteConfig.fetch(cacheExpiration())
                .addOnCompleteListener(context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean result = false;
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                            result = mFirebaseRemoteConfig.getBoolean(SHOW_SPLASH) && !MainPreference.isPremium(context);
                        }
                        serviceListener.onComplete(result);
                    }
                });
    }

    public void canFinishWithInterstitial(final ServiceListener<Boolean> serviceListener){

        mFirebaseRemoteConfig.fetch(cacheExpiration())
                .addOnCompleteListener(context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean result = false;
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                            result = mFirebaseRemoteConfig.getBoolean(FINISH_WITH_INTERSTITIAL);
                        }
                        serviceListener.onComplete(result);
                    }
                });
    }

    public void canShowInterstitial(final ServiceListener<Boolean> serviceListener){

        if (!ConfigUtil.SHOW_AFTER_DAYS){
            serviceListener.onComplete(true);
            return;
        }


        mFirebaseRemoteConfig.fetch(cacheExpiration())
                .addOnCompleteListener(context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        boolean result = false;
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                            Calendar today = Calendar.getInstance();
                            Calendar launchCal = Calendar.getInstance();
                            launchCal.setTimeInMillis(MainPreference.getFirstLaunchDate(context));
                            result = mFirebaseRemoteConfig.getLong(DAYS_BEFORE_INSTERSTITIAL) < DateUtils.daysBetween(today, launchCal);
                        }
                        serviceListener.onComplete(result);
                    }
                });
    }

    private long cacheExpiration(){
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        return cacheExpiration;

    }

}