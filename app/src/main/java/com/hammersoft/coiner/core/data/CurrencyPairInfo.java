package com.hammersoft.coiner.core.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrencyPairInfo {

	@SerializedName("currencyPairs")
	@Expose
	private List<CurrencyPair> currencyPairs = null;

	public List<CurrencyPair> getCurrencyPairs() {
		return currencyPairs;
	}

	public void setCurrencyPairs(List<CurrencyPair> currencyPairs) {
		this.currencyPairs = currencyPairs;
	}
	
	public String toString(){
		String result = "";
		
		for(CurrencyPair cp : currencyPairs){
			result = result.concat(cp.getSymbol() + "|" + cp.getMaxBid() + "|" + cp.getMinAsk() + "|" + cp.getAverage());
		}
		
		return result;
	}

}