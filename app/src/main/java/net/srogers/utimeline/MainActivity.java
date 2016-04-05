package net.srogers.utimeline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import net.srogers.utimeline.model.UTimelineEvent;
import net.srogers.utimeline.model.User;

import java.text.SimpleDateFormat;
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

    public void addFutureEvent(View view) {
        Intent intent = new Intent(this, PlanFutureActivity.class);
        startActivity(intent);
    }

    public void viewDetail(View view) {
        int index = (Integer) view.getTag();
        Intent intent = new Intent(this, DetailViewActivity.class);
        intent.putExtra("eventIndex", index);
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
            final UTimelineEvent event = values.get(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.timeline_row, parent, false);
            TextView titleView = (TextView) rowView.findViewById(R.id.row_title);
            TextView memoView = (TextView) rowView.findViewById(R.id.row_memo);
            TextView dateView = (TextView) rowView.findViewById(R.id.row_date);
            Button detailbutton = (Button) rowView.findViewById(R.id.view_detail_button);
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yy");

            titleView.setText(event.getTitle());
            memoView.setText(event.getDescription());
            dateView.setText(dateFormat.format(event.getDate()));
            detailbutton.setTag(position);

            return rowView;
        }
    }
}
