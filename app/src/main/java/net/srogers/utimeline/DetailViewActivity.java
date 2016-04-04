package net.srogers.utimeline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.srogers.utimeline.model.Timeline;
import net.srogers.utimeline.model.UTimelineEvent;
import net.srogers.utimeline.model.UTimelineMedia;
import net.srogers.utimeline.model.User;

import java.io.File;
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

    //Event detail views
    private ImageView mEventImage;
    private TextView mEventTitle;
    private TextView mEventDate;
    private TextView mEventDescription;

    public static final int DELETE_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get user information
        mUser = User.getCurrentUser();
        mTimeline = mUser.getTimeline();
        mUTimelineEvent = mUser.getCurrentEvent();

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
                //TODO: Somehow let NewEventActivity know it needs to edit the current event
                //Intent intent = new Intent(this, NewEventActivity.class);
                //startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DELETE_DIALOG_ID);
            }
        });

        setContentView(R.layout.activity_detail_view);
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
                                //mTimeline.deleteEvent(mUTimelineEvent);
                                //Intent intent = new Intent(this, TimelineActivity.class);
                                //startActivity(intent);
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
        UTimelineMedia eventImage = media.get(0);
        mEventImage.setImageURI(Uri.fromFile(new File(eventImage.getLocation())));
    }

    /**
     * Updates the event title of the event detail page based on current event
     */
    public void updateEventTitle() {
        mEventTitle.setText(mUTimelineEvent.getTitle());
    }

    /**
     * Updates the event description of the event detail page based on current event
     */
    public void updateEventDescription() {
        mEventDescription.setText(mUTimelineEvent.getDescription());
    }

    /**
     * Updates the event date of the event detail page based on current event
     */
    public void updateEventDate() {
        mEventDate.setText(mUTimelineEvent.getDate().toString());
    }

}
