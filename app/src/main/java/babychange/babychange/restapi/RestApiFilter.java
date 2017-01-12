package babychange.babychange.restapi;

/**
 * Created by ian on 12/01/2017.
 */

public class RestApiFilter {
    private String uiName;

    private String name;
    private String value;

    public RestApiFilter(String uiName, String name, String value) {
        this.uiName = uiName;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }
}
