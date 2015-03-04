package com.example.sshah.gilt_android;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

/**
 * Created by sshah on 2/2/15.
 */
public class GiltSale {

    private String name;
    private String sale_url;
    private String store;
    private String sale_detail;
    private String image_url;
    private String saleDescription;

    public ArrayList<String> getProducts() {
        return products;
    }

    private ArrayList<String> products;


    public String getSaleDescription() {
        return saleDescription;
    }

    public void setSaleDescription(String saleDescription) {
        this.saleDescription = saleDescription;
    }


    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getSale_detail() {
        return sale_detail;
    }

    public void setSale_detail(String sale_detail) {
        this.sale_detail = sale_detail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSale_url() {
        return sale_url;
    }

    public void setSale_url(String sale_url) {
        this.sale_url = sale_url;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }


    public GiltSale(JSONObject jsonSale) {
        try {
            this.setName(jsonSale.getString("name"));

            this.setSale_detail(jsonSale.getString("sale"));
            this.setSale_url(jsonSale.getString("sale_url"));
            this.setSaleDescription(jsonSale.getString("description"));
            this.setStore(jsonSale.getString("store"));

            // Get an sale image URL
            JSONObject images = jsonSale.getJSONObject("image_urls");
            JSONArray image_315x295_array = images.getJSONArray("315x295");
            JSONObject image_315x295_object = image_315x295_array.getJSONObject(0);
            this.setImage_url(image_315x295_object.getString("url"));


            // Get products
            if (jsonSale.has("products")) {
                JSONArray productsJSON = jsonSale.getJSONArray("products");
                ArrayList<String> productURLS = new ArrayList<>(productsJSON.length());

                for (int y = 0; y < productsJSON.length(); y++) {
                    productURLS.add(productsJSON.getString(y));
                }

                this.products = productURLS;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Other image sizes
        // JSONObject image_161_110 = images.getJSONObject("161x110");
        // JSONObject image_161_110 = images.getJSONObject("315x295");
        // JSONObject image_161_110 = images.getJSONObject("744x281");
    }

    public static void getSales(final GetSalesResponseHandler responseHandler) {
        GiltClient.get("/sales/active.json", null, new SSJSONResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                GiltLog.d("OnSuccess");
                ArrayList<GiltSale> sales = parseSales(response);
                responseHandler.onCompletion(sales);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                responseHandler.onCompletion(new ArrayList<GiltSale>());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                responseHandler.onCompletion(new ArrayList<GiltSale>());
            }
        });
    }


    public void getAllProducts(final GetProductsResponseHandler responseHandler)
    {
        GiltLog.d("Getting products for sale: " + this.name);

        int numProducts = getProducts().size();
        int numToDownload = Math.min(30,numProducts);

        final RequestCounter counter = new RequestCounter(numToDownload);

        final ArrayList<GiltProduct> products = new ArrayList<>();

        for(int x = 0; x < numToDownload; x++) {

            String productURL = getProducts().get(x);

            getProductInfo(productURL, new GetProductResponseHandler() {
                @Override
                public void onSuccess(GiltProduct product) {
                    products.add(product);

                    if(counter.requestCompleted()) {
                        // DONE with all requests
                        responseHandler.onCompletion(products);
                    }
                }

                @Override
                public void onFailure() {
                    if(counter.requestCompleted()) {
                        // DONE with all requests
                        responseHandler.onCompletion(products);
                    }
                }
            });

        }

    }

    private static void getProductInfo(String productURL, final GetProductResponseHandler handler)
    {
        GiltClient.getAbsolute(productURL, null, new SSJSONResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                GiltProduct product = new GiltProduct(response);
                handler.onSuccess(product);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                handler.onFailure();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                handler.onFailure();
            }
        });
    }

    private static ArrayList<GiltSale> parseSales(JSONObject response)
    {
        ArrayList<GiltSale> list = new ArrayList<GiltSale>();

        try {
            JSONArray salesJSON = response.getJSONArray("sales");

            for (int x = 0; x < salesJSON.length(); x++) {
                JSONObject singleSaleJSON = salesJSON.getJSONObject(x);
                GiltSale gSale = new GiltSale(singleSaleJSON);
                list.add(gSale);
            }
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private class RequestCounter
    {
        private int numRequests;
        private int completedRequests;

        public RequestCounter(int requests)
        {
            this.numRequests = requests;
            this.completedRequests = 0;
        }

        public boolean requestCompleted()
        {
            this.completedRequests++;
            if(completedRequests == numRequests) {
                return true;
            } else {
                return false;
            }
        }
    }

    public interface GetProductResponseHandler {
        void onSuccess(GiltProduct product);
        void onFailure();
    }

    public interface GetProductsResponseHandler {
        void onCompletion(ArrayList<GiltProduct> products);
    }

    public interface GetSalesResponseHandler {
        void onCompletion(ArrayList<GiltSale> sales);
    }





}
