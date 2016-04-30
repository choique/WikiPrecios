package home.myapplication.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import home.myapplication.R;
import home.myapplication.appL.AppConfig;
import home.myapplication.controller.HttpHandler;
import home.myapplication.controller.HttpResponseHandler;
import home.myapplication.controller.JsonParser;
import home.myapplication.model.Commerce;
import home.myapplication.model.Query;
import home.myapplication.model.User;

/**
 * Esta clase contiene el control de la interface de la lista de comercios. Permite seleccionar un comercio
 * de una lista de comercios
 */
public class CommerceListViewActivity extends Activity implements HttpResponseHandler {

    ListView list;
    ArrayAdapter adapter;
    private Context context;

    private Commerce commerce;
    private User user;
    private Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_commerce);
        context = this;
//    obtiene el usuario y el consulta compartida
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");
        query = b.getParcelable("query");

        JSONArray json = null;
        List lst = new ArrayList();
        // intenta leer la lista de comercios recibidos, del activity padre.
        try {
            json = new JSONArray(getIntent().getStringExtra("data"));
            lst = JsonParser.getList(Commerce.class, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Instancia del ListView
        list = (ListView) findViewById(R.id.list_commerce);

        //Inicializar el adapter con la fuente de datos
        adapter = new ItemArrayAdapter(this, lst);

        //Relacionando la list con el adapter
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                commerce = (Commerce) adapter.getItem(position);
                sendRequest();
            }
        });

    }

    /**
     * Realiza la peticion de los precios mas votados por los usuarios, del producto buscado, en el comercio,
     * seleccionado por el usuario.
     */
    private void sendRequest() {
        HttpHandler http = new HttpHandler(AppConfig.URL_PRICES_SUG, HttpHandler.GET_REQUEST);
        http.addParams("comercio", String.valueOf(commerce.getId()));
        http.addParams("producto", query.getBarcode());
        http.setListener(this);
        http.sendRequest();
    }

    /**
     * Retorna true en caso de que la entrada sea vacia.
     * @param data
     * @return
     */
    private boolean resultIsEmpty(String data) {
        JSONArray json = null;
        List lst = null;
        try {
            json = new JSONArray(data);
            lst = JsonParser.getList(Query.class, json);
            Log.e("ComercioLV"," result : "+lst);
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
        Intent j;
        Log.e("ComercioLV","data  : "+data.toString());
//        SI LA lista de precios sugeridos se encuentra vacia, permite inicializar un nuevo precio.
        if (resultIsEmpty(data.toString())) {
            j = new Intent(context, PriceActivity.class);
            Log.e("ComercioLV","Lista Vacia");
        } else {
//            si la lista de precios sugeridos no se encuentra vacia, muestra la lista de precios sugeridos.
            j = new Intent(context, SuggestedPriceListViewActivity.class);
//            comparte la lista de precios.
            j.putExtra("precios", data.toString());
            Log.e("ComercioLV", "Lista Con Elementos");
        }
//        en cualquier caso comparte el usuario, la consulta, y el comercio.
        j.putExtra("user", user);
        j.putExtra("query", query);
        j.putExtra("commerce", commerce);
        finish();
//        inializa la activity correspondiente.
        context.startActivity(j);
    }
}