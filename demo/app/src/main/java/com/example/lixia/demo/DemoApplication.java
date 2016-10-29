package com.example.lixia.demo;

import android.app.Application;
import android.util.Config;

import com.cardinfolink.pos.sdk.CILSDK;


/**
 * Created by lixia on 2016/10/25.
 */

public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CILSDK.setDebug(true);
        CILSDK.connect(this);
    }
}
