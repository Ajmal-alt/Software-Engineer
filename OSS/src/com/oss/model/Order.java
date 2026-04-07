package com.oss.model;

public class Order {
    private int    orderId;
    private String orderCode;
    private int    customerId;
    private String customerName;
    private String orderDate;
    private double subtotal;
    private double taxAmount;
    private double discountAmount;
    private double shippingCharge;
    private double totalAmount;
    private String shippingAddress;
    private String status;
    private String notes;

    public int    getOrderId()               { return orderId; }
    public void   setOrderId(int v)          { this.orderId = v; }
    public String getOrderCode()             { return orderCode; }
    public void   setOrderCode(String v)     { this.orderCode = v; }
    public int    getCustomerId()            { return customerId; }
    public void   setCustomerId(int v)       { this.customerId = v; }
    public String getCustomerName()          { return customerName; }
    public void   setCustomerName(String v)  { this.customerName = v; }
    public String getOrderDate()             { return orderDate; }
    public void   setOrderDate(String v)     { this.orderDate = v; }
    public double getSubtotal()              { return subtotal; }
    public void   setSubtotal(double v)      { this.subtotal = v; }
    public double getTaxAmount()             { return taxAmount; }
    public void   setTaxAmount(double v)     { this.taxAmount = v; }
    public double getDiscountAmount()        { return discountAmount; }
    public void   setDiscountAmount(double v){ this.discountAmount = v; }
    public double getShippingCharge()        { return shippingCharge; }
    public void   setShippingCharge(double v){ this.shippingCharge = v; }
    public double getTotalAmount()           { return totalAmount; }
    public void   setTotalAmount(double v)   { this.totalAmount = v; }
    public String getShippingAddress()       { return shippingAddress; }
    public void   setShippingAddress(String v){ this.shippingAddress = v; }
    public String getStatus()                { return status; }
    public void   setStatus(String v)        { this.status = v; }
    public String getNotes()                 { return notes; }
    public void   setNotes(String v)         { this.notes = v; }
}
