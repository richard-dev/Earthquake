package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by richard on 3/28/17.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
    /** Tag for log messages */
    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    private String mURL;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        // If invalid url strings
        if (mURL == null) {
            return null;
        }

        // Create earthquake arraylist from QueryUtils which extracts data from a JSON object
        // and puts it in an EarthQuake ArrayList.
        List<Earthquake> earthquakes = QueryUtils.extractEarthquakes(mURL);
        return earthquakes;
    }
}
