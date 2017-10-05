package com.hammersoft.coiner.core;

import com.google.gson.Gson;
import com.hammersoft.coiner.core.data.CoinHistoryDay;
import com.hammersoft.coiner.core.post.HttpClientPost;
import com.hammersoft.coiner.core.post.exception.HttpPostException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by JoãoMarcelo on 19/03/2017.
 */

public class CryptoCompareService {

    //https://www.cryptocompare.com/api/#-api-data-histoday-

    private static final String CRYPTOCOMPARE_API_BASE_URL = "https://min-api.cryptocompare.com";
    private static final String CRYPTOCOMPARE_API_HISTORY = "/data/pricehistorical";
    private static final String CRYPTOCOMPARE_API_HISTORY_DAY = "/data/histoday";
    private static final String CRYPTOCOMPARE_API_PARAM_SEP_URL = "?";
    private static final String CRYPTOCOMPARE_API_PARAM_SEP = "&";
    private static final String CRYPTOCOMPARE_API_PARAM_FSYM = "fsym=";
    private static final String CRYPTOCOMPARE_API_PARAM_TSYMS = "tsyms=";
    private static final String CRYPTOCOMPARE_API_PARAM_TSYM = "tsym=";
    private static final String CRYPTOCOMPARE_API_PARAM_TS = "ts=";
    private static final String CRYPTOCOMPARE_API_PARAM_LIMIT = "limit=";
    private static final String CRYPTOCOMPARE_API_PARAM_AGGREGATE = "aggregate=";
    private static final String CRYPTOCOMPARE_API_PARAM_E = "e=CCCAGG";
    private static final String CRYPTOCOMPARE_API_PARAM_APP = "appname=";

    public static Double getHistoryValue(String coinFrom, String coinTo, long timestamp) throws JSONException, IOException, HttpPostException{
        String fullUrl =
                CRYPTOCOMPARE_API_BASE_URL +
                CRYPTOCOMPARE_API_PARAM_SEP_URL +
                CRYPTOCOMPARE_API_HISTORY +
                CRYPTOCOMPARE_API_PARAM_FSYM + coinFrom +
                CRYPTOCOMPARE_API_PARAM_SEP +
                CRYPTOCOMPARE_API_PARAM_TSYMS + coinTo +
                CRYPTOCOMPARE_API_PARAM_SEP +
                CRYPTOCOMPARE_API_PARAM_TS + timestamp;

        JSONObject json = getJsonRaw(fullUrl);

        return json.getJSONObject(coinFrom).getDouble(coinTo);
    }

    public static CoinHistoryDay getHistoryDay(String coinFrom, String coinTo, int limit, int aggregate) throws JSONException, IOException, HttpPostException{
        String fullUrl =
                CRYPTOCOMPARE_API_BASE_URL +
                CRYPTOCOMPARE_API_HISTORY_DAY +
                CRYPTOCOMPARE_API_PARAM_SEP_URL +
                CRYPTOCOMPARE_API_PARAM_FSYM + coinFrom +
                CRYPTOCOMPARE_API_PARAM_SEP +
                CRYPTOCOMPARE_API_PARAM_TSYM + coinTo +
                CRYPTOCOMPARE_API_PARAM_SEP +
                CRYPTOCOMPARE_API_PARAM_LIMIT + limit +
                CRYPTOCOMPARE_API_PARAM_SEP +
                CRYPTOCOMPARE_API_PARAM_AGGREGATE + aggregate +
                CRYPTOCOMPARE_API_PARAM_SEP +
                CRYPTOCOMPARE_API_PARAM_E;


        return (CoinHistoryDay) getJson(fullUrl, CoinHistoryDay.class);
    }

    public static Double getHistoryBTCAll(String coinFrom, String coinList, long timestamp) throws JSONException, IOException, HttpPostException{
        String fullUrl =
                CRYPTOCOMPARE_API_BASE_URL +
                CRYPTOCOMPARE_API_PARAM_SEP_URL +
                CRYPTOCOMPARE_API_HISTORY +
                CRYPTOCOMPARE_API_PARAM_FSYM + coinFrom +
                CRYPTOCOMPARE_API_PARAM_SEP +
                CRYPTOCOMPARE_API_PARAM_TSYMS + coinList +
                CRYPTOCOMPARE_API_PARAM_SEP +
                CRYPTOCOMPARE_API_PARAM_TS + timestamp;

        JSONObject json = getJsonRaw(fullUrl);

        return json.getJSONObject(coinFrom).getDouble(coinList);
    }

    public static JSONObject getJsonRaw(String url) throws IOException, JSONException, HttpPostException{
        HttpClientPost post = new HttpClientPost();
        JSONObject jsonObj = post.getFromURL(url);
        return jsonObj;
    }

    public static Object getJson(String url, Class<?> jsonClass) throws IOException, JSONException, HttpPostException{
        JSONObject jsonObj = getJsonRaw(url);
        Gson gson = new Gson();
        Object ret = gson.fromJson(jsonObj.toString(), jsonClass);
        return ret;
    }

    public static Object getJson(String url, Class<?> jsonClass, String adjust) throws IOException, JSONException, HttpPostException{
        HttpClientPost post = new HttpClientPost();

        JSONObject jsonObj = post.getFromURL(url, adjust);

        Gson gson = new Gson();
        Object ret = gson.fromJson(jsonObj.toString(), jsonClass);
        return ret;
    }

}
