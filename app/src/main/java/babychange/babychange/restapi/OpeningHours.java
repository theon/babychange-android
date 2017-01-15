package babychange.babychange.restapi;

import java.io.Serializable;

/**
 * Created by ian on 12/01/2017.
 */

public class OpeningHours implements Serializable {
    public DayOpeningHours mon;
    public DayOpeningHours tue;
    public DayOpeningHours wed;
    public DayOpeningHours thu;
    public DayOpeningHours fri;
    public DayOpeningHours sat;
    public DayOpeningHours sun;
}
