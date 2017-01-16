package babychange.babychange;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import babychange.babychange.restapi.Facility;
import babychange.babychange.restapi.NewReview;
import babychange.babychange.restapi.NewReviewResponse;
import babychange.babychange.restapi.Place;
import babychange.babychange.restapi.RestApiClientHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static babychange.babychange.restapi.Place.PLACE_REVIEW_EXTRA_KEY;

/**
 * Created by ian on 16/01/2017.
 */

public class PlaceReviewActivity extends AppCompatActivity {

    public final static String NEW_VIEW_CREATED_EXTRA = "NEW_VIEW_CREATED";

    private Place place;

    private AlertDialog selectFacilitiesDialog;
    private ArrayList<Facility> selectedFacilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_place_review);
    }

    @Override
    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        place = (Place) getIntent().getSerializableExtra(PLACE_REVIEW_EXTRA_KEY);
        setTitle("Reviewing " + place.name);
        selectedFacilities = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Facilities");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        String[] facilityLabels = Lists.transform(place.facilities, Functions.toStringFunction())
                .toArray(new String[place.facilities.size()]);

        final TextView selectedFacilitiesText = (TextView)findViewById(R.id.placeReviewSelectedFaciltiesText);
        final Joiner joiner = Joiner.on(", ");

        builder.setMultiChoiceItems(facilityLabels, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        Facility facility = place.facilities.get(which);
                        if (isChecked) {
                            selectedFacilities.add(facility);
                        } else if (selectedFacilities.contains(facility)) {
                            selectedFacilities.remove(facility);
                        }

                        List<String> selectedFacilityQueryNames = Lists.transform(selectedFacilities, Functions.toStringFunction());
                        selectedFacilitiesText.setText(joiner.join(selectedFacilityQueryNames));
                    }
                });

        selectFacilitiesDialog = builder.create();
    }

    public void selectFacilities(View button) {
        selectFacilitiesDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_review, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.placeReviewDoneMenuItem:
                postReview();
                return true;
        }
        return false;
    }

    private void postReview() {
        final NewReview newReview = new NewReview();
        newReview.place = place.id;

        RatingBar ratingBar = (RatingBar)findViewById(R.id.placingReviewRatingBar);
        newReview.rating = ratingBar.getRating();

        newReview.facilities = Lists.transform(selectedFacilities, new Function<Facility,String>() {
            @Override public String apply(Facility input) { return input.queryName; }
        });

        EditText reviewText = (EditText)findViewById(R.id.placeReviewReviewText);
        newReview.review = reviewText.getText().toString();

        Call<NewReviewResponse> request = RestApiClientHolder.restClient.createReview(newReview);
        request.enqueue(new Callback<NewReviewResponse>() {
            @Override
            public void onResponse(Call<NewReviewResponse> call, Response<NewReviewResponse> response) {
                NewReviewResponse body = response.body();
                if(response.isSuccessful() && body.success) {
                    Toast.makeText(PlaceReviewActivity.this, "Review successfully posted", Toast.LENGTH_LONG).show();
                    Intent data = new Intent("PLACE_REVIEW");
                    data.putExtra(NEW_VIEW_CREATED_EXTRA, newReview);
                    setResult(RESULT_OK, data);
                    finish();
                } else {
                    Log.e("TAG", "Failed to post review for " + place.id + ". " + body.cause.or(""));
                    postReviewFailed();
                }
            }

            @Override
            public void onFailure(Call<NewReviewResponse> call, Throwable t) {
                Log.e("TAG", "Failed to post review for " + place.id, t);
                postReviewFailed();
            }
        });
    }

    private void postReviewFailed() {
        Toast.makeText(PlaceReviewActivity.this, "Failed to post review, please try again", Toast.LENGTH_LONG).show();
    }
}
