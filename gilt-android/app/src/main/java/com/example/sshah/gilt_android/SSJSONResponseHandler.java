package com.example.sshah.gilt_android;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by sshah on 2/2/15.
 */
public abstract class SSJSONResponseHandler extends JsonHttpResponseHandler {
    @Override
    public abstract void onSuccess(int statusCode, Header[] headers, JSONObject response);

    @Override
    public abstract void onSuccess(int statusCode, Header[] headers, JSONArray response);

    @Override
    public abstract void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse);
}
