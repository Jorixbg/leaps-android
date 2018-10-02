package club.leaps.networking.test;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Ivan on 10/16/2017.
 */

public class Coordinates implements Serializable {


    @SerializedName("coord_lat")
    private double latitude;
    @SerializedName("coord_lnt")
    private double longitude;

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
