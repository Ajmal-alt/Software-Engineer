package com.mms.model;

public class Lead {
    private int    leadId;
    private String leadCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private String designation;
    private String city;
    private String state;
    private String source;
    private String interestArea;
    private String budgetRange;
    private String status;
    private String priority;
    private String notes;
    private int    assignedTo;
    private String assignedToName;
    private String createdAt;

    public String getFullName() { return firstName + " " + lastName; }

    public int    getLeadId()              { return leadId; }
    public void   setLeadId(int v)         { this.leadId=v; }
    public String getLeadCode()            { return leadCode; }
    public void   setLeadCode(String v)    { this.leadCode=v; }
    public String getFirstName()           { return firstName; }
    public void   setFirstName(String v)   { this.firstName=v; }
    public String getLastName()            { return lastName; }
    public void   setLastName(String v)    { this.lastName=v; }
    public String getEmail()               { return email; }
    public void   setEmail(String v)       { this.email=v; }
    public String getPhone()               { return phone; }
    public void   setPhone(String v)       { this.phone=v; }
    public String getCompany()             { return company; }
    public void   setCompany(String v)     { this.company=v; }
    public String getDesignation()         { return designation; }
    public void   setDesignation(String v) { this.designation=v; }
    public String getCity()                { return city; }
    public void   setCity(String v)        { this.city=v; }
    public String getState()               { return state; }
    public void   setState(String v)       { this.state=v; }
    public String getSource()              { return source; }
    public void   setSource(String v)      { this.source=v; }
    public String getInterestArea()        { return interestArea; }
    public void   setInterestArea(String v){ this.interestArea=v; }
    public String getBudgetRange()         { return budgetRange; }
    public void   setBudgetRange(String v) { this.budgetRange=v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status=v; }
    public String getPriority()            { return priority; }
    public void   setPriority(String v)    { this.priority=v; }
    public String getNotes()               { return notes; }
    public void   setNotes(String v)       { this.notes=v; }
    public int    getAssignedTo()          { return assignedTo; }
    public void   setAssignedTo(int v)     { this.assignedTo=v; }
    public String getAssignedToName()      { return assignedToName; }
    public void   setAssignedToName(String v){ this.assignedToName=v; }
    public String getCreatedAt()           { return createdAt; }
    public void   setCreatedAt(String v)   { this.createdAt=v; }
}
