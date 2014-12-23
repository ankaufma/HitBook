package htwg.de.hitbook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import htwg.de.hitbook.database.DatabaseAccess;
import htwg.de.hitbook.model.FelledTree;


public class HistoryDetailActivity extends ActionBarActivity {

    TextView tvId;
    TextView tvFelled;
    TextView tvLumber, tvTheTeam;
    TextView tvTreeDia, tvTreLen, tvVolume;
    ImageView ivTreePic;
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
        tvFelled=(TextView) findViewById(R.id.textViewFelled);
        tvLumber = (TextView) findViewById(R.id.textViewLumber);
        tvTheTeam = (TextView) findViewById(R.id.textViewTheTeam);
        tvTreeDia = (TextView) findViewById(R.id.textViewTreeDia);
        tvTreLen = (TextView) findViewById(R.id.textViewTreeLen);
        tvVolume = (TextView) findViewById(R.id.textViewVolume);
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

        //Load image
        ivTreePic = (ImageView)findViewById(R.id.imageViewTreePic);
        dbAccess.open();
        ivTreePic.setImageBitmap(dbAccess.getPictureById(id));
        dbAccess.close();

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
