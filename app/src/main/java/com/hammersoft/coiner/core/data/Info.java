package com.hammersoft.coiner.core.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Info {

	@SerializedName("name")
	@Expose
	private String name;
	@SerializedName("symbol")
	@Expose
	private String symbol;
	@SerializedName("walletStatus")
	@Expose
	private Object walletStatus;
	@SerializedName("withdrawFee")
	@Expose
	private Double withdrawFee;
	@SerializedName("difficulty")
	@Expose
	private Object difficulty;
	@SerializedName("minDepositAmount")
	@Expose
	private Integer minDepositAmount;
	@SerializedName("minWithdrawAmount")
	@Expose
	private Double minWithdrawAmount;
	@SerializedName("minOrderAmount")
	@Expose
	private Double minOrderAmount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public Object getWalletStatus() {
		return walletStatus;
	}

	public void setWalletStatus(Object walletStatus) {
		this.walletStatus = walletStatus;
	}

	public Double getWithdrawFee() {
		return withdrawFee;
	}

	public void setWithdrawFee(Double withdrawFee) {
		this.withdrawFee = withdrawFee;
	}

	public Object getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Object difficulty) {
		this.difficulty = difficulty;
	}

	public Integer getMinDepositAmount() {
		return minDepositAmount;
	}

	public void setMinDepositAmount(Integer minDepositAmount) {
		this.minDepositAmount = minDepositAmount;
	}

	public Double getMinWithdrawAmount() {
		return minWithdrawAmount;
	}

	public void setMinWithdrawAmount(Double minWithdrawAmount) {
		this.minWithdrawAmount = minWithdrawAmount;
	}

	public Double getMinOrderAmount() {
		return minOrderAmount;
	}

	public void setMinOrderAmount(Double minOrderAmount) {
		this.minOrderAmount = minOrderAmount;
	}

}