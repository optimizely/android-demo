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
import android.os.AsyncTask;
import android.text.Html;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Fetches the TTC alerts for the user.
 *
 * @version 0.1
 * @author Grzegorz Wlodarek
 */
public class TTCAlertsResult extends Activity {

    //Instance variables
    private String alerts = "";

    /**
     * Default Constructor
     */
    public TTCAlertsResult() {

        //Get details to determine estimations
        runTime rt = new runTime();
        String url = ("https://www.ttc.ca/Service_Advisories/all_service_alerts.jsp");

        rt.execute(url);
    }

    //Thread to run in background
    private class runTime extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {

            HttpResponse response;
            HttpGet httpGet;
            HttpClient mHttpClient;
            String contents = "";

            try {

                mHttpClient = new DefaultHttpClient();


                httpGet = new HttpGet(urls[0]);

                response = mHttpClient.execute(httpGet);
                contents = EntityUtils.toString(response.getEntity(), "UTF-8");

            } catch (IOException e) {
                System.out.println("Error");
            }

            String extraction = "<div class=\"alert-content\"><p class=\"veh-replace\">([^<]*)" +
                    "</p><p class=\"alert-updated\">([^<]*)</p></div>";

            Pattern patternObject = Pattern.compile(extraction);
            Matcher matcherObject = patternObject.matcher(contents);

            while (matcherObject.find()) {
                alerts += matcherObject.group(1) + "<br><br>" + "<font color='#d70000'>" + matcherObject.group(2) + "</font>" + "<br><br><br>";
            }

            return alerts;
        }

        @Override
        protected void onPostExecute(String result){
            TTCAlerts.setAlertsText(Html.fromHtml(result));

            if (TTCAlerts.getAlertsText().isEmpty()) {
                TTCAlerts.setAlertsText("No alerts have been issued by the TTC at this time.");
            }

        }
    }
}