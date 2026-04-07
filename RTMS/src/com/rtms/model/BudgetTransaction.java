package com.rtms.model;

public class BudgetTransaction {
    private int    txnId;
    private String txnRef;
    private int    categoryId;
    private String categoryName;
    private String txnType;
    private double amount;
    private String description;
    private String txnDate;
    private int    eventId;
    private String eventName;
    private int    sponsorId;
    private String sponsorName;

    public int    getTxnId()               { return txnId; }
    public void   setTxnId(int v)          { this.txnId=v; }
    public String getTxnRef()              { return txnRef; }
    public void   setTxnRef(String v)      { this.txnRef=v; }
    public int    getCategoryId()          { return categoryId; }
    public void   setCategoryId(int v)     { this.categoryId=v; }
    public String getCategoryName()        { return categoryName; }
    public void   setCategoryName(String v){ this.categoryName=v; }
    public String getTxnType()             { return txnType; }
    public void   setTxnType(String v)     { this.txnType=v; }
    public double getAmount()              { return amount; }
    public void   setAmount(double v)      { this.amount=v; }
    public String getDescription()         { return description; }
    public void   setDescription(String v) { this.description=v; }
    public String getTxnDate()             { return txnDate; }
    public void   setTxnDate(String v)     { this.txnDate=v; }
    public int    getEventId()             { return eventId; }
    public void   setEventId(int v)        { this.eventId=v; }
    public String getEventName()           { return eventName; }
    public void   setEventName(String v)   { this.eventName=v; }
    public int    getSponsorId()           { return sponsorId; }
    public void   setSponsorId(int v)      { this.sponsorId=v; }
    public String getSponsorName()         { return sponsorName; }
    public void   setSponsorName(String v) { this.sponsorName=v; }
}
