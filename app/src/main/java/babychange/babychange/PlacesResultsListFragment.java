package babychange.babychange;

import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import babychange.babychange.restapi.Place;
import babychange.babychange.restapi.PlaceSearchResult;

/**
 * Created by ian on 09/01/2017.
 */

public class PlacesResultsListFragment extends ListFragment {

    public void setRows(List<PlaceSearchResult> rows) {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        ArrayAdapter adapter = new ArrayAdapter<PlaceSearchResult>(getActivity(), R.layout.fragment_place_layout, rows) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.fragment_place_layout, parent, false);
                }

                PlaceSearchResult result = getItem(position);

                TextView placeNameView = (TextView)convertView.findViewById(R.id.placeNameText);
                placeNameView.setText(result.place.name);

                TextView addressView = (TextView)convertView.findViewById(R.id.addressText);
                addressView.setText(result.place.address);

                TextView distanceView = (TextView)convertView.findViewById(R.id.distanceText);
                distanceView.setText(result.distanceInMetres + "m");

                return convertView;
            }
        };
        setListAdapter(adapter);
    }
}
