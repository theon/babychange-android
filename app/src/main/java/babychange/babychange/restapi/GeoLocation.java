package babychange.babychange.restapi;

import java.io.Serializable;

/**
 * Created by ian on 12/01/2017.
 */

public class GeoLocation implements Serializable {
    public double lat;
    public double lon;

    public GeoLocation() {}

    public GeoLocation(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }
}
