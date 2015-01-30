package com.gregorywlodarek.torontotransit.torontotransit;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by sshah on 1/29/15.
 */
public class FakeLocationManager {

    public Location getLastKnownLocation(String provider)
    {
        Location returnLocation = new Location(provider);
        returnLocation.setLatitude(43.7000);
        returnLocation.setLongitude(-79.4000);
        return returnLocation;
    }
}
