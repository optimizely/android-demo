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
import android.graphics.BitmapFactory;
import android.os.Bundle;

/**
 * Contains the information on how to use the application.
 * This page is no longer accessible since version 1.1
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class Instructions extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructions);

        ZoomableImageView touch = (ZoomableImageView)findViewById(R.id.instructionsImage);
        touch.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.guide));

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
