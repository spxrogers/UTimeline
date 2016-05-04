package net.srogers.utimeline;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.srogers.utimeline.model.UTimelineEvent;
import net.srogers.utimeline.model.UTimelineMedia;
import net.srogers.utimeline.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private int mEvent;
    private List<UTimelineMedia> mMedia;

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

        mEvent = getIntent().getIntExtra("eventIndex", -1);
        Log.d(TAG, "In onCreate and index is: " + mEvent);
        if (mEvent == -1) {
            createNewEvent();
        } else {
            editEvent();
        }
    }

    private void createNewEvent() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        mMedia = new ArrayList<>();

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

    private void editEvent() {
        User user = User.getCurrentUser();

        UTimelineEvent event = user.getEvent(mEvent);
        Date date = event.getDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

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

        LinearLayout scrollView = (LinearLayout) findViewById(R.id.image_scroller);
        String path = null;
        mMedia = event.getMedia();
        if (mMedia.size() > 0)
            path = mMedia.get(0).getLocation();
        if (path != null) {
            for(UTimelineMedia media : mMedia)
                scrollView.addView(insertPhoto(media.getLocation()));
        }
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

        mEventDate.setText(new StringBuilder().append(m).append("-").append(d).append("-" + Integer.toString(year)));
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
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = timeStamp + ".jpg";
                    File storageDir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);
                    mImageLocation = storageDir.getAbsolutePath() + "/" + imageFileName;
                    File file = new File(mImageLocation);
                    Uri outputFileUri = Uri.fromFile(file);
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
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
        LinearLayout scrollView = (LinearLayout) findViewById(R.id.image_scroller);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TITLE_REQUEST_CODE :
                    Button titleButton = (Button) findViewById(R.id.add_event_title);
                    titleButton.setText(data.getStringExtra(TITLE_MESSAGE));
                    Log.d(TAG, "Title received: " + data.getStringExtra(TITLE_MESSAGE));
                    break;
                case REQUEST_CAMERA :
                    File imgFile = new File(mImageLocation);
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        int nh = (int) (myBitmap.getHeight() * (512.0 / myBitmap.getWidth()));
                        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(imgFile);
                            scaled.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                            // PNG is a lossless format, the compression factor (100) is ignored
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //ivImage.setImageBitmap(scaled);
                        mMedia.add(new UTimelineMedia(mImageLocation));
                        scrollView.addView(insertPhoto(mImageLocation));
                    }
                    break;
                case SELECT_FILE :
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

                    mMedia.add(new UTimelineMedia(selectedImagePath));
                    scrollView.addView(insertPhoto(selectedImagePath));
                    break;
            }
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


    public void saveEvent(View v) {
        Button titleButton = (Button) findViewById(R.id.add_event_title);
        String titleText = titleButton.getText().toString();

        EditText description = (EditText) findViewById(R.id.add_event_description);
        String descriptionText = description.getText().toString();

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        Date date = c.getTime();

        User user = User.getCurrentUser();
        if (mEvent != -1) {
            UTimelineEvent event = user.getEvent(mEvent);
            event.setTitle(titleText);
            event.setDescription(descriptionText);
            event.setDate(date);
            event.setMedia(mMedia);
        } else {
            UTimelineEvent newEvent = new UTimelineEvent(titleText, descriptionText, date);
            newEvent.setMedia(mMedia);
            user.addEvent(newEvent);
        }

        // check if exists in future
        List<String> planned = user.getPlanned();
        while (planned.contains(titleText)) {
            planned.remove(titleText);
        }
        user.setPlanned(planned);

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
