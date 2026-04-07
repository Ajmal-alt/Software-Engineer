package com.oss.model;

public class Customer extends User {
    private int    customerId;
    private String customerCode;
    private String firstName;
    private String lastName;
    private String phone;
    private String dateOfBirth;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private int    loyaltyPoints;
    private String customerStatus;

    public Customer() { super(); }

    public Customer(int userId, String username, String email, String role,
                    int customerId, String customerCode, String firstName, String lastName) {
        super(userId, username, email, role);
        this.customerId   = customerId;
        this.customerCode = customerCode;
        this.firstName    = firstName;
        this.lastName     = lastName;
    }

    @Override
    public void displayDashboard() {
        System.out.println("  Welcome, " + firstName + " " + lastName + " [" + customerCode + "]");
        System.out.println("  1. Browse Products");
        System.out.println("  2. Search Product");
        System.out.println("  3. Add to Cart");
        System.out.println("  4. View My Cart");
        System.out.println("  5. Place Order");
        System.out.println("  6. View My Orders");
        System.out.println("  7. Track Order");
        System.out.println("  8. View My Invoices");
        System.out.println("  9. View My Profile");
        System.out.println("  10. Logout");
    }

    public String getFullName() { return firstName + " " + lastName; }

    public int    getCustomerId()             { return customerId; }
    public void   setCustomerId(int v)        { this.customerId = v; }
    public String getCustomerCode()           { return customerCode; }
    public void   setCustomerCode(String v)   { this.customerCode = v; }
    public String getFirstName()              { return firstName; }
    public void   setFirstName(String v)      { this.firstName = v; }
    public String getLastName()               { return lastName; }
    public void   setLastName(String v)       { this.lastName = v; }
    public String getPhone()                  { return phone; }
    public void   setPhone(String v)          { this.phone = v; }
    public String getDateOfBirth()            { return dateOfBirth; }
    public void   setDateOfBirth(String v)    { this.dateOfBirth = v; }
    public String getGender()                 { return gender; }
    public void   setGender(String v)         { this.gender = v; }
    public String getAddress()                { return address; }
    public void   setAddress(String v)        { this.address = v; }
    public String getCity()                   { return city; }
    public void   setCity(String v)           { this.city = v; }
    public String getState()                  { return state; }
    public void   setState(String v)          { this.state = v; }
    public String getPincode()                { return pincode; }
    public void   setPincode(String v)        { this.pincode = v; }
    public int    getLoyaltyPoints()          { return loyaltyPoints; }
    public void   setLoyaltyPoints(int v)     { this.loyaltyPoints = v; }
    public String getCustomerStatus()         { return customerStatus; }
    public void   setCustomerStatus(String v) { this.customerStatus = v; }
}
