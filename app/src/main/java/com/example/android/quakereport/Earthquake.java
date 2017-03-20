package com.example.android.quakereport;

import java.text.SimpleDateFormat;
import java.util.Calendar;
/**
 * Created by v759870 on 3/15/2017.
 */

public class Earthquake {
    private int mPosition;

    // private int mDrawableResource;

    private String mMagnitude;

    private String mCity;

    private long mTime;


    // Constructor
    public Earthquake(String magnitude, String city, long time) {
        mMagnitude = magnitude;
        mCity = city;
        mTime = time;
    }

    // Getters
    public String getMagnitude() {
        return mMagnitude;
    }

    public String getCity() {
        return mCity;
    }

    public long getDate() {
        return mTime;
    }
}
