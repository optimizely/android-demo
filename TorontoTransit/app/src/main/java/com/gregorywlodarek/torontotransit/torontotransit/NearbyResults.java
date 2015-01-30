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
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Fetches time estimations for Nearby.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class NearbyResults extends Observable {

    //Instance variables
    private ArrayList<String> data;
    private static NearbyResults reference = null;
    private ArrayList<String> newData;
    private String returnText = "";
    private String url;
    private runTime rt;
    private static MainActivity context = null;
    private static boolean runStatus = true;


    /**
     * Default Constructor.
     */
    private NearbyResults() {

    }

    //Singleton Design Pattern
    public static NearbyResults createNearbyResults() {
        if (reference == null) {
            reference = new NearbyResults();
        }
        return reference;
    }

    public void run(){
        newData = new ArrayList<String>();

        //Execute the thread.
        rt = new runTime();

        runStatus = true;

        //Get the nearby stops
        GetNearbyStops n = new GetNearbyStops(Nearby.getAllStops());
        n.getStops();

    }

    public void executeFromGetStops(){
        if (rt.getStatus() != AsyncTask.Status.RUNNING) {
            ArrayList<String> taskData = new ArrayList<String>(data);
            rt.execute(taskData);
        }
    }

    public void setData(ArrayList<String> d) {
        data = d;
    }

    public void subscribe(Observer o) {
        addObserver(o);
    }

    //Thread to run in background
    private class runTime extends AsyncTask<ArrayList<String>, String, String> {

        @Override
        protected String doInBackground(ArrayList<String>... urls) {
            for (String stop : urls[0]) {

                String[] stopList = stop.split(" \\| ");

                if (stopList.length >= 5) {
                    url = ("http://webservices.nextbus.com/service/publicXMLFeed?command=predictions" +
                            "&a=ttc&stopId=" + stopList[4]);
                } else if (stopList.length <= 4) {
                    continue;
                }

                HttpResponse response;
                HttpGet httpGet;
                HttpClient mHttpClient;
                String contents;
                String cleanedUpList = "";
                String returnText = "";

                try {

                    mHttpClient = new DefaultHttpClient();


                    httpGet = new HttpGet(url);

                    response = mHttpClient.execute(httpGet);
                    contents = EntityUtils.toString(response.getEntity(), "UTF-8");

                } catch (IOException e) {
                    System.out.println("Error, no internet");
                    runStatus = false;
                    break;
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

                if (cleanedUpList.isEmpty()) {
                    newData.add(stop);
                }

                lines = cleanedUpList.split("\n");
                cleanedUpList = "";
                for (String s : lines) {
                    if (s.contains("[a-zA-Z]")) {
                        cleanedUpList += s + "\n";
                    } else if (s.contains("towards")) {
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
                        if (s.matches("^[0-9]+$") /*&& !s.contains("towards")*/) {
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
                            String temp = "";
                            if (s.contains("towards")) {
                                //temp = s.replaceAll("[a-zA-Z0-9 -]+towards ", "to ");
                                Pattern line = Pattern.compile("([A-Za-z]+) - ([0-9]+) " +
                                        "([a-zA-Z0-9-/', ]+) towards ([a-zA-Z0-9-/', ]+)");
                                Matcher m = line.matcher(s);
                                if (m.matches()) {
                                    temp = m.group(2) + " - " + m.group(1) + " | " + m.group(3) +
                                            " | " + "to " + m.group(4);
                                }
                            } else {
                                temp = s.replaceAll("[a-zA-Z0-9 ]+- ", "");
                            }
                            returnText += temp + "\n";
                        }
                    }
                    String[] returnTextList = returnText.split("\n");
                    String toAdd = "";
                    int resultsCount = 0;


                    for (int ele = 0; ele < returnTextList.length; ele++) {
                        //String str : returnTextList
                        if (returnTextList[ele].matches("[0-9][0-9]:[0-9][0-9]") && resultsCount < 3
                                && !returnTextList[ele].contains("to ")) {
                            toAdd += returnTextList[ele] + "\n";
                            resultsCount += 1;
                            if (ele == returnTextList.length - 1) {
                                String tempString = stop;
                                String[] toAddList = toAdd.split("\n");
                                if (toAddList.length > 0) {
                                    tempString += " | " + toAddList[0];
                                    String tempTimes = "";
                                    for (int i = 1; i < toAddList.length; i++) {
                                        tempTimes += toAddList[i] + "\n";
                                    }
                                    if (tempTimes.length() != 0) {
                                        tempTimes = tempTimes.substring(0, tempTimes.length() - 1);
                                    }
                                    tempString += " | " + tempTimes;
                                    newData.add(tempString);
                                    resultsCount = 0;
                                }
                            }
                        } else if (!returnTextList[ele].matches("[0-9][0-9]:[0-9][0-9]")) {

                            if (Pattern.compile("[a-zA-Z]{2,}").matcher(toAdd).find()) {
                                String tempString = stop;
                                String[] toAddList = toAdd.split("\n");
                                if (toAddList.length > 0) {
                                    tempString += " | " + toAddList[0];
                                    String tempTimes = "";
                                    for (int i = 1; i < toAddList.length; i++) {
                                        tempTimes += toAddList[i] + "\n";
                                    }
                                    if (tempTimes.length() != 0) {
                                        tempTimes = tempTimes.substring(0, tempTimes.length() - 1);
                                    }
                                    tempString += " | " + tempTimes;
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
                                tempString += " | " + toAddList[0];
                                String tempTimes = "";
                                for (int i = 1; i < toAddList.length; i++) {
                                    tempTimes += toAddList[i] + "\n";
                                }
                                if (tempTimes.length() != 0) {
                                    tempTimes = tempTimes.substring(0, tempTimes.length() - 1);
                                }
                                tempString += " | " + tempTimes;
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
        protected void onPostExecute(String result) {

            ArrayList<String> finalResult = new ArrayList<String>();

            for (String s : newData) {
                boolean exists = false;

                if (s.contains("N/A")) {
                    continue;
                }

                if (!(s.split(" \\| ").length <= 5)) {
                    if (finalResult.isEmpty()) {
                        finalResult.add(s);
                    } else {
                        for (String n : finalResult) {
                            if (s.split(" \\| ")[0].equals(n.split(" \\| ")[0]) &&
                                    s.split(" \\| ")[5].equals(n.split(" \\| ")[5])) {
                                exists = true;
                            }
                        }

                        if (!exists) {
                            finalResult.add(s);
                        }
                    }
                }
            }


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
            if (!runStatus) {
                setChanged();
                notifyObservers("InternetIssues");
            } else if (finalResult.size() == 0 ||
                    finalResult.get(0).split(" \\|").length == 5) {
                setChanged();
                notifyObservers("NoBuses");
            } else if (haveConnectedMobile || haveConnectedWifi) {
                Nearby.setNearbyStopsResult(finalResult);

                setChanged();
                notifyObservers("NearbyResults");
            }


        }
    }

    public static void setNewContext(MainActivity c) {
        context = c;
    }
}