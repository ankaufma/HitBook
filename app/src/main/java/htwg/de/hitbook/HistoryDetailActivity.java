package htwg.de.hitbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import htwg.de.hitbook.database.DatabaseAccess;
import htwg.de.hitbook.model.FelledTree;


public class HistoryDetailActivity extends ActionBarActivity {

    TextView tvId;
    TextView tvFelled;
    TextView tvLumber, tvTheTeam;
    TextView tvTreeDia, tvTreLen, tvVolume;
    TextView tvArea;
    ImageView ivTreePic;
    ImageButton ibDelete;
    Integer id;
    FelledTree felledTree;
    DatabaseAccess dbAccess;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        context = this.getBaseContext();
        dbAccess = new DatabaseAccess(context);

        // Get the id of the selected tree
        Intent intent = getIntent();
        id = Integer.parseInt(intent.getStringExtra("id"));

        tvId = (TextView) findViewById(R.id.textViewID);
        tvFelled = (TextView) findViewById(R.id.textViewFelled);
        tvLumber = (TextView) findViewById(R.id.textViewLumber);
        tvTheTeam = (TextView) findViewById(R.id.textViewTheTeam);
        tvTreeDia = (TextView) findViewById(R.id.textViewTreeDia);
        tvTreLen = (TextView) findViewById(R.id.textViewTreeLen);
        tvVolume = (TextView) findViewById(R.id.textViewVolume);
        tvArea = (TextView) findViewById(R.id.textViewTreeArea);
        ibDelete = (ImageButton) findViewById(R.id.imageButtonDelete);
        dbAccess.open();
        this.felledTree = dbAccess.getFelledTreeById(id);
        dbAccess.close();

        // Fill TextViews from database
        tvId.setText(id.toString());
        tvLumber.setText(felledTree.getLumberjack());
        tvFelled.setText(felledTree.getDate());
        tvTheTeam.setText(felledTree.getTeam());
        tvTreeDia.setText(((Double)felledTree.getDiameter()).toString());
        tvTreLen.setText(((Double)felledTree.getHeight()).toString());
        tvVolume.setText(felledTree.getVolumeAsString());
        tvArea.setText(felledTree.getAreaDescription());

        //Load image
        ivTreePic = (ImageView)findViewById(R.id.imageViewTreePic);

        // use thumbnail
        ivTreePic.setImageBitmap(felledTree.getThumbnail());

//        else {
//            // Else use thumbnail
//            ivTreePic.setImageBitmap(felledTree.getThumbnail());
//        }
        //dbAccess.open();
        //ivTreePic.setImageBitmap(dbAccess.getThumbnailById(id));
        //dbAccess.close();

        // Add delete Button Listener
        ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(HistoryDetailActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle(getString(R.string.delete_alert_title));

                    // Setting Dialog Message
                    alertDialog.setMessage(getString(R.string.delete_alert_message));

                    // On pressing Settings button
                    alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            // continue with delete
                            dbAccess.open();
                            dbAccess.deleteTreeById(id);
                            dbAccess.close();
                            // Kill Activity
                            finish();
                        }
                    });
                    // on pressing cancel button
                    alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    // Showing Alert Message
                    alertDialog.show();

                }catch (Exception e){
                    Log.d("HistoryDetailActivity","Delete Error");
                }
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();

        // use thumbnail
        ivTreePic.setImageBitmap(felledTree.getThumbnail());
    }

    @Override
    public void onResume(){
        super.onResume();

        // Check for Full-Sized Image
        File imgFile = new  File(HitbookActivity.EXTERNAL_STORAGE_FOLDER_PATH+felledTree.getIdAsString()+".jpg");
        if(imgFile.exists()) {
            // Add Image to View
            try {
                ivTreePic.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));

            }catch (Exception e){
                Log.d("HistoryDetailActivity","Error loading high-res pic");
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history_detail, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
