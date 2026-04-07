package com.mms.model;

public class Order {
    private int    orderId;
    private String orderCode;
    private int    customerId;
    private String customerName;
    private String orderDate;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private int    promoId;
    private String promoCode;
    private String status;
    private String notes;

    public int    getOrderId()             { return orderId; }
    public void   setOrderId(int v)        { this.orderId=v; }
    public String getOrderCode()           { return orderCode; }
    public void   setOrderCode(String v)   { this.orderCode=v; }
    public int    getCustomerId()          { return customerId; }
    public void   setCustomerId(int v)     { this.customerId=v; }
    public String getCustomerName()        { return customerName; }
    public void   setCustomerName(String v){ this.customerName=v; }
    public String getOrderDate()           { return orderDate; }
    public void   setOrderDate(String v)   { this.orderDate=v; }
    public double getTotalAmount()         { return totalAmount; }
    public void   setTotalAmount(double v) { this.totalAmount=v; }
    public double getDiscountAmount()      { return discountAmount; }
    public void   setDiscountAmount(double v){ this.discountAmount=v; }
    public double getFinalAmount()         { return finalAmount; }
    public void   setFinalAmount(double v) { this.finalAmount=v; }
    public int    getPromoId()             { return promoId; }
    public void   setPromoId(int v)        { this.promoId=v; }
    public String getPromoCode()           { return promoCode; }
    public void   setPromoCode(String v)   { this.promoCode=v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status=v; }
    public String getNotes()               { return notes; }
    public void   setNotes(String v)       { this.notes=v; }
}
