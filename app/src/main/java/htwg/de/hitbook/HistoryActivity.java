package htwg.de.hitbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import htwg.de.hitbook.database.DatabaseAccess;

/**
 * Created by Ecki on 14.12.2014.
 */
public class HistoryActivity extends ActionBarActivity {

    List<String> dates = new ArrayList<String>();
    List<String> ids = new ArrayList<String>();
    DatabaseAccess dbAccess;
    ListView listView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        context = this.getBaseContext();

        dbAccess = new DatabaseAccess(this.getBaseContext());

        // Init ListView
        listView = (ListView) findViewById(R.id.listView);

        // Start Detail Activity, if an Item in List was clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //Started Detail View Activtiy via Intent
                Intent intent = new Intent();
                intent.setClass(context, HistoryDetailActivity.class);
                intent.putExtra("id",((TextView)view).getText());
                startActivity(intent);
            }
        });

        fillList();

    }

    public void fillList(){


        dbAccess.open();
//        dbAccess.createNewFelledTree("John","West","","123","345",8,80,null);
//        dbAccess.createNewFelledTree("Joe","West","","123","345",8,80);
//        dbAccess.createNewFelledTree("Jack","East","","123","345",8,80);

        dates = dbAccess.getAllDates();
        ids = dbAccess.getAllIds();

        dbAccess.close();

        ArrayAdapter<String> aa = new ArrayAdapter<String>(HistoryActivity.this,android.R.layout.simple_list_item_1,ids);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(aa);
    }
}
