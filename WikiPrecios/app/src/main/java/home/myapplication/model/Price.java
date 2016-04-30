package home.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import home.myapplication.R;
import home.myapplication.helper.Util;

/**
 * Created by Ema on 06/10/2015.
 * Esta clase representa al Precio de un producto
 * Implementa parceable para poder ser compartido entre activies
 */
public class Price implements Parcelable, Listable {
    private static final String TAG = Price.class.getSimpleName();
    private String id;
    private String price;
    private String commerce;
    private String distance;

    private int category;
    private boolean favourite;

    public Price(String id, String price, String commerce) {
        this.id = id;
        this.price = price;
        this.commerce = commerce;
        this.category = R.drawable.ic_price;
        this.favourite = false;
    }

    public Price(String id, String price, String commerce, String distance) {
        this.id = id;
        this.price = price;
        this.commerce = commerce;
        this.distance = distance;
        this.category = R.drawable.ic_price;
        this.favourite = false;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCommerce() {
        return commerce;
    }

    public void setCommerce(String commerce) {
        this.commerce = commerce;
    }

    @Override
    public String getTitle() {
        return "" + String.valueOf(price);
    }

    @Override
    public String getSubtitle() {
        if (distance.isEmpty()) {
            return commerce;
        } else {
            Log.e(TAG, "distance " + distance + " parser to " + Util.round(Double.parseDouble(distance), 3) + "km");
            return commerce + "( " + Util.round(Double.parseDouble(distance), 3) + "km )";
        }
    }

    public int getImg() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.price);
        dest.writeString(this.commerce);
        dest.writeString(this.distance);
        dest.writeInt(this.category);
        dest.writeByte(favourite ? (byte) 1 : (byte) 0);
    }

    protected Price(Parcel in) {
        this.id = in.readString();
        this.price = in.readString();
        this.commerce = in.readString();
        this.distance = in.readString();
        this.category = in.readInt();
        this.favourite = in.readByte() != 0;
    }

    public static final Creator<Price> CREATOR = new Creator<Price>() {
        public Price createFromParcel(Parcel source) {
            return new Price(source);
        }

        public Price[] newArray(int size) {
            return new Price[size];
        }
    };
}
