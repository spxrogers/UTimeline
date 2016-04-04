package net.srogers.utimeline;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.srogers.utimeline.model.User;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "In onCreate");

        if (User.getCurrentUser().getTimeline().hasEvents()) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.no_event_startup);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "In onResume");

        if (User.getCurrentUser().getTimeline().hasEvents()) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.no_event_startup);
        }

    }

    public void addEvent(View v) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }
}
