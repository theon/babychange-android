package babychange.babychange;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import babychange.babychange.restapi.BabyPlace;
import babychange.babychange.restapi.DayOpeningHours;
import babychange.babychange.restapi.Facility;
import babychange.babychange.restapi.GeoLocation;
import babychange.babychange.restapi.OpeningHours;
import babychange.babychange.restapi.RestApiClientHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ian on 17/01/2017.
 */

public class NewPlaceActivity extends AppCompatActivity {

    MyAdapter mAdapter;

    ViewPager mPager;

    Button mPreviousButton;
    Button mNextButton;

    Place mPlace;

    BabyPlace placeToCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_place);

        placeToCreate = new BabyPlace();

        mAdapter = new MyAdapter(getSupportFragmentManager());

        mPager = (ViewPager)findViewById(R.id.createPlacePager);
        mPager.setAdapter(mAdapter);

        // Watch for button clicks.
        mPreviousButton = (Button)findViewById(R.id.createPlacePreviousButton);
        mNextButton = (Button)findViewById(R.id.createPlaceNextButton);

        onPageChange();

        //Set next button invisible, until place is selected
        mNextButton.setVisibility(View.INVISIBLE);

        mNextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int currentIndex = mPager.getCurrentItem();

                System.out.println("Checking if can proceed");

                if(mAdapter.mCreatePlaceFragments.get(currentIndex).canProceed()) {

                    System.out.println("Yes, we can!");

                    if (currentIndex == mAdapter.getCount() - 1) {
                        onFinishButtonClick();
                    } else {
                        mPager.setCurrentItem(currentIndex + 1, true);
                    }
                }
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
            }
        });

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageScrollStateChanged(int state) {}
            @Override
            public void onPageSelected(int position) {
                onPageChange();
            }
        });
    }

    protected void onPageChange() {
        int current = mPager.getCurrentItem();
        setTitle(mAdapter.mCreatePlaceFragments.get(current).getTitle());

        if (current == 0) {
            mPreviousButton.setVisibility(View.INVISIBLE);
            mNextButton.setVisibility(View.VISIBLE);
        } else {
            int totalPages = mAdapter.getCount();
            if (current == totalPages - 1) {
                mPreviousButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
                mNextButton.setText("Finish");
            } else {
                mPreviousButton.setVisibility(View.VISIBLE);
                mNextButton.setVisibility(View.VISIBLE);
                mNextButton.setText("Next");
            }
        }
    }

    public void onFinishButtonClick() {
        Call<BabyPlace> request = RestApiClientHolder.restClient.createPlace(placeToCreate);
        request.enqueue(new Callback<BabyPlace>() {
            @Override
            public void onResponse(Call<BabyPlace> call, Response<BabyPlace> response) {
                if(response.isSuccessful()) {
                    finish();
                    Toast.makeText(NewPlaceActivity.this, "Place created!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        System.out.println(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    onFailure(call, null);
                }
            }

            @Override
            public void onFailure(Call<BabyPlace> call, Throwable t) {
                Toast.makeText(NewPlaceActivity.this, "Unable to create place", Toast.LENGTH_LONG).show();
            }
        });
    }

    public final static int PLACE_PICKER_REQUEST = 1;

    public void pickLocation(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            Log.e("TAG", "Unable to use Google Maps BabyPlace Picker", e);
            Toast.makeText(this, "Problem picking place. If this keeps happening, let us know!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                mPlace = PlacePicker.getPlace(this, data);
                mAdapter.basicDetailsFragment.setPlace(mPlace);
                mAdapter.openingTimesFragment.setPlace(mPlace);
                placeToCreate.location = new GeoLocation(mPlace.getLatLng().latitude, mPlace.getLatLng().longitude);

                mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
            }
        }
    }

    public static class MyAdapter extends FragmentPagerAdapter {

        NewPlaceLocationFragment locationFragment = new NewPlaceLocationFragment();
        NewPlaceBasicDetailsFragment basicDetailsFragment = new NewPlaceBasicDetailsFragment();
        NewPlaceFacilitiesFragment facilitiesFragment = new NewPlaceFacilitiesFragment();
        NewPlaceFacilityValuesFragment facilityValuesFragment = new NewPlaceFacilityValuesFragment();
        NewPlaceOpeningTimesFragment openingTimesFragment = new NewPlaceOpeningTimesFragment();

        List<NewPlacePageFragment> mCreatePlaceFragments;

        public MyAdapter(FragmentManager fm) {
            super(fm);
            mCreatePlaceFragments = Lists.newArrayList(
                locationFragment,
                basicDetailsFragment,
                facilitiesFragment,
                facilityValuesFragment/*,
                openingTimesFragment*/
            );
        }

        @Override
        public int getCount() {
            return mCreatePlaceFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mCreatePlaceFragments.get(position);
        }
    }

    public static abstract class NewPlacePageFragment extends NewPlaceFragment {
        public abstract String getTitle();
    }

    public static abstract class NewPlaceFragment extends Fragment {
        public boolean canProceed() {
            return true;
        }

        public void alert(String message) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
        }

        public BabyPlace getPlaceToCreate() {
            return ((NewPlaceActivity)getActivity()).placeToCreate;
        }
    }

    public static class NewPlaceLocationFragment extends NewPlacePageFragment {

        @Override
        public String getTitle() {
            return "Place Location";
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_new_place_location, container, false);
            return v;
        }
    }

    public static class NewPlaceBasicDetailsFragment extends NewPlacePageFragment {

        private EditText nameText;
        private EditText addressText;
        private EditText phoneText;

        @Override
        public String getTitle() {
            return "Place Details";
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_new_place_basic_details, container, false);
            nameText = (EditText)v.findViewById(R.id.newPlaceNameText);
            addressText = (EditText)v.findViewById(R.id.newPlaceAddressText);
            phoneText = (EditText)v.findViewById(R.id.newPlacePhoneText);
            return v;
        }

        public void setPlace(Place place) {
            nameText.setText(place.getName());
            addressText.setText(place.getAddress());
            phoneText.setText(place.getPhoneNumber());
        }

        @Override
        public boolean canProceed() {
            String name = nameText.getText().toString().trim();
            String address = addressText.getText().toString().trim();
            String phone = phoneText.getText().toString().trim();

            if(name.isEmpty()) {
                alert("A place must have a name!");
                return false;
            }

            BabyPlace place = getPlaceToCreate();
            place.name = name;
            place.address = address;
            place.phone = phone;

            //TODO: Get from user
            place.categories = "dummy";

            return true;
        }
    }

    public static class NewPlaceFacilitiesFragment extends NewPlacePageFragment implements FacilitiesService.OnFacilitiesCallback {

        @Override
        public String getTitle() {
            return "Place Facilities";
        }

        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FacilitiesService.getAllowedFacilities(this);
        }

        @Override
        public boolean canProceed() {
            for(Facility facility: getPlaceToCreate().facilities) {
                if(facility.values.contains("Yes") || facility.values.contains("No")) {
                    return true;
                }
            }

            alert("You must select Yes or No for at least one facility");

            return false;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_new_place_facilties, container, false);
            return v;
        }

        @Override
        public void onFacilities(List<Facility> facilities) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            for(Facility facility: facilities) {
                //Instantiate our place
                Facility placeFacility = new Facility();
                placeFacility.name = facility.name;
                placeFacility.queryName = facility.queryName;
                placeFacility.values = Lists.newArrayList("Unknown");
                getPlaceToCreate().facilities.add(placeFacility);

                NewPlaceFacilityRadioButtonsFragment radioButtonsFragment = new NewPlaceFacilityRadioButtonsFragment();
                Bundle facilityBundle = new Bundle();
                facilityBundle.putParcelable(Facility.FACILITY_BUNDLE_KEY, facility);
                radioButtonsFragment.setArguments(facilityBundle);
                fragmentTransaction.add(R.id.newPlaceFacilities, radioButtonsFragment);
            }

            fragmentTransaction.commit();
        }

        @Override
        public void onFailure(Throwable t) {
            Toast.makeText(getActivity(), "Unable to get list of facilities", Toast.LENGTH_LONG).show();
        }
    }

    public static class NewPlaceFacilityRadioButtonsFragment extends NewPlaceFragment implements RadioGroup.OnCheckedChangeListener {

        private Facility facility;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_new_place_facility_radio_buttons, container, false);

            facility = getArguments().getParcelable(Facility.FACILITY_BUNDLE_KEY);
            TextView facilityName = (TextView) view.findViewById(R.id.newPlaceFacilityText);
            facilityName.setText(facility.name);


            RadioGroup radioGroup = (RadioGroup)view.findViewById(R.id.newPlaceFacilityRadioButtons);
            radioGroup.setOnCheckedChangeListener(this);


            return view;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            Facility placeFacility = getPlaceToCreate().getFacility(facility.queryName);

            placeFacility.values.remove("Yes");
            placeFacility.values.remove("No");
            placeFacility.values.remove("Unknown");

            switch(checkedId) {
                case R.id.newPlaceFacilityYesRadio:
                    placeFacility.values.add("Yes"); break;
                case R.id.newPlaceFacilityNoRadio:
                    placeFacility.values.add("No"); break;
                default:
                    placeFacility.values.add("Unknown"); break;
            }

            ((NewPlaceActivity)getActivity()).mAdapter.facilityValuesFragment.updateCheckboxVisibilities();
        }
    }

    public static class NewPlaceFacilityValuesFragment extends NewPlacePageFragment implements FacilitiesService.OnFacilitiesCallback {

        private Map<String,NewPlaceFacilityValuesCheckboxesFragment> checkboxesByFacilityQueryName;

        @Override
        public String getTitle() {
            return "Place Facility Details";
        }

        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            FacilitiesService.getAllowedFacilities(this);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_new_place_facility_values, container, false);
            return v;
        }

        @Override
        public void onFacilities(List<Facility> facilities) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            checkboxesByFacilityQueryName = new HashMap<>();

            for(Facility facility: facilities) {
                NewPlaceFacilityValuesCheckboxesFragment valuesFragment = new NewPlaceFacilityValuesCheckboxesFragment();
                checkboxesByFacilityQueryName.put(facility.queryName, valuesFragment);

                Bundle facilityBundle = new Bundle();
                facilityBundle.putParcelable(Facility.FACILITY_BUNDLE_KEY, facility);
                valuesFragment.setArguments(facilityBundle);
                fragmentTransaction.add(R.id.newPlaceFacilityValues, valuesFragment);
            }

            fragmentTransaction.commit();
        }

        @Override
        public void onFailure(Throwable t) {
            Toast.makeText(getActivity(), "Unable to get list of facilities", Toast.LENGTH_LONG).show();
        }

        public void updateCheckboxVisibilities() {
            //Get which facilities have been selected
            System.out.println("NewPlaceFacilityValuesFragment.onStart");

            for(Facility placeFacility: getPlaceToCreate().facilities) {
                View checkboxesView = checkboxesByFacilityQueryName.get(placeFacility.queryName).getView();
                if(placeFacility.values.contains("Yes")) {
                    checkboxesView.setVisibility(View.VISIBLE);
                } else {
                    checkboxesView.setVisibility(View.GONE);
                }
            }
        }
    }

    public static class NewPlaceFacilityValuesCheckboxesFragment extends NewPlaceFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_new_place_facility_values_checkboxes, container, false);

            final Facility facility = getArguments().getParcelable(Facility.FACILITY_BUNDLE_KEY);
            final TextView facilityName = (TextView) view.findViewById(R.id.newPlaceFacilityValuesText);
            facilityName.setText(facility.name);

            LinearLayout facilityValues = (LinearLayout) view.findViewById(R.id.newPlaceFacilityValuesCheckboxes);

            for(final String facilityValue: facility.values) {
                if(!facilityValue.equals("Yes")) {
                    CheckBox valueCheckbox = new CheckBox(getActivity());
                    valueCheckbox.setText(facilityValue);
                    valueCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Facility placeFacility = getPlaceToCreate().getFacility(facility.queryName);
                            if(isChecked) {
                                placeFacility.values.add(facilityValue);
                            } else {
                                placeFacility.values.remove(facilityValue);
                            }
                        }
                    });
                    facilityValues.addView(valueCheckbox);
                }
            }

            return view;
        }
    }

    public static class NewPlaceOpeningTimesFragment extends NewPlacePageFragment {

        @Override
        public String getTitle() {
            return "Place Opening Times";
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_new_place_opening_times, container, false);
            return v;
        }

        public void setPlace(Place place) {
            //TODO: Get Opening hours from place rest api
        }
    }
}
