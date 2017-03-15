package com.hammersoft.coiner.core;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import br.com.hammersoft.core.constants.ECurrency;
import br.com.hammersoft.core.data.AvailableCurrencies;
import br.com.hammersoft.core.data.AvailablePairsInfo;
import br.com.hammersoft.core.data.CurrencyPairExInfo;
import br.com.hammersoft.core.data.CurrencyPairInfo;
import br.com.hammersoft.core.data.Info;
import br.com.hammersoft.core.data.PairInfo;
import br.com.hammersoft.core.post.HttpClientPost;
import br.com.hammersoft.core.post.exception.HttpPostException;

public final class LiveCoinService {
	
	private static final String LIVECOIN_API_BASE_URL = "https://api.livecoin.net";
	private static final String LIVECOIN_API_TICKER_URL = "/exchange/ticker";
	private static final String LIVECOIN_API_BIDASK_URL = "/exchange/maxbid_minask";
	private static final String LIVECOIN_API_COININFO_URL = "/info/coinInfo";
	private static final String LIVECOIN_API_TICKER_PARAM = "currencyPair=";
	private static final String LIVECOIN_API_PARAM_SEP = "?";
	private static final String LIVECOIN_API_PARAM_SEP_BAR = "/";

	public LiveCoinService() {
		
	}
	
	public static AvailablePairsInfo getAvailablePairsInfo() throws JSONException, IOException, HttpPostException{
		String fullUrl = 
				LIVECOIN_API_BASE_URL + 
				LIVECOIN_API_TICKER_URL;
		
		return (AvailablePairsInfo) getJson(fullUrl, AvailablePairsInfo.class, "pairInfo");
	}
	
	public static CurrencyPairExInfo getPairExInfo(String currencyFrom, String currencyTo) throws JSONException, IOException, HttpPostException{
		String fullUrl = 
				LIVECOIN_API_BASE_URL + 
				LIVECOIN_API_TICKER_URL + 
				LIVECOIN_API_PARAM_SEP + 
				LIVECOIN_API_TICKER_PARAM +
				currencyFrom + 
				LIVECOIN_API_PARAM_SEP_BAR + 
				currencyTo;
		
		return (CurrencyPairExInfo) getJson(fullUrl, CurrencyPairExInfo.class);
	}
	
	public static CurrencyPairInfo getPairInfo(String currencyFrom, String currencyTo) throws JSONException, IOException, HttpPostException{
		String fullUrl = 
				LIVECOIN_API_BASE_URL + 
				LIVECOIN_API_BIDASK_URL + 
				LIVECOIN_API_PARAM_SEP + 
				LIVECOIN_API_TICKER_PARAM +
				currencyFrom + 
				LIVECOIN_API_PARAM_SEP_BAR + 
				currencyTo;
		
		return (CurrencyPairInfo) getJson(fullUrl, CurrencyPairInfo.class);
	}
	
	public static AvailableCurrencies getAvailableCurrencies() throws JSONException, IOException, HttpPostException{
		String fullUrl = 
				LIVECOIN_API_BASE_URL + 
				LIVECOIN_API_COININFO_URL;
		
		return (AvailableCurrencies) getJson(fullUrl, AvailableCurrencies.class);
	}

	public static Object getJson(String url, Class<?> jsonClass) throws IOException, JSONException, HttpPostException{
		JSONObject jsonObj = new JSONObject();
		HttpClientPost post = new HttpClientPost();
		
		jsonObj = post.getFromURL(url);
		
		Gson gson = new Gson();
		Object ret = gson.fromJson(jsonObj.toString(), jsonClass);
		return ret; 
	}
	
	public static Object getJson(String url, Class<?> jsonClass, String adjust) throws IOException, JSONException, HttpPostException{
		JSONObject jsonObj = new JSONObject();
		HttpClientPost post = new HttpClientPost();
		
		jsonObj = post.getFromURL(url, adjust);
		
		Gson gson = new Gson();
		Object ret = gson.fromJson(jsonObj.toString(), jsonClass);
		return ret; 
	}
	
	public static void main(String[] args) throws JSONException, IOException, HttpPostException {
		AvailablePairsInfo api = LiveCoinService.getAvailablePairsInfo();
		for(PairInfo info : api.getPairInfo()){
			System.out.println(info);
		}

		/*
		AvailableCurrencies ac = LiveCoinService.getAvailableCurrencies();
		for(Info i : ac.getInfo()){
			if(!i.getSymbol().contains(ECurrency.BTC.getSymbol())){
				System.out.println(LiveCoinService.getPairInfo(i.getSymbol(), ECurrency.USD.getSymbol()));
				System.out.println(LiveCoinService.getPairInfo(i.getSymbol(), ECurrency.BTC.getSymbol()));
			}
		}*/
	}
}
