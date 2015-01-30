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
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Locate nearby stops on a map.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class Nearby extends Fragment implements Observer {
    private static ArrayList<String> allStops = new ArrayList<String>();
    private static ArrayList<String> nearbyStops = new ArrayList<String>();
    private static ArrayList<String> nearbyStopsResult = new ArrayList<String>();
    private static MainActivity context;
    private View thisView;
    private LayoutInflater i;
    private static Activity acc;
    private ListView nearbyList;
    private TextView nearbyText;
    private FakeLocationManager lm = new FakeLocationManager();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //On creation of the view, do the following.
        i = inflater;

        //Import all stops into allStops array
        AssetManager am = context.getAssets();
        BufferedReader in;

        //Inflate the view with the layout.
        View nearby = inflater.inflate(R.layout.nearby_frag, container, false);

        try {
            //Try to open the stops data.
            in = new BufferedReader(new InputStreamReader(am.open("allStops.txt")));
            String line;

            //Append the data to a list
            while((line = in.readLine()) != null) {
                allStops.add(line);
            }

            in.close();

        } catch (IOException ex) {
            System.out.println("Error, cannot load all the stops.");
        }

        thisView = nearby;

        nearbyList = (ListView) nearby.findViewById(R.id.nearbyListView);
        nearbyText = (TextView) nearby.findViewById(R.id.nearbyText);

        nearbyText.setText("Searching for nearby stops...");
        nearbyText.setVisibility(View.VISIBLE);
        nearbyList.setVisibility(View.GONE);

        //Return the view.
        return nearby;
    }

    public Nearby() {
        super();
        NearbyResults.createNearbyResults().subscribe(this);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        acc = activity;
    }



    private void getNearbyStops(View v) {
        Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation == null) {
            lastKnownLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (lastKnownLocation != null) {

            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }

            if (haveConnectedMobile || haveConnectedWifi) {
                nearbyText.setVisibility(View.GONE);
                nearbyList.setVisibility(View.VISIBLE);

                int currentLocationOnList = nearbyList.getFirstVisiblePosition();

                //Create an adapter for the list
                ArrayAdapter<String> adapter = new NearbyListAdapter();

                //Set the ListView with the adapter.
                nearbyList.setAdapter(adapter);
                nearbyList.setSelection(currentLocationOnList);
            } else if (!haveConnectedMobile && !haveConnectedWifi) {
                //Set the text of the TextView.
                nearbyText.setText("Uh-oh!\nWe can't connect to the internet");
                nearbyList.setVisibility(View.GONE);
                nearbyText.setVisibility(View.VISIBLE);
            }
        } else {
            //Set the text of the TextView.
            nearbyText.setText("Uh-oh!\nWe can't find your location");
            nearbyText.setVisibility(View.VISIBLE);
            nearbyList.setVisibility(View.GONE);
        }
    }

    public static void setNewContext(MainActivity c) {
        context = c;
    }

    public static ArrayList<String> getAllStops() {
        return new ArrayList<String>(allStops);
    }

    public static void setNearbyStopsResult(ArrayList<String> a) {
        nearbyStopsResult = a;
    }

    public static ArrayList<String> getNearbyStops() {
        return new ArrayList<String>(nearbyStops);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (o.equals("NearbyResults")) {
            getNearbyStops(thisView);
        } else if (o.toString().equals("InternetIssues")) {
            nearbyFailed();
        } else if (o.toString().equals("NoBuses")) {
            noBusesResult();
        }
    }

    private void noBusesResult() {
        nearbyText.setText("No nearby bus stops found");
        nearbyList.setVisibility(View.GONE);
        nearbyText.setVisibility(View.VISIBLE);
    }

    private void nearbyFailed() {
        Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation == null) {
            lastKnownLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (lastKnownLocation != null) {

            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }

            if (!haveConnectedMobile && !haveConnectedWifi) {
                //Set the text of the TextView.
                nearbyText.setText("Uh-oh!\nNo internet connection found");
                nearbyList.setVisibility(View.GONE);
                nearbyText.setVisibility(View.VISIBLE);
            }
        } else {
            //Set the text of the TextView.
            nearbyText.setText("Uh-oh!\nNo location found.");
            nearbyText.setVisibility(View.VISIBLE);
            nearbyList.setVisibility(View.GONE);
        }


    }


    private class NearbyListAdapter extends ArrayAdapter<String> {
        View itemView;


        public NearbyListAdapter() {
            //Default constructor
            //Create an array adapter with the current context, our custom layout, and the data to
            //create a list view out of.
            super(acc, R.layout.nearby, nearbyStopsResult);

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //Make sure we have a view to work with (may have been given null)
            itemView = convertView;
            if (itemView == null) {
                itemView = i.inflate(R.layout.nearby, parent, false);
            }



            //find item to work with
            String currentNearby = nearbyStopsResult.get(position);
            final String[] dataList = currentNearby.split(" \\| ");

            //fill the view
            TextView stopName = (TextView) itemView.findViewById(R.id.item_stopName);
            stopName.setText(dataList[1]);

            TextView busDestination = (TextView) itemView.findViewById(R.id.item_busDestination);
            TextView timesText = (TextView) itemView.findViewById(R.id.item_timesText);
            TextView routeName = (TextView) itemView.findViewById(R.id.item_routeName);
            TextView direction = (TextView) itemView.findViewById(R.id.item_busDirection);
            TextView routeNumber = (TextView) itemView.findViewById(R.id.item_busNumber);
            //call results...
            if (dataList.length > 8) {
                busDestination.setText(dataList[7]);
                routeName.setText(dataList[6]);

                direction.setText(dataList[5].split(" - ")[1]);
                routeNumber.setText(dataList[5].split(" - ")[0]);

                //call results...
                timesText.setText(dataList[8]);
            }

            if (dataList.length <= 5) {
                busDestination.setText("N/A");
                routeName.setText("N/A");

                direction.setText("N/A");
                routeNumber.setText("N/A");

                //call results...
                timesText.setText("   No\nBuses");
            }


            if (busDestination.getText().toString().equals("Destination")) {
                busDestination.setText("No information");
                timesText.setText("   No\nBuses");
            }

            RelativeLayout listContainer =
                    (RelativeLayout) itemView.findViewById(R.id.list_container);

            listContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double stopLatitude = Double.parseDouble(dataList[2]);
                    Double stopLongitude = Double.parseDouble(dataList[3]);
                    String labelLocation = dataList[1];

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + stopLatitude
                            + ">,<" + stopLongitude + ">?q=<" + stopLatitude + ">,<" +
                            stopLongitude + ">(" + labelLocation + ")"));

                    startActivity(intent);
                }
            });


            //Return the view.
            return itemView;


        }

    }

}