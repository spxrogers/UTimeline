package net.srogers.utimeline;

import android.app.Application;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by steven on 3/30/16.
 */
public class MyApplication extends Application {
    private String TAG = "UTIMELINE";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Realm.getDefaultInstance(); // throws if not configured yet
        } catch (Exception e) {
            Log.i(TAG, "Realm default intance not configured, configuring new one...");

            // configure realm for utimeline
            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
            Realm.deleteRealm(realmConfiguration); // clean slate
            Realm.setDefaultConfiguration(realmConfiguration); // set new default
        }
    }
}
