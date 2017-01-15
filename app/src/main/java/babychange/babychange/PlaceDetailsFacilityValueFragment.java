package babychange.babychange;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import babychange.babychange.restapi.Facility;

/**
 * Created by ian on 12/01/2017.
 */

public class PlaceDetailsFacilityValueFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_details_facility_value, container, false);

        String facilityValue = getArguments().getString(Facility.FACILITY_VALUE_BUNDLE_KEY);
        TextView facilityName = (TextView) view.findViewById(R.id.placeDetailsFacilityValueText);
        facilityName.setText(facilityValue);

        return view;
    }
}
