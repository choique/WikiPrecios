package home.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ema on 31/10/2015.
 * Esta clase representa a un usuario de la app
 * Implementa parceable para poder ser compartido entre activies
 */
public class User implements Parcelable {
    private int id;
    private String name;
    private String surname;
    private String mail;
    private String password;
    private int score;
    private int hits;

    public User(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    public User(int id, String name, String surname, String mail, String password, int score, int hits) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.password = password;
        this.score = score;
        this.hits = hits;
    }

    public User( String name, String surname, String mail, String password, int score, int hits) {
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.password = password;
        this.score = score;
        this.hits = hits;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", score=" + score +
                ", hits=" + hits +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.surname);
        dest.writeString(this.mail);
        dest.writeString(this.password);
        dest.writeInt(this.score);
        dest.writeInt(this.hits);
    }

    protected User(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.surname = in.readString();
        this.mail = in.readString();
        this.password = in.readString();
        this.score = in.readInt();
        this.hits = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
