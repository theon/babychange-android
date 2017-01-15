package babychange.babychange;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import babychange.babychange.restapi.Facility;
import babychange.babychange.restapi.Place;

import static babychange.babychange.restapi.Place.PLACE_DETAILS_EXTRA_KEY;

/**
 * Created by ian on 12/01/2017.
 */

public class PlaceDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_place_details);
    }

    @Override
    public void onPostCreate(Bundle bundle) {
        Place place = (Place)getIntent().getSerializableExtra(PLACE_DETAILS_EXTRA_KEY);

        setTitle(place.name);

        TextView placeAddressText = (TextView)findViewById(R.id.placeDetailsAddressText);
        placeAddressText.setText(place.address);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        for(Facility facility: place.facilities) {
            PlaceDetailsFacilityFragment facilityFragment = new PlaceDetailsFacilityFragment();
            Bundle facilityBundle = new Bundle();
            facilityBundle.putParcelable(Facility.FACILITY_BUNDLE_KEY, facility);
            facilityFragment.setArguments(facilityBundle);

            fragmentTransaction.add(R.id.placeDetailsFacilities, facilityFragment);
        }

        fragmentTransaction.commit();
        super.onPostCreate(bundle);
    }
}
