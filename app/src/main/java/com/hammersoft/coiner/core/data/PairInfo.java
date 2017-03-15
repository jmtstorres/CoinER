package com.hammersoft.coiner.core.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PairInfo {

	@SerializedName("cur")
	@Expose
	private String cur;
	@SerializedName("symbol")
	@Expose
	private String symbol;
	@SerializedName("last")
	@Expose
	private Double last;
	@SerializedName("high")
	@Expose
	private Double high;
	@SerializedName("low")
	@Expose
	private Double low;
	@SerializedName("volume")
	@Expose
	private Double volume;
	@SerializedName("vwap")
	@Expose
	private Double vwap;
	@SerializedName("max_bid")
	@Expose
	private Double maxBid;
	@SerializedName("min_ask")
	@Expose
	private Double minAsk;
	@SerializedName("best_bid")
	@Expose
	private Double bestBid;
	@SerializedName("best_ask")
	@Expose
	private Double bestAsk;

	public String getCur() {
		return cur;
	}

	public void setCur(String cur) {
		this.cur = cur;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Double getLast() {
		return last;
	}

	public void setLast(Double last) {
		this.last = last;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public Double getLow() {
		return low;
	}

	public void setLow(Double low) {
		this.low = low;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getVwap() {
		return vwap;
	}

	public void setVwap(Double vwap) {
		this.vwap = vwap;
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

	public Double getBestBid() {
		return bestBid;
	}

	public void setBestBid(Double bestBid) {
		this.bestBid = bestBid;
	}

	public Double getBestAsk() {
		return bestAsk;
	}

	public void setBestAsk(Double bestAsk) {
		this.bestAsk = bestAsk;
	}

	@Override
	public String toString() {
		return "PairInfo [cur=" + cur + ", symbol=" + symbol + ", last=" + last + ", high=" + high + ", low=" + low
				+ ", volume=" + volume + ", vwap=" + vwap + ", maxBid=" + maxBid + ", minAsk=" + minAsk + ", bestBid="
				+ bestBid + ", bestAsk=" + bestAsk + "]";
	}
}
