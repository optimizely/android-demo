package com.example.sshah.gilt_android;

import android.app.Application;

import com.localytics.android.Localytics;

/**
 * Created by jdeffibaugh on 10/1/15.
 */
public class GiltApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Localytics.integrate(this);
        Localytics.setLoggingEnabled(true);
        Localytics.setCustomerId("User12345");

    }
}
