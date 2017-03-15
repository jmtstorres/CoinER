package com.hammersoft.coiner.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvailablePairsInfo {

	@SerializedName("pairInfo")
	@Expose
	private List<PairInfo> pairInfo = null;

	public List<PairInfo> getPairInfo() {
		return pairInfo;
	}

	public void setPairInfo(List<PairInfo> pairInfo) {
		this.pairInfo = pairInfo;
	}
}
