package home.myapplication.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import home.myapplication.R;
import home.myapplication.appL.AppConfig;
import home.myapplication.controller.HttpHandler;
import home.myapplication.controller.HttpResponseHandler;
import home.myapplication.controller.JsonParser;
import home.myapplication.model.Category;
import home.myapplication.model.Listable;
import home.myapplication.model.Query;
import home.myapplication.model.SpecialProduct;
import home.myapplication.model.User;
import home.myapplication.service.LocationService;
/**
 * Esta clase contiene el control de la interface que muestra la lista de productos especiales, permite
 * seleccionar un elemento de la lista.
 */
public class SpecialProductListViewActivity extends Activity implements HttpResponseHandler {

    ListView list;
    ArrayAdapter adapter;
    private Context context;
    private TextView input;

    List all;

    private User user;
    private Category category;
    private SpecialProduct specialProduct;
    private Location location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_special_product_list_view);
        all = null;
        context = this;
//        obtiene el usuario y categoria de productos
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");
        category = b.getParcelable("category");
//        inicializa el servicio de localizacion
        LocationService service = new LocationService(getApplicationContext());
        location = service.getLocation();
        //Instancia del ListView
        list = (ListView) findViewById(R.id.list_special_product);
        input = (TextView) findViewById(R.id.inputSearch);

        //Inicializar el adapter con la fuente de datos
        adapter = new ItemArrayAdapter(this, new ArrayList<Listable>());

        //Relacionando la list con el adapter
        list.setAdapter(adapter);
        sendRequest();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                specialProduct = (SpecialProduct) adapter.getItem(position);
                sendRequestCommerce();
            }
        });
        input.addTextChangedListener(filterTextWatcher);
    }
    /**
     * Se ocupa de realizar una consulta, que permite obtener los producto especiales de
     * una categoria
     */
    public void sendRequest() {
        HttpHandler http = new HttpHandler(AppConfig.URL_GET_C, HttpHandler.GET_REQUEST);
        http.addParams("rubro", String.valueOf(category.getId()));
        http.setListener(this);
        http.sendRequest();
    }

    /**
     * Se ocupa de realizar una consulta, que permite obtener los comercios mas cercanos al usuario
     */
    public void sendRequestCommerce() {
        HttpHandler http = new HttpHandler(AppConfig.URL_COMMERCES_SUG, HttpHandler.GET_REQUEST);
        if(location == null){
            Toast notificacion = Toast.makeText(context, context.getString(R.string.msg_services_disable), Toast.LENGTH_SHORT);
            notificacion.show();
            finish();
        }
        Toast notificacion = Toast.makeText(context, ""+location.getProvider(), Toast.LENGTH_SHORT);
        notificacion.show();
        Log.e("SpecialProduct", "provider: " + location.getProvider());

        http.addParams("latitud", String.valueOf(location.getLatitude()));
        http.addParams("longitud", String.valueOf(location.getLongitude()));
        http.addParams("usuario", "'" + user.getMail() + "'");
        Query query = new Query();
        query.setBarcode(specialProduct.getId());
        query.setLocation(location);

        Help help = new Help(context,query,user);
        http.setListener(help);
        http.sendRequest();
    }
    /**
     * En caso de que la consulta sea exitosa, se procesa la info recibida.
     * @param data informacion recibida
     */
    @Override
    public void onSuccess(Object data) {
        JSONArray json = null;
        all = new ArrayList();
        try {
            json = new JSONArray(data.toString());
            all = JsonParser.getList(SpecialProduct.class, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.addAll(all);
    }

    /**
     * Metodo que se ocupa del buscador de productos especiales
     */
    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {}
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals("")) {
                List<Listable> filteredTitles = new ArrayList<Listable>();
                for (int i=0; i<all.size(); i++) {
                    if (all.get(i).toString().contains(s)) {
                        filteredTitles.add((SpecialProduct) all.get(i));
                    }
                }
                adapter = new ItemArrayAdapter(context,filteredTitles);
                list.setAdapter(adapter);
            }
            else {
                adapter = new ItemArrayAdapter(context,all);
                list.setAdapter(adapter);
            }
        }
    };
}

/**
 * Clase de ayuda que permite esperar un consulta adicional.
 */
class Help implements HttpResponseHandler {
   Context context;
    Query query;
    User user;
    public Help(Context context,Query query,User user) {
        this.context = context;
        this.query = query;
        this.user = user;
    }

    @Override
   public void onSuccess(Object data) {
           Intent j = new Intent(context, CommerceListViewActivity.class);
           j.putExtra("user",user);
           j.putExtra("query", query);
           j.putExtra("data", data.toString());
           context.startActivity(j);
   }


}

