package com.example.sshah.gilt_android;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sshah on 3/3/15.
 */
public class GiltProduct implements Parcelable {

    public static String TAG = "GiltProduct";

    private String name;
    private String brand;
    private String id;
    private String url;
    private String imageURL;
    private String description;
    private String maxSalesRetailPrice;
    private String salePrice;

    public String getMaxSalesRetailPrice() {
        return maxSalesRetailPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public GiltProduct(JSONObject jsonProduct)
    {
        try {
            this.name = jsonProduct.getString("name");
            this.brand = jsonProduct.getString("brand");
            this.id = jsonProduct.getString("id");

            // Get the description
            JSONObject content = jsonProduct.getJSONObject("content");
            this.description = content.getString("description");

            // This is so that the new lines properly show in Android TextViews
            //this.description = this.description.replace("\\\n", System.getProperty("line.separator"));

            this.url = jsonProduct.getString("product");

            // Get an sale image URL
            JSONObject images = jsonProduct.getJSONObject("image_urls");
            JSONArray image_300x400_array = images.getJSONArray("300x400");
            JSONObject image_300x400_object = image_300x400_array.getJSONObject(0);
            this.imageURL = image_300x400_object.getString("url");

            //Get prices
            JSONArray skusJSON = jsonProduct.getJSONArray("skus");
            JSONObject firstSKU = skusJSON.getJSONObject(0);
            this.salePrice = firstSKU.getString("sale_price");
            this.maxSalesRetailPrice = firstSKU.getString("msrp_price");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int describeContents() {
        return 87;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        /*
        private String name;
        private String brand;
        private String id;
        private String url;
        private String imageURL;
        private String description;
        private String maxSalesRetailPrice;
        private String salePrice;
        */

        dest.writeString(name);
        dest.writeString(brand);
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(imageURL);
        dest.writeString(description);
        dest.writeString(maxSalesRetailPrice);
        dest.writeString(salePrice);
    }

    private GiltProduct(Parcel in)
    {
        this.name = in.readString();
        this.brand = in.readString();
        this.id = in.readString();
        this.url = in.readString();
        this.imageURL = in.readString();
        this.description = in.readString();
        this.maxSalesRetailPrice = in.readString();
        this.salePrice = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public GiltProduct createFromParcel(Parcel in) {
            return new GiltProduct(in);
        }

        public GiltProduct[] newArray(int size) {
            return new GiltProduct[size];
        }
    };


}
