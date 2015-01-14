package htwg.de.hitbook;

import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import htwg.de.hitbook.database.DatabaseAccess;
import htwg.de.hitbook.model.FelledTree;
import htwg.de.hitbook.service.GPSManager;


public class HitbookActivity extends ActionBarActivity {

    private final static int REQUEST_IMAGE_CAPTURE = 1;
    private final static int THUMBSIZE = 64*2;
    private final static String EXTERNAL_STORAGE_FOLDER_NAME = "/Hitbook/";
    public final static String EXTERNAL_STORAGE_FOLDER_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()+EXTERNAL_STORAGE_FOLDER_NAME;
    private final static String PICTURE_PATH_NAME = "picPath";

    Button bCamera;
 //   ImageButton ibRefresh;
    EditText etLumberjack, etTeam;
    EditText etDiameter,etLength;
    EditText etArea;
    TextView tvLongitude, tvLatitude;
    Bitmap thumbnail;
    BroadcastReceiver broadcastReceiver;
    String mCurrentPhotoPath;

    //ImageView imageView;
    DatabaseAccess dbAccess;
    Context context;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            // set the camera button enabled if al necessary edittexts are filled
            bCamera.setEnabled(allTextFieldsFilled());

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hitbook);

        context = this.getBaseContext();
        dbAccess = new DatabaseAccess(context);
        //dbAccess.deleteDatabase(context);
        etLumberjack = (EditText) findViewById(R.id.editText);
        etTeam = (EditText) findViewById(R.id.editText2);
        etDiameter = (EditText) findViewById(R.id.editTextDiameter);
        etLength = (EditText) findViewById(R.id.editTextLength);
        etArea = (EditText) findViewById(R.id.editTextArea);
        tvLongitude = (TextView) findViewById(R.id.textViewLongitudeNumber);
        tvLatitude = (TextView) findViewById(R.id.textViewLatitudeNumber);

        bCamera = (Button) findViewById(R.id.camera);
     //   ibRefresh = (ImageButton) findViewById(R.id.imageButtonRefresh);
        //imageView = (ImageView) findViewById(R.id.imageView);

        etLumberjack.addTextChangedListener(textWatcher);
        etTeam.addTextChangedListener(textWatcher);
        etDiameter.addTextChangedListener(textWatcher);
        etLength.addTextChangedListener(textWatcher);

        // Add Listeners for the Buttons
        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeAPicture();
            }
        });
//        ibRefresh.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                startGPSService();
//                ibRefresh.setEnabled(false);
//            }
//        });

        // Check if Entry was already made
        setContentToLoadedPref(etLumberjack,"lumberjack");
        setContentToLoadedPref(etTeam,"team");

        // Add GPS Receiver for GPS Service
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               // ibRefresh.setEnabled(true);
                refreshPosition(
                        (Location)intent.getExtras().getParcelable(GPSManager.LOCATION));
            }
        };
    }

    private void startGPSService(){
        Intent intent = new Intent(this, GPSManager.class);
        startService(intent);
    }

    private void stopGPSService(){
        Intent intent = new Intent(this, GPSManager.class);
        stopService(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hitbook, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case (R.id.action_settings):
                showSettings();
                return true;
            case (R.id.map):
                showMap();
                return true;
            case (R.id.history):
                showHistory();
                return true;
            case (R.id.bluetooth):
                doDiscover();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void showHistory(){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void showMap() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("Picture", thumbnail);
        startActivity(intent);
    }

    public void doDiscover() {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost("http://192.168.43.94:9000/addFelledTrees");
                    json.put("lumberjack", "Ich bins... Juhu...");
                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        Toast.makeText(context,in.toString(),Toast.LENGTH_LONG).show();
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context,"ERROR",Toast.LENGTH_LONG).show();
                }

                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
        //mBluetoothAdapter.startDiscovery();
    }
    // Register the BroadcastReceiver

    /**
     * This function creates a new tree in the database by using the information the
     * user has written into the layout
     */
    public FelledTree createNewTree(){

        FelledTree felledTree;

        dbAccess.open();
        felledTree = dbAccess.createNewFelledTree(
                        etLumberjack.getText().toString(),
                        etTeam.getText().toString(),
                        etArea.getText().toString(),
                        tvLatitude.getText().toString(),
                        tvLongitude.getText().toString(),
                        Double.parseDouble(etLength.getText().toString()) ,
                        Double.parseDouble(etDiameter.getText().toString()),
                        thumbnail
        );
        dbAccess.close();

        return felledTree;
    }

    /**
     * Check whether all text fields are filled before taking a picture
     * @return True if TextFields are filled
     */
    public boolean allTextFieldsFilled(){
        return !(etLumberjack.getText().toString().isEmpty() ||
                etTeam.getText().toString().isEmpty() ||
                etLength.getText().toString().isEmpty() ||
                etDiameter.getText().toString().isEmpty());
    }

    private void setContentToLoadedPref(EditText tv, String pref){
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        String text = sharedPref.getString(pref,null);
        if(text!=null){
            tv.setText(text);
        }
    }

    private void saveContentToPref(EditText tv, String pref){
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPref.edit();
        String text = tv.getText().toString();
        prefsEditor.putString(pref,text);
        prefsEditor.apply();

    }

    /**
     * Updates the shown GPS Position in Layout
     */
    private void refreshPosition(Location location){
        tvLatitude.setText(
                ((Double) location.getLatitude()).toString());
        tvLongitude.setText(
                ((Double) location.getLongitude()).toString());
    }

    @Override
    public void onResume(){
        super.onResume();
        // Register the receiver
        registerReceiver(broadcastReceiver, new IntentFilter(GPSManager.NOTIFICATION));
        // Start GPS Service
        startGPSService();
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        // Start GPS Service
        stopGPSService();
    }

    /**
     * opens camera to take a picture
     */
    private void takeAPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException e){
                Log.d("HitbookActivity","Error saving picture");
            }
            // save Full-Sized picture only, if the File was successfully created, else just thumbnail
            if(photoFile !=null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }

            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            // Change picture rotation
            //changePictureRotation();

            // get Thumbnail of the image
            thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(mCurrentPhotoPath),
                    THUMBSIZE, THUMBSIZE);

            FelledTree felledTree;

            if (thumbnail != null) {
                felledTree = createNewTree();

                //Rename Full-Sized Picture to ID.jpg
                File to = new File(EXTERNAL_STORAGE_FOLDER_PATH,felledTree.getIdAsString()+".jpg");
                File from = new File(mCurrentPhotoPath);
                if(!from.renameTo(to))
                {
                    Log.d("HitbookActivity","Error renaimng Full-sized Image");
                }
                Toast.makeText(context,getString(R.string.toast_tree_successful),Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context,getString(R.string.toast_error_creating_tree),Toast.LENGTH_LONG).show();
            }
            // Save last team and lumberjack
            saveContentToPref(etLumberjack,"lumberjack");
            saveContentToPref(etTeam,"team");
        }
    }

    private void changePictureRotation(){
        try {
            ExifInterface exif = new ExifInterface(mCurrentPhotoPath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
            // Convert to degrees
            if (rotation == ExifInterface.ORIENTATION_ROTATE_90) { rotation = 90; }
            else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) {  rotation = 180; }
            else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) {  rotation = 270; }
            // Create rotation matrix
            Matrix matrix = new Matrix();
            if (rotation != 0f) {matrix.preRotate(rotation);}

            Bitmap adjustedBitmap = Bitmap.createBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath), 0, 0, 1000, 800, matrix, true);

            // save Bitmap
            File file = new File(mCurrentPhotoPath);
            FileOutputStream fOut = new FileOutputStream(file);

            adjustedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

        }catch (Exception e){
            Log.d("HitbookActivty","Rotation of picture not possible");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(EXTERNAL_STORAGE_FOLDER_PATH);
        if(!storageDir.exists()) {
            // Create Directory if it doesn't exist
            if(!storageDir.mkdir()){
                Log.d("HitbookActivity","Error creating directory");
            }
        }
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        //mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();

        return image;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save Picture path
        savedInstanceState.putString(PICTURE_PATH_NAME, mCurrentPhotoPath);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        mCurrentPhotoPath = savedInstanceState.getString(PICTURE_PATH_NAME);
    }
}
