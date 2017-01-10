package babychange.babychange;

import android.app.ListFragment;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by ian on 09/01/2017.
 */

public class PlacesResultsListFragment extends ListFragment {

    public void setRows(List<Map<String,String>> rows) {
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), rows, R.layout.fragment_place_layout, new String[] {"placeName", "address", "distance"}, new int[] { R.id.placeNameText, R.id.addressText, R.id.distanceText });
        setListAdapter(adapter);
    }
}
