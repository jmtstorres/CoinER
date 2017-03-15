package com.hammersoft.coiner.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrencyPair {

	@SerializedName("symbol")
	@Expose
	private String symbol;
	@SerializedName("maxBid")
	@Expose
	private Double maxBid;
	@SerializedName("minAsk")
	@Expose
	private Double minAsk;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getMaxBid() {
		return maxBid;
	}

	public void setMaxBid(Double maxBid) {
		this.maxBid = maxBid;
	}

	public Double getMinAsk() {
		return minAsk;
	}

	public void setMinAsk(Double minAsk) {
		this.minAsk = minAsk;
	}
	
	public Double getAverage(){
		return (maxBid + minAsk)/2.0;
	}
}
