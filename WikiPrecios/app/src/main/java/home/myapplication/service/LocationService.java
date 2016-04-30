package home.myapplication.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


/**
 * Created by ema on 16/09/2015.
 * Esta clase permite obtener la ubicacion del usuario al momento de realizar la
 * consulta de un precio de un producto.
 * Esta clase obtiene la localizacion del usuario, pero se encuentra en estado de
 * desarrollo, debido a que suele no ser exacta...
 */
public class LocationService extends Service implements LocationListener {
    private Context context;

    Location location= null;
    boolean gpsIsActive = false;

    protected LocationManager locationManager;

    public LocationService() {
        super();
        this.context = this.getApplicationContext();
    }

    public LocationService(Context c) {
        super();
        this.context = c;
    }

    /**
     * Old getLocation... Obtener la ubicacion del usuario v1, esta implementacion no funcionaba si
     * el gps estaba desactivado
     */
//    public Location getLocation() {
//        try {
//            locationManager = (LocationManager) this.context.getSystemService(LOCATION_SERVICE);
//            gpsIsActive = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
//            Log.e("LocationService","isActive"+gpsIsActive);
//            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 1000 * 60, 10, this);
//            location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
//            Log.e("LocationService","location :"+location);
//        } catch (Exception e) {
//             e.printStackTrace();
//            Log.e("LocationExcepcion",e.toString() );
//        }
//        return location;
//    }
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean canGetLocation;
    long MIN_TIME_BW_UPDATES = 60000;
    float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    /**
     * GetLocation v2 : esta implementacion obtiene la ubicacion del usuario pero a diferencia de la v1
     * permite obtener la ubicacion a travez del servicio de internet.
     * @return la localizacion del usuario
     */
    public Location getLocation() {
        Location gpsLocation = null;
        Location networkLocation = null;
        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                return null;
            } else {
                this.canGetLocation = true;
                Log.e("LocationService"," isNetworkEnable :"+isNetworkEnabled);
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                // if GPS Enabled get lat/long using GPS Services
                Log.e("LocationService"," isGpsEnable :"+isGPSEnabled);
                if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                 }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (gpsLocation != null && networkLocation != null) {
            location= (getBetterLocation(gpsLocation, networkLocation));
        } else if (gpsLocation != null) {
            location=  gpsLocation;
        } else if (networkLocation != null) {
            location = networkLocation;
        }
        return location;
    }


    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    }

    @Override
    public void onProviderEnabled(String provider) {    }

    @Override
    public void onProviderDisabled(String provider) {    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;


    /** Determines whether one Location reading is better than the current Location fix.
     * Code taken from
     * http://developer.android.com/guide/topics/location/obtaining-user-location.html
     *
     * @param newLocation  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new
     *        one
     * @return The better Location object based on recency and accuracy.
     */
    protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return newLocation;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return newLocation;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return newLocation;
        } else if (isNewer && !isLessAccurate) {
            return newLocation;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return newLocation;
        }
        return currentBestLocation;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
