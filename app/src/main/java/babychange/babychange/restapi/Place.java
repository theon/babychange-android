package babychange.babychange.restapi;

import java.util.List;
import java.util.Map;

/**
 * Created by ian on 12/01/2017.
 */

public class Place {
    public String name;
    public String categories;
    public String address;
    public String phone;
    public GeoLocation location;
    public Map<String, List<String>> facilities;
    public OpeningHours openingHours;
}
