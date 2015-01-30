// Copyright 2014 Grzegorz Wlodarek
// Distributed under the terms of the GNU General Public License.
//
// This file is part of Toronto Transit.
//
// This is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This file is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this file.  If not, see <http://www.gnu.org/licenses/>.

//Package
package com.gregorywlodarek.torontotransit.torontotransit;

//Imports
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


/*
    DATA LIST INFO

    0-route id
    1-stop id
    2-route number
    3-route direction
    4-route name
    5-stop name
    6-lat
    7-long
    8-to location
    9-estimations
    10-sms (may not exist)

 */

/**
 * Contains all the favourites data.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class FavouritesData extends Observable {
    //Instance variables
    private HashMap<String, String> data;
    private static FavouritesData reference = null;
    private ArrayList<String> newData;
    private static MainActivity context = null;
    //private String locationProvider = LocationManager.NETWORK_PROVIDER;
    private LocationManager lm = (LocationManager) context.getSystemService(
            Context.LOCATION_SERVICE);

    /**
     * Default Constructor.
     */
    private FavouritesData() {
        data = new HashMap<String, String>();
    }

    /**
     * Retrieves the data and returns it to the user.
     *
     * @return the current data held in storage in a new HashMap so original data cannot be
     * manipulated.
     */
    public HashMap<String, String> getData() {
        return new HashMap<String, String>(data);
    }

    /**
     * Setup the data. Only used when loading data on application bootup.
     *
     * @param hashMapData The data that will be used for the session.
     */
    public void setData(HashMap<String, String> hashMapData) {
        data = hashMapData;
        setChanged();
        notifyObservers(new HashMap<String, String>(data));
    }

    public static void setNewContext(MainActivity c) {
        context = c;
    }

    //Set up new Data
    public void setNewData(ArrayList<String> list) {
        newData = new ArrayList<String>(list);
        setChanged();
        notifyObservers();
    }

    //Singleton Design Pattern
    public static FavouritesData createFavouritesData() {
        if (reference == null) {
            reference = new FavouritesData();
        }
        return reference;
    }

    public void addFavourite(String stopID, String stopDetails) {
        data.put(stopID, stopDetails);
        setChanged();
        notifyObservers("Adding");
        new FavouritesResult();
    }

    public void removeFavourite(String stopID) {
        data.remove(stopID);
        setChanged();
        notifyObservers("Removing");
        FavouritesResult.allowFavouritesFetching();
        MainActivity.setRunThread(true);
        new FavouritesResult();
        }

    public ArrayList<String> getNewData() {
        //Location lastKnownLocation = lm.getLastKnownLocation(locationProvider);
        Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation == null) {
            lastKnownLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (lastKnownLocation == null) {
            if (newData != null) {
                Collections.sort(newData, new CompareByText());
            }
        } else {
            if (newData != null) {
                Collections.sort(newData, new CompareByGeography());
            }
        }

        if (newData != null) {
            return new ArrayList<String>(newData);
        } else {
            return null;
        }
    }

    public boolean checkForStopID(String stopID) {
        return data.containsKey(stopID);
    }

    public void subscribe(Observer o) {
        addObserver(o);
    }

    public void unsubscribe(Observer o) {
        deleteObserver(o);
    }

    //Returns the data in an ArrayList of Strings form by going through every value in data.
    public ArrayList<String> getFavourites() {
        //Location lastKnownLocation = lm.getLastKnownLocation(locationProvider);
        Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation == null) {
            lastKnownLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        ArrayList<String> favouritesList = new ArrayList<String>();
        for (String s : data.values()) {
            favouritesList.add(s);
        }
        if (lastKnownLocation == null) {
            Collections.sort(favouritesList, new CompareByText());
        } else {
            Collections.sort(favouritesList, new CompareByGeography());
        }

        return favouritesList;
    }

    //Comparator class to sort the ArrayList before returning it for getFavourites() method.
    //Sorts by comparing the text.
    private class CompareByText implements Comparator<String> {

        //Method to do the comparing.
        public int compare(String e1, String e2) {

                String[] e1List = e1.split("\\|");
                String[] e2List = e2.split("\\|");

                if (e1List[2].equals("1S")) {
                    return -1;
                }

                if (e2List[2].equals("1S")) {
                    return 1;
                }

                if (Integer.parseInt(e1List[2]) < Integer.parseInt(e2List[2])) {
                    return -1;
                }

                if (Integer.parseInt(e1List[2]) > Integer.parseInt(e2List[2])) {
                    return 1;
                }

                if (Integer.parseInt(e1List[2]) == Integer.parseInt(e2List[2])) {
                    if (e1List[3].startsWith("E") && e2List[3].startsWith("W")) {
                        return -1;
                    }

                    if (e1List[3].startsWith("W") && e2List[3].startsWith("E")) {
                        return 1;
                    }

                    return e1List[5].compareTo(e2List[5]);
                }

            return 0;
        }
    }


    //Comparator class to sort the ArrayList before returning it for getFavourites() method.
    //Sorts by comparing the longitude and latitude.
    private class CompareByGeography implements Comparator<String> {
        //private Location lastKnownLocation = lm.getLastKnownLocation(locationProvider);
        private Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        private double earthRadius = 6371; //kilometers

        //Method to do the comparing.
        public int compare(String e1, String e2) {
            if (lastKnownLocation == null) {
                lastKnownLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            double userLatitude = lastKnownLocation.getLatitude();
            double userLongitude = lastKnownLocation.getLongitude();

            String[] e1List = e1.split("\\|");
            double e1Lat = Double.parseDouble(e1List[6]);
            double e1Long = Double.parseDouble(e1List[7]);

            String[] e2List = e2.split("\\|");
            double e2Lat = Double.parseDouble(e2List[6]);
            double e2Long = Double.parseDouble(e2List[7]);

            double e1dLat = Math.toRadians(e1Lat - userLatitude);
            double e1dLng = Math.toRadians(e1Long - userLongitude);
            double e1sindLat = Math.sin(e1dLat / 2);
            double e1sindLng = Math.sin(e1dLng / 2);
            double e1a = Math.pow(e1sindLat, 2) + Math.pow(e1sindLng, 2) * Math.cos(Math.toRadians(
                    userLatitude) * Math.cos(Math.toRadians(e1Lat)));
            double e1c = 2 * Math.atan2(Math.sqrt(e1a), Math.sqrt(1-e1a));
            float e1dist = (float) (earthRadius * e1c);

            double e2dLat = Math.toRadians(e2Lat - userLatitude);
            double e2dLng = Math.toRadians(e2Long - userLongitude);
            double e2sindLat = Math.sin(e2dLat / 2);
            double e2sindLng = Math.sin(e2dLng / 2);
            double e2a = Math.pow(e2sindLat, 2) + Math.pow(e2sindLng, 2) * Math.cos(Math.toRadians(
                    userLatitude) * Math.cos(Math.toRadians(e2Lat)));
            double e2c = 2 * Math.atan2(Math.sqrt(e2a), Math.sqrt(1-e2a));
            float e2dist = (float) (earthRadius * e2c);

            if (e1dist > e2dist) {
                return 1;
            } else if (e1dist < e2dist) {
                return -1;
            } else {
                return 0;
            }

        }
    }

}




