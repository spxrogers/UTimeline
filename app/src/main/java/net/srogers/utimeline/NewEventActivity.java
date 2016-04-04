package net.srogers.utimeline;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity for creating or editing an event
 */
public class NewEventActivity extends AppCompatActivity {

    private User mUser;
    private TextView mEventDate;
    private String mImageLocation;

    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 0;
    static final int REQUEST_CAMERA = 1;
    static final int SELECT_FILE = 2;

    private static String TAG = "NewEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_event);

        mUser = User.getCurrentUser();

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

        /*final EditText description = (EditText)findViewById(R.id.add_event_description);
        description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(description.getWindowToken(), 0);
                }
            }
        });*/

    }

    private void setDateTextView() {
        final Calendar c = Calendar.getInstance();

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

    public void selectImage(View v) {
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
        if (resultCode == RESULT_OK) {
            ImageView ivImage = (ImageView) findViewById(R.id.add_event_image);
            if (requestCode == REQUEST_CAMERA) {
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
                mImageLocation = destination.toURI().toString();
            } else if (requestCode == SELECT_FILE) {
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
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                ivImage.setImageBitmap(bm);
                mImageLocation = selectedImageUri.toString();
            }
        }
    }

    public void saveEvent(View v) {
        Button titleButton = (Button) findViewById(R.id.add_event_title);
        String titleText = titleButton.getText().toString();
        EditText description = (EditText) findViewById(R.id.add_event_description);
        String descriptionText = description.getText().toString();
        Date date = new Date(year, month, day);
        UTimelineEvent newEvent = new UTimelineEvent(titleText, descriptionText, date);

        UTimelineMedia media = new UTimelineMedia(mImageLocation);
        newEvent.addMedia(media);

        mUser.getTimeline().addEvent(newEvent);

        finish();
    }

    public void cancelCreateEvent(View v) {
        finish();
    }
}
