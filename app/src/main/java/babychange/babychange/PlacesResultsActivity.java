package babychange.babychange;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import babychange.babychange.restapi.PlaceSearchResults;
import babychange.babychange.restapi.RestApiClient;
import babychange.babychange.restapi.RestApiFilter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class PlacesResultsActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private PlacesResultsListFragment resultsListFragment;

    private RestApiClient restClient;

    private class FilterCheckBox {
        private CheckBox checkBox;
        private RestApiFilter apiFilter;

        private FilterCheckBox(CheckBox checkBox, RestApiFilter apiFilter) {
            this.checkBox = checkBox;
            this.apiFilter = apiFilter;
        }
    }

    private List<FilterCheckBox> facilityFilterCheckboxes;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.6:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        restClient = retrofit.create(RestApiClient.class);

        setContentView(R.layout.activity_places_results);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.places_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_menu_item:
                DrawerLayout searchFilterDrawer = (DrawerLayout)findViewById(R.id.places_search_filter_drawer);
                searchFilterDrawer.openDrawer(GravityCompat.END);
                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        FragmentManager fragmentManager = getFragmentManager();
        resultsListFragment = (PlacesResultsListFragment) fragmentManager.findFragmentById(R.id.resultsListFragment);
        resultsListFragment.setEmptyText("No results.");

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        checkPermission(Manifest.permission.INTERNET);

        mGoogleApiClient.connect();

        LinearLayout facilityFiltersLayout = (LinearLayout) findViewById(R.id.facility_filters_layout);
        facilityFiltersLayout.removeAllViews();

        ArrayList<String> filters = new ArrayList<>();
        filters.add("babyChanging");
        filters.add("highchairs");

        ArrayList<String> values = new ArrayList<>();
        values.add("Yes");
        values.add("Wow");

        int eightDp = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

        facilityFilterCheckboxes = new ArrayList<>();
        for(String filter: filters) {
            TextView title = new TextView(this);
            title.setText(filter);
            title.setPadding(eightDp, eightDp, eightDp, eightDp);
//            title.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
//            title.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            facilityFiltersLayout.addView(title);

            for(String value: values) {
                CheckBox filterCheckbox = new CheckBox(this);
                filterCheckbox.setText(value);
                filterCheckbox.setPadding(eightDp, eightDp, eightDp, eightDp);
//                filterCheckbox.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
//                filterCheckbox.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);

                facilityFiltersLayout.addView(filterCheckbox);
                facilityFilterCheckboxes.add(new FilterCheckBox(filterCheckbox, new RestApiFilter(filter, filter, value)));
            }
        }

        super.onStart();
    }

    public void searchWithFilters(View button) {
        //TODO: Reuse searchFilterDrawer
        DrawerLayout searchFilterDrawer = (DrawerLayout)findViewById(R.id.places_search_filter_drawer);
        searchFilterDrawer.closeDrawer(GravityCompat.END);
        onConnected(null);
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
        //resultsListFragment.setLoadingText("Finding places near you...");

        List<RestApiFilter> selectedFilters = new ArrayList<>();

        for(FilterCheckBox fcb: facilityFilterCheckboxes) {
            if(fcb.checkBox.isChecked()) {
                selectedFilters.add(fcb.apiFilter);
            }
        }

        Call<PlaceSearchResults> response = restClient.findPlaces(location.getLatitude(), location.getLongitude(), selectedFilters);

        response.enqueue(new Callback<PlaceSearchResults>() {
            @Override
            public void onResponse(Call<PlaceSearchResults> call, Response<PlaceSearchResults> response) {
                resultsListFragment.setRows(response.body().places);
            }
            @Override
            public void onFailure(Call<PlaceSearchResults> call, Throwable t) {
                Toast.makeText(PlacesResultsActivity.this, "Unable to contact server. Check your Internet connection", Toast.LENGTH_LONG).show();
                Log.e("TAG", "BabyChange API Call failed", t);
                finish();
            }
        });
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
