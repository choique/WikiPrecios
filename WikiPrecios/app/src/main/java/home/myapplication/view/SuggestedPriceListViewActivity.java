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
import home.myapplication.model.Price;
import home.myapplication.model.Query;
import home.myapplication.model.User;

public class SuggestedPriceListViewActivity extends Activity implements HttpResponseHandler{

    ListView lista;
    List lst;
    ArrayAdapter adaptador;

    private Context context;
    private Commerce commerce;
    private Price price;
    private User user;
    private Query query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_suggested_price);
        context = this;
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");
        query = b.getParcelable("query");
        commerce = b.getParcelable("commerce");
        JSONArray json = null;
        List lst= new ArrayList();
        try {
            json = new JSONArray(getIntent().getStringExtra("precios"));
            lst = JsonParser.getList(Query.class,json);
            lst.add(new Price("-1",context.getString(R.string.title_another_price),context.getString(R.string.title_enter_manually),""));
        } catch (JSONException e) {
                e.printStackTrace();Log.e("PriceSLV"," JSonExcepcion");
    }

    //Instancia del ListView
    lista = (ListView)findViewById(R.id.list_suggested_price);

        //Inicializar el adapter con la fuente de datos
    adaptador = new ItemArrayAdapter(this,lst);

        //Relacionando la list con el adapter
        lista.setAdapter(adaptador);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                price = (Price) adaptador.getItem(position);
                Log.e("PriceSelect","Id precio"+price.getId());
                if(price.getId()== "-1"){
                    Intent j = new Intent(context, PriceActivity.class);
                    j.putExtra("user", user);
                    j.putExtra("query", query);
                    j.putExtra("commerce", commerce);
                    finish();
                    context.startActivity(j);
                }else{
                    sendRequest();
                }

            }
        });

    }

    private void sendRequest() {
        HttpHandler http = new HttpHandler(AppConfig.URL_PRICES_SAVE,HttpHandler.GET_REQUEST);
        http.addParams("comercio", String.valueOf(commerce.getId()));
        http.addParams("precio", price.getPrice());
        boolean isText= false;
        try {
            Double.parseDouble(query.getBarcode());
        }catch (Exception e){
            isText = true;
        }
        Log.e("SSugestedPrice", "isText: "+ isText);
        http.addParams("producto",query.getBarcode());
        http.addParams("usuario", "'" + user.getMail() + "'");
        http.addParams("latitud", String.valueOf(query.getLocation().getLatitude()));
        http.addParams("longitud", String.valueOf(query.getLocation().getLongitude()));
        http.setListener(this);
        http.sendRequest();
    }
    @Override
    public void onSuccess(Object data) {
        Intent j = new Intent(context, PriceListViewActivity.class);

        query.setPrice(Double.parseDouble(price.getPrice()));
        j.putExtra("user", user);
        j.putExtra("query", query);
        j.putExtra("precios", data.toString());
        Log.e("onSuccess  --> ", data.toString());
        finish();
        context.startActivity(j);

    }
}