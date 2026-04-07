package com.mms.model;

public class Product {
    private int    productId;
    private String productCode;
    private String productName;
    private String category;
    private String description;
    private double unitPrice;
    private double costPrice;
    private int    stockQuantity;
    private String unit;
    private String brand;
    private String status;

    public int    getProductId()            { return productId; }
    public void   setProductId(int v)       { this.productId=v; }
    public String getProductCode()          { return productCode; }
    public void   setProductCode(String v)  { this.productCode=v; }
    public String getProductName()          { return productName; }
    public void   setProductName(String v)  { this.productName=v; }
    public String getCategory()             { return category; }
    public void   setCategory(String v)     { this.category=v; }
    public String getDescription()          { return description; }
    public void   setDescription(String v)  { this.description=v; }
    public double getUnitPrice()            { return unitPrice; }
    public void   setUnitPrice(double v)    { this.unitPrice=v; }
    public double getCostPrice()            { return costPrice; }
    public void   setCostPrice(double v)    { this.costPrice=v; }
    public int    getStockQuantity()        { return stockQuantity; }
    public void   setStockQuantity(int v)   { this.stockQuantity=v; }
    public String getUnit()                 { return unit; }
    public void   setUnit(String v)         { this.unit=v; }
    public String getBrand()                { return brand; }
    public void   setBrand(String v)        { this.brand=v; }
    public String getStatus()               { return status; }
    public void   setStatus(String v)       { this.status=v; }
}
