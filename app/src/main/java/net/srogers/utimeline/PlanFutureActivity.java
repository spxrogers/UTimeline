package net.srogers.utimeline;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by steven on 4/4/16.
 */
public class PlanFutureActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.plan_future);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.plan_future);
    }
}
