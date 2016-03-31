package net.srogers.utimeline;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import net.srogers.utimeline.model.Timeline;
import net.srogers.utimeline.model.User;

public class MainActivity extends AppCompatActivity {


    private User mUser;
    private Timeline mTimeline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = User.getCurrentUser();
        mTimeline = mUser.getTimeline();

        if (mTimeline.hasEvents()) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.no_event_startup);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTimeline.hasEvents()) {
            setContentView(R.layout.activity_main);
        } else {
            setContentView(R.layout.no_event_startup);
        }
    }
}
