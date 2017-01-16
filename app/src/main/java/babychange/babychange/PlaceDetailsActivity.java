package babychange.babychange;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import babychange.babychange.restapi.Facility;
import babychange.babychange.restapi.Place;
import babychange.babychange.restapi.RestApiClientHolder;
import babychange.babychange.restapi.Review;
import babychange.babychange.restapi.ReviewResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        final Place place = (Place)getIntent().getSerializableExtra(PLACE_DETAILS_EXTRA_KEY);

        setTitle(place.name);

        TextView placeAddressText = (TextView)findViewById(R.id.placeDetailsAddressText);
        placeAddressText.setText(place.address);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for(Facility facility: place.facilities) {
            PlaceDetailsFacilityFragment facilityFragment = new PlaceDetailsFacilityFragment();
            Bundle facilityBundle = new Bundle();
            facilityBundle.putParcelable(Facility.FACILITY_BUNDLE_KEY, facility);
            facilityFragment.setArguments(facilityBundle);

            fragmentTransaction.add(R.id.placeDetailsFacilities, facilityFragment);
        }

        Call<ReviewResults> request = RestApiClientHolder.restClient.findReviews(place.id);
        request.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Call<ReviewResults> call, Response<ReviewResults> response) {
                PlaceReviewsListFragment reviewsListFragment = (PlaceReviewsListFragment)fragmentManager.findFragmentById(R.id.placeReviewsFragment);
                ReviewResults reviewResults = response.body();
                RatingBar averageRatingBar = (RatingBar)findViewById(R.id.placeDetailsAverageRating);
                reviewsListFragment.setRows(reviewResults.reviews);
                if(reviewResults.reviews.isEmpty()) {
                    averageRatingBar.setVisibility(View.GONE);
                } else {
                    averageRatingBar.setRating(reviewResults.averageRating);
                }
            }

            @Override
            public void onFailure(Call<ReviewResults> call, Throwable t) {
                Log.e("TAG", "Error getting reviews for place " + place.id, t);
            }
        });

        fragmentTransaction.commit();
        super.onPostCreate(bundle);
    }
}
