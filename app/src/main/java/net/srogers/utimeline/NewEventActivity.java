package net.srogers.utimeline;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import net.srogers.utimeline.model.Timeline;
import net.srogers.utimeline.model.UTimelineEvent;
import net.srogers.utimeline.model.User;

import java.util.Calendar;
import java.util.Date;

/**
 * Activity for creating or editing an event
 */
public class NewEventActivity extends Activity {

    private User mUser;
    private Timeline mTimeline;
    private UTimelineEvent mEvent;


    public NewEventActivity() {
        mUser = User.getCurrentUser();
        mTimeline = mUser.getTimeline();
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Date eDate = new Date(year, month, day);
            // mEvent.setDate(eDate);
        }
    }
}
