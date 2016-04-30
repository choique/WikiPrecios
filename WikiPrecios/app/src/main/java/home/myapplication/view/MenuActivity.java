package home.myapplication.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import home.myapplication.R;
import home.myapplication.helper.SessionManager;
import home.myapplication.model.User;

/**
 * Esta clase contiene el control de la interface del menu principal de la app
 */
public class MenuActivity extends Activity {
    private Button btnUser;
    private Button btnCamera;
    private Button btnSpecialProduct;
    private Button btnFavourite;
    private Button btnLogout;
    private Button btnExit;

    private User user;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
//        obtiene al usuario logeado
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");

        btnUser = (Button) findViewById(R.id.btn_user_menu);
        btnCamera = (Button) findViewById(R.id.btn_camera_menu);
        btnSpecialProduct = (Button) findViewById(R.id.btn_special_product_menu);
        btnFavourite = (Button) findViewById(R.id.btn_favourite_menu);
        btnLogout = (Button) findViewById(R.id.btn_logout_menu);
        btnExit = (Button) findViewById(R.id.btn_exit_menu);

        // session manager
        session = new SessionManager(getApplicationContext());
        // si el usuario no esta logeado, cierra la sesion
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewActivity(UserActivity.class);
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewActivity(CameraActivity.class);
            }
        });
        btnSpecialProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewActivity(CategoryListViewActivity.class);
            }
        });
        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewActivity(FavouriteTabbedListViewActivity.class);
            }
        });


    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false, "error","error");
        // Launching the login activity
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Inicializa una nueva activity
     * @param activity
     */
    private void viewActivity(Class activity){
        Intent intent = new Intent(MenuActivity.this,activity);
        intent.putExtra("user", user);
        startActivity(intent);
      }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_BACK) {
            //code
            AlertDialog.Builder showPlace = new AlertDialog.Builder(
                    MenuActivity.this);
            showPlace.setMessage("Esta seguro que desea salir?");
            showPlace.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            showPlace.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            showPlace.show();

        }
        return super.onKeyDown(keyCode, event);
    }


}