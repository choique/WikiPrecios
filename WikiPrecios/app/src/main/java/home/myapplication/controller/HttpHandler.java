package home.myapplication.controller;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Controlador de consultas Http, se ocupa de realizar las consultas al servidor, pasa los parametros,
 * permite realizar consultar por GET y POST
 */
public class HttpHandler extends JsonHttpResponseHandler {
    // Tipo de consultas
    public static final int GET_REQUEST= 0;
    public static final int POST_REQUEST= 1;
    // Url a consultar
    private String baseUrl;
    // Clase que espera la respuesta de dicha consulta
    private HttpResponseHandler listener;
    // Parametros de la consulta
    private RequestParams requestParams;
    // Tipo de consulta a realizar = GET o POST
    private int requestMode;

    /**
     * Contructor de la clase HttpHandler
     * @param baseUrl url donde se realizara la consulta
     * @param requestMode tipo de consulta : Get p Post
     */
    public HttpHandler(String baseUrl, int requestMode) {
        this.baseUrl = baseUrl;
        this.requestParams = new RequestParams();
        this.requestMode = requestMode;
    }

    /**
     * Se ocupa de enviar la cunsulta al servidor
     * @return true si el envio de la consulta fue exitosa
     */
    public Boolean sendRequest() {
        Boolean request = true;
        Log.e("HttpHandler","sending request");
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            if(requestMode == GET_REQUEST){
                client.get(baseUrl, requestParams, this);
                Log.e("HttpHandler", "sending request GET");
            }else{
                client.post(baseUrl, requestParams, this);
                Log.e("HttpHandler", "sending request POST");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("HttpHandlerExcetion", e + "");
        }
        return request;
    }

    /**
     * Permite agregar parametros a la consulta
     * @param key el nombre del parametro a agregar
     * @param value el valor del parametro a agregar
     */
    public void addParams(String key, String value) {
        requestParams.add(key, value);
        Log.e("HttpHandler","addParams : "+key +"  "+value);
    }

    /**
     * Retorna el listener de la consulta
     * @return
     */
    public HttpResponseHandler getListener() {
        return listener;
    }

    /**
     * Setea el listener de la consulta
     * @param listener
     */
    public void setListener(HttpResponseHandler listener) {
        this.listener = listener;
    }

    /**
     * En caso de que la consulta sea resspondida correctamente y sin errores se ejecuta onSuccess,
     * en este caso onSuccess se ocupa de llamar al listener para que este pueda trabajar con la respuesta
     * recibida
     * @param statusCode
     * @param headers
     * @param response
     */
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        try {
            Log.e("HttpHandler Success", response.toString());
             listener.onSuccess(response);
        } catch (Exception e) {
            Log.e("HttpHandler Excepcion", e.toString());
        }
    }

    /**
     * En caso de que la consulta falle se ejecutara onFailure, por ahora no existe tratamiento
     * para este error debido a su aucencia durante el desarrollo
     * @param statusCode
     * @param headers
     * @param throwable
     * @param errorResponse
     */
    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        Log.e("HttpHandler onFailure", "");
    }


    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        try {
            Log.e("HttpHandler Success", response.toString());
            listener.onSuccess(response);
        } catch (Exception e) {
            Log.e("HttpHandler Excepcion", e.toString());

        }
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        Log.e("HttpHandler Failure", "");
        sendRequest();
    }
}