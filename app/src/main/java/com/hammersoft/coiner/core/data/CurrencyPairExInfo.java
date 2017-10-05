package com.hammersoft.coiner.core.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrencyPairExInfo {
	
	@SerializedName("last")
	@Expose
	private Float last;
	@SerializedName("high")
	@Expose
	private Float high;
	@SerializedName("low")
	@Expose
	private Float low;
	@SerializedName("volume")
	@Expose
	private Float volume;
	@SerializedName("vwap")
	@Expose
	private Float vwap;
	@SerializedName("max_bid")
	@Expose
	private Float maxBid;
	@SerializedName("min_ask")
	@Expose
	private Float minAsk;
	@SerializedName("best_bid")
	@Expose
	private Float bestBid;
	@SerializedName("best_ask")
	@Expose
	private Float bestAsk;

	public Float getLast() {
		return last;
	}

	public void setLast(Float last) {
		this.last = last;
	}

	public Float getHigh() {
		return high;
	}

	public void setHigh(Float high) {
		this.high = high;
	}

	public Float getLow() {
		return low;
	}

	public void setLow(Float low) {
		this.low = low;
	}

	public Float getVolume() {
		return volume;
	}

	public void setVolume(Float volume) {
		this.volume = volume;
	}

	public Float getVwap() {
		return vwap;
	}

	public void setVwap(Float vwap) {
		this.vwap = vwap;
	}

	public Float getMaxBid() {
		return maxBid;
	}

	public void setMaxBid(Float maxBid) {
		this.maxBid = maxBid;
	}

	public Float getMinAsk() {
		return minAsk;
	}

	public void setMinAsk(Float minAsk) {
		this.minAsk = minAsk;
	}

	public Float getBestBid() {
		return bestBid;
	}

	public void setBestBid(Float bestBid) {
		this.bestBid = bestBid;
	}

	public Float getBestAsk() {
		return bestAsk;
	}

	public void setBestAsk(Float bestAsk) {
		this.bestAsk = bestAsk;
	}
	
	public Float getAverage(){
		return (bestBid + bestAsk)/2.0f;
	}

	public String toString(){
		return "last:" + last 
		+ "\nhigh:" + high
		+ "\nlow:" + low
		+ "\nvolume:" + volume
		+ "\nvwap:" + vwap
		+ "\nmax_bid:" + maxBid
		+ "\nmin_ask:" + minAsk
		+ "\nbest_bid:" + bestBid
		+ "\nbest_ask:" + bestAsk
		+ "\naverage:" + getAverage();
	}
}