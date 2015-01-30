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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * Keeps track of when to alert the user when their bus is coming.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class BusAlert implements Observer {
    //Instance variables
    private FavouritesData fd = FavouritesData.createFavouritesData();
    private int minutesToAlert;
    private String routeID;
    private String stopID;
    private ArrayList<String> data;
    private static MainActivity context = null;
    private int notificationID;

    public BusAlert(int minutesToAlert, String routeID, String stopID) {

        //Convert minutes to seconds
        this.minutesToAlert = minutesToAlert * 60;
        this.routeID = routeID;
        this.stopID = stopID;
        if (fd.getNewData() != null) {
            this.data = fd.getNewData();
        } else {
            this.data = fd.getFavourites();
        }
        fd.subscribe(this);
    }

    private void sendNotification() {
        //Creates a notification in the notification center
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);

        notification.setContentTitle(this.routeID + " bus arriving");
        notification.setContentText("Your bus will arrive within " + this.minutesToAlert / 60 +
                " minutes");
        notification.setTicker(this.routeID + " bus arriving");
        Bitmap largeIcon = BitmapFactory.decodeResource(
                context.getResources(), R.drawable.ic_launcher);
        notification.setLargeIcon(largeIcon);
        notification.setSmallIcon(R.drawable.alertnotif);

        //Setup vibrate, LED and sound
        notification.setVibrate(new long[] {0, 1000, 0, 0, 0});
        notification.setLights(Color.BLUE, 3000, 3000);

        //Get default sound as notification sound for alert
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.setSound(uri);

        //Cancel notification on click after shown.
        notification.setAutoCancel(true);

        Intent resultIntent = new Intent(context, BusAlert.class);

        //This ensures that navigating backward from the Activity leads out of the app to Home page
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Adds the back stack for the Intent
        stackBuilder.addParentStack(context.getClass());

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        notification.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(
                        context.getApplicationContext().NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(notificationID, notification.build());

    }

    @Override
    public void update(Observable observable, Object o) {
        if (fd.getNewData() != null) {
            this.data = fd.getNewData();
        } else {
            this.data = fd.getFavourites();
        }

        for (String s : data) {

            if (s.split("\\|")[0].equals(this.routeID) && s.split("\\|")[1].equals(stopID)) {
                String earliestTime = s.split("\\|")[9];
                if (!earliestTime.equals("   No\nBuses")) {
                    String earliestTimeFirst = earliestTime.split("\n")[0];
                    if (earliestTimeFirst.startsWith("0")) {
                        earliestTimeFirst = earliestTimeFirst.substring(1);
                    }
                    String[] timeSplit = earliestTimeFirst.split(":");
                    int minutes = Integer.parseInt(timeSplit[0]);
                    int seconds = Integer.parseInt(timeSplit[1]);
                    int totalTime = (minutes * 60) + seconds;

                    if (totalTime <= this.minutesToAlert) {
                        sendNotification();
                        fd.unsubscribe(this);
                        Favourites.removeCurrentAlert(this);
                        break;
                    }
                }
            }
        }
    }

    public static void setNewContext(MainActivity c) {
        context = c;
    }

    public String getRouteAndStopIDs() {
        return (routeID + " " + stopID);
    }
}
