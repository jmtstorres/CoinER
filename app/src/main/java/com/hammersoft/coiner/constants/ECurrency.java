package com.hammersoft.coiner.constants;

public enum ECurrency {
	BTC("BTC"),
	USD("USD");

	private ECurrency(String identifier){
		this.identifier = identifier;
	}
	
	private String identifier;

	public String getSymbol() {
		return identifier;
	}
}
