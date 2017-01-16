package babychange.babychange.restapi;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ian on 16/01/2017.
 */
public class NewReview implements Parcelable {
    public float rating;
    public String place;
    public List<String> facilities;
    public String review;

    public NewReview(Parcel in) {
        rating = in.readFloat();
        place = in.readString();
        facilities = in.createStringArrayList();
        review = in.readString();
    }

    public static final Creator<NewReview> CREATOR = new Creator<NewReview>() {
        @Override
        public NewReview createFromParcel(Parcel in) {
            return new NewReview(in);
        }

        @Override
        public NewReview[] newArray(int size) {
            return new NewReview[size];
        }
    };

    public NewReview() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(rating);
        dest.writeString(place);
        dest.writeStringList(facilities);
        dest.writeString(review);
    }
}
