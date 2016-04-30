package home.myapplication.controller;

/**
 * Created by jorge on 21/09/2015.
 */

/**
 * Toda clase que implente HttpResponseHandler se ocupara de trabajar con la respuesta
 * recibida de HttpHandler
 */
public interface HttpResponseHandler {
    void onSuccess(Object data);
}
