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
import android.os.Bundle;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
 * Page that has all of the latest TTC alerts in the MISC tab.
 *
 * @version 1.0
 * @author Grzegorz Wlodarek
 */
public class TTCAlerts extends Activity {
    private static TextView alertsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttcalerts);

        alertsText = (TextView) findViewById(R.id.alertsText);
        alertsText.setMovementMethod(new ScrollingMovementMethod());
        new TTCAlertsResult();
    }

    public static void setAlertsText(String s) {
        alertsText.setText(s);
    }

    public static void setAlertsText(Spanned s) {
        alertsText.setText(s);
    }

    public static String getAlertsText() {
        return alertsText.getText().toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
