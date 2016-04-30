package home.myapplication.helper;

import java.util.Comparator;

import home.myapplication.model.Price;

/**
 * Permite comparar Price
 * Created by Ema on 20/11/2015.
 */
public class PriceComparator implements Comparator<Price> {
//        return c1.getSDate().compareTo(c2.getSDate());

    @Override
    public int compare(Price price1, Price price2) {
        return price1.getPrice().compareTo(price2.getPrice());
    }
}