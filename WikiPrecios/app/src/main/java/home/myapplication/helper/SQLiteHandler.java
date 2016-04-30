package home.myapplication.helper;

/**
 * Created by Ema on 27/10/2015.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import home.myapplication.model.User;

/**
 * Esta clase se ocupaba para controlar una conexion a la base de datos SQLite
 * pero se encuentra sin usar, debido a que su implementacion fue innecesaria durante el desarrollo
 * Pero esta clase puede implementarce perfectamente.
 */
public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DB3";

    // Tables name
    private static final String TABLE_USER = "user";
    private static final String TABLE_COMMERCE = "commerce";
    private static final String TABLE_FAVOURITE = "favourites";

    // User Table Columns names
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_SURNAME = "surname";
    private static final String KEY_USER_MAIL = "mail";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_USER_SCORE = "score";
    private static final String KEY_USER_HITS = "hits";


    // Commerce Table Columns names
    private static final String KEY_COMMERCE_ID = "id";
    private static final String KEY_COMMERCE_NAME = "name";
    private static final String KEY_COMMERCE_LATITUDE = "latitude";
    private static final String KEY_COMMERCE_LONGITUDE = "longitude";
    private static final String KEY_COMMERCE_ADDRESS = "address";

    // Favourite Table Columns names
    private static final String KEY_FAVOURITE_ID = "id";
    private static final String KEY_USER_FK = "idUser";
    private static final String KEY_COMMERCE_FK = "idCommerce";

    //Creating User Table
    private static final String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + KEY_USER_ID + " INTEGER PRIMARY KEY," + KEY_USER_NAME + " TEXT," + KEY_USER_SURNAME + " TEXT,"
            + KEY_USER_MAIL + " TEXT UNIQUE," + KEY_USER_PASSWORD + " TEXT,"
            + KEY_USER_SCORE + " INTEGER," + KEY_USER_HITS + " INTEGER"
            + ")";
    //Creating Commerce Table
    private static final String CREATE_COMMERCE_TABLE = "CREATE TABLE " + TABLE_COMMERCE + "("
            + KEY_COMMERCE_ID + " INTEGER PRIMARY KEY," + KEY_COMMERCE_NAME + " TEXT," + KEY_COMMERCE_LATITUDE + " TEXT,"
            + KEY_COMMERCE_LONGITUDE + " TEXT," + KEY_COMMERCE_ADDRESS + " TEXT"
            + ")";
    //Creating Fauvorite Table
    private static final String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE + "("
            + KEY_FAVOURITE_ID + " INTEGER PRIMARY KEY," + KEY_USER_FK + " INTEGER," + KEY_COMMERCE_FK + " INTEGER"
            + ")";


    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_COMMERCE_TABLE);
        db.execSQL(CREATE_FAVOURITE_TABLE);

        Log.e(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMERCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE);
        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = putUser(user);
        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

//        Log.e(TAG, "New user inserted into sqlite: " + id);
    }
    public ContentValues putUser(User user){
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName()); // Name
        values.put(KEY_USER_SURNAME, user.getSurname()); // Surname
        values.put(KEY_USER_MAIL, user.getMail()); // Email
        values.put(KEY_USER_PASSWORD, user.getPassword()); // Password
        values.put(KEY_USER_SCORE, user.getScore()); // Password
        values.put(KEY_USER_HITS, user.getHits()); // Password
        return values;
    }

    /**
     * Getting user data from database
     * @param cursor
     * @return a user
     */
    public User getUserFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(KEY_USER_ID));
        String name = cursor.getString(cursor.getColumnIndex(KEY_USER_NAME));
        String surname = cursor.getString(cursor.getColumnIndex(KEY_USER_SURNAME));
        String mail = cursor.getString(cursor.getColumnIndex(KEY_USER_MAIL));
        String password = cursor.getString(cursor.getColumnIndex(KEY_USER_PASSWORD));
        int score = cursor.getInt(cursor.getColumnIndex(KEY_USER_SCORE));
        int hits = cursor.getInt(cursor.getColumnIndex(KEY_USER_HITS));

        return new User(id, name, surname, mail, password, score, hits);
    }
    /**
     * Getting first user data from database
     */
    public User getUserDetails() {
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        User user = null;
        if (cursor.getCount() > 0) {
            user = getUserFromCursor(cursor);
        }
        cursor.close();
        db.close();
        // return user
        Log.e(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }


    public User getUser(String mail) {
        String selectQuery = "SELECT  * FROM " + TABLE_USER
                              +" WHERE "+ KEY_USER_MAIL + " = '" + mail+"' ";
//        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        User user= null;
        if (cursor.getCount() == 0) {
          return null;
        }
        cursor.moveToFirst();
        Log.e(TAG, "cursor : " + cursor.getCount());
        if (cursor.getCount() > 0) {
            user = getUserFromCursor(cursor);
        }

        cursor.close();
        db.close();
        // return user
        Log.e(TAG, "Fetching user from Sqlite: " + user.toString());
        return user;
    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = putUser(user);
        // updating row
        return db.update(TABLE_USER, values, KEY_USER_ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }


    public List<User> getAllUser() {
        List<User> users = new ArrayList<User>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                User td = getUserFromCursor(c);
                Log.e(TAG,td.toString());
                // adding to task list
                users.add(td);
            } while (c.moveToNext());
        }
        return users;
    }
    /**
     * Re crate database Delete all tables and create them again
     */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.e(TAG, "Deleted all user info from sqlite");
    }

}