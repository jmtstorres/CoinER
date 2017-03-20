package com.hammersoft.coiner.core.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CoinData {

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Url")
    @Expose
    private String url;
    @SerializedName("ImageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("CoinName")
    @Expose
    private String coinName;
    @SerializedName("FullName")
    @Expose
    private String fullName;
    @SerializedName("Algorithm")
    @Expose
    private String algorithm;
    @SerializedName("ProofType")
    @Expose
    private String proofType;
    @SerializedName("FullyPremined")
    @Expose
    private String fullyPremined;
    @SerializedName("TotalCoinSupply")
    @Expose
    private String totalCoinSupply;
    @SerializedName("TotalCoinsMined")
    @Expose
    private String totalCoinsMined;
    @SerializedName("PreMinedValue")
    @Expose
    private String preMinedValue;
    @SerializedName("TotalCoinsFreeFloat")
    @Expose
    private String totalCoinsFreeFloat;
    @SerializedName("SortOrder")
    @Expose
    private String sortOrder;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoinName() {
        return coinName;
    }

    public void setCoinName(String coinName) {
        this.coinName = coinName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getProofType() {
        return proofType;
    }

    public void setProofType(String proofType) {
        this.proofType = proofType;
    }

    public String getFullyPremined() {
        return fullyPremined;
    }

    public void setFullyPremined(String fullyPremined) {
        this.fullyPremined = fullyPremined;
    }

    public String getTotalCoinSupply() {
        return totalCoinSupply;
    }

    public void setTotalCoinSupply(String totalCoinSupply) {
        this.totalCoinSupply = totalCoinSupply;
    }

    public String getTotalCoinsMined() {
        return totalCoinsMined;
    }

    public void setTotalCoinsMined(String totalCoinsMined) {
        this.totalCoinsMined = totalCoinsMined;
    }

    public String getPreMinedValue() {
        return preMinedValue;
    }

    public void setPreMinedValue(String preMinedValue) {
        this.preMinedValue = preMinedValue;
    }

    public String getTotalCoinsFreeFloat() {
        return totalCoinsFreeFloat;
    }

    public void setTotalCoinsFreeFloat(String totalCoinsFreeFloat) {
        this.totalCoinsFreeFloat = totalCoinsFreeFloat;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

}