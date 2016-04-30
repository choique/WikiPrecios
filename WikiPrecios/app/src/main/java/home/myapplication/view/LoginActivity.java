package home.myapplication.view;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import home.myapplication.R;
import home.myapplication.appL.AppConfig;
import home.myapplication.controller.HttpHandler;
import home.myapplication.controller.HttpResponseHandler;
import home.myapplication.controller.JsonParser;
import home.myapplication.helper.SessionManager;
import home.myapplication.model.User;

/**
 * LoginActivity controla la vista que permite a los usuarios logearce en la app
 */
public class LoginActivity extends Activity implements HttpResponseHandler {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;

    private ProgressDialog pDialog;

    private SessionManager session;
    private HttpHandler http;

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        context = this;

        inputEmail = (EditText) findViewById(R.id.email_login);
        inputPassword = (EditText) findViewById(R.id.password_login);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLinkToRegister = (Button) findViewById(R.id.btn_link_to_register_screen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());
//        session.setLogin(false, "","");

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Log.e(TAG, "isLoged");
            String mail = session.getUserLoged();
            String password = session.getUserPassword();
//            Si el usuario se encuentra logeado, se inicia la sesion
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            User user = new User(mail,password);
            Log.e(TAG, "Json User: " + user);
            // Launch main activity
            intent.putExtra("user", user);
            startActivity(intent);
            finish();

        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String mail = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!mail.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(mail, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(context,context.getString(R.string.msg_logging_enter_credential),
                            Toast.LENGTH_LONG).show();
                }
            }

        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(context, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * function to verify login details in mysql db (wikiprecios)
     */
    private void checkLogin(final String mail, final String password) {

        http = new HttpHandler(AppConfig.URL_LOGIN, HttpHandler.POST_REQUEST);
        http.addParams("user","'"+ mail+"'");
        http.addParams("password","'"+ password+"'");
        http.setListener(this);
        http.sendRequest();
        pDialog.setMessage(context.getString(R.string.msg_logging));
        showDialog();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * LoginActivity implementa HttpResponseHandler, por lo que LoginActivity sera la ocupara de
     * recibir la informacion enviada por el sistema, en este caso la informacion del usuario a logear
     * y si este pertenece o no al sistema
     * @param data informacion en formato json enviado por la url
     */
    @Override
    public void onSuccess(Object data) {
        hideDialog();
        Log.e(TAG, "onSuccess " + data.toString());
        try {
            JSONObject json = (JSONObject) data;
            if (json.has("noUser")) {
                Toast.makeText(context, context.getString(R.string.msg_logging_error), Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                User user = JsonParser.parseToUser(json);
                Log.e(TAG, "Json User: " + user);
                session.setLogin(true, user.getMail(),user.getPassword());

                // Launch main activity
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            Toast.makeText(context,context.getString(R.string.msg_logging_json_error)+ e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}