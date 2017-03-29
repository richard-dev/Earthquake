package com.example.android.quakereport;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by richard on 3/28/17.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // update the preference summary when the settings activity is launched.
            // Given the key of a preference, we can use PreferenceFragment's
            // findPreference() method to get the Preference object,
            // and setup the preference using a helper method called bindPreferenceSummaryToValue().
            Preference minMag = findPreference(getString(R.string.settings_min_mag_key));
            bindPreferenceSummaryToValue(minMag);
        }

        // define the bindPreferenceSummaryToValue() helper method to set the
        // current EarhtquakePreferenceFragment instance as the listener on each preference.
        // We also read the current value of the preference stored in the
        // SharedPreferences on the device, and display that in the preference
        // summary (so that the user can see the current value of the preference).
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.
                    getDefaultSharedPreferences(preference.getContext());
            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        // First declare that the EarthquakePreferenceFragment class should implement the
        // OnPreferenceChangeListener interface, and override the onPreferenceChange() method.
        // The code in this method takes care of updating the displayed preference summary
        // after it has been changed.
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }
    }
}
