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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Info tab.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class Info extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View info = inflater.inflate(R.layout.info_frag, container, false);

        //TextView infoText = (TextView) info.findViewById(R.id.te);

        //Button teamButton = (Button) info.findViewById(R.id.teamButton);
        Button mapButton = (Button) info.findViewById(R.id.subwayButton);
        //Button instructionsButton = (Button) info.findViewById(R.id.instructionsButton);
        Button ttcAlertsButton = (Button) info.findViewById(R.id.ttcAlertsButton);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Info.this.getActivity(), SubwayMap.class);
                startActivity(i);
            }
        });

        /*
        teamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Info.this.getActivity(), TheTeam.class);
                startActivity(i);
            }
        });


        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Info.this.getActivity(), Instructions.class);
                startActivity(i);
            }
        });
        */

        ttcAlertsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Info.this.getActivity(), TTCAlerts.class);
                startActivity(i);
            }
        });


        return info;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }
}