package babychange.babychange;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.NoScrollListFragment;
import android.support.v4.app.NoScrollListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import babychange.babychange.restapi.Review;

/**
 * Created by ian on 15/01/2017.
 */

public class PlaceReviewsListFragment extends NoScrollListFragment {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText("No reviews yet");
    }

    public void setRows(List<Review> rows) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        ArrayAdapter adapter = new ArrayAdapter<Review>(getActivity(), R.layout.fragment_review, rows) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.fragment_review, parent, false);
                }

                Review review = getItem(position);


                RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.placeReviewRatingBar);
                ratingBar.setRating(review.rating);

                TextView userText = (TextView) convertView.findViewById(R.id.placeReviewUserText);
                userText.setText(review.user);

                TextView dateText = (TextView) convertView.findViewById(R.id.placeReviewDateText);
                dateText.setText(review.howLongAgo());

                TextView reviewText = (TextView) convertView.findViewById(R.id.placeReviewText);
                reviewText.setText(review.review);

                return convertView;
            }
        };
        setListAdapter(adapter);
    }
}
