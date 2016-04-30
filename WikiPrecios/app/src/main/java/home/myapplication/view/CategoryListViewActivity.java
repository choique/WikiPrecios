package home.myapplication.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import home.myapplication.model.Category;
import home.myapplication.model.Listable;
import home.myapplication.model.User;

/**
 * Esta clase contiene el control de la interface que permite al usuario seleccionar
 * una categoria, de una lista de categorias.
 */
public class CategoryListViewActivity extends Activity implements HttpResponseHandler {

    private List listAll;
    ListView list;
    ArrayAdapter adapter;
    private Context context;

    private User user;
    private Category category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_category);
        context = this;
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");
        //Instancia del ListView
        list = (ListView) findViewById(R.id.listCategory);

        //Inicializar el adapter con la fuente de datos
        adapter = new ItemArrayAdapter(this, new ArrayList<Listable>());

        //Relacionando la list con el adapter
        list.setAdapter(adapter);
//        evento que se dispara al momento de seleccionar una categoria
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category = (Category) adapter.getItem(position);//obtiene el elemento selccionado y se lo asigna a categoria
                Intent j = new Intent(context, SpecialProductListViewActivity.class);// crear el siguiente activity
                // comparte el usuario y la categoria con el siguiente activity
                j.putExtra("user", user);
                j.putExtra("category", category);
                context.startActivity(j);

            }
        });
        sendRequest();
    }

    /**
     * Envio de la peticion para obtener la lista de categorias.
     */
    public void sendRequest() {
        HttpHandler http = new HttpHandler(AppConfig.URL_GET_R,HttpHandler.GET_REQUEST);
        http.setListener(this);
        http.sendRequest();
    }

    /**
     * En caso de que la consulta sea exitosa, se procesa la info recibida.
     * @param data informacion recibida
     */
    @Override
    public void onSuccess(Object data) {
        JSONArray json = null;
        listAll = new ArrayList();
        try {
            json = new JSONArray(data.toString());
            listAll = JsonParser.getList(Category.class, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // se muestra la lista de categorias.
        adapter.addAll(listAll);
    }
}
