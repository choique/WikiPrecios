package home.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import home.myapplication.R;

/**
 * Created by Ema on 16/11/2015.
 * Esta clase representa a un producto especial, un producto especial pertenece a una categoria de productos especiales,
 * un producto especia no tiene un codigo de barras estandar, por ej: frutas, verduras, carnes, etc
 */
public class SpecialProduct implements Parcelable,Listable {
    private static final String TAG = SpecialProduct.class.getSimpleName();
    private String id;
    private String product;
    private int realCategory;

    private int category;
    private boolean favourite;

    public SpecialProduct(String id, String product, int realCategory) {
        this.id = id;
        this.product = product;
        this.realCategory = realCategory;
        favourite = false;
        category = R.drawable.ic_space;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getRealCategory() {
        return realCategory;
    }

    public void setRealCategory(int realCategory) {
        this.realCategory = realCategory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.product);
        dest.writeInt(this.realCategory);
        dest.writeInt(this.category);
        dest.writeByte(favourite ? (byte) 1 : (byte) 0);
    }

    public SpecialProduct() {
    }

    protected SpecialProduct(Parcel in) {
        this.id = in.readString();
        this.product = in.readString();
        this.realCategory = in.readInt();
        this.category = in.readInt();
        this.favourite = in.readByte() != 0;
    }

    public static final Creator<SpecialProduct> CREATOR = new Creator<SpecialProduct>() {
        public SpecialProduct createFromParcel(Parcel source) {
            return new SpecialProduct(source);
        }

        public SpecialProduct[] newArray(int size) {
            return new SpecialProduct[size];
        }
    };

    @Override
    public String getTitle() {
        return product;
    }

    @Override
    public String getSubtitle() {
        return id;
    }

    @Override
    public int getImg() {
        return category;
    }

    @Override
    public boolean isFavourite() {
        return favourite;
    }

    @Override
    public String toString() {
        return product ;
    }
}
