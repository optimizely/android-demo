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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.optimizely.CodeBlocks.CodeBlock;
import com.optimizely.CodeBlocks.DefaultCodeBlock;
import com.optimizely.CodeBlocks.OptimizelyCodeBlock;
import com.optimizely.Optimizely;
import com.optimizely.Variable.LiveVariable;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Holds all of the users favourite bus stops.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class Favourites extends Fragment implements Observer {

    //Instance variables
    private FavouritesData fd;
    private ArrayList<String> data = new ArrayList<String>();
    private ListView favouritesList;
    private TextView text;
    private LayoutInflater i;
    private static ArrayList<RelativeLayout> hiddenOptionsArray = new ArrayList<RelativeLayout>();
    private static ArrayList<String> newData;
    private static Activity acc;
    private static ArrayList<BusAlert> currentAlerts = new ArrayList<BusAlert>();
    private static MainActivity context = null;

    private static LiveVariable<Integer> intVariable = Optimizely.integerVariable("basicIntVariable",0);

    private OptimizelyCodeBlock alertDialogCodeBlock = Optimizely.codeBlockWithBranchNames("AlertBranch", "Branch1","Branch2","Branch3");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the view with the correct layout and ViewGroup
        View favourites = inflater.inflate(R.layout.favourites_frag, container, false);
        //Set the text of the textView
        text = ((TextView) favourites.findViewById(R.id.nearbyText));
        text.setText("No favourites found!\nYou can add favourites in the Find tab.");

        //Create a reference to FavouritesData
        fd = FavouritesData.createFavouritesData();

        //Give the variable the ListView reference
        favouritesList = (ListView) favourites.findViewById(R.id.favouritesListView);
        i = inflater;

        //Need to populate the list on boot otherwise it is empty until a new favourite is added
        //or removed.
        //Get the data of favourites.
        data = fd.getFavourites();

        //Generate the favourites list.
        populateFavouritesList();


        //If empty, set the text to no favs
        if (data.isEmpty()) {
            favouritesList.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
        } else {
            favouritesList.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
        }


        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("Experiment");

        alertDialogCodeBlock.execute(new DefaultCodeBlock() {
            @Override
            public void execute() {
                Log.d("OptimizelySuneet", "Default code block. Live var: " + intVariable.get());
            }
        }, new CodeBlock("Branch1") {
            @Override
            public void execute() {
                Log.d("OptimizelySuneet","Code block branch 1. Live var: " + intVariable.get());
                //builder.create().show();
            }
        }, new CodeBlock("Branch2") {
            @Override
            public void execute() {
                Log.d("OptimizelySuneet","Code block branch 2. Live var: " + intVariable.get());
                //builder.create().show();
            }
        }, new CodeBlock("Branch3") {
            @Override
            public void execute() {
                Log.d("OptimizelySuneet","Code block branch 3. Live var: " + intVariable.get());
                //builder.create().show();
            }
        });






        //Return the view.
        return favourites;
    }

    public static void setNewContext(MainActivity c) {
        context = c;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        acc = activity;
    }


    public void update(Observable o, Object args) {
        //Check internet
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }


        //Get the latest data and reconstruct the favourites list.
        data = fd.getFavourites();


        if (fd.getNewData() != null && !fd.getNewData().isEmpty() && !(args instanceof String)) {
            newData = fd.getNewData();
            data = newData;
        }

        if (data.isEmpty()) {
            favouritesList.setVisibility(View.GONE);
            text.setVisibility(View.VISIBLE);
        } else {
            favouritesList.setVisibility(View.VISIBLE);
            text.setVisibility(View.GONE);
        }


        if (haveConnectedWifi || haveConnectedMobile) {
            //Allow fetching
            FavouritesResult.allowFavouritesFetching();
            populateFavouritesList();

        } else {
            //Add new favourite but don't fetch results.
            FavouritesResult.cancelFavouritesFetching();
            populateFavouritesList();
        }
    }

    private void populateFavouritesList() {
        //Construct the favourites list.

        int currentLocationOnList = favouritesList.getFirstVisiblePosition();

        //Create an adapter for the list
        ArrayAdapter<String> adapter = new FavouritesListAdapter();

        //Set the ListView with the adapter.
        favouritesList.setAdapter(adapter);
        favouritesList.setSelection(currentLocationOnList);
    }

    public static ArrayList<RelativeLayout> getHiddenOptionsArray() {
        return new ArrayList<RelativeLayout>(hiddenOptionsArray);
    }

    public static void removeCurrentAlert(BusAlert ba) {
        currentAlerts.remove(ba);
    }

    public static ArrayList<BusAlert> getCurrentAlerts() {
        return new ArrayList<BusAlert>(currentAlerts);
    }

    private class FavouritesListAdapter extends ArrayAdapter<String> {
        View itemView;


        public FavouritesListAdapter() {
            //Default constructor
            //Create an array adapter with the current context, our custom layout, and the data to
            //create a list view out of.
            super(acc, R.layout.favorites, data);

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //Make sure we have a view to work with (may have been given null)
            itemView = convertView;
            if (itemView == null) {
                itemView = i.inflate(R.layout.favorites, parent, false);
            }

            //find item to work with
            String currentFavourite = data.get(position);
            final String[] dataList = currentFavourite.split("\\|");

            //fill the view
            TextView busNumber = (TextView) itemView.findViewById(R.id.item_busNumber);
            busNumber.setText(dataList[2]);

            TextView busDirection = (TextView) itemView.findViewById(R.id.item_busDirection);
            busDirection.setText(dataList[3]);

            TextView routeName = (TextView) itemView.findViewById(R.id.item_routeName);
            routeName.setText(dataList[4]);

            TextView stopName = (TextView) itemView.findViewById(R.id.item_stopName);
            stopName.setText(dataList[5]);

            TextView busDestination = (TextView) itemView.findViewById(R.id.item_busDestination);
            //call results...
            busDestination.setText(dataList[8]);

            TextView timesText = (TextView) itemView.findViewById(R.id.item_timesText);
            //call results...
            timesText.setText(dataList[9]);

            //Find the views hidden options.
            final RelativeLayout hiddenOptions = (RelativeLayout)
                    itemView.findViewById(R.id.hidden_options);

            //Add it to the global arraylist of hidden option views.
            hiddenOptionsArray.add(hiddenOptions);

            RelativeLayout listContainer =
                    (RelativeLayout) itemView.findViewById(R.id.list_container);

            //Hidden options buttons
            ImageButton smsStop = (ImageButton) itemView.findViewById(R.id.smsButton);
            ImageButton favouriteButton = (ImageButton) itemView.findViewById(R.id.favouriteButton);
            ImageButton mapsButton = (ImageButton) itemView.findViewById(R.id.mapsButton);
            final ImageButton alertButton = (ImageButton) itemView.findViewById(R.id.alarmButton);

            //Later add sms info here.
            if (dataList.length < 11) {
                smsStop.setEnabled(false);
            }

                        //Remove the favourites via hidden options.
            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fd.removeFavourite(dataList[1] + " " + dataList[0]);
                }
            });

            //Hidden options text
            TextView hiddenRouteNumber = (TextView) itemView.findViewById(
                    R.id.hidden_options_route);
            TextView hiddenRouteDirection = (TextView) itemView.findViewById(
                    R.id.hidden_options_direction);



            String hiddenRouteNumberString = dataList[2];
            String hiddenRouteDirectionString = dataList[3];

            hiddenRouteNumber.setText(hiddenRouteNumberString);
            hiddenRouteDirection.setText(hiddenRouteDirectionString);

            //SMS Button to send text
            smsStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dataList.length == 11) {
                        SMS sms = new SMS("898882", dataList[10], dataList[3].substring(0, 1),
                                dataList[2]);
                        sms.sendMessage();
                        for (RelativeLayout r : hiddenOptionsArray) {
                            if (r.getVisibility() == View.VISIBLE) {
                                r.setVisibility(View.GONE);
                            }
                        }
                        //msg sent toast
                    } else {
                        System.out.println("No SMS Possible");
                    }

                }
            });

            mapsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double stopLatitude = Double.parseDouble(dataList[6]);
                    Double stopLongitude = Double.parseDouble(dataList[7]);
                    String labelLocation = dataList[5];

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + stopLatitude
                            + ">,<" + stopLongitude + ">?q=<" + stopLatitude + ">,<" +
                            stopLongitude + ">(" + labelLocation + ")"));
                    startActivity(intent);
                }
            });

            alertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (RelativeLayout r : hiddenOptionsArray) {
                        if (r.getVisibility() == View.VISIBLE) {
                            r.setVisibility(View.GONE);
                        }
                    }

                    if (checkIfExistsInCurrentAlerts()) {
                        alertButton.setBackgroundResource(R.drawable.ic_alert_delete);
                    } else {
                        alertButton.setBackgroundResource(R.drawable.alert);
                    }

                    //Cancel alert dialog builder
                    AlertDialog.Builder alertDialogBuilderDelete = new AlertDialog.Builder(context);

                    //Set title
                    alertDialogBuilderDelete.setTitle("Delete Bus Alert");

                    //Set dialog message
                    alertDialogBuilderDelete.setMessage("Cancel the current alert for this bus?");

                    //Set dialog to be not cancelable
                    alertDialogBuilderDelete.setCancelable(false);

                    //Set the "YES" button
                    alertDialogBuilderDelete.setPositiveButton(
                            "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (BusAlert ba : currentAlerts) {
                                if (ba.getRouteAndStopIDs().equals(dataList[0] + " " + dataList[1]))
                                {
                                    alertButton.setBackgroundResource(R.drawable.alert);
                                    currentAlerts.remove(ba);
                                }
                            }
                            FavouritesResult.allowFavouritesFetching();
                            MainActivity.setRunThread(true);
                        }
                    });

                    //Set the "NO" button
                    alertDialogBuilderDelete.setNegativeButton(
                            "No", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            FavouritesResult.allowFavouritesFetching();
                            MainActivity.setRunThread(true);
                        }
                    });

                    //Build the alert dialog from alert dialog builder.
                    AlertDialog alertDialogDelete = alertDialogBuilderDelete.create();


                    //Create alert dialog box
                    AlertDialog.Builder alertDialogBuilderAdd = new AlertDialog.Builder(context);

                    //Set title
                    alertDialogBuilderAdd.setTitle("Add Bus Alert");

                    //Set dialog message
                    alertDialogBuilderAdd.setMessage("Choose the number of minutes before the bus" +
                            " arrives in order to inform you.");

                    //Set dialog to be not cancelable
                    alertDialogBuilderAdd.setCancelable(false);

                    //Set the view
                    final EditText input = new EditText(context);
                    input.setMaxLines(1);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                    input.setTextSize(50);
                    input.setGravity(Gravity.CENTER);
                    input.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

                    alertDialogBuilderAdd.setView(input);

                    //Set the "YES" button
                    alertDialogBuilderAdd.setPositiveButton(
                            "Start", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (input.getText().toString().length() == 0) {
                                        //Hide the soft touch keyboard before finishing this activity.
                                        //(Done to avoid resizing issue experienced before.)
                                        InputMethodManager imm = (InputMethodManager) context.
                                                getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                                        //display in long period of time
                                        Toast.makeText(context.getApplicationContext(),
                                                "Invalid time input. Cancelling action.",
                                                Toast.LENGTH_LONG).show();

                                        FavouritesResult.allowFavouritesFetching();
                                        MainActivity.setRunThread(true);
                                    } else {
                                        //Hide the soft touch keyboard before finishing this activity.
                                        //(Done to avoid resizing issue experienced before.)
                                        InputMethodManager imm = (InputMethodManager) context.
                                                getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                                        alertButton.setBackgroundResource(
                                                R.drawable.ic_alert_delete);

                                        currentAlerts.add(new BusAlert(Integer.parseInt(
                                                input.getText().toString()),
                                                dataList[0], dataList[1]));

                                        FavouritesResult.allowFavouritesFetching();
                                        MainActivity.setRunThread(true);
                                    }
                                }
                            });

                    //Set the "NO" button
                    alertDialogBuilderAdd.setNegativeButton(
                            "Cancel", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //Hide the soft touch keyboard before finishing this activity.
                            //(Done to avoid resizing issue experienced before.)
                            InputMethodManager imm = (InputMethodManager)context.
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                            dialogInterface.cancel();
                            FavouritesResult.allowFavouritesFetching();
                            MainActivity.setRunThread(true);
                        }
                    });

                    //Build the alert dialog from alert dialog builder.
                    AlertDialog alertDialogAdd = alertDialogBuilderAdd.create();
                    alertDialogAdd.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


                    final Button alertDialogAddButton = alertDialogAdd.getButton(
                            AlertDialog.BUTTON_POSITIVE);


                    if (currentAlerts.isEmpty() || !checkIfExistsInCurrentAlerts()) {
                        //Show the alert dialog to add.
                        FavouritesResult.cancelFavouritesFetching();
                        MainActivity.setRunThread(false);
                        alertDialogAdd.show();

                        //If starts with 0... make text empty.
                        input.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                                if (input.length() == 1 && input.getText().toString().startsWith("0")) {
                                    input.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {

                            }
                        });

                    } else {
                        //Show the alert dialog to delete.
                        FavouritesResult.cancelFavouritesFetching();
                        MainActivity.setRunThread(false);
                        alertDialogDelete.show();
                    }
                }

                public boolean checkIfExistsInCurrentAlerts() {
                    for (BusAlert ba : currentAlerts) {
                        if (ba.getRouteAndStopIDs().equals(dataList[0] + " " + dataList[1])) {
                            return true;
                        }
                    }
                    return false;
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                //@Override
                //public boolean onLongClick(View view) {

                //}

                @Override
                public void onClick(View view) {
                    //Check which views hidden options are visible and hide it
                    for (RelativeLayout r : hiddenOptionsArray) {
                        if (r.getVisibility() == View.VISIBLE) {
                            r.setVisibility(View.GONE);
                            MainActivity.setRunThread(true);
                            FavouritesResult.allowFavouritesFetching();
                        }
                    }

                    //The view that was clicked, set the visibility to visible.
                    if (hiddenOptions.getVisibility() == View.GONE) {
                        hiddenOptions.setVisibility(View.VISIBLE);
                        MainActivity.setRunThread(false);
                        FavouritesResult.cancelFavouritesFetching();
                    }

                    //return true;
                }
            });

            //Return the view.
            return itemView;


        }

    }

}