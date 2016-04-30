package home.myapplication.controller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import home.myapplication.model.Category;
import home.myapplication.model.Commerce;
import home.myapplication.model.Price;
import home.myapplication.model.Query;
import home.myapplication.model.SpecialProduct;
import home.myapplication.model.User;

/**
 * Permite parsear clases a partir de JSONObject
 */
public class JsonParser {

    /**
     * Crea un nuevo User a partir de la informacion recibida en un JSONObject
     * @param json
     * @return un nuevo usuario
     * @throws JSONException
     */
    public static User parseToUser(JSONObject json) throws JSONException {
        String name = json.get("name").toString();
        String surname = json.get("surname").toString();
        String mail = json.get("mail").toString();
        String password = json.get("password").toString();
        int score = json.getInt("calificacion");
        int hits = json.getInt("acumulado");
        return new User(name,surname,mail,password,score,hits);
    }

    /**
     * Crea un nuevo Commerce a partir de la informacion recibida en un JSONObject
     * @param object el json que contiene la informacion del commerce
     * @return un nuevo comercio
     * @throws JSONException
     */
    public static Commerce parseToCommerce(JSONObject object) throws JSONException {
        String nombre = object.get("nombre").toString();
        String domicilio = object.get("domicilio").toString();
        String distance="";
        if(object.has("distancia")){
         distance= object.get("distancia").toString();
        }
        int id = object.getInt("id");
//        Double lat = object.getDouble("latitud");
//        Double lng = object.getDouble("longitud");
        int favourite = object.getInt("favorito");
        boolean f;
        if(favourite == 1)f=true;else f=false;
//        return new Commerce(id, nombre, lat, lng, domicilio);
        return new Commerce(id, nombre, domicilio,f,distance);
    }

    /**
     * Crea un nuevo Price a partir de la informacion recibida en un JSONObject
     * @param object el json que contiene la informacion del Price recibido
     * @return un nuevo Price
     * @throws JSONException
     */
    private static Object parseToPrice(JSONObject object) throws JSONException {
        String comercio = object.getString("comercio");
        String id = object.getString("idProducto");
        String precio = object.getString("precio");
        String distance = object.getString("distancia");
        return new Price(id, precio, comercio,distance);
    }

    /**
     * Crea un nuevo Price a partir de la informacion recibida en un JSONObject
     * @param object el json que contiene la informacion del Price recibido
     * @param n posiblemente innecesario
     * @return el nuevo precio sugerido
     * @throws JSONException
     */
    private static Object parseToSuggestedPrice(JSONObject object, int n) throws JSONException {
        String comercio = "";
        String distance = "";
        String id = "1";
        String precio = object.getString("precio");
        return new Price(id, precio, comercio,distance);
    }

    /**
     * Crea un Category a partir de la informacion recibida en un JSONObject
     * @param object el json que contiene la informacion de la Category recibida
     * @return la nueva Category
     * @throws JSONException
     */
    private static Object parseToCategory(JSONObject object) throws JSONException {
        int idRubro = object.getInt("idRubro");
        String letra = object.getString("letra");
        String nombre = object.getString("nombre");
        return new Category(idRubro,letra,nombre);
    }

    /**
     * Crea un nuevo SpecialProduct a partir de la informacion recibida en un JSONObject
     * @param object el json que contiene la informacion del nuevo SpecialProduct
     * @return un nuevo SpecialProduct
     * @throws JSONException
     */
    private static Object parseToSpecialProduct(JSONObject object) throws JSONException {
        int realCategory = object.getInt("rubro");
        String id = object.getString("idProducto");
        String product = object.getString("categoria");
        return new SpecialProduct(id,product,realCategory);
    }

    /**
     * Este metodo se ocupa de generar una lista de Objetos a partir de la informacion recibida
     * en un JsonArray
     * @param c la clase que contiene el JSONArray
     * @param json la informacion a analizar
     * @return una lista de objetos con la informacion recibida en el json
     * @throws JSONException
     */
    public static List getList(Class c, JSONArray json) throws JSONException {
        List lst = new ArrayList<>();

        for (int n = 0; n < json.length(); n++) {
            JSONObject object = json.getJSONObject(n);
            if (c.equals(Commerce.class)) {
                lst.add(JsonParser.parseToCommerce(object));
            }
            if (c.equals(Price.class)) {
                lst.add(JsonParser.parseToPrice(object));
            }
            if (c.equals(Query.class)) {
                lst.add(JsonParser.parseToSuggestedPrice(object, n));
            }
            if (c.equals(Category.class)) {
                lst.add(JsonParser.parseToCategory(object));
            }
            if (c.equals(SpecialProduct.class)) {
                lst.add(JsonParser.parseToSpecialProduct(object));
            }
        }
        return lst;
    }

}
