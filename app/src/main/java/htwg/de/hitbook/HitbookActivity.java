package htwg.de.hitbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import htwg.de.hitbook.database.DatabaseAccess;
import htwg.de.hitbook.service.GPSManager;
import htwg.de.hitbook.service.GPSTracker;


public class HitbookActivity extends ActionBarActivity {

    private final int REQUEST_IMAGE_CAPTURE = 1;
    Button bCamera;
 //   ImageButton ibRefresh;
    EditText etLumberjack, etTeam;
    EditText etDiameter,etLength;
    EditText etArea;
    TextView tvLongitude, tvLatitude;
    Bitmap imageBitmap;
    BroadcastReceiver broadcastReceiver;


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

    /**
     * opens camera to take a picture
     */
    private void takeAPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            //imageView.setImageBitmap(imageBitmap);
            createNewTree();

            // Save last team and lumberjack
            saveContentToPref(etLumberjack,"lumberjack");
            saveContentToPref(etTeam,"team");
        }
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
                return true;
            case (R.id.map):
                showMap();
                return true;
            case (R.id.history):
                showHistory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showHistory(){
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    public void showMap() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra("Picture", imageBitmap);
        startActivity(intent);
    }

    /**
     * This function creates a new tree in the database by using the information the
     * user has written into the layout
     */
    public void createNewTree(){

        dbAccess.open();
        dbAccess.createNewFelledTree(
                etLumberjack.getText().toString(),
                etTeam.getText().toString(),
                etArea.getText().toString(),
                tvLatitude.getText().toString(),
                tvLongitude.getText().toString(),
                Double.parseDouble(etLength.getText().toString()) ,
                Double.parseDouble(etDiameter.getText().toString()),
                imageBitmap
        );
        dbAccess.close();
    }

    /**
     * Check whether all text fields are filled before taking a picture
     * @return
     */
    public boolean allTextFieldsFilled(){
        if(     etLumberjack.getText().toString().isEmpty() ||
                etTeam.getText().toString().isEmpty() ||
                etLength.getText().toString().isEmpty() ||
                etDiameter.getText().toString().isEmpty())
        {
            return false;
        }
        else return true;
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
                ((Double)location.getLongitude()).toString());
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
}
