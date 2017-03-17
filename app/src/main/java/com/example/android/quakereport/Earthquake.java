package com.example.android.quakereport;

/**
 * Created by v759870 on 3/15/2017.
 */

public class Earthquake {
    private int mPosition;

    // private int mDrawableResource;

    private String mMagnitude;

    private String mCity;

    private String mDate;


    // Constructor
    public Earthquake(String magnitude, String city, String date) {
        mMagnitude = magnitude;
        mCity = city;
        mDate = date;
    }

    // Getters
    public String getMagnitude() {
        return mMagnitude;
    }

    public String getCity() {
        return mCity;
    }

    public String getDate() {
        return mDate;
    }
}
