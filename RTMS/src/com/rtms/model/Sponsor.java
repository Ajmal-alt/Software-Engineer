package com.rtms.model;

public class Sponsor {
    private int    sponsorId;
    private String sponsorCode;
    private String companyName;
    private String contactPerson;
    private String contactPhone;
    private String contactEmail;
    private String industry;
    private String sponsorType;
    private double contractValue;
    private String contractStart;
    private String contractEnd;
    private String logoPlacement;
    private String status;

    public int    getSponsorId()             { return sponsorId; }
    public void   setSponsorId(int v)        { this.sponsorId=v; }
    public String getSponsorCode()           { return sponsorCode; }
    public void   setSponsorCode(String v)   { this.sponsorCode=v; }
    public String getCompanyName()           { return companyName; }
    public void   setCompanyName(String v)   { this.companyName=v; }
    public String getContactPerson()         { return contactPerson; }
    public void   setContactPerson(String v) { this.contactPerson=v; }
    public String getContactPhone()          { return contactPhone; }
    public void   setContactPhone(String v)  { this.contactPhone=v; }
    public String getContactEmail()          { return contactEmail; }
    public void   setContactEmail(String v)  { this.contactEmail=v; }
    public String getIndustry()              { return industry; }
    public void   setIndustry(String v)      { this.industry=v; }
    public String getSponsorType()           { return sponsorType; }
    public void   setSponsorType(String v)   { this.sponsorType=v; }
    public double getContractValue()         { return contractValue; }
    public void   setContractValue(double v) { this.contractValue=v; }
    public String getContractStart()         { return contractStart; }
    public void   setContractStart(String v) { this.contractStart=v; }
    public String getContractEnd()           { return contractEnd; }
    public void   setContractEnd(String v)   { this.contractEnd=v; }
    public String getLogoPlacement()         { return logoPlacement; }
    public void   setLogoPlacement(String v) { this.logoPlacement=v; }
    public String getStatus()                { return status; }
    public void   setStatus(String v)        { this.status=v; }
}
