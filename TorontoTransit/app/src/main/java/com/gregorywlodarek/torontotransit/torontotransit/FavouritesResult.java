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
import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Fetches time estimations for Favourites.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class FavouritesResult {

    //Instance variables
    private FavouritesData fd = FavouritesData.createFavouritesData();
    private ArrayList<String> newData;
    private static MainActivity context = null;
    private static boolean cancelled = false;


    /**
     * Default Constructor.
     */
    public FavouritesResult() {
        ArrayList<String> data = fd.getFavourites();
        newData = new ArrayList<String>();

        //Execute the thread.
        runTime rt = new runTime();
        MainActivity.setRefreshFavouritesTab(false);
        rt.execute(data);
    }

    //Thread to run in background
    private class runTime extends AsyncTask<ArrayList<String>, String, String> {

        @Override
        protected String doInBackground(ArrayList<String>... urls) {
            for (String stop : urls[0]) {
                String url;
                String[] stopList = stop.split("\\|");
                url = ("http://webservices.nextbus.com/service/publicXMLFeed?command=" +
                        "predictions&a=ttc&r=" + stopList[0] + "&s=" + stopList[1]);

                HttpResponse response;
                HttpGet httpGet;
                HttpClient mHttpClient;
                String contents = "";
                String cleanedUpList = "";
                String returnText = "";

                try {

                    mHttpClient = new DefaultHttpClient();


                    httpGet = new HttpGet(url);

                    response = mHttpClient.execute(httpGet);
                    contents = EntityUtils.toString(response.getEntity(), "UTF-8");

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }


                //Clean up the XML file to a simple viewable text string.
                String[] lines = contents.split("\n");

                for (String s : lines) {
                    s = s.trim();
                    if ((!s.startsWith("<predictions")) && (s.startsWith("<direction")
                            || s.startsWith("<prediction"))) {
                        cleanedUpList += s + "\n";
                    }

                    /* Previous conditions
                    if (s.startsWith("<predictions")) {
                        continue;
                    } else if (s.startsWith("<direction") || s.startsWith("<prediction")) {
                        cleanedUpList += s + "\n";
                    }
                    */

                }

                cleanedUpList = cleanedUpList.trim().replaceAll("<direction title=\"", "");
                cleanedUpList = cleanedUpList.trim().replaceAll("/>", "");
                cleanedUpList = cleanedUpList.trim().replaceAll(">", "");
                cleanedUpList = cleanedUpList.trim().replaceAll(" minutes.*", "");
                cleanedUpList = cleanedUpList.trim().replaceAll("<prediction epochTime=\"", "");
                cleanedUpList = cleanedUpList.trim().replaceAll("\"", "");
                cleanedUpList = cleanedUpList.trim().replaceAll("seconds=", "");

                if (cleanedUpList.isEmpty()) {
                    newData.add(stop);
                }

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
                newData.add(stop);
            } else {
                //Create the results string.
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

                        returnText += minStr + ":" + secStr + "\n";
                    } else {
                        //If the line isn't a time estimation.
                        String temp;
                        if (s.contains("towards")) {
                            temp = s.replaceAll("[a-zA-Z0-9 -]+towards ", "to ");
                        } else {
                            temp = s.replaceAll("[a-zA-Z0-9 ]+- ", "");
                        }
                        returnText += temp + "\n";
                    }
                }
                String[] returnTextList = returnText.split("\n");
                String toAdd = "";
                int resultsCount = 0;

                for (int ele = 0; ele<returnTextList.length; ele++) {
                    //String str : returnTextList
                    if (returnTextList[ele].matches("[0-9][0-9]:[0-9][0-9]") && resultsCount < 3) {
                        toAdd += returnTextList[ele] + "\n";
                        resultsCount += 1;
                        if (ele == returnTextList.length - 1) {
                            String tempString = stop;
                            String[] toAddList = toAdd.split("\n");
                            if (toAddList.length > 0) {
                                tempString = tempString.replace("No information", toAddList[0]);
                                String tempTimes = "";
                                for (int i = 1; i < toAddList.length; i++) {
                                    tempTimes += toAddList[i] + "\n";
                                }
                                tempTimes = tempTimes.substring(0, tempTimes.length() - 1);
                                tempString = tempString.replace("   No\nBuses", tempTimes);
                                newData.add(tempString);
                                resultsCount = 0;
                            }
                        }
                    } else if (!returnTextList[ele].matches("[0-9][0-9]:[0-9][0-9]")) {

                        if (Pattern.compile("[a-zA-Z]{2,}").matcher(toAdd).find()) {
                            String tempString = stop;
                            String[] toAddList = toAdd.split("\n");
                            if (toAddList.length > 0) {
                                tempString = tempString.replace("No information", toAddList[0]);
                                String tempTimes = "";
                                for (int i = 1; i<toAddList.length; i++) {
                                    tempTimes += toAddList[i] + "\n";
                                }
                                tempTimes = tempTimes.substring(0, tempTimes.length()-1);
                                tempString = tempString.replace("   No\nBuses", tempTimes);
                                newData.add(tempString);
                                resultsCount = 0;
                            } else {
                                newData.add(stop);
                                resultsCount = 0;
                            }
                            toAdd = returnTextList[ele] + "\n";
                        } else {
                            toAdd = returnTextList[ele] + "\n";
                        }

                    } else if (ele == returnTextList.length - 1) {
                        String tempString = stop;
                        String[] toAddList = toAdd.split("\n");
                        if (toAddList.length > 0) {
                            tempString = tempString.replace("No information", toAddList[0]);
                            String tempTimes = "";
                            for (int i = 1; i < toAddList.length; i++) {
                                tempTimes += toAddList[i] + "\n";
                            }
                            tempTimes = tempTimes.substring(0, tempTimes.length() - 1);
                            tempString = tempString.replace("   No\nBuses", tempTimes);
                            newData.add(tempString);
                            resultsCount = 0;
                        }
                    }

                }
            }
        }

            return "";
        }

        @Override
        protected void onPostExecute(String result){
            MainActivity.setRefreshFavouritesTab(true);

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


            if (!cancelled && (haveConnectedMobile || haveConnectedWifi)) {
                fd.setNewData(newData);
            }
        }

    }

    public static void cancelFavouritesFetching() {
        cancelled = true;
    }

    public static void allowFavouritesFetching() {
        cancelled = false;
    }

    public static void setNewContext(MainActivity c) {
        context = c;
    }
}
