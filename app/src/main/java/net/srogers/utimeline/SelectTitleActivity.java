package net.srogers.utimeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

/**
 * Created by ellen2 on 4/3/16.
 */
public class SelectTitleActivity  extends AppCompatActivity {

    private static String TAG = "SelectTitleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_titles);

        ListView listView = (ListView) findViewById(R.id.title_list);
        final EditText customTitle = (EditText) findViewById(R.id.custom_title);

        final String[] titles = getResources().getStringArray(R.array.event_titles);
        listView.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, titles));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                returnTitle(titles[position]);
            }
        });

        customTitle.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    returnTitle(customTitle.getText().toString());
                }
                return false;
            }
        });
    }

    private void returnTitle(String title) {
        Intent intent = new Intent();
        Log.d(TAG, "Title to be sent back: " + title);
        intent.putExtra(NewEventActivity.TITLE_MESSAGE, title);
        setResult(RESULT_OK, intent);
        finish();
    }

}
