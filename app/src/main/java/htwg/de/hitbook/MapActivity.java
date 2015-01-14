package htwg.de.hitbook;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import htwg.de.hitbook.database.DatabaseAccess;
import htwg.de.hitbook.model.FelledTree;
import htwg.de.hitbook.service.GPSTracker;


public class MapActivity extends ActionBarActivity {
    GoogleMap map;
    GPSTracker gps;
    DatabaseAccess dba = new DatabaseAccess(this);
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
     //   gps = new GPSTracker(this);
        gps = new GPSTracker(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);
        dba.open();
        addMyMarkers(dba.getAllFelledTrees());
        dba.close();
    }

    private void addMyMarkers(List<FelledTree> thisFelledTrees) {
        for(FelledTree ft: thisFelledTrees) {
            map.addMarker(new MarkerOptions().
                    position(new LatLng(Double.parseDouble(ft.getLatitude()), Double.parseDouble(ft.getLongitude()))).
                    title(ft.getLumberjack() + " has felled that tree on " + ft.getDate() + " in " + ft.getAreaDescription()).
                    icon(BitmapDescriptorFactory.fromBitmap(ft.getThumbnail())));
        }
    }

    private void removeMyMarkers() {
        map.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        Log.d("MainActivity", "Wird On Create ausgeführt?");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case (R.id.action_settings):
                return true;
            case (R.id.justToday):
                removeMyMarkers();
                dba.open();
                addMyMarkers(filterForDate(dba.getAllFelledTrees(),dba.getDate()));
                dba.close();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<FelledTree> filterForDate(List<FelledTree> felledTrees, String date){

        List<FelledTree> treesToDelete = new ArrayList<>();
        FelledTree felledTree;

        String treeDate;

        for(int i=0; i<felledTrees.size(); i++){
            felledTree = felledTrees.get(i);
            treeDate = felledTree.getDate().substring(0,10);
            if (!treeDate.equals(date.substring(0,10))){
                treesToDelete.add(felledTree);
            }
        }

        felledTrees.removeAll(treesToDelete);

        return felledTrees;
    }
}
