package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.content.Context.POWER_SERVICE;
import static com.example.android.quakereport.MainActivity.LOG_TAG;

/**
 * Created by richard on 3/17/17.
 */

public final class QueryUtils {
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    public QueryUtils() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Earthquake> extractEarthquakes(String stringURL) {
        // Create URL object
        URL url = createURL(stringURL);

        // Perform Http request ot the URL and receive a JSON response.
        String JSONResponse = null;

        try {
            JSONResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem parsing the results", e);
        }

        // Extract the data we need from the JSON response
        ArrayList<Earthquake> earthquakes = extractFeaturesFromJSON(JSONResponse);


        // Return the list of earthquakes: String magnitude, String city, String date
        return earthquakes;
    }

    // Create URL object
    private static URL createURL(String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating URL", e);
            return null;
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as a response
    private static String makeHttpRequest(URL url) throws IOException {
        String JSONResponse = "";

        // If the URL is null, then return early
        if (url == null) {
            return JSONResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* 10 seconds */);
            urlConnection.setConnectTimeout(15000 /* 15 seconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                JSONResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results", e);
        } finally {
            // Clean up connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            // Clean up stream
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return JSONResponse;
    }

    // Convert the inputstream into a string which contains JSON response from server
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine(); // read the first line
            while (line != null) {
                // Loop through each line until it's null
                sb.append(line); // Append line
                line = reader.readLine(); // read next line
            }
        }
        return sb.toString();
    }

    private static ArrayList<Earthquake> extractFeaturesFromJSON(String earthquakeJSON) {
        // If the JSON response is empty or null, return early
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        try {
            ArrayList<Earthquake> earthquakes = new ArrayList<>();
            JSONObject root = new JSONObject(earthquakeJSON);
            JSONArray featuresArray = root.getJSONArray("features");
            for (int i = 0; i < featuresArray.length(); i++) {
                JSONObject featuresObject = featuresArray.getJSONObject(i);
                JSONObject propertiesObject = featuresObject.getJSONObject("properties");

                earthquakes.add(new Earthquake(
                        propertiesObject.getDouble("mag"),
                        propertiesObject.getString("place"),
                        Long.parseLong(propertiesObject.getString("time")),
                        propertiesObject.getString("url")
                ));
            }
            return earthquakes;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problems parsing JSON", e);
        }
        return null;
    }
    //            JSONObject root = new JSONObject(SAMPLE_JSON_RESPONSE);
//            JSONArray featuresArray = root.getJSONArray("features");
//
//            for (int i = 0; i < featuresArray.length(); i++) {
//                JSONObject featuresObject = featuresArray.getJSONObject(i);
//                JSONObject propertiesObject = featuresObject.getJSONObject("properties");
//
//                earthquakes.add(new Earthquake(
//                        propertiesObject.getDouble("mag"),
//                        propertiesObject.getString("place"),
//                        Long.parseLong(propertiesObject.getString("time")),
//                        propertiesObject.getString("url")
//                ));
//            }

}
