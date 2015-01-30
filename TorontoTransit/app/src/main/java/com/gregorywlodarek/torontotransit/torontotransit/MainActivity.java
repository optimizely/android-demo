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
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Process;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import com.optimizely.Optimizely;

/**
 * Main activity.
 * Creates button listeners for every scrollable tab.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class MainActivity extends FragmentActivity implements Observer {
    private ViewPager Tab;
    //private TabPagerAdapter TabAdapter;
    private ActionBar actionBar;
    private static boolean runThread = true;
    private Handler mHandler = new Handler();
    private FavouritesData fd;
    private static boolean refreshFavouritesTab = true;
    private LocationManager lm;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create the tabs on create
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Optimizely.setVerboseLogging(true);
        Optimizely.startOptimizely("AAM7hIkAW0q7sdbkQszwnKwONyFQ6cfZ~2373321043", getApplication());

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

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
            });
        } else {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

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
                    });
        }

        //Set the context for FavouritesData and SMS
        FavouritesData.setNewContext(this);
        SMS.setNewContext(this);
        BusAlert.setNewContext(this);
        Favourites.setNewContext(this);
        FavouritesResult.setNewContext(this);
        NearbyResults.setNewContext(this);
        Result.setNewContext(this);
        Nearby.setNewContext(this);
        GetNearbyStops.setNewContext(this);

        TabPagerAdapter TabAdapter = new TabPagerAdapter(getSupportFragmentManager());
        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar = getActionBar();
                        actionBar.setSelectedNavigationItem(position);
                    }
                }
        );
        Tab.setAdapter(TabAdapter);
        Tab.setOffscreenPageLimit(4);

        //Subscribe this class to FavouritesData
        fd = FavouritesData.createFavouritesData();
        fd.subscribe(this);

        //Load favourites
        File file = new File(getFilesDir(), "Data");
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            HashMap<String, String> dataToRetrieve = new HashMap<String, String>();
            try {
                dataToRetrieve = (HashMap<String, String>) inputStream.readObject();
            } catch (ClassCastException e) {
                System.out.println("Cannot load data.");
            }
            inputStream.close();
            fd.setData(dataToRetrieve);
        } catch (FileNotFoundException e) {
            System.out.println("Error, can't load data.");
        } catch (OptionalDataException e) {
            System.out.println("Error, can't load data.");
        } catch (ClassNotFoundException e) {
            System.out.println("Error, can't load data.");
        } catch (IOException e) {
            System.out.println("Error, can't load data.");
        }

        //Setup the title.
        actionBar = getActionBar();
        try {
            actionBar.setTitle("");
        } catch (NullPointerException e) {
            System.out.println("Cannot set the title.");
        }

        //Enable Tabs on Action Bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener(){
            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                if (tab.getPosition() == 0 && refreshFavouritesTab) {
                    //Get favourite estimations when favourites tab is reselected.
                    updateLocation();
                    new FavouritesResult();
                } else if (tab.getPosition() == 2) {
                    updateLocation();
                    NearbyResults.createNearbyResults().run();
                    /*
                    Toast.makeText(getApplicationContext(), "Finding nearby stops...", Toast.

                            LENGTH_LONG).show();
                    */
                }
            }
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                Tab.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0 && refreshFavouritesTab) {
                    //Get favourite estimations when favourites is selected.
                    updateLocation();
                    new FavouritesResult();
                } else if (tab.getPosition() == 2) {
                    updateLocation();
                    NearbyResults.createNearbyResults().run();
                    /*
                    //Toast.makeText(getApplicationContext(), "Finding nearby stops...", Toast.
                            LENGTH_LONG).show();
                            */
                }
            }
            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
            }};

        //Add New Tabs
        actionBar.addTab(actionBar.newTab().setText("Favourites").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Find").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Nearby").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Misc").setTabListener(tabListener));

        //Update Find tab and Favourites tab in the background.
        Thread getTimes = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                //Check if all conditions met before requesting an update.
                                if (Find.getCanClickRoute() && Find.getCanClickDirection() &&
                                        Find.getCanClickStop() && Find.getCanClickResult() &&
                                        runThread) {
                                    //Update the information.
                                    new Result();
                                }
                                if (runThread) {
                                    boolean haveConnectedWifi = false;
                                    boolean haveConnectedMobile = false;

                                    ConnectivityManager cm = (ConnectivityManager)
                                            getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                                    for (NetworkInfo ni : netInfo) {
                                        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                                            if (ni.isConnected())
                                                haveConnectedWifi = true;
                                        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                                            if (ni.isConnected())
                                                haveConnectedMobile = true;
                                    }

                                    if (haveConnectedWifi || haveConnectedMobile) {
                                        //Allow fetching
                                        FavouritesResult.allowFavouritesFetching();
                                    } else {
                                        //Don't fetch results.
                                        FavouritesResult.cancelFavouritesFetching();

                                    }

                                    new FavouritesResult();
                                }

                            }
                        });
                    } catch (Exception e) {
                        System.out.println("Cannot run thread.");
                    }
                }
            }
        });

        //Start the thread.
        getTimes.start();
        updateLocation();
    }

    public static void setRefreshFavouritesTab(boolean b) {
        refreshFavouritesTab = b;
    }

    public void updateLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        LocationListener l = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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
        };

        lm.requestSingleUpdate(criteria, l, null);

    }

    @Override
    public void onBackPressed() {
        boolean hadHiddenOptionOpen = false;
        ArrayList<RelativeLayout> listOfHiddenOptions = Favourites.getHiddenOptionsArray();

        for (RelativeLayout r : listOfHiddenOptions) {
            if (r.getVisibility() == View.VISIBLE) {
                r.setVisibility(View.GONE);
                setRunThread(true);
                FavouritesResult.allowFavouritesFetching();
                hadHiddenOptionOpen = true;
            }
        }

        if (!hadHiddenOptionOpen) {
            Process.killProcess(Process.myPid());
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        //Pause the thread.
        if (Favourites.getCurrentAlerts().isEmpty()) {
            runThread = false;
        } else {
            runThread = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Resume the thread.
        runThread = true;
        updateLocation();
    }

    public static void setRunThread(boolean b) {
        runThread = b;
    }

    public void update(Observable o, Object arg) {
        //Save the current data in the FavouritesData to a file.
        //Whenever a favourites is added or removed, data is saved automatically.
        File file = new File(getFilesDir(), "Data");
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(fd.getData());
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error, can't save data.");
        } catch (IOException e) {
            System.out.println("Error, can't save data.");
        }
    }
}
