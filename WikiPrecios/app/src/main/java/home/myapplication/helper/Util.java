package home.myapplication.helper;

/**
 * Created by Ema on 04/11/2015.
 * Esta clase contiene metodos auxiliares
 */
public class Util {
    /**
     * Metodo auxiliar de redondeo
      * @param value valor a redondear
     * @param places lugares despues de la coma, (decimales)
     * @return el numero redondeado
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
