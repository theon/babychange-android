package babychange.babychange;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Date;

import babychange.babychange.restapi.Facility;
import babychange.babychange.restapi.NewReview;
import babychange.babychange.restapi.Place;
import babychange.babychange.restapi.RestApiClientHolder;
import babychange.babychange.restapi.Review;
import babychange.babychange.restapi.ReviewResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static babychange.babychange.PlaceReviewActivity.NEW_VIEW_CREATED_EXTRA;
import static babychange.babychange.restapi.Place.PLACE_DETAILS_EXTRA_KEY;
import static babychange.babychange.restapi.Place.PLACE_REVIEW_EXTRA_KEY;

/**
 * Created by ian on 12/01/2017.
 */

public class PlaceDetailsActivity extends AppCompatActivity {

    public static final int RETURN_FROM_REVIEW_ACTIVITY = 1;

    private Place place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_place_details);
    }

    @Override
    public void onPostCreate(Bundle bundle) {
        place = (Place)getIntent().getSerializableExtra(PLACE_DETAILS_EXTRA_KEY);

        setTitle(place.name);

        TextView placeAddressText = (TextView)findViewById(R.id.placeDetailsAddressText);
        placeAddressText.setText(place.address);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        for(Facility facility: place.facilities) {
            PlaceDetailsFacilityFragment facilityFragment = new PlaceDetailsFacilityFragment();
            Bundle facilityBundle = new Bundle();
            facilityBundle.putParcelable(Facility.FACILITY_BUNDLE_KEY, facility);
            facilityFragment.setArguments(facilityBundle);

            fragmentTransaction.add(R.id.placeDetailsFacilities, facilityFragment);
        }
        fragmentTransaction.commit();

        loadReviews();

        super.onPostCreate(bundle);
    }

    protected void loadReviews() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        //final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

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
        //fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reviewPlaceMenuItem:
                gotoReviewForm(item.getActionView());
                return true;
        }
        return false;
    }

    public void gotoReviewForm(View button) {
        Intent toPlaceReviewActivity = new Intent(this, PlaceReviewActivity.class);
        toPlaceReviewActivity.putExtra(PLACE_REVIEW_EXTRA_KEY, place);
        startActivityForResult(toPlaceReviewActivity, RETURN_FROM_REVIEW_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RETURN_FROM_REVIEW_ACTIVITY && resultCode == RESULT_OK) {
            NewReview newReview = data.getParcelableExtra(NEW_VIEW_CREATED_EXTRA);

            FragmentManager fragmentManager = getSupportFragmentManager();
            PlaceReviewsListFragment reviewsListFragment = (PlaceReviewsListFragment)fragmentManager.findFragmentById(R.id.placeReviewsFragment);
            ArrayAdapter<Review> adapter = (ArrayAdapter<Review>)reviewsListFragment.getListAdapter();

            Review r = new Review();
            r.review = newReview.review;
            r.setDate(new Date());
            r.facilities = newReview.facilities;
            r.place = place.id;
            r.placeLocation = place.location;
            r.rating = newReview.rating;
            r.user = "-1";
            adapter.insert(r, 0);
        }
    }
}
