package babychange.babychange.restapi;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by ian on 15/01/2017.
 */

public class Review implements Parcelable {
    public float rating;
    public String date;
    public String place;
    public GeoLocation placeLocation;
    public List<String> facilities;
    public String review;
    public String user;

    private static final SimpleDateFormat DATE_FORMAT = createDateFormat();

    public Review() { }

    protected Review(Parcel in) {
        rating = in.readFloat();
        date = in.readString();
        place = in.readString();
        facilities = in.createStringArrayList();
        review = in.readString();
        user = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    private static SimpleDateFormat createDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format;
    }

    public void setDate(Date d) {
        date = DATE_FORMAT.format(d);
    }

    private String durationString(int amount, String unit) {
        if(amount == 1) return amount + " " + unit + " ago";
        else            return amount + " " + unit + "s ago";
    }

    public String howLongAgo() {
        try {
            Date reviewDate = DATE_FORMAT.parse(date);
            long millisNow = new GregorianCalendar(TimeZone.getTimeZone("UTC")).getTime().getTime();
            long secondsAgo = (millisNow - reviewDate.getTime()) / 1000;
            float daysAgo = (float)secondsAgo / 60 / 60 / 24;

            if(daysAgo < 1) {
                return "today";
            }
            else if(daysAgo < 7) {
                return durationString((int)daysAgo, "day");
            } else if (daysAgo < 29) {
                float weeksAgo = daysAgo / 7;
                return durationString((int)weeksAgo, "week");
            } else if(daysAgo < 365) {
                //TODO: This doesn't work..... better way?
                float monthsAgo = daysAgo / 30.4f;
                return durationString(Math.max(1, (int)monthsAgo), " month");
            } else {
                float yearsAgo = daysAgo / 365;
                return durationString(Math.max(1, (int)yearsAgo), " year");
            }


        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeFloat(rating);
        dest.writeString(date);
        dest.writeString(place);
        dest.writeStringList(facilities);
        dest.writeString(review);
        dest.writeString(user);
    }
}
