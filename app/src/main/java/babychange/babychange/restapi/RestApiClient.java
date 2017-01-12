package babychange.babychange.restapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApiClient {
    @GET("places/{lat},{lon}")
    Call<PlaceSearchResults> findPlaces(@Path("lat") double lat,
                                        @Path("lon") double lon,
                                        @Query("facility")List<RestApiFilter> facilities);

    @GET("filters")
    Call<AllowedFilters> getAllowedFilters();
}
