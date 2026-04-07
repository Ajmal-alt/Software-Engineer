package com.mms.model;

public class Promotion {
    private int    promoId;
    private String promoCode;
    private String promoName;
    private String promoType;
    private double discountValue;
    private double minPurchase;
    private double maxDiscount;
    private String startDate;
    private String endDate;
    private int    usageLimit;
    private int    usageCount;
    private String applicableTo;
    private String description;
    private String status;

    public int    getPromoId()             { return promoId; }
    public void   setPromoId(int v)        { this.promoId=v; }
    public String getPromoCode()           { return promoCode; }
    public void   setPromoCode(String v)   { this.promoCode=v; }
    public String getPromoName()           { return promoName; }
    public void   setPromoName(String v)   { this.promoName=v; }
    public String getPromoType()           { return promoType; }
    public void   setPromoType(String v)   { this.promoType=v; }
    public double getDiscountValue()       { return discountValue; }
    public void   setDiscountValue(double v){ this.discountValue=v; }
    public double getMinPurchase()         { return minPurchase; }
    public void   setMinPurchase(double v) { this.minPurchase=v; }
    public double getMaxDiscount()         { return maxDiscount; }
    public void   setMaxDiscount(double v) { this.maxDiscount=v; }
    public String getStartDate()           { return startDate; }
    public void   setStartDate(String v)   { this.startDate=v; }
    public String getEndDate()             { return endDate; }
    public void   setEndDate(String v)     { this.endDate=v; }
    public int    getUsageLimit()          { return usageLimit; }
    public void   setUsageLimit(int v)     { this.usageLimit=v; }
    public int    getUsageCount()          { return usageCount; }
    public void   setUsageCount(int v)     { this.usageCount=v; }
    public String getApplicableTo()        { return applicableTo; }
    public void   setApplicableTo(String v){ this.applicableTo=v; }
    public String getDescription()         { return description; }
    public void   setDescription(String v) { this.description=v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status=v; }
}
