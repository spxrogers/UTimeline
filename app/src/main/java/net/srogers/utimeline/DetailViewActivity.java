package net.srogers.utimeline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.srogers.utimeline.model.Timeline;
import net.srogers.utimeline.model.UTimelineEvent;
import net.srogers.utimeline.model.UTimelineMedia;
import net.srogers.utimeline.model.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity to view the details of a specific event
 * This activity should be spawned by an intent when a User is trying to view a specific event
 */
public class DetailViewActivity extends AppCompatActivity {

    private User mUser;
    private Timeline mTimeline;
    private UTimelineEvent mUTimelineEvent;
    private int mEventIndex;

    //Event detail views
    private ImageView mEventImage;
    private TextView mEventTitle;
    private TextView mEventDate;
    private TextView mEventDescription;

    public static final int DELETE_DIALOG_ID = 1;
    private static String TAG = "DetailViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get the passed in eventIndex
        Intent getExtra = getIntent();
        mEventIndex = getExtra.getIntExtra("eventIndex", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get user information
        mUser = User.getCurrentUser();
        mTimeline = mUser.getTimeline();

        //Attempt to get the event at eventIndex
        if (mEventIndex != -1) {
            Log.d(TAG, "Displaying event #: " + mEventIndex);
            mUTimelineEvent = mUser.getEvent(mEventIndex);
            if (mUTimelineEvent == null) {
                Log.d(TAG, "Event index given does not exist in event list.");
                finish();
            }
        } else {
            Log.d(TAG, "No event number was given to view.");
            finish();
        }

        //Set the layout
        setContentView(R.layout.activity_detail_view);

        //get event detail view information
        mEventImage = (ImageView) findViewById(R.id.event_detail_image);
        mEventTitle = (TextView) findViewById(R.id.event_detail_title);
        mEventDate = (TextView) findViewById(R.id.event_detail_date);
        mEventDescription = (TextView) findViewById(R.id.event_detail_description);

        //update view information from current event
        updateEventDate();
        updateEventDescription();
        updateEventImage();
        updateEventTitle();

        //get edit and delete buttons
        Button editButton = (Button) findViewById(R.id.event_detail_edit_button);
        Button deleteButton = (Button) findViewById(R.id.event_detail_delete_button);

        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DetailViewActivity.this, NewEventActivity.class);
                intent.putExtra("eventIndex", mEventIndex);
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DELETE_DIALOG_ID);
            }
        });

        LinearLayout scrollView = (LinearLayout) findViewById(R.id.image_scroller);
        if (mUTimelineEvent.getMedia() != null && mUTimelineEvent.getMedia().size() > 0) {
            for (UTimelineMedia media : mUTimelineEvent.getMedia()) {
                scrollView.addView(insertPhoto(media.getLocation()));
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DELETE_DIALOG_ID:
                AlertDialog.Builder deleteEventDialogBuilder = new AlertDialog.Builder(DetailViewActivity.this);
                deleteEventDialogBuilder.setMessage("Are you sure you want to delete this Event?");
                deleteEventDialogBuilder.setCancelable(true);

                deleteEventDialogBuilder.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mUser.deleteEvent(mEventIndex);
                                mUser.saveUser();
                                finish();
                            }
                        });

                deleteEventDialogBuilder.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog deleteEventAlert = deleteEventDialogBuilder.create();
                deleteEventAlert.show();
        }
        return null;
    }

    /**
     * Updates the image of the event detail page based on current event
     */
    public void updateEventImage() {
        List<UTimelineMedia> media = mUTimelineEvent.getMedia();
        UTimelineMedia eventImage = null;
        String location = null;

        if (media.size() > 0) {
            eventImage = media.get(0);
            location = eventImage.getLocation();
        }

        if (location != null && !location.isEmpty()) {
            Bitmap bm = BitmapFactory.decodeFile(eventImage.getLocation());
            BitmapDrawable bmd = new BitmapDrawable(getResources(), bm);
            mEventImage.setBackgroundDrawable(bmd);
        } else {
            mEventImage.setImageResource(R.drawable.uttower);
            Log.d(TAG, "Event did not have an image to update.");
        }
    }

    /**
     * Updates the event title of the event detail page based on current event
     */
    public void updateEventTitle() {
        String title = mUTimelineEvent.getTitle();
        if (mUTimelineEvent.getTitle() != null) {
            if (title != null && !title.isEmpty()) {
                if (title.length() > 25)
                    title = title.substring(0, 23) + "...";
                mEventTitle.setText(title);
            }
        } else {
            Log.d(TAG, "Event did not have Title to update.");
        }
    }

    /**
     * Updates the event description of the event detail page based on current event
     */
    public void updateEventDescription() {
        if (!mUTimelineEvent.getDescription().isEmpty()) {
            mEventDescription.setText(mUTimelineEvent.getDescription());
        } else {
            mEventDescription.setVisibility(View.GONE);
            Log.d(TAG, "Event did not have description to update.");
        }
    }

    /**
     * Updates the event date of the event detail page based on current event
     */
    public void updateEventDate() {
        if (mUTimelineEvent.getDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("E MM/dd/yyyy");
            mEventDate.setText(df.format(mUTimelineEvent.getDate()));
        } else {
            Log.d(TAG, "Event did not have date to update.");
        }
    }

    View insertPhoto(String path){
        Bitmap bm = decodeSampledBitmapFromUri(path, 220, 220);

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new ViewGroup.LayoutParams(250, 250));
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(220, 220));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bm);

        layout.addView(imageView);
        return layout;

    }

    public Bitmap decodeSampledBitmapFromUri(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }
}
