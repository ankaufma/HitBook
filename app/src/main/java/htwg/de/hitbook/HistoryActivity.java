package htwg.de.hitbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import htwg.de.hitbook.adapter.HistoryListItemAdapter;
import htwg.de.hitbook.database.DatabaseAccess;
import htwg.de.hitbook.model.FelledTree;

/**
 * Created by Ecki on 14.12.2014.
 */
public class HistoryActivity extends ActionBarActivity {

    List<FelledTree> felledTrees = new ArrayList<FelledTree>();
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
                intent.putExtra("id",((TextView)view.findViewById(R.id.textViewItemID)).getText());
                startActivity(intent);
            }
        });



    }

    @Override
    public void onResume(){
        super.onResume();
        fillList();
    }

    public void fillList(){


        dbAccess.open();
//        dbAccess.createNewFelledTree("John","West","","123","345",8,80,null);
//        dbAccess.createNewFelledTree("Joe","West","","123","345",8,80);
//        dbAccess.createNewFelledTree("Jack","East","","123","345",8,80);

        felledTrees = dbAccess.getAllFelledTrees();

        dbAccess.close();

        HistoryListItemAdapter myAdapter = new HistoryListItemAdapter(context, R.layout.list_item_history, felledTrees);

        try {
            listView.setAdapter(myAdapter);
        }
        catch (Exception e){
            Log.d("HistoryActivity","Error at setting Adapter");
        }


    }
}
