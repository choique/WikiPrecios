package home.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import home.myapplication.R;

/**
 * Created by Ema on 10/11/2015.
 * Esta clase representa a una categoria de productos especiales, dichas categorias solo permiten sin codigos de
 * barras.
 */
public class Category implements Parcelable,Listable {
    private int id;
    private String initials;
    private String name;
    private int img;
    public Category(int id, String letra, String nombre) {
        this.id = id;
        this.initials = letra;
        this.name = nombre;
        this.img =  R.drawable.ic_space;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.initials);
        dest.writeString(this.name);
    }

    protected Category(Parcel in) {
        this.id = in.readInt();
        this.initials = in.readString();
        this.name = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public String getTitle() {
        return initials;
    }

    @Override
    public String getSubtitle() {
        return name;
    }

    @Override
    public int getImg() {
        return img;
    }

    @Override
    public boolean isFavourite() {
        return false;
    }
}
