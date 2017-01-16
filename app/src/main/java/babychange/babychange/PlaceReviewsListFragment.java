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

import com.google.common.collect.Lists;

import java.util.ArrayList;
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
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        ArrayAdapter adapter = new ArrayAdapter<Review>(getActivity(), R.layout.fragment_review, new ArrayList<Review>()) {
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

    public void setRows(List<Review> rows) {
        ArrayAdapter<Review> adapter = (ArrayAdapter<Review>)getListAdapter();
        adapter.clear();
        adapter.addAll(rows);
        adapter.notifyDataSetChanged();
    }
}
