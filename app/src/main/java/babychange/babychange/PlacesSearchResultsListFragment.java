package babychange.babychange;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import babychange.babychange.restapi.PlaceSearchResult;

/**
 * Created by ian on 09/01/2017.
 */

public class PlacesSearchResultsListFragment extends ListFragment {

    public void setRows(List<PlaceSearchResult> rows) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        ArrayAdapter adapter = new ArrayAdapter<PlaceSearchResult>(getActivity(), R.layout.fragment_place_search_result, rows) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.fragment_place_search_result, parent, false);
                }

                PlaceSearchResult result = getItem(position);

                TextView placeNameView = (TextView)convertView.findViewById(R.id.searchResultPlaceNameText);
                placeNameView.setText(result.place.name);

                TextView addressView = (TextView)convertView.findViewById(R.id.searchResultAddressText);
                addressView.setText(result.place.address);

                TextView distanceView = (TextView)convertView.findViewById(R.id.searchResultDistanceText);
                distanceView.setText(result.distanceInMetres + "m");

                return convertView;
            }
        };
        setListAdapter(adapter);
    }
}
