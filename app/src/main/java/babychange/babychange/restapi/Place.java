package babychange.babychange.restapi;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ian on 12/01/2017.
 */

public class Place implements Serializable {
    public final static String PLACE_DETAILS_EXTRA_KEY = "PLACE_DETAILS";
    public final static String PLACE_REVIEW_EXTRA_KEY = "PLACE_REVIEW";

    public String id;
    public String name;
    public String categories;
    public String address;
    public String phone;
    public GeoLocation location;
    public ArrayList<Facility> facilities;
    public OpeningHours openingHours;

}
