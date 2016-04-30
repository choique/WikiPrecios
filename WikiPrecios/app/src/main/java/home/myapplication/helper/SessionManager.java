package home.myapplication.helper;

/**
 * Created by Ema on 27/10/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Clase auxiliar que permite controlar las sesiones que se mantiene abiertas en la app.
 */
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_USER_LOGGED = "userLoged";
    private static final String KEY_USER_PASSWORD = "userPassword";

    /**
     * Contructor de clase
     * @param context
     */
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Guarda la informacion del ultimo usuario logeado a la app, sirve a la app para mentener una
     * session de usuario abierta.
     * @param isLoggedIn si existe un usuario logeado
     * @param userLoged el usuario logeado
     * @param userPassword password del usuario
     */
    public void setLogin(boolean isLoggedIn,String userLoged,String userPassword) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString(KEY_USER_LOGGED, userLoged);
        editor.putString(KEY_USER_PASSWORD, userPassword);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified!");
    }

    public String getUserLoged(){
        return pref.getString(KEY_USER_LOGGED,null);
    }

    public String getUserPassword(){
        return pref.getString(KEY_USER_PASSWORD,null);
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}