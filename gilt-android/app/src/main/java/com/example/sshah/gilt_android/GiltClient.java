package com.example.sshah.gilt_android;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * Created by sshah on 2/2/15.
 */
public class GiltClient {

    //https://api.gilt.com/v1/sales/active.json?apikey=400f39436e49823611730ac9f0f2c258e5f60277ff4a92e102f81a7dc6cabd93

    private static final String BASE_URL = "https://api.gilt.com/v1/";

    private static AsyncHttpClient initializeClient()
    {
        AsyncHttpClient asyncClient = new AsyncHttpClient();
        asyncClient.addHeader("Content-Type", "application/json");
        asyncClient.addHeader("Accept", "application/json");
        asyncClient.setTimeout(10000);
        return asyncClient;
    }

    private static String getAbsoluteUrl(String relativeURL)
    {
        URLBuilder urlBuilder = new URLBuilder(BASE_URL);
        urlBuilder.appendPathComponent(relativeURL);
        return urlBuilder.build();
    }

    private static AsyncHttpClient client = initializeClient();

    public static void get(String relativeUrl, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        String absoluteURL = getAbsoluteUrl(relativeUrl);
        getAbsolute(absoluteURL, params, responseHandler);
    }

    public static void getAbsolute(String absoluteURL, RequestParams params, ResponseHandlerInterface responseHandler)
    {
        if(params == null) {
            params = new RequestParams();
        }
        params.add("apikey","400f39436e49823611730ac9f0f2c258e5f60277ff4a92e102f81a7dc6cabd93");

        String finalURL = AsyncHttpClient.getUrlWithQueryString(false,absoluteURL,params);

        client.setURLEncodingEnabled(false);
        client.get(finalURL,responseHandler);

    }

}
