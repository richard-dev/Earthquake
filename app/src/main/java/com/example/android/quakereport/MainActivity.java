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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.content.Loader;
import android.app.LoaderManager.LoaderCallbacks;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String USGS_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int EARTHQUAKE_LOADER_ID = 0;
    private EarthquakeAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgressBar;


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

        // Assign progress bar id
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Check if internet is available.
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // To retrieve an earthquake, we need to get the loader manager and tell the
            // loader manager to initialize the loader with the specified ID,
            // the second argument allows us to pass a bundle of additional information,
            // which we'll skip. The third argument is what object should receive the
            // LoaderCallbacks (and therefore, the data when the load is complete!) -
            // which will be this activity. This code goes inside the onCreate() method of the
            // EarthquakeActivity, so that the loader can be initialized as soon as the app opens.
            getLoaderManager().initLoader(EARTHQUAKE_LOADER_ID, null, this);
            Log.i(LOG_TAG, "start Loader");
        } else {
            // Hide progress bar
            mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
            mProgressBar.setVisibility(GONE);
            // Set no internet text
            mEmptyStateTextView.setText("No Internet connection found.");
            Log.i(LOG_TAG, "skip loader, no internet connection.");
        }

    }

    // Settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // When user clicks on a menu item.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Loader
    // We need onCreateLoader(), for when the LoaderManager has determined that the loader
    // with our specified ID isn't running, so we should create a new one.
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        // Get preference settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Get min mag from string value and set default
        String minMag = sharedPreferences.getString(
                getString(R.string.settings_min_mag_key),
                getString(R.string.settings_min_mag_default)
        );
        // Get order by key and default in string value
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_key),
                getString(R.string.settings_order_default)
        );

        // Create uri object
        Uri baseUri = Uri.parse(USGS_URL);
        Uri.Builder ub = baseUri.buildUpon();
        // build parameters
        ub.appendQueryParameter("format", "geojson");
        ub.appendQueryParameter("limit", "20");
        ub.appendQueryParameter("minmag", minMag);
        ub.appendQueryParameter("orderby", "time");

        Log.i(LOG_TAG, "onCreateLoader");
        return new EarthquakeLoader(this, ub.toString());
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

        // Hide progress bar.
        mProgressBar.setVisibility(GONE);
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
