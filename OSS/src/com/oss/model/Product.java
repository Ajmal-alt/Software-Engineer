package com.oss.model;

public class Product {
    private int    productId;
    private String productCode;
    private String productName;
    private int    categoryId;
    private String categoryName;
    private String description;
    private double unitPrice;
    private double costPrice;
    private int    stockQty;
    private String unit;
    private String brand;
    private double taxPercent;
    private double discountPct;
    private String status;

    public double getEffectivePrice() {
        return unitPrice * (1 - discountPct / 100.0);
    }

    public int    getProductId()           { return productId; }
    public void   setProductId(int v)      { this.productId = v; }
    public String getProductCode()         { return productCode; }
    public void   setProductCode(String v) { this.productCode = v; }
    public String getProductName()         { return productName; }
    public void   setProductName(String v) { this.productName = v; }
    public int    getCategoryId()          { return categoryId; }
    public void   setCategoryId(int v)     { this.categoryId = v; }
    public String getCategoryName()        { return categoryName; }
    public void   setCategoryName(String v){ this.categoryName = v; }
    public String getDescription()         { return description; }
    public void   setDescription(String v) { this.description = v; }
    public double getUnitPrice()           { return unitPrice; }
    public void   setUnitPrice(double v)   { this.unitPrice = v; }
    public double getCostPrice()           { return costPrice; }
    public void   setCostPrice(double v)   { this.costPrice = v; }
    public int    getStockQty()            { return stockQty; }
    public void   setStockQty(int v)       { this.stockQty = v; }
    public String getUnit()                { return unit; }
    public void   setUnit(String v)        { this.unit = v; }
    public String getBrand()               { return brand; }
    public void   setBrand(String v)       { this.brand = v; }
    public double getTaxPercent()          { return taxPercent; }
    public void   setTaxPercent(double v)  { this.taxPercent = v; }
    public double getDiscountPct()         { return discountPct; }
    public void   setDiscountPct(double v) { this.discountPct = v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status = v; }
}
