package net.srogers.utimeline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.srogers.utimeline.model.UTimelineEvent;
import net.srogers.utimeline.model.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUser = User.getCurrentUser();

        Log.d(TAG, "In onCreate");

        if (mUser.hasEvents()) {
            setContentView(R.layout.activity_main);
            configureAndDisplayTimeline();
        } else {
            setContentView(R.layout.no_event_startup);
        }
    }

    private void configureAndDisplayTimeline() {
        final ListView theTimeline = (ListView) findViewById(R.id.timeline_list);
        final List<UTimelineEvent> listData = mUser.getEvents();
        final TimelineAdapter adapter = new TimelineAdapter(this, R.layout.timeline_row, listData);
        assert theTimeline != null;
        theTimeline.setAdapter(adapter);

        theTimeline.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                // TODO: below line is wrong. I need the DetailActivity not yet merged, "NewEventActivity.class" is a placeholder
                Intent intent = new Intent(MainActivity.this, NewEventActivity.class);
                intent.putExtra("eventIndex", position);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUser = User.getCurrentUser();

        Log.d(TAG, "In onResume");

        if (mUser.hasEvents()) {
            setContentView(R.layout.activity_main);
            configureAndDisplayTimeline();
        } else {
            setContentView(R.layout.no_event_startup);
        }

    }

    public void addEvent(View v) {
        Intent intent = new Intent(this, NewEventActivity.class);
        startActivity(intent);
    }

    private class TimelineAdapter extends ArrayAdapter<UTimelineEvent> {
        private final Context context;
        private final List<UTimelineEvent> values;

        public TimelineAdapter(Context context, int resource, List<UTimelineEvent> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.timeline_row, parent, false);
            TextView titleView = (TextView) rowView.findViewById(R.id.row_title);
            TextView memoView = (TextView) rowView.findViewById(R.id.row_memo);

            final UTimelineEvent event = values.get(position);
            titleView.setText(event.getTitle());
            memoView.setText(event.getDescription());

            return rowView;
        }
    }
}
