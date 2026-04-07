package com.oss.model;

public class Invoice {
    private int    invoiceId;
    private String invoiceNo;
    private int    orderId;
    private String orderCode;
    private int    paymentId;
    private String paymentRef;
    private int    customerId;
    private String customerName;
    private String invoiceDate;
    private double subtotal;
    private double taxAmount;
    private double discount;
    private double shipping;
    private double grandTotal;
    private String status;

    public int    getInvoiceId()           { return invoiceId; }
    public void   setInvoiceId(int v)      { this.invoiceId = v; }
    public String getInvoiceNo()           { return invoiceNo; }
    public void   setInvoiceNo(String v)   { this.invoiceNo = v; }
    public int    getOrderId()             { return orderId; }
    public void   setOrderId(int v)        { this.orderId = v; }
    public String getOrderCode()           { return orderCode; }
    public void   setOrderCode(String v)   { this.orderCode = v; }
    public int    getPaymentId()           { return paymentId; }
    public void   setPaymentId(int v)      { this.paymentId = v; }
    public String getPaymentRef()          { return paymentRef; }
    public void   setPaymentRef(String v)  { this.paymentRef = v; }
    public int    getCustomerId()          { return customerId; }
    public void   setCustomerId(int v)     { this.customerId = v; }
    public String getCustomerName()        { return customerName; }
    public void   setCustomerName(String v){ this.customerName = v; }
    public String getInvoiceDate()         { return invoiceDate; }
    public void   setInvoiceDate(String v) { this.invoiceDate = v; }
    public double getSubtotal()            { return subtotal; }
    public void   setSubtotal(double v)    { this.subtotal = v; }
    public double getTaxAmount()           { return taxAmount; }
    public void   setTaxAmount(double v)   { this.taxAmount = v; }
    public double getDiscount()            { return discount; }
    public void   setDiscount(double v)    { this.discount = v; }
    public double getShipping()            { return shipping; }
    public void   setShipping(double v)    { this.shipping = v; }
    public double getGrandTotal()          { return grandTotal; }
    public void   setGrandTotal(double v)  { this.grandTotal = v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status = v; }
}
