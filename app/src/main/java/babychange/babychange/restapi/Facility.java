package babychange.babychange.restapi;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ian on 12/01/2017.
 */

public class Facility implements Serializable, Parcelable {
    public final static String FACILITY_BUNDLE_KEY = "FACILITY";
    public final static String FACILITY_VALUE_BUNDLE_KEY = "FACILITY_VALUE";

    public String name;
    public String queryName;
    public ArrayList<String> values;

    public Facility(Parcel source) {
        name = source.readString();
        values = new ArrayList<>();
        source.readStringList(values);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(values);
    }

    @Override
    public String toString() {
        return name;
    }

    public static final Creator<Facility> CREATOR = new Creator<Facility>() {
        @Override
        public Facility[] newArray(int size) {
            return new Facility[size];
        }

        @Override
        public Facility createFromParcel(Parcel source) {
            return new Facility(source);
        }
    };
}
