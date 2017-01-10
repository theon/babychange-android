package babychange.babychange;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by ian on 10/01/2017.
 */

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.start);
    }

    public void searchForPlaces(View view) {
        Intent intent = new Intent(this, PlacesResultsActivity.class);
        startActivity(intent);
    }
}
