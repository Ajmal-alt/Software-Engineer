package com.oss.model;

public class OrderItem {
    private int    itemId;
    private int    orderId;
    private int    productId;
    private String productName;
    private int    quantity;
    private double unitPrice;
    private double discountPct;
    private double taxPercent;
    private double lineTotal;

    public int    getItemId()            { return itemId; }
    public void   setItemId(int v)       { this.itemId = v; }
    public int    getOrderId()           { return orderId; }
    public void   setOrderId(int v)      { this.orderId = v; }
    public int    getProductId()         { return productId; }
    public void   setProductId(int v)    { this.productId = v; }
    public String getProductName()       { return productName; }
    public void   setProductName(String v){ this.productName = v; }
    public int    getQuantity()          { return quantity; }
    public void   setQuantity(int v)     { this.quantity = v; }
    public double getUnitPrice()         { return unitPrice; }
    public void   setUnitPrice(double v) { this.unitPrice = v; }
    public double getDiscountPct()       { return discountPct; }
    public void   setDiscountPct(double v){ this.discountPct = v; }
    public double getTaxPercent()        { return taxPercent; }
    public void   setTaxPercent(double v){ this.taxPercent = v; }
    public double getLineTotal()         { return lineTotal; }
    public void   setLineTotal(double v) { this.lineTotal = v; }
}
