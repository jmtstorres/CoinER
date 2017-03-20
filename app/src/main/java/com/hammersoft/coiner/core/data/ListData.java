package com.hammersoft.coiner.core.data;

/**
 * Created by JoãoMarcelo on 15/03/2017.
 */

public class ListData {

    private Double valUSD;
    private Double valBTC;
    private String symbol;

    public ListData(Double valUSD, Double valBTC, String symbol) {
        this.valUSD = valUSD;
        this.valBTC = valBTC;
        this.symbol = symbol;
    }

    public Double getValUSD() {
        return valUSD;
    }

    public Double getValBTC() {
        return valBTC;
    }

    public String getSymbol() {
        return symbol;
    }
}
