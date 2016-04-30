package home.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import home.myapplication.R;
import home.myapplication.helper.Util;

/**
 * Created by Ema on 03/10/2015.
 * Esta clase representa a un comercio, o punto de venta
 * Implementa parceable para poder ser compartido entre activies
 */
public class Commerce implements Parcelable, Listable {
    private static final String TAG = Commerce.class.getSimpleName();
    private int id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String address;
    private boolean favourite;
    private String distance;
    private int category;

    public Commerce(int id, String name, Double latitude, Double longitude, String address) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.category = R.drawable.ic_commerce;
        this.favourite = false;
    }

    public Commerce(int id, String name, String address, boolean f, String d) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.category = R.drawable.ic_commerce;
        this.favourite = f;
        this.distance = d;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Commerce(String name, String address, int category) {
        this.name = name;
        this.address = address;
        this.category = category;
        this.favourite = false;
    }

    public Commerce(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSubtitle() {
        if (distance.isEmpty()) {
            return address;
        } else {
            Log.e(TAG, "distance " + distance + " paser to " + Util.round(Double.parseDouble(distance), 3) + "km");
            return address + "( " + Util.round(Double.parseDouble(distance), 3) + "km )";
        }
    }

    public int getImg() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String toString() {
        return name + "," + address + " ," + favourite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.latitude);
        dest.writeValue(this.longitude);
        dest.writeString(this.address);
        dest.writeByte(favourite ? (byte) 1 : (byte) 0);
        dest.writeString(this.distance);
        dest.writeInt(this.category);
    }

    protected Commerce(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.address = in.readString();
        this.favourite = in.readByte() != 0;
        this.distance = in.readString();
        this.category = in.readInt();
    }

    public static final Creator<Commerce> CREATOR = new Creator<Commerce>() {
        public Commerce createFromParcel(Parcel source) {
            return new Commerce(source);
        }

        public Commerce[] newArray(int size) {
            return new Commerce[size];
        }
    };
}