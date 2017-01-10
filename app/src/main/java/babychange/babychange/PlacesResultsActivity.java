package babychange.babychange;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class PlacesResultsActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private TextView statusText;
    private ProgressBar statusProgressBar;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        queue = Volley.newRequestQueue(this);

        setContentView(R.layout.activity_places_results);
    }

    @Override
    protected void onStart() {

        statusText = (TextView) findViewById(R.id.statusText);
        statusProgressBar = (ProgressBar) findViewById(R.id.statusProgressBar);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        checkPermission(Manifest.permission.INTERNET);

        mGoogleApiClient.connect();
        super.onStart();
    }

    private void checkPermission(String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ permission }, 1);
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently

        // ...
        System.out.println("Oh crap, I'm here!" + result.toString());

        if(result.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
            int r = googleAPI.isGooglePlayServicesAvailable(this);
            if(r != ConnectionResult.SUCCESS) {
                if (googleAPI.isUserResolvableError(r)) {
                    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
                    googleAPI.getErrorDialog(this, r, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            System.out.println(statusText);

            statusText.setText("Getting your location...");
        } catch(SecurityException e) {
            //booo
            System.out.println("Booooo, I'm here!");
            Log.e("TAG", "onConnected: ", e);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println(location);
        statusText.setText("Finding places near you...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://192.168.1.6:8080/places/" + location.getLatitude() + "," + location.getLongitude(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            statusProgressBar.setVisibility(View.GONE);
                            statusText.setVisibility(View.GONE);

                            JSONArray places = response.getJSONArray("places");
                            ArrayList<Map<String, String>> rows = new ArrayList();

                            for(int i = 0; i < places.length(); i++) {
                                JSONObject placeResult = places.getJSONObject(i);
                                JSONObject place = placeResult.getJSONObject("place");
                                Map<String, String> row = new HashMap();
                                row.put("placeName", place.getString("name"));
                                row.put("address", place.getString("address"));
                                row.put("distance", placeResult.getInt("distanceInMetres") + "m");
                                rows.add(row);
                            }

                            FragmentManager fragmentManager = getFragmentManager();
                            PlacesResultsListFragment resultsList = (PlacesResultsListFragment) fragmentManager.findFragmentById(R.id.resultsListFragment);
                            resultsList.setRows(rows);

                        } catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                statusText.setText(error.getLocalizedMessage());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(request);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
}
