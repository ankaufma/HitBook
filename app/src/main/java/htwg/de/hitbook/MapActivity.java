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

import htwg.de.hitbook.service.GPSTracker;


public class MapActivity extends ActionBarActivity {
    GoogleMap map;
    GPSTracker gps;
    Bitmap bmp;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        bmp = getIntent().getExtras().getParcelable("Picture");
     //   gps = new GPSTracker(this);
        gps = new GPSTracker(this);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        map = mapFragment.getMap();
        map.setMyLocationEnabled(true);
        while(!addTreeMarker()) addTreeMarker();
    }

    private boolean addTreeMarker() {
        if(gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            gps.stopUsingGPS();
            if(bmp != null) {
                map.addMarker(new MarkerOptions().
                        position(new LatLng(latitude, longitude)).
                        title("Felled Tree").
                        icon(BitmapDescriptorFactory.fromBitmap(bmp)));
            } else {
                map.addMarker(new MarkerOptions().
                        position(new LatLng(latitude, longitude)).
                        title("Felled Tree"));
            }
            return true;
        } else {
            gps.showSettingsAlert();
            return false;
        }
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
            case (R.id.map):
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
