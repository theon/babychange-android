package babychange.babychange.restapi;

import retrofit2.Retrofit;
import retrofit2.adapter.guava.GuavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ian on 15/01/2017.
 */

public class RestApiClientHolder {
    public static RestApiClient restClient = createClient();
    private static RestApiClient createClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.5:8080")
                .addCallAdapterFactory(GuavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RestApiClient.class);
    }
}
