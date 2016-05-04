package net.srogers.utimeline;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import net.srogers.utimeline.model.UTimelineEvent;
import net.srogers.utimeline.model.User;

import java.util.List;

/**
 * Created by steven on 4/4/16.
 */
public class PlanFutureActivity extends AppCompatActivity {

    User mUser;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        mUser = User.getCurrentUser();

        setContentView(R.layout.plan_future);

        configureViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUser = User.getCurrentUser();

        setContentView(R.layout.plan_future);

        configureViews();
    }

    private void configureViews() {
        ListView listView = (ListView) findViewById(R.id.future_titles);
        final String[] titles = mUser.getPlanned().toArray(new String[mUser.getPlanned().size()]);
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, titles));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tapTitle(titles[position]);
            }
        });
    }

    public void addFutureTitle(View view) {
        EditText textView = (EditText) findViewById(R.id.futevent_title);
        assert textView != null;
        String title = textView.getText().toString();

        if (title.equals("")) {
            Toast.makeText(this, "Must enter title", Toast.LENGTH_SHORT).show();
        }
        
        for (UTimelineEvent event : mUser.getEvents()) {
            if (event.getTitle().toLowerCase().equals(title.toLowerCase())) {
                Toast.makeText(this, "Event already added", Toast.LENGTH_LONG).show();
                return;
            }
        }


        List<String> planned = mUser.getPlanned();
        for (String plan : planned) {
            if (plan.toLowerCase().equals(title.toLowerCase())) {
                Toast.makeText(this, "Event already planned", Toast.LENGTH_LONG).show();
                return;
            }
        }

        planned.add(title);
        mUser.setPlanned(planned);
        mUser.saveUser();
        configureViews();

        textView.getText().clear();
    }

    private void tapTitle(String title) {
        Intent intent = new Intent(this, NewEventActivity.class);
        intent.putExtra("future_title", title);
        startActivity(intent);
    }
}
