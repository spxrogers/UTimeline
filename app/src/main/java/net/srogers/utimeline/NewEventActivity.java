package net.srogers.utimeline;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.srogers.utimeline.model.UTimelineEvent;
import net.srogers.utimeline.model.UTimelineMedia;
import net.srogers.utimeline.model.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity for creating or editing an event
 */
public class NewEventActivity extends AppCompatActivity {

    // Storage Permission Constants
    private static final int REQUEST_EXTERNAL_STORAGE = 4;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private TextView mEventDate;
    private String mImageLocation;
    private UTimelineEvent mEvent;

    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 0;
    static final int REQUEST_CAMERA = 1;
    static final int SELECT_FILE = 2;
    static final int TITLE_REQUEST_CODE = 3;

    public static String TITLE_MESSAGE = "title";

    private static String TAG = "NewEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_event);

        String index = getIntent().getStringExtra("eventIndex");
        Log.d(TAG, "In onCreate and index is: " + index);
        if(index == null) {
            createNewEvent();
        } else {
            editEvent(index);
        }
    }

    private void createNewEvent() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        mEventDate = (TextView) findViewById(R.id.event_date);
        Button setDate = (Button) findViewById(R.id.date_picker);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        setDateTextView();
    }

    private void editEvent(String index) {
        /*User user = User.getCurrentUser();

        UTimelineEvent event = user.getEvent(Integer.valueOf(index));
        Date date = event.getDate();
        year = date.getYear();
        month = date.getMonth();
        day = date.getDay();

        mEventDate = (TextView) findViewById(R.id.event_date);
        Button setDate = (Button) findViewById(R.id.date_picker);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        setDateTextView();

        Button titleButton = (Button) findViewById(R.id.add_event_title);
        titleButton.setText(event.getTitle());

        EditText description = (EditText) findViewById(R.id.add_event_description);
        description.setText(event.getDescription());

        ImageView picture = (ImageView) findViewById(R.id.add_event_image);
        String path = event.getMedia().get(0).getLocation();
        if(path != null)
            scaleAndSetImage(picture, path);

        mEvent = event;*/

    }

    private void setDateTextView() {
        String m;
        String d;

        if(month + 1 < 10)
            m = "0" + Integer.toString(month + 1);
        else
            m = Integer.toString(month + 1);
        if(day < 10)
            d = "0" + Integer.toString(day);
        else
            d = Integer.toString(day);

        mEventDate.setText(new StringBuilder()
                .append(m).append("-").append(d).append("-").append(year));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker p, int y, int m, int d) {
                        year = y;
                        month = m;
                        day = d;
                        setDateTextView();
                    }
                }, year, month, day);
        }
        return null;
    }

    public void selectTitle(View v) {
        Intent intent = new Intent(this, SelectTitleActivity.class);
        startActivityForResult(intent, TITLE_REQUEST_CODE);
    }

    public void selectImage(View v) {
        verifyStoragePermissions(this);

        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(NewEventActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "In onActivityResult with requestCode " + requestCode + " and resultCode " + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TITLE_REQUEST_CODE :
                    Button titleButton = (Button) findViewById(R.id.add_event_title);
                    titleButton.setText(data.getStringExtra(TITLE_MESSAGE));
                    Log.d(TAG, "Title received: " + data.getStringExtra(TITLE_MESSAGE));
                    break;
                case REQUEST_CAMERA :
                    ImageView ivImage = (ImageView) findViewById(R.id.add_event_image);
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    File destination = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
                    Log.d(TAG, "Photo destination Uri: " + destination.toURI().toString());
                    Log.d(TAG, "Photo destination path: " + destination.getAbsolutePath());
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ivImage.setImageBitmap(thumbnail);
                    mImageLocation = destination.getAbsolutePath();
                    break;
                case SELECT_FILE :
                    ivImage = (ImageView) findViewById(R.id.add_event_image);
                    Uri selectedImageUri = data.getData();
                    Log.d(TAG, "Selected image Uri: " + selectedImageUri.toString());
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                            null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    Log.d(TAG, "Selected image path: " + selectedImagePath);

                    scaleAndSetImage(ivImage, selectedImagePath);

                    mImageLocation = selectedImagePath;
                    break;
            }
        }
    }

    private void scaleAndSetImage(ImageView view, String path) {
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path, options);
        view.setImageBitmap(bm);
    }

    public void saveEvent(View v) {
        Button titleButton = (Button) findViewById(R.id.add_event_title);
        String titleText = titleButton.getText().toString();

        EditText description = (EditText) findViewById(R.id.add_event_description);
        String descriptionText = description.getText().toString();

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        Date date = c.getTime();

        User user = User.getCurrentUser();
        if(mEvent != null) {
            mEvent.setTitle(titleText);
            mEvent.setDescription(descriptionText);
            mEvent.setDate(date);
            //mEvent.getMedia().get(0).setLocation(mImageLocation);
        } else {

            UTimelineEvent newEvent = new UTimelineEvent(titleText, descriptionText, date);
            UTimelineMedia media = new UTimelineMedia(mImageLocation);
            newEvent.addMedia(media);
            user.addEvent(newEvent);
        }

        user.saveUser();
        finish();
    }

    public void cancelCreateEvent(View v) {
        finish();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permissions granted!");
                } else {
                    // todo: show toast message?
                    Log.d(TAG, "Permissions weren't granted.");
                }
            }
        }
    }
}
