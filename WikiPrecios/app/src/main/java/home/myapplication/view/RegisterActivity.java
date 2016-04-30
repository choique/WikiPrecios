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

import org.json.JSONException;
import org.json.JSONObject;

import home.myapplication.R;
import home.myapplication.appL.AppConfig;
import home.myapplication.controller.HttpHandler;
import home.myapplication.controller.HttpResponseHandler;
import home.myapplication.helper.SessionManager;
/**
 * Esta clase contiene el control de la interface de registracion, que permite a los usuarios registrarce en la app
 */
public class RegisterActivity extends Activity implements HttpResponseHandler {
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputName;
    private EditText inputSurname;

    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private Context context;
    private HttpHandler http;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        context = this;
        inputName = (EditText) findViewById(R.id.name_register);
        inputSurname = (EditText) findViewById(R.id.surname_register);
        inputEmail = (EditText) findViewById(R.id.email_register);
        inputPassword = (EditText) findViewById(R.id.password_register);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnLinkToLogin = (Button) findViewById(R.id.btn_link_to_login_screen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(context);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(context,MenuActivity.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputName.getText().toString().trim();
                String surname = inputSurname.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                Log.e("RegisterActivity:" ,"surname: " +surname+"   name: "+name+"   password: "+ password+"  email:  "+email);
                if (!name.isEmpty() && !surname.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(surname,name, email, password);
                } else {
                    Toast.makeText(context,context.getString(R.string.msg_register_enter_credential), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(context,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(String surname,String name,String email,String password) {

        http = new HttpHandler(AppConfig.URL_REGISTER2, HttpHandler.GET_REQUEST);
        http.addParams("name", "'"+name+"'");
        http.addParams("surname","'"+surname+"'");
        http.addParams("mail","'"+email+"'");
        http.addParams("password","'"+password+"'");
        http.setListener(this);
        http.sendRequest();
        pDialog.setMessage(context.getString(R.string.msg_registering));
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
     * En caso de que la consulta sea exitosa, se procesa la info recibida.
     * @param data informacion recibida
     */
    @Override
    public void onSuccess(Object data) {
        hideDialog();
        Log.e("RegisterActivity", data.toString());
//        {"mensaje":"Usuario registrado con exito","registrado":true}
//        {"mensaje":"El mail ingresado ya existe","registrado":false}
        try {
            JSONObject json = (JSONObject) data;
             boolean isRegister = json.getBoolean("registrado");
            if (!isRegister) {
                Toast.makeText(context, json.getString("mensaje"), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context,json.getString("mensaje")+", "+ context.getString(R.string.msg_register_try_login),Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.msg_register_json_error) + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}