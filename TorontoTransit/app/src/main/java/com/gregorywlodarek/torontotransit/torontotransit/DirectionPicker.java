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
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * List of possible directions.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class DirectionPicker extends Activity {

    //Instance variables
    private EditText searchText;
    private ListView routeList;
    private ArrayList<String> sortedRoute;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the content view.
        setContentView(R.layout.activity_picker);

        AssetManager am = getAssets();
        BufferedReader in;

        sortedRoute = new ArrayList<String>();

        try {
            //Try to open the directions file
            in = new BufferedReader(new InputStreamReader(am.open("data/" + Find.getRouteID() +
                    "/directions.txt")));
            String line;

            //While there is a line, add it to the ArrayList.
            while((line = in.readLine()) != null) {
                sortedRoute.add(line);
            }

        } catch (IOException ex) {
            System.out.println("Cannot open directions file.");
        }

        //Give the variables views.
        searchText = (EditText) findViewById(R.id.searchText);
        routeList = (ListView) findViewById(R.id.routeList);

        //Create a new list adapter with a simple layout and sortedRoute as the data to be
        //displayed.
        listAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, sortedRoute);

        //Make the ListView show the listAdapter just created.
        routeList.setAdapter(listAdapter);

        //Searching the ListView function.
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                //Get the text in the search bar
                String searchedText = searchText.getText().toString();

                //Create a temporary list
                ArrayList<String> newList = new ArrayList<String>();

                //Check all the items in the current list and check if it contains the searched text
                //If it does, add to temporary list.
                for (String s : sortedRoute) {
                    if (s.toLowerCase().contains(searchedText.toLowerCase())) {
                        newList.add(s);
                    }
                }

                //Create a list adapter with the temporary list.
                listAdapter = new ArrayAdapter<String>(
                        DirectionPicker.this, android.R.layout.simple_list_item_1, newList);

                //Set the ListView to the new adapter.
                routeList.setAdapter(listAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Set the text of various items.
                Find.setDirectionText(adapterView.getAdapter().getItem(i).toString());
                Find.setDirectionButtonText(adapterView.getAdapter().getItem(i).toString());

                //Set the buttons clickable states.
                Find.setCanClickRoute(true);
                Find.setCanClickDirection(true);
                Find.setCanClickStop(true);
                Find.setCanClickResult(false);

                //Set the buttons texts.
                Find.setStopButtonText("Select stop");
                Find.setResultButtonText("");

                //Set the results information to invisible.
                Find.routeNumberVisibility(false);
                Find.routeDirectionVisibility(false);
                Find.routeNameVisibility(false);
                Find.resultVisibility(false);
                Find.colonVisibility(false);
                Find.favouriteStopVisibility(false);
                Find.smsStopVisibility(false);
                Find.refreshStopVisibility(false);

                //Set the direction text to chosen direction by user.
                Find.setRouteDirection(adapterView.getAdapter().getItem(i).toString());

                //Hide the soft touch keyboard before finishing this activity.
                //(Done to avoid resizing issue experienced before.)
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

                //Finish this activity.
                finish();
            }
        });
    }

}
