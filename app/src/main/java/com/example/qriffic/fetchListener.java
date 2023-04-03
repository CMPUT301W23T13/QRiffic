package com.example.qriffic;

/**
 * This interface defines a fetchListener
 * <p>
 * This block references the following web page:
 * Link: https://programming.guide/java/create-a-custom-event.html
 * Author: Unavailable
 * Date: 10/03/2023
 */
public interface fetchListener {
    void onFetchComplete();  // called if the username is in the database

    void onFetchFailure();  // called if the username is not in the database
}
