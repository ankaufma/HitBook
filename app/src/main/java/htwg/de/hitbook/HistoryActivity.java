package htwg.de.hitbook;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import htwg.de.hitbook.database.DatabaseAccess;

/**
 * Created by Ecki on 14.12.2014.
 */
public class HistoryActivity extends ActionBarActivity {

    List<String> dates = new ArrayList<String>();
    DatabaseAccess dbAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        fillList();

    }

    public void fillList(){


        dbAccess = new DatabaseAccess(this.getBaseContext());

        dbAccess.open();
        dbAccess.createNewFelledTree("John","West","","123","345",8,80,"2014.12.17");
        dbAccess.createNewFelledTree("Joe","West","","123","345",8,80,"2014.12.14");
        dbAccess.createNewFelledTree("Jack","East","","123","345",8,80,"2014.10.20");

        dates = dbAccess.getAllDates();

        dbAccess.close();

        dates.add("Eins");
        dates.add("Zwei");
        dates.add("Drei");

        ArrayAdapter<String> aa = new ArrayAdapter<String>(HistoryActivity.this,android.R.layout.simple_list_item_1,dates);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(aa);
    }
}
