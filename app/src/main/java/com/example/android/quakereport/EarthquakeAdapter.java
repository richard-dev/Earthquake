package com.example.android.quakereport;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EarthquakeAdapter extends ArrayAdapter<Earthquake> {
    // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
    // the second argument is used when the ArrayAdapter is populating a single TextView.
    // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
    // going to use this second argument, so it can be any value. Here, we used 0.
    public EarthquakeAdapter(Activity context, ArrayList<Earthquake> earthquakes) {
        super(context, 0, earthquakes);
    }

    // Overrides
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        // Check if view was already created. If not then inflate a new view.
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // The position of the item within the adapter's data set of the item whose view we want
        Earthquake currentEarthquake = getItem(position);

        // Magnitude
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        TextView magnitudeTextView = (TextView) listItemView.findViewById(R.id.magnitude_textview);
        magnitudeTextView.setText(decimalFormat.format(currentEarthquake.getMagnitude()));

        // String to split
        String city = currentEarthquake.getCity();
        String cityArray[] = city.split("of ");
        // City offset
        TextView cityoffsetTextView = (TextView) listItemView.findViewById(R.id.cityoffset_textview);
        // City
        TextView cityTextView = (TextView) listItemView.findViewById(R.id.city_textview);
        if (cityArray.length == 1) {
            // If offset not available
            cityoffsetTextView.setText("");
            cityTextView.setText(cityArray[0]);
        } else {
            // If offset available
            cityoffsetTextView.setText(cityArray[0] + " of");
            cityTextView.setText(cityArray[1]);
        }

        // Date
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_textview);
        // Format milliseconds to date and time
        String date;
        // Create new Calendar
        Calendar cal = Calendar.getInstance();
        // Create date formatter
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy h:mm a");
        cal.setTimeInMillis(currentEarthquake.getDate());
        date = dateFormatter.format(cal.getTime());

        dateTextView.setText(date);

        return listItemView;
    }
}
