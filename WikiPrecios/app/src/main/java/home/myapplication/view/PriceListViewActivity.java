package home.myapplication.view;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import home.myapplication.R;
import home.myapplication.controller.JsonParser;
import home.myapplication.model.Price;
import home.myapplication.model.Query;
import home.myapplication.model.User;

public class PriceListViewActivity extends Activity {

    ListView listView;
    List listBase;
    List list;

    ArrayAdapter adaptador;
    private Button btnReturn;
//    private Session session;
    private Context context;
    private User user;
    private Query query;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_price);
        context = this;
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");
        query = b.getParcelable("query");

       btnReturn = (Button) findViewById(R.id.btn_return_price);
       btnReturn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });

       JSONArray json = null;
        try {
            json = new JSONArray(getIntent().getStringExtra("precios"));
            listBase = JsonParser.getList(Price.class, json);
            list= JsonParser.getList(Price.class, json);
//            Collections.sort(list, new PriceComparator());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Instancia del ListView
        listView = (ListView)findViewById(R.id.list_price);
        //Inicializar el adapter con la fuente de datos
        adaptador = new ItemArrayAdapter(this, list);

        //Relacionando la list con el adapter
        listView.setAdapter(adaptador);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }
}