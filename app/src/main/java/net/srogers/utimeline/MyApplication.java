package net.srogers.utimeline;

import android.app.Application;

import io.paperdb.Paper;

/**
 * Created by steven on 3/30/16.
 */
public class MyApplication extends Application {
    private String TAG = "UTIMELINE";

    @Override
    public void onCreate() {
        super.onCreate();

        Paper.init(this);
    }
}
