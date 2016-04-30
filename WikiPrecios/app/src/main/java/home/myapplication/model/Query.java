package home.myapplication.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ema on 31/10/2015.
 * Esta clase representa a una consulta, una consulta suele contener los atributos necesarios
 * para cargar un nuevo precio de un producto, y poder realizar la consulta del mismo precio en otros
 * comercios.
 */
public class Query implements Parcelable {

    private int id;
    private String barcode;
    private Location location;
    private Double price;

    public Query() {
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.barcode);
        dest.writeParcelable(this.location, 0);
        dest.writeValue(this.price);
    }

    protected Query(Parcel in) {
        this.id = in.readInt();
        this.barcode = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());
        this.price = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<Query> CREATOR = new Creator<Query>() {
        public Query createFromParcel(Parcel source) {
            return new Query(source);
        }

        public Query[] newArray(int size) {
            return new Query[size];
        }
    };
}
