package com.mms.model;

public class Customer {
    private int    customerId;
    private String customerCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private String designation;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String customerType;
    private String segment;
    private double totalPurchases;
    private int    loyaltyPoints;
    private String status;
    private int    leadId;
    private String assignedToName;
    private String createdAt;

    public String getFullName() { return firstName + " " + lastName; }

    public int    getCustomerId()           { return customerId; }
    public void   setCustomerId(int v)      { this.customerId=v; }
    public String getCustomerCode()         { return customerCode; }
    public void   setCustomerCode(String v) { this.customerCode=v; }
    public String getFirstName()            { return firstName; }
    public void   setFirstName(String v)    { this.firstName=v; }
    public String getLastName()             { return lastName; }
    public void   setLastName(String v)     { this.lastName=v; }
    public String getEmail()                { return email; }
    public void   setEmail(String v)        { this.email=v; }
    public String getPhone()                { return phone; }
    public void   setPhone(String v)        { this.phone=v; }
    public String getCompany()              { return company; }
    public void   setCompany(String v)      { this.company=v; }
    public String getDesignation()          { return designation; }
    public void   setDesignation(String v)  { this.designation=v; }
    public String getAddress()              { return address; }
    public void   setAddress(String v)      { this.address=v; }
    public String getCity()                 { return city; }
    public void   setCity(String v)         { this.city=v; }
    public String getState()                { return state; }
    public void   setState(String v)        { this.state=v; }
    public String getPincode()              { return pincode; }
    public void   setPincode(String v)      { this.pincode=v; }
    public String getCustomerType()         { return customerType; }
    public void   setCustomerType(String v) { this.customerType=v; }
    public String getSegment()              { return segment; }
    public void   setSegment(String v)      { this.segment=v; }
    public double getTotalPurchases()       { return totalPurchases; }
    public void   setTotalPurchases(double v){ this.totalPurchases=v; }
    public int    getLoyaltyPoints()        { return loyaltyPoints; }
    public void   setLoyaltyPoints(int v)   { this.loyaltyPoints=v; }
    public String getStatus()               { return status; }
    public void   setStatus(String v)       { this.status=v; }
    public int    getLeadId()               { return leadId; }
    public void   setLeadId(int v)          { this.leadId=v; }
    public String getAssignedToName()       { return assignedToName; }
    public void   setAssignedToName(String v){ this.assignedToName=v; }
    public String getCreatedAt()            { return createdAt; }
    public void   setCreatedAt(String v)    { this.createdAt=v; }
}
