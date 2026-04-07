package com.oss.model;

public class CartItem {
    private int    cartId;
    private int    customerId;
    private int    productId;
    private String productName;
    private String productCode;
    private double unitPrice;
    private double discountPct;
    private double taxPercent;
    private int    stockQty;
    private int    quantity;
    private double lineTotal;
    private String addedAt;

    public int    getCartId()             { return cartId; }
    public void   setCartId(int v)        { this.cartId = v; }
    public int    getCustomerId()         { return customerId; }
    public void   setCustomerId(int v)    { this.customerId = v; }
    public int    getProductId()          { return productId; }
    public void   setProductId(int v)     { this.productId = v; }
    public String getProductName()        { return productName; }
    public void   setProductName(String v){ this.productName = v; }
    public String getProductCode()        { return productCode; }
    public void   setProductCode(String v){ this.productCode = v; }
    public double getUnitPrice()          { return unitPrice; }
    public void   setUnitPrice(double v)  { this.unitPrice = v; }
    public double getDiscountPct()        { return discountPct; }
    public void   setDiscountPct(double v){ this.discountPct = v; }
    public double getTaxPercent()         { return taxPercent; }
    public void   setTaxPercent(double v) { this.taxPercent = v; }
    public int    getStockQty()           { return stockQty; }
    public void   setStockQty(int v)      { this.stockQty = v; }
    public int    getQuantity()           { return quantity; }
    public void   setQuantity(int v)      { this.quantity = v; }
    public double getLineTotal()          { return lineTotal; }
    public void   setLineTotal(double v)  { this.lineTotal = v; }
    public String getAddedAt()            { return addedAt; }
    public void   setAddedAt(String v)    { this.addedAt = v; }
}
