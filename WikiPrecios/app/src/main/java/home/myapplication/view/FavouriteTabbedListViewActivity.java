package home.myapplication.view;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

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
import home.myapplication.model.Listable;
import home.myapplication.model.User;

/**
 * Esta clase se ocupa de controlar la interface de la lista de comercios favoritos de un usuario,
 * permite modificar, esta lista.
 *
 * This activity allows you to have multiple views (in this case two {@link ListView}s)
 * in one tab activity.  The advantages over separate activities is that you can
 * maintain tab state much easier and you don't have to constantly re-create each tab
 * activity when the tab is selected.
 */
public class FavouriteTabbedListViewActivity extends TabActivity implements TabHost.OnTabChangeListener,HttpResponseHandler {

    private static final String LIST1_TAB_TAG = "Todos";
    private static final String LIST2_TAB_TAG = "Favoritos";

    // The two views in our tabbed example
    private ListView listViewCommerce;
    private ListView listViewFauvorite;

    private List listAllCommerce;
    private TabHost tabHost;
    private Context context;
    private ArrayAdapter adapterCommerce;
    private ArrayAdapter adapterFavourite;
    private User user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_favourite);
        this.context = this;
        tabHost = getTabHost();
        tabHost.setOnTabChangedListener(this);
//        obtiene el usuario
        Bundle b = getIntent().getExtras();
        user = b.getParcelable("user");

        // setup list view Commerces
        listViewCommerce = (ListView) findViewById(R.id.listCommerce);
        adapterCommerce = new ItemArrayAdapter(this,new ArrayList<Listable>());
        listViewCommerce.setAdapter(adapterCommerce);

        // setup list view Fauvorites
        listViewFauvorite = (ListView) findViewById(R.id.listFavourite);
        adapterFavourite = new ItemArrayAdapter(this,new ArrayList<Listable>());
        listViewFauvorite.setAdapter(adapterFavourite);

        // add an onclicklistener to add an item from the first list to the second list
        sendRequest();
        initListeners();

        // add views to tab host
        tabHost.addTab(tabHost.newTabSpec(LIST1_TAB_TAG).setIndicator(LIST1_TAB_TAG).setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String arg0) {
                return listViewCommerce;
            }
        }));
        tabHost.addTab(tabHost.newTabSpec(LIST2_TAB_TAG).setIndicator(LIST2_TAB_TAG).setContent(new TabHost.TabContentFactory() {
            public View createTabContent(String arg0) {
                return listViewFauvorite;
            }
        }));

    }

    public void initListeners(){
        listViewCommerce.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.e("", adapterCommerce.getItem(position).toString());
                AlertDialog.Builder showPlace = new AlertDialog.Builder(
                        FavouriteTabbedListViewActivity.this);
                showPlace.setMessage(context.getString(R.string.msg_add_to_favourites));
                showPlace.setPositiveButton(context.getString(R.string.btn_add), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Commerce commerce = (Commerce) adapterCommerce.getItem(position);
                        int p = adapterFavourite.getPosition(commerce);
                        if (p == -1) {
                            commerce.setFavourite(true);
                            adapterCommerce.notifyDataSetChanged();
                            adapterFavourite.add(adapterCommerce.getItem(position));
                        }
                    }
                });
                showPlace.setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                showPlace.show();
                return false;
            }
        });

        listViewFauvorite.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.e("Comercios", adapterCommerce.getItem(position).toString());
                Log.e("Favoritos", adapterFavourite.getItem(position).toString());

                AlertDialog.Builder showPlace = new AlertDialog.Builder(
                        FavouriteTabbedListViewActivity.this);
                showPlace.setMessage(context.getString(R.string.msg_remove_from_favourites));
                showPlace.setPositiveButton(context.getString(R.string.btn_remove), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Commerce commerce = (Commerce) adapterFavourite.getItem(position);
                        commerce.setFavourite(false);
                        adapterCommerce.notifyDataSetChanged();
                        adapterFavourite.remove(commerce);
                    }
                });
                showPlace.setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                showPlace.show();
                return false;
            }
        });
    }

    public void sendRequest() {
        HttpHandler http = new HttpHandler(AppConfig.URL_COMMERCES_FAVOURITES,HttpHandler.GET_REQUEST);
        http.addParams("usuario", "'" + user.getMail() + "'");
        http.setListener(this);
        http.sendRequest();
    }

    @Override
    public void onSuccess(Object data) {
        JSONArray json = null;
        listAllCommerce= new ArrayList();
        try {
            json = new JSONArray(data.toString());
            listAllCommerce = JsonParser.getList(Commerce.class, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapterCommerce.addAll(listAllCommerce);
        for(int i=0;i< listAllCommerce.size();i++){
            Commerce commerce = (Commerce) listAllCommerce.get(i);
            Log.e("Comercio: ",commerce.toString());
            if(commerce.isFavourite() == true){
                adapterFavourite.add(commerce);
            }
        }
        onTabChanged(LIST2_TAB_TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        HttpHandler http = new HttpHandler(AppConfig.URL_COMMERCES_FAVOURITES_SAVE,HttpHandler.GET_REQUEST);
        String favouritesString ="";

        for(int i=0;i< adapterFavourite.getCount();i++){
            Commerce commerce = (Commerce) adapterFavourite.getItem(i);
            favouritesString+=""+commerce.getId()+",";
        }

        Log.e("Favourites", "String Favoritos " + favouritesString);
        http.addParams("usuario","'"+user.getMail()+"'" );
        http.addParams("comercios",favouritesString );

        http.sendRequest();
        Log.e("Favourites","onPause()");
    }

    @Override
    public void onTabChanged(String tabId) {
        tabHost.refreshDrawableState();
    }
}