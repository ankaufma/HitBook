package htwg.de.hitbook.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import htwg.de.hitbook.R;


public class GPSManager extends Service implements android.location.LocationListener{

    public static final String LOCATION = "Location";
    public static final String NOTIFICATION = "htwg.de.hitbook";
    private static final Integer UPDATE_INTERVAL_GPS_MS = 300;
    private static final Integer UPDATE_INTERVAL_NETWORK_MS = 300;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    LocationManager locationManager;
    String provider;
    Location oldLocation;

    public void initGPS() {

        // Get the location Manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean gpsEnabled = false;
        boolean networkEnabled = false;

        try {
            // Check if Location Service is activated
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
           Log.d("GPSManager","Problem getting Provider status");
        }

//        if (!enabled) {
//           Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//           startActivity(intent);
//        }

        if(gpsEnabled || networkEnabled) {
            // Define the criteria how to select the locatioin provider -> use
            // default
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);

            // if location is already available
            if (location != null) {
                //System.out.println("Provider " + provider + " has been selected.");
                onLocationChanged(location);
            }


            // Update Position every x ms
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_INTERVAL_GPS_MS, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_INTERVAL_NETWORK_MS, 1, this);
        }
        else {
            // Show warning on deactivated gps
            Toast.makeText(this,getString(R.string.toast_gps_not_activated),Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Publishes the determined position for other activites
     * @param loc Location to publish
     */
    private void publishPosition(Location loc) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(LOCATION, loc);
        sendBroadcast(intent);

        // Service stops
        //locationManager.removeUpdates(this);
        //stopSelf();
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onCreate() {
        initGPS();

    }

    @Override
    public void onDestroy(){
        // Stop getting Location updates if Service is destroyed
        locationManager.removeUpdates(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onLocationChanged(Location location) {
        if(isBetterLocation(location, oldLocation)) {
            oldLocation = location;
            publishPosition(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
