package babychange.babychange;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import babychange.babychange.restapi.RestApiFilter;

import static babychange.babychange.restapi.RestApiFilter.ENABLED_FILTERS_EXTRA_KEY;

/**
 * Created by ian on 10/01/2017.
 */

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_start);
    }

    public void searchForPlaces(View view) {

        ArrayList<RestApiFilter> enabledFilters = new ArrayList<>();

        switch(view.getId()) {
            case R.id.babyChangeButton:
                enabledFilters.add(new RestApiFilter("babyChanging", "Yes"));
                break;
            //TODO: Do other buttons
        }

        Intent intent = new Intent(this, PlacesSearchActivity.class);
        intent.putParcelableArrayListExtra(ENABLED_FILTERS_EXTRA_KEY, enabledFilters);
        startActivity(intent);
    }
}
