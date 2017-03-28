/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String USGS_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=5&limit=10";
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 0;
    private EarthquakeAdapter mAdapter;
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Create a new {@link ArrayAdapter} of earthquakes
        ListView listView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());
        listView.setAdapter(mAdapter);

        // Set an onclick listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = mAdapter.getItem(position);

                // Convert the string url into a uri object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getURL());

                // Create a new intent to view the earthquake Uri
                Intent detailsIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                startActivity(detailsIntent);
            }
        });

        // Empty text view if no data to show
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        // To retrieve an earthquake, we need to get the loader manager and tell the
        // loader manager to initialize the loader with the specified ID,
        // the second argument allows us to pass a bundle of additional information,
        // which we'll skip. The third argument is what object should receive the
        // LoaderCallbacks (and therefore, the data when the load is complete!) -
        // which will be this activity. This code goes inside the onCreate() method of the
        // EarthquakeActivity, so that the loader can be initialized as soon as the app opens.
        getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this);
        Log.i(LOG_TAG, "initLoader");
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        // We need onCreateLoader(), for when the LoaderManager has determined that the loader
        // with our specified ID isn't running, so we should create a new one.
        Log.i(LOG_TAG, "onCreateLoader");
        return new EarthquakeLoader(this, USGS_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // Set empty state text
        mEmptyStateTextView.setText("No earthquakes found.");

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
            Log.i(LOG_TAG, "onLoadFinished: adding to mAdapter.");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        // Where we're being informed that the data from our loader is no longer valid.
        // This isn't actually a case that's going to come up with our simple loader,
        // but the correct thing to do is to remove all the earthquake data from our UI
        // by clearing out the adapter’s data set.
        mAdapter.clear();
        Log.i(LOG_TAG, "clearing mAdapter.");
    }
}
