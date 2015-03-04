package com.example.sshah.gilt_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sshah on 3/3/15.
 */
public class GiltProduct {

    private String name;
    private String brand;
    private String id;
    private String url;
    private String imageURL;
    private String description;


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

            JSONObject content = jsonProduct.getJSONObject("content");
            this.description = content.getString("description");
            this.url = jsonProduct.getString("product");

            // Get an sale image URL
            JSONObject images = jsonProduct.getJSONObject("image_urls");
            JSONArray image_300x400_array = images.getJSONArray("300x400");
            JSONObject image_300x400_object = image_300x400_array.getJSONObject(0);
            this.imageURL = image_300x400_object.getString("url");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
