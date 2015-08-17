package com.example.sshah.gilt_android;

import android.os.Parcel;

import android.os.Parcelable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Created by sshah on 2/2/15.
 */
public class GiltSale implements Parcelable {

    public static String TAG = "GiltSale";

    private static ArrayList<GiltSale> allSales = null;

    private String name;
    private String sale_url;
    private String store;
    private String sale_detail;
    private String image_url;
    private String saleDescription;
    private List<String> saleProducts;
    private int numDaysLeft;


    public int getNumDaysLeft() {
        return numDaysLeft;
    }

    public List<String> getSaleProducts() {
        return saleProducts;
    }

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


    public GiltSale(String saleName, String saleURL)
    {
        this.setName(saleName);
        this.setSale_url(saleURL);
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
            JSONArray image_315x295_array = images.getJSONArray("744x281");
            JSONObject image_315x295_object = image_315x295_array.getJSONObject(0);
            this.setImage_url(image_315x295_object.getString("url"));

            // Other image sizes
            // JSONObject image_161_110 = images.getJSONObject("161x110");
            // JSONObject image_161_110 = images.getJSONObject("315x295");
            // JSONObject image_161_110 = images.getJSONObject("744x281");

            ArrayList<String> productURLS = new ArrayList<String>();
            // Get products
            if (jsonSale.has("products")) {
                JSONArray productsJSON = jsonSale.getJSONArray("products");


                for (int y = 0; y < productsJSON.length(); y++) {
                    productURLS.add(productsJSON.getString(y));
                }
            }

            this.saleProducts = productURLS;

            //"2015-03-05T02:00:00Z",
            //"2015-03-02T02:00:00Z"


            // Get the endDate
            Date endDate;
            String dateString = jsonSale.getString("ends");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                endDate = format.parse(dateString);
            } catch (ParseException e) {
                //e.printStackTrace();
                GregorianCalendar gc  = new GregorianCalendar();
                gc.add(Calendar.DATE, 1);
                endDate = gc.getTime();
            }

            GregorianCalendar gc2 = new GregorianCalendar();
            Date now = gc2.getTime();

            long diffInMS = endDate.getTime() - now.getTime();

            long msInADay = 1000*60*60*24;
            int numDays = (int)(diffInMS/msInADay);
            this.numDaysLeft = numDays;


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public String getEndsInDaysString()
    {
        int numDays = getNumDaysLeft();

        if(numDays == 0) {
            return "ENDS TODAY";
        } else if (numDays == 1) {
            return "ENDS TOMORROW";
        } else {
            return "ENDS IN " + numDays + " DAYS";
        }
    }

    public static void getSales(final GetSalesResponseHandler responseHandler) {

        if(allSales != null) {
            responseHandler.onCompletion(allSales);
            return;
        }

        GiltClient.get("/sales/active.json", null, new SSJSONResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ArrayList<GiltSale> sales = parseSales(response);
                allSales = sales;
                responseHandler.onCompletion(sales);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                responseHandler.onCompletion(new ArrayList<GiltSale>());
                GiltLog.d("failed to get sales: " + response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                responseHandler.onCompletion(new ArrayList<GiltSale>());
                GiltLog.d("failed to get sales: " + throwable);
            }
        });
    }


    public void getAllProducts(final GetProductsResponseHandler responseHandler)
    {
        int numProducts = getSaleProducts().size();
        int numToDownload = Math.min(30,numProducts);

        final RequestCounter counter = new RequestCounter(numToDownload);

        final ArrayList<GiltProduct> products = new ArrayList<GiltProduct>();

        for(int x = 0; x < numToDownload; x++) {

            String productURL = getSaleProducts().get(x);

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

                //Don't add sales with 0 products
                if(gSale.getSaleProducts().size() > 0) {
                    list.add(gSale);
                }
            }
        }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public int describeContents() {
        return 93;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        /*
        private String name;
        private String sale_url;
        private String store;
        private String sale_detail;
        private String image_url;
        private String saleDescription;
        private ArrayList<String> products;
        private int numDaysLeft;
        */

        dest.writeString(name);
        dest.writeString(sale_url);
        dest.writeString(store);
        dest.writeString(sale_detail);
        dest.writeString(image_url);
        dest.writeString(saleDescription);
        dest.writeStringList(saleProducts);
        dest.writeInt(numDaysLeft);
    }

    private GiltSale(Parcel in)
    {
        this.name = in.readString();
        this.sale_url = in.readString();
        this.store = in.readString();
        this.sale_detail = in.readString();
        this.image_url = in.readString();
        this.saleDescription = in.readString();
        this.saleProducts = new ArrayList<String>();
        in.readStringList(this.saleProducts);
        this.numDaysLeft = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GiltSale createFromParcel(Parcel in) {
            return new GiltSale(in);
        }

        public GiltSale[] newArray(int size) {
            return new GiltSale[size];
        }
    };

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
