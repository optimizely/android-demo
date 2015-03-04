package com.example.sshah.gilt_android;

/**
 * Created by sshah on 2/2/15.
 */

public class URLBuilder {

    private String baseURL;
    private String fullURL;

    public URLBuilder(String baseURL)
    {
        if(baseURL.endsWith("/")) {
            this.baseURL = baseURL;
        } else {
            this.baseURL = baseURL + "/";
        }

        fullURL = this.baseURL;
    }

    public void appendPathComponent(String pathComponent)
    {
        if(pathComponent.startsWith("/")) {
            pathComponent = pathComponent.substring(1);
        }

        if(this.fullURL.endsWith("/")) {
            this.fullURL = this.fullURL + pathComponent;
        } else {
            this.fullURL = this.fullURL + "/" + pathComponent;
        }
    }

    public String build()
    {
        return this.fullURL;
    }
}

