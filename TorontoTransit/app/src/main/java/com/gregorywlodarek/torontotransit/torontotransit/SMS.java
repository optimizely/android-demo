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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.telephony.SmsManager;

/**
 * Sends an SMS text to TTC for offline use estimations.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class SMS {
    private String TTCNumber;
    private String stopID;
    private String directionLetter;
    private String routeID;
    private SmsManager smsManager;
    private static MainActivity context = null;

    public SMS(String number, String stop, String directionChar, String route) {
        this.TTCNumber = number;
        this.stopID = stop;
        this.directionLetter = directionChar;
        this.routeID = route;
        this.smsManager = SmsManager.getDefault();
    }

    public static void setNewContext(MainActivity c) {
        context = c;
    }

    public void sendMessage() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        //Set title
        alertDialogBuilder.setTitle("Sending SMS to TTC");

        //Set dialog message
        alertDialogBuilder.setMessage("Send a text message to TTC for bus predictions?\n\n" +
                "Standard rates apply, check with your service provider.");

        //Set dialog to be not cancelable
        alertDialogBuilder.setCancelable(false);

        //Set the "YES" button
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendAfterConfirmation();
                FavouritesResult.allowFavouritesFetching();
                MainActivity.setRunThread(true);
            }
        });

        //Set the "NO" button
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                FavouritesResult.allowFavouritesFetching();
                MainActivity.setRunThread(true);
            }
        });

        //Build the alert dialog from alert dialog builder.
        AlertDialog alertDialog = alertDialogBuilder.create();

        //Show the alert dialog.
        FavouritesResult.cancelFavouritesFetching();
        MainActivity.setRunThread(false);
        alertDialog.show();
    }

    private void sendAfterConfirmation() {
        String message = this.stopID + " " + this.routeID + " " + this.directionLetter;
        try {
            smsManager.sendTextMessage(this.TTCNumber, null, message, null, null);
        } catch (Exception e) {
            System.out.println("SMS failed.");
        }
    }
}
