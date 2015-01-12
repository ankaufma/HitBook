package htwg.de.hitbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import htwg.de.hitbook.database.DatabaseAccess;


public class SettingsActivity extends ActionBarActivity {

    Button btnDelAll;
    DatabaseAccess dbAccess;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = this.getBaseContext();
        dbAccess = new DatabaseAccess(context);

        btnDelAll = (Button) findViewById(R.id.buttonDeleteAllData);
        btnDelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);

                    // Setting Dialog Title
                    alertDialog.setTitle(getString(R.string.delete_all_alert_title));

                    // Setting Dialog Message
                    alertDialog.setMessage(getString(R.string.delete_all_alert_message));

                    // On pressing Settings button
                    alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            // continue with delete
                            dbAccess.open();
                            dbAccess.deleteDatabase(context);
                            dbAccess.close();

                            Toast.makeText(context,getString(R.string.toast_all_data_deleted),Toast.LENGTH_LONG).show();
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
                    Log.d("SettingsActivity", "Delete Error");
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
