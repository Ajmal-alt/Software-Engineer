package com.oss.model;

public class Payment {
    private int    paymentId;
    private String paymentRef;
    private int    orderId;
    private String orderCode;
    private int    customerId;
    private String customerName;
    private double amount;
    private String paymentMode;
    private String paymentDate;
    private String status;
    private String remarks;

    public int    getPaymentId()           { return paymentId; }
    public void   setPaymentId(int v)      { this.paymentId = v; }
    public String getPaymentRef()          { return paymentRef; }
    public void   setPaymentRef(String v)  { this.paymentRef = v; }
    public int    getOrderId()             { return orderId; }
    public void   setOrderId(int v)        { this.orderId = v; }
    public String getOrderCode()           { return orderCode; }
    public void   setOrderCode(String v)   { this.orderCode = v; }
    public int    getCustomerId()          { return customerId; }
    public void   setCustomerId(int v)     { this.customerId = v; }
    public String getCustomerName()        { return customerName; }
    public void   setCustomerName(String v){ this.customerName = v; }
    public double getAmount()              { return amount; }
    public void   setAmount(double v)      { this.amount = v; }
    public String getPaymentMode()         { return paymentMode; }
    public void   setPaymentMode(String v) { this.paymentMode = v; }
    public String getPaymentDate()         { return paymentDate; }
    public void   setPaymentDate(String v) { this.paymentDate = v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status = v; }
    public String getRemarks()             { return remarks; }
    public void   setRemarks(String v)     { this.remarks = v; }
}
