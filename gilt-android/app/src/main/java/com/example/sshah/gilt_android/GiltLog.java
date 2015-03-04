package com.example.sshah.gilt_android;

import android.util.Log;

import java.util.Locale;

/**
 * Created by sshah on 3/3/15.
 */
public class GiltLog {

    static final boolean VERBOSE = true;

    public static void d(String format, Object... args) {
        Log.d("GiltApp", String.format(Locale.getDefault(), format, args));
    }



}
