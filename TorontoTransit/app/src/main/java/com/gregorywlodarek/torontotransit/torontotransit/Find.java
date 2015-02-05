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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.optimizely.Optimizely;

import java.util.ArrayList;

/**
 * Handles all the features to find the users stop and determine the time for the next bus.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class Find extends Fragment
{

    //Instance variables
    private static Button route;
    private static Button direction;
    private static Button stop;
    private static TextView result;
    private static boolean canClickRoute = true;
    private static boolean canClickDirection = false;
    private static boolean canClickStop = false;
    private static boolean canClickResult = false;
    private static String routeText;
    private static String routeID;
    private static String directionText;
    private static String stopText;
    private static String stopID;
    private static String stopLat;
    private static String stopLong;
    private static String stopSMS;
    private static TextView routeNumber;
    private static TextView routeName;
    private static TextView routeDirection;
    private static TextView colon;
    private static ImageButton favouriteStop;
    private static ImageButton smsStop;
    private static ImageButton refreshStop;
    private static FavouritesData fd;
    private static ArrayList<String> destinations;
    private static ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the view with the correct layout and ViewGroup
        View find = inflater.inflate(R.layout.find_frag, container, false);

        //Give variables their correct views.
        fd = FavouritesData.createFavouritesData();
        route = (Button) find.findViewById(R.id.routeButton);
        direction = (Button) find.findViewById(R.id.directionButton);
        stop = (Button) find.findViewById(R.id.stopButton);
        result = (TextView) find.findViewById(R.id.resultButton);
        result.setMovementMethod(new ScrollingMovementMethod());
        routeNumber = (TextView) find.findViewById(R.id.routeNumber);
        routeName = (TextView) find.findViewById(R.id.item_routeName);
        routeDirection = (TextView) find.findViewById(R.id.directionText);
        colon = (TextView) find.findViewById(R.id.colon);
        favouriteStop = (ImageButton) find.findViewById(R.id.favouriteStop);
        smsStop = (ImageButton) find.findViewById(R.id.smsStop);
        refreshStop = (ImageButton) find.findViewById(R.id.refreshButton);
        destinations = new ArrayList<String>();
        pb = (ProgressBar) find.findViewById(R.id.progressBar);

        //Set the buttons states
        route.setEnabled(canClickRoute);
        result.setEnabled(canClickResult);
        stop.setEnabled(canClickStop);
        direction.setEnabled(canClickDirection);

        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Optimizely.trackEvent("selectedRoute");
                Optimizely.sendEvents();
                Intent intent = new Intent(Find.this.getActivity(), RoutePicker.class);
                startActivity(intent);
            }
        });

        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Find.this.getActivity(), DirectionPicker.class);
                startActivity(intent);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Find.this.getActivity(), StopPicker.class);
                startActivity(intent);
            }
        });

        //Refresh info in results button functionality.
        refreshStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Result();
            }
        });

        smsStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SMS sms = new SMS("898882", stopSMS, directionText.substring(0, 1),
                        routeNumber.getText().toString());
                sms.sendMessage();
            }
        });

        //Adds favourite method in Find
        favouriteStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fd.checkForStopID(getStopID() + " " + getRouteID())) {
                    //If the stop is already favourited, remove it.
                    favouriteStop.setImageResource(R.drawable.notfavorited);
                    fd.removeFavourite(getStopID() + " " + getRouteID());
                } else {
                    //If it isn't favourited, add it to favourites.
                    //If it has an sms number add the correct item below.
                    favouriteStop.setImageResource(R.drawable.favorited);
                        if (getStopSMS().length() > 1) {
                            fd.addFavourite(getStopID() + " " + getRouteID(), getRouteID() + "|"
                                    + getStopID() + "|" +
                                    getRouteNumber() + "|" + getRouteDirection() + "|" +
                                    getRouteName() + "|" + getStopText() + "|" + getStopLat() + "|"
                                    + getStopLong() + "|" + "No information" + "|" +
                                    "   No\nBuses" + "|" + getStopSMS());
                        } else {
                            fd.addFavourite(getStopID() + " " + getRouteID(), getRouteID() + "|"
                                    + getStopID() + "|" +
                                    getRouteNumber() + "|" + getRouteDirection() + "|" +
                                    getRouteName() + "|" + getStopText() + "|" + getStopLat() +
                                    "|" + getStopLong() + "|" + "No information" + "|" +
                                    "   No\nBuses");
                        }
                }
            }
        });

        return find;
    }

    //Setters & Getters below

    public static void setRouteText(String s) {
        routeText = s;
    }

    public static String getRouteText() {
        return routeText;
    }

    public static void setRouteID(String s) {
        routeID = s;
    }

    public static String getRouteID() {
        return routeID;
    }

    public static void setDirectionText(String s) {
        directionText = s;
    }

    public static String getDirectionText() {
        return directionText;
    }

    public static void setStopText(String s) {
        stopText = s;
    }

    public static String getStopText() {
        return stopText;
    }

    public static void setStopID(String s) {
        stopID = s;
    }

    public static String getStopID() {
        return stopID;
    }

    public static void setStopLat(String s) {
        stopLat = s;
    }

    public static String getStopLat() {
        return stopLat;
    }

    public static void setStopLong(String s) {
        stopLong = s;
    }

    public static String getStopLong() {
        return stopLong;
    }

    public static void setStopSMS(String s) {
        stopSMS = s;
    }

    public static String getStopSMS() {
        return stopSMS;
    }

    public static void setRouteButtonText(String s) {
        route.setText(s);
    }

    public static void setDirectionButtonText(String s) {
        direction.setText(s);
    }

    public static void setStopButtonText(String s) {
        stop.setText(s);
    }

    public static void setResultButtonText(String s) {
        result.setText(s);
    }

    public static void setResultButtonText(Spanned s) {
        result.setText(s);
    }

    public static String getResultButtonText() { return result.getText().toString(); }

    public static void setCanClickRoute(boolean b) {
        canClickRoute = b;
        route.setEnabled(b);
    }

    public static boolean getCanClickRoute() {
        return canClickRoute;
    }

    public static void setCanClickDirection(boolean b) {
        canClickDirection = b;
        direction.setEnabled(b);
    }

    public static boolean getCanClickDirection() {
        return canClickDirection;
    }

    public static void setCanClickStop(boolean b) {
        canClickStop = b;
        stop.setEnabled(b);
    }

    public static boolean getCanClickStop() {
        return canClickStop;
    }

    public static void setCanClickResult(boolean b) {
        canClickResult = b;
        result.setEnabled(b);
    }

    public static boolean getCanClickResult() {
        return canClickResult;
    }

    public static String getRouteNumber() {
        return routeNumber.getText().toString();
    }

    public static void setRouteNumber(String s) {
        routeNumber.setText(s);
    }

    public static String getRouteName() {
        return routeName.getText().toString();
    }

    public static void setRouteName(String s) {
        routeName.setText(s);
    }

    public static String getRouteDirection() {
        return routeDirection.getText().toString();
    }

    public static void setRouteDirection(String s) {
        routeDirection.setText(s);
    }

    public static void routeNumberVisibility(boolean b) {
        if (b)
            routeNumber.setVisibility(View.VISIBLE);

        if (!b)
            routeNumber.setVisibility(View.INVISIBLE);
    }

    public static void routeNameVisibility(boolean b) {
        if (b)
            routeName.setVisibility(View.VISIBLE);

        if (!b)
            routeName.setVisibility(View.INVISIBLE);
    }

    public static void routeDirectionVisibility(boolean b) {
        if (b)
            routeDirection.setVisibility(View.VISIBLE);

        if (!b)
            routeDirection.setVisibility(View.INVISIBLE);
    }

    public static void resultVisibility(boolean b) {
        if (b)
            result.setVisibility(View.VISIBLE);

        if (!b)
            result.setVisibility(View.INVISIBLE);
    }

    public static void colonVisibility(boolean b) {
        if (b)
            colon.setVisibility(View.VISIBLE);

        if (!b)
            colon.setVisibility(View.INVISIBLE);
    }

    public static void favouriteStopVisibility(boolean b) {
        if (b)
            favouriteStop.setVisibility(View.VISIBLE);

        if (!b)
            favouriteStop.setVisibility(View.INVISIBLE);
    }

    public static void smsStopVisibility(boolean b) {
        if (b)
            smsStop.setVisibility(View.VISIBLE);

        if (!b)
            smsStop.setVisibility(View.INVISIBLE);
    }

    public static void refreshStopVisibility(boolean b) {
        if (b)
            refreshStop.setVisibility(View.VISIBLE);

        if (!b)
            refreshStop.setVisibility(View.INVISIBLE);
    }

    public static void checkIfExistsInFavourites() {
        if (fd.checkForStopID(getStopID() + " " + getRouteID())) {
            favouriteStop.setImageResource(R.drawable.favorited);
        } else {
            favouriteStop.setImageResource(R.drawable.notfavorited);
        }
    }

    public static void addDestination(String s) {
        destinations.add(s);
    }

    public static void clearDestinations() {
        destinations.clear();
    }

    public static void canClickRefreshStop(boolean b) {
        refreshStop.setEnabled(b);
    }

    public static void pbVisibility(boolean b) {
        if (b)
            pb.setVisibility(View.VISIBLE);

        if (!b)
            pb.setVisibility(View.GONE);
    }

}