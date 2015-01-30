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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

/**
 * Fetches the time for the next bus off the internet and gives it to the user.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class Result extends Activity {

    //Instance variable
    private static MainActivity context = null;

    /**
     * Default Constructor
     * Check if favourites is the requester
     */
    public Result() {

        //Get details to determine estimations
        String routeID = Find.getRouteID();
        String stopID = Find.getStopID();
        runTime rt = new runTime();
        String url = ("http://webservices.nextbus.com/service/publicXMLFeed?command=" +
                "predictions&a=ttc&r=" + routeID + "&s=" + stopID);

        //Disable refresh button to disable refresh queue
        Find.canClickRefreshStop(false);

        //Show spinner hide refresh
        Find.refreshStopVisibility(false);
        Find.pbVisibility(true);


        rt.execute(url);
    }

    //Set the view to the find fragment on creation.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_frag);

    }


    //Thread to run in background
    private class runTime extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {

            HttpResponse response;
            HttpGet httpGet;
            HttpClient mHttpClient;
            String contents = "";
            String cleanedUpList = "";
            String returnText = "";

            try {

                mHttpClient = new DefaultHttpClient();

                httpGet = new HttpGet(urls[0]);

                response = mHttpClient.execute(httpGet);
                contents = EntityUtils.toString(response.getEntity(), "UTF-8");

            } catch (IOException e) {
                System.out.println("Error");
            }


            //Clean up the XML file to a simple viewable text string.
            String[] lines = contents.split("\n");

            for (String s : lines) {
                s = s.trim();
                if ((!s.startsWith("<predictions")) && (s.startsWith("<direction")
                        || s.startsWith("<prediction"))) {
                    cleanedUpList += s + "\n";
                }
            }

            cleanedUpList = cleanedUpList.trim().replaceAll("<direction title=\"", "");
            cleanedUpList = cleanedUpList.trim().replaceAll("/>", "");
            cleanedUpList = cleanedUpList.trim().replaceAll(">", "");
            cleanedUpList = cleanedUpList.trim().replaceAll(" minutes.*", "");
            cleanedUpList = cleanedUpList.trim().replaceAll("<prediction epochTime=\"", "");
            cleanedUpList = cleanedUpList.trim().replaceAll("\"", "");
            cleanedUpList = cleanedUpList.trim().replaceAll("seconds=", "");

            lines = cleanedUpList.split("\n");
            cleanedUpList = "";
            for (String s : lines) {
                if (s.contains("[a-zA-Z]")) {
                    cleanedUpList += s + "\n";
                } else {
                    s = s.replaceAll("[0-9]+\\s", "");
                    cleanedUpList += s + "\n";
                }
            }


            //Check if there are no buses running for the stop.
            boolean noBuses = false;

            if (cleanedUpList.isEmpty()) {
                noBuses = true;
            }

            lines = cleanedUpList.split("\n");

            if (noBuses) {
                returnText = "No buses are currently operating for this stop";
            } else {
                //Create the results string.
                Find.clearDestinations();
                for (String s : lines) {

                    if (s.matches("^[0-9]+$")) {
                        //If the line is a time estimation.
                        int fullTime = Integer.parseInt(s);
                        int minutes = fullTime / 60;
                        fullTime = fullTime % 60;
                        int seconds = fullTime;

                        String minStr = Integer.toString(minutes);
                        String secStr = Integer.toString(seconds);

                        if (minStr.length() == 1) {
                            minStr = "0" + minStr;
                        }

                        if (secStr.length() == 1) {
                            secStr = "0" + secStr;
                        }

                        returnText += "&nbsp;&nbsp;" + minStr + ":" + secStr + "<br>";
                    } else {
                        //If the line isn't a time estimation.
                        String temp;
                        if (s.contains("towards")) {
                            temp = s.replaceAll("[a-zA-Z0-9 -]+towards ", "to ");
                        } else {
                            temp = s.replaceAll("[a-zA-Z0-9 ]+- ", "");
                        }
                        Find.addDestination(temp);
                        temp = "<font color='#363636'>" + temp + "</font>";
                        returnText += temp + "<br>";
                    }
                }
            }

             return returnText;
        }

        @Override
        protected void onPostExecute(String result){
            //Check network state.
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
                //Set the visibility and the result text of the results container.
                Find.setResultButtonText(Html.fromHtml(result));

                if (Find.getResultButtonText().isEmpty()) {
                    Find.setResultButtonText(Html.fromHtml("<br><font color='#000000'>No" +
                            " information currently available for this stop.</font>"));
                }
            }

            //Hide spinner show refresh
            Find.refreshStopVisibility(true);
            Find.pbVisibility(false);

            Find.routeNumberVisibility(true);
            Find.routeDirectionVisibility(true);
            Find.routeNameVisibility(true);
            Find.resultVisibility(true);
            Find.colonVisibility(false);
            Find.checkIfExistsInFavourites();
            Find.favouriteStopVisibility(true);
            Find.smsStopVisibility(true);
            Find.refreshStopVisibility(true);

            //Enable refresh button after results are received.
            Find.canClickRefreshStop(true);



        }
    }

    public static void setNewContext(MainActivity c) {
        context = c;
    }
}
