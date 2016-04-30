package home.myapplication.appL;

/**
 * Created by Ema on 27/10/2015.
 */

/**
 * AppConfig: contiene las url que seran consultadas por la app.
 */
public class AppConfig {
    // Servicios POST
    /**
     * Permite a la app logear a sus usuarios. La url recibe como parametros el email y la contraseña
     */
    public static String URL_LOGIN = "http://precios.draggon.com.ar/wikiPrecios/login";
    /**
     * Permite a la app registrar usuarios al sistema. La url recibe como parametros el nombre, apellido, email y
     * contraseña del nuevo usuario
     */
    public static String URL_REGISTER2 = "http://precios.draggon.com.ar/wikiPrecios/re";

    // Servicios GET
    /**
     * Permite a la app consultar por los comercios mas cermanos. La url recibe como parametro la ubicacion del usuario
     */
    public static String URL_COMMERCES_SUG = "http://precios.draggon.com.ar/wikiPrecios/comercios/";
    /**
     * Permite a la app consultar por los comercios y filtra por los comercios favoritos del usuario.
     */
    public static String URL_COMMERCES_FAVOURITES = "http://precios.draggon.com.ar/wikiPrecios/comerciosFavoritos/";
    /**
     * Permite guardar las modificaciones de la lista de comercios favoritos de los usuarios.
     */
    public static String URL_COMMERCES_FAVOURITES_SAVE = "http://precios.draggon.com.ar/wikiPrecios/favorito/";
    /**
     * Brinda algunos precios sugeridos de un producto ingresado por el usuario.
     */
    public static String URL_PRICES_SUG = "http://precios.draggon.com.ar/wikiPrecios/precioSugerido/";
    /**
     * Guarda el precio ingresado por el usuario en el sistema y le brinda al usuario todos los precios
     * del producto ingresado en los distintos comercios.
     */
    public static String URL_PRICES_SAVE = "http://precios.draggon.com.ar/wikiPrecios/precio/";

    /**
     * Brinda la lista de Rubros que existen en el sistema
     */
    public static String URL_GET_R = "http://precios.draggon.com.ar/wikiPrecios/obtenerRubros/";
    /**
     * Brinda la lista de categorias que pertenecer a un rubro especifico.
     */
    public static String URL_GET_C = "http://precios.draggon.com.ar/wikiPrecios/obtenerCategorias/";
}