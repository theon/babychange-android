package babychange.babychange;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import babychange.babychange.restapi.Facility;

/**
 * Created by ian on 12/01/2017.
 */

public class PlaceDetailsFacilityFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_details_facility, container, false);

        Facility facility = getArguments().getParcelable(Facility.FACILITY_BUNDLE_KEY);
        TextView facilityName = (TextView) view.findViewById(R.id.placeDetailsFacilityName);
        facilityName.setText(facility.name);

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        for(String facilityValue: facility.values) {
            PlaceDetailsFacilityValueFragment facilityValueFragment = new PlaceDetailsFacilityValueFragment();
            Bundle facilityBundle = new Bundle();
            facilityBundle.putString(Facility.FACILITY_VALUE_BUNDLE_KEY, facilityValue);
            facilityValueFragment.setArguments(facilityBundle);
            fragmentTransaction.add(R.id.placeDetailsFacilityValues, facilityValueFragment);
        }

        fragmentTransaction.commit();

        return view;
    }
}
