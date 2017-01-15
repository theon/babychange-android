package babychange.babychange.restapi;

import java.util.List;

/**
 * Created by ian on 12/01/2017.
 */

public class AllowedFilters {
    public List<AllowedFilter> facility;

    public class AllowedFilter {
        public String name;
        public String queryName;
        public List<String> allowedValues;
    }
}
