package com.printxpress.android;

import android.app.Application;

public class PrintXpressApp extends Application {

    private static PrintXpressApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static PrintXpressApp getInstance() {
        return instance;
    }
}
