package home.myapplication.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import home.myapplication.R;
import home.myapplication.appL.AppConfig;
import home.myapplication.controller.HttpHandler;
import home.myapplication.controller.HttpResponseHandler;
import home.myapplication.controller.JsonParser;
import home.myapplication.model.Commerce;
import home.myapplication.model.Price;
import home.myapplication.model.Query;
import home.myapplication.model.User;

//import android.support.v7.app.AppCompatActivity;
/**
 * Esta clase contiene el control de la interface de precio, que permite setear un nuevo precio a un
 * producto.
 */
public class PriceActivity extends Activity implements HttpResponseHandler {

//    private Session session;
    private Context context;
    private Commerce commerce;
    private EditText inputPrice;
    private User user;
    private Query query;

    private TextView titleQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        context = this;
//        obtiene el usuario, la consulta, y el comercio.
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");
        query = b.getParcelable("query");
        commerce = b.getParcelable("commerce");

        inputPrice = (EditText) findViewById(R.id.price_price);
        titleQuery = (TextView) findViewById(R.id.title_query_price);
        TextView input = (TextView) findViewById(R.id.price_price);
        input.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        titleQuery.setText(context.getString(R.string.title_name_commerce) + commerce.getTitle() + "\n"+context.getString(R.string.title_name_barcode) + query.getBarcode());

        findViewById(R.id.btn_continue_price).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getPrice() == null) {
                    Toast notificacion = Toast.makeText(context,context.getString(R.string.msg_enter_price), Toast.LENGTH_LONG);
                    notificacion.show();
                } else {
                    Log.e("PriceActivity", " Send Request");
                    sendRequest();
                }
            }
        });
        findViewById(R.id.btn_cancel_price).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Se ocupa de realizar la consulta, que permite al usuario ver los precios del producto buscado en los demas comercios.
     */
    private void sendRequest() {
        HttpHandler http = new HttpHandler(AppConfig.URL_PRICES_SAVE, HttpHandler.GET_REQUEST);
        http.addParams("comercio", String.valueOf(commerce.getId()));
        http.addParams("precio", String.valueOf(getPrice()));
        http.addParams("producto", query.getBarcode());
        http.addParams("usuario", "'" + user.getMail() + "'");
        http.addParams("latitud", String.valueOf(query.getLocation().getLatitude()));
        http.addParams("longitud", String.valueOf(query.getLocation().getLongitude()));
        http.setListener(this);
        http.sendRequest();
    }

    /**
     * Obtiene el precio ingresado por el usuario, si es invalido retorna null
     * @return
     */
    private Double getPrice() {
        if (inputPrice.getText().toString().isEmpty()) {
            return null;
        }
        Double p;
        try {
            p=Double.parseDouble(inputPrice.getText().toString());
        }catch (Exception e){
            return null;
        }
        return p;
    }

    /**
     * Retorna true si el string ingresado es invalido.
     * @param data
     * @return
     */
    private boolean resultIsEmpty(String data) {
        JSONArray json = null;
        List lst = null;
        try {
            json = new JSONArray(data);
            lst = JsonParser.getList(Price.class, json);
            Log.e("PriceAC"," result : "+lst);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lst.isEmpty();
    }

    /**
     * En caso de que la consulta sea exitosa, se procesa la info recibida.
     * @param data informacion recibida
     */
    @Override
    public void onSuccess(Object data) {
        Log.e("PriceAC", "sucess PriceActivity");
        if(resultIsEmpty(data.toString())){// si no se cuentran resultados, indica que se creo un nuevo producto
            //que no existia en ningun comercio
            Log.e("PriceAc","sucess PriceActivity");
            Toast.makeText(context,context.getString(R.string.msg_new_product),Toast.LENGTH_LONG).show();
        }else{
            //muestra los precios del producto en los demas comercios
            Intent j = new Intent(context, PriceListViewActivity.class);
            query.setPrice(getPrice());
            //comparte la consulta y los precios
            j.putExtra("query", query);
            j.putExtra("precios", data.toString());
            Log.e("onSuccess  --> ", data.toString());
            finish();
            // inicia la vista de Precios.
            context.startActivity(j);
        }
    }
}
