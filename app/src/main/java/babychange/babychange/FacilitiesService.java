package babychange.babychange;

import android.os.Parcelable;
import android.util.Log;

import java.util.List;

import babychange.babychange.restapi.AllowedFilters;
import babychange.babychange.restapi.Facility;
import babychange.babychange.restapi.RestApiClientHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ian on 18/01/2017.
 */

public class FacilitiesService {

    public interface OnFacilitiesCallback {
        void onFacilities(List<Facility> facilities);
        void onFailure(Throwable t);
    }

    private static volatile AllowedFilters allowedFilters = null;

    public static void getAllowedFacilities(final OnFacilitiesCallback callback) {
        if(allowedFilters != null) {
            callback.onFacilities(allowedFilters.facility);
        } else {
            updateAllowedFacilities(callback);
        }
    }


    private static void updateAllowedFacilities(final OnFacilitiesCallback callback) {
        Call<AllowedFilters> response = RestApiClientHolder.restClient.getAllowedFilters();
        response.enqueue(new Callback<AllowedFilters>() {
            @Override
            public void onResponse(Call<AllowedFilters> call, Response<AllowedFilters> response) {
                allowedFilters = response.body();
                callback.onFacilities(allowedFilters.facility);
            }

            @Override
            public void onFailure(Call<AllowedFilters> call, Throwable t) {
                Log.e("TAG", "Failed to get list of allowed filters", t);
                callback.onFailure(t);
            }
        });
    }
}
