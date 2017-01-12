package babychange.babychange.restapi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ian on 12/01/2017.
 */

public class RestApiFilter implements Parcelable {

    public static final String ENABLED_FILTERS_EXTRA_KEY = "ENABLED_FILTERS";

    private String name;
    private String value;

    public RestApiFilter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public RestApiFilter(Parcel source) {
        this.name = source.readString();
        this.value = source.readString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RestApiFilter) {
            RestApiFilter other = (RestApiFilter) obj;
            return name.equals(other.name) && value.equals(other.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return name + ":" + value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(value);
    }

    public static final Creator<RestApiFilter> CREATOR = new Creator<RestApiFilter>() {
        @Override
        public RestApiFilter[] newArray(int size) {
            return new RestApiFilter[size];
        }

        @Override
        public RestApiFilter createFromParcel(Parcel source) {
            return new RestApiFilter(source);
        }
    };
}
