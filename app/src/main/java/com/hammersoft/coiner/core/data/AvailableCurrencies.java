package com.hammersoft.coiner.core.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvailableCurrencies {

	@SerializedName("success")
	@Expose
	private Boolean success;
	@SerializedName("minimalOrderBTC")
	@Expose
	private String minimalOrderBTC;
	@SerializedName("info")
	@Expose
	private List<Info> info = null;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMinimalOrderBTC() {
		return minimalOrderBTC;
	}

	public void setMinimalOrderBTC(String minimalOrderBTC) {
		this.minimalOrderBTC = minimalOrderBTC;
	}

	public List<Info> getInfo() {
		return info;
	}

	public void setInfo(List<Info> info) {
		this.info = info;
	}
	
	public String toString(){
		String result = "";
		
		for(Info i : info){
			result = result.concat(i.getName() + "|" + i.getSymbol() + "|" + i.getDifficulty());
		}
		
		return result;
	}
}
