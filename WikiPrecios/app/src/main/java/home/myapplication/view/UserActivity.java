package home.myapplication.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import home.myapplication.R;
import home.myapplication.appL.AppConfig;
import home.myapplication.controller.HttpHandler;
import home.myapplication.controller.HttpResponseHandler;
import home.myapplication.controller.JsonParser;
import home.myapplication.model.User;
/**
 * Esta clase contiene el control de la interface que permite ver la informacion del usuario
 */
public class UserActivity extends Activity implements HttpResponseHandler {
    private static String TAG = UserActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private HttpHandler http;
    private User user;
    private Context context;

    private Button btnContinue;
    private TextView titleName;
    private TextView titleEmail;
    private TextView titleScore;
    private TextView titleHits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");
        context = this;
        titleName = (TextView) findViewById(R.id.title_name_user);
        titleEmail = (TextView) findViewById(R.id.title_email_user);
        titleScore = (TextView) findViewById(R.id.title_score_user);
        titleHits = (TextView) findViewById(R.id.title_hits_user);

        btnContinue = (Button) findViewById(R.id.btn_continue_user);
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        checkLogin(user.getMail(),user.getPassword());
    }

    private void checkLogin(final String mail, final String password) {
        // Tag used to cancel the request
        http = new HttpHandler(AppConfig.URL_LOGIN, HttpHandler.POST_REQUEST);
        http.addParams("user","'"+ mail+"'");
        http.addParams("password","'"+ password+"'");
        http.setListener(this);
        http.sendRequest();
        pDialog.setMessage(context.getString(R.string.msg_loading)+"");
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
    @Override
    public void onSuccess(Object data) {
        hideDialog();
        Log.e(TAG, "onSuccess " + data.toString());
        try {
            JSONObject json = (JSONObject) data;
            if (json.has("noUser")) {
                Toast.makeText(context,context.getString(R.string.msg_logging_error), Toast.LENGTH_LONG).show();
            } else {
                User user = JsonParser.parseToUser(json);
                titleName.setText(context.getString(R.string.title_name)+user.getName() + " " + user.getSurname());
                titleEmail.setText(context.getString(R.string.title_email)+user.getMail());
                titleScore.setText(context.getString(R.string.title_score)+user.getScore());
                titleHits.setText(context.getString(R.string.title_hits)+user.getHits());
            }
        } catch (JSONException e) {
            // JSON error
            e.printStackTrace();
            Toast.makeText(context,context.getString(R.string.msg_logging_json_error) +": " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
