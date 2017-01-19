package babychange.babychange.restapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestApiClient {
    @GET("places/{lat},{lon}")
    Call<PlaceSearchResults> findPlaces(@Path("lat") double lat,
                                        @Path("lon") double lon,
                                        @Query("facility")List<RestApiFilter> facilities);

    @POST("places")
    Call<BabyPlace> createPlace(@Body BabyPlace place);

    @GET("filters")
    Call<AllowedFilters> getAllowedFilters();

    @GET("reviews")
    Call<ReviewResults> findReviews(@Query("place") String place);

    @POST("reviews")
    Call<Review> createReview(@Body NewReview review);
}
