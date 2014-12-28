package htwg.de.hitbook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import htwg.de.hitbook.adapter.HistoryListItemAdapter;
import htwg.de.hitbook.comparator.TreeComparator;
import htwg.de.hitbook.database.DatabaseAccess;
import htwg.de.hitbook.model.FelledTree;

/**
 * Created by Ecki on 14.12.2014.
 */
public class HistoryActivity extends ActionBarActivity {


    List<FelledTree> felledTrees = new ArrayList<FelledTree>();
    DatabaseAccess dbAccess;
    ListView listView;
    Spinner spinnerSort;
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
                // Get Actual FelledTree
                FelledTree felledTree = (FelledTree)listView.getAdapter().getItem(position);


                //Start Detail View Activtiy via Intent
                Intent intent = new Intent();
                intent.setClass(context, HistoryDetailActivity.class);
                intent.putExtra("id",felledTree.getIdAsString());
                //intent.putExtra("id",((TextView)view.findViewById(R.id.textViewItemID)).getText());
                startActivity(intent);
            }
        });



        // Init Spinner
        spinnerSort = (Spinner) findViewById(R.id.spinnerHistory);
        List<String> spinnerItems = new ArrayList<String>();
        // Selectable Options
        spinnerItems.add(getString(R.string.s_optn_date));
        spinnerItems.add(getString(R.string.s_optn_date_inv));
        spinnerItems.add(getString(R.string.s_optn_lumberjack));
        spinnerItems.add(getString(R.string.s_optn_lumberjack_inv));
        spinnerItems.add(getString(R.string.s_optn_team));
        spinnerItems.add(getString(R.string.s_optn_team_inv));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item,spinnerItems);
        spinnerSort.setAdapter(arrayAdapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(context,view.toString(),Toast.LENGTH_LONG).show();
                fillList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
        felledTrees = dbAccess.getAllFelledTrees();
        dbAccess.close();

        String sortingMethod = spinnerSort.getSelectedItem().toString();

        // Sort List depending on choosen sorting method by the user
        if(sortingMethod.equals(getString(R.string.s_optn_date)) ||
                sortingMethod.equals(getString(R.string.s_optn_lumberjack)) ||
                sortingMethod.equals(getString(R.string.s_optn_team))){
            Collections.sort(felledTrees, new TreeComparator(TreeComparator.SORT_OPTION_DATE_NEW_OLD));

            if (sortingMethod.equals(getString(R.string.s_optn_lumberjack))){
                Collections.sort(felledTrees, new TreeComparator(TreeComparator.SORT_OPTION_LUMBER_A_Z));
            }
            if (sortingMethod.equals(getString(R.string.s_optn_team))){
                Collections.sort(felledTrees, new TreeComparator(TreeComparator.SORT_OPTION_TEAM_A_Z));
            }
        }
        if(sortingMethod.equals(getString(R.string.s_optn_date_inv)) ||
                sortingMethod.equals(getString(R.string.s_optn_lumberjack_inv)) ||
                sortingMethod.equals(getString(R.string.s_optn_team_inv))){
            Collections.sort(felledTrees, new TreeComparator(TreeComparator.SORT_OPTION_DATE_OLD_NEW));

            if (sortingMethod.equals(getString(R.string.s_optn_lumberjack_inv))){
                Collections.sort(felledTrees, new TreeComparator(TreeComparator.SORT_OPTION_LUMBER_Z_A));
            }
            if (sortingMethod.equals(getString(R.string.s_optn_team_inv))){
                Collections.sort(felledTrees, new TreeComparator(TreeComparator.SORT_OPTION_TEAM_Z_A));
            }
        }


        HistoryListItemAdapter myAdapter = new HistoryListItemAdapter(context, R.layout.list_item_history, felledTrees);

        try {
            listView.setAdapter(myAdapter);
        }
        catch (Exception e){
            Log.d("HistoryActivity","Error at setting Adapter");
        }


    }
}
