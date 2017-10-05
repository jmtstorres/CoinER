package com.hammersoft.coiner.core.data;

import android.support.annotation.NonNull;

/**
 * Created by JoãoMarcelo on 15/03/2017.
 */

public class ListData implements Comparable{

    private boolean alert;
    private Float valUSD;
    private Float valBTC;
    private String symbol;
    private String name;

    public ListData(Float valUSD, Float valBTC, String symbol, String name, boolean alert) {
        this.valUSD = valUSD;
        this.valBTC = valBTC;
        this.symbol = symbol;
        this.name = name;
        this.alert = alert;
    }

    public Float getValUSD() {
        return valUSD;
    }

    public Float getValBTC() {
        return valBTC;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public boolean isAlert() {
        return alert;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.symbol.compareTo(((ListData)o).getSymbol());
    }
}
