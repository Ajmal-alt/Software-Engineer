package com.rtms.model;

public class Driver extends User {
    private int    driverId;
    private String driverCode;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String nationality;
    private String phone;
    private String licenseNumber;
    private String licenseGrade;
    private String licenseExpiry;
    private int    experienceYears;
    private int    totalRaces;
    private int    totalWins;
    private int    totalPodiums;
    private int    championshipPts;
    private String contractStart;
    private String contractEnd;
    private double salary;
    private String driverStatus;

    public Driver() { super(); }
    public Driver(int userId, String username, String email, String role,
                  int driverId, String driverCode, String firstName, String lastName) {
        super(userId, username, email, role);
        this.driverId=driverId; this.driverCode=driverCode;
        this.firstName=firstName; this.lastName=lastName;
    }

    @Override
    public void displayDashboard() {
        System.out.println("  Welcome, " + firstName + " " + lastName + " [" + driverCode + "]");
        System.out.println("  1. View My Profile & Stats");
        System.out.println("  2. View Upcoming Race Events");
        System.out.println("  3. View My Race History");
        System.out.println("  4. View Championship Standing");
        System.out.println("  5. View My Contract Details");
        System.out.println("  6. Logout");
    }

    public String getFullName() { return firstName + " " + lastName; }

    public int    getDriverId()              { return driverId; }
    public void   setDriverId(int v)         { this.driverId=v; }
    public String getDriverCode()            { return driverCode; }
    public void   setDriverCode(String v)    { this.driverCode=v; }
    public String getFirstName()             { return firstName; }
    public void   setFirstName(String v)     { this.firstName=v; }
    public String getLastName()              { return lastName; }
    public void   setLastName(String v)      { this.lastName=v; }
    public String getDateOfBirth()           { return dateOfBirth; }
    public void   setDateOfBirth(String v)   { this.dateOfBirth=v; }
    public String getNationality()           { return nationality; }
    public void   setNationality(String v)   { this.nationality=v; }
    public String getPhone()                 { return phone; }
    public void   setPhone(String v)         { this.phone=v; }
    public String getLicenseNumber()         { return licenseNumber; }
    public void   setLicenseNumber(String v) { this.licenseNumber=v; }
    public String getLicenseGrade()          { return licenseGrade; }
    public void   setLicenseGrade(String v)  { this.licenseGrade=v; }
    public String getLicenseExpiry()         { return licenseExpiry; }
    public void   setLicenseExpiry(String v) { this.licenseExpiry=v; }
    public int    getExperienceYears()       { return experienceYears; }
    public void   setExperienceYears(int v)  { this.experienceYears=v; }
    public int    getTotalRaces()            { return totalRaces; }
    public void   setTotalRaces(int v)       { this.totalRaces=v; }
    public int    getTotalWins()             { return totalWins; }
    public void   setTotalWins(int v)        { this.totalWins=v; }
    public int    getTotalPodiums()          { return totalPodiums; }
    public void   setTotalPodiums(int v)     { this.totalPodiums=v; }
    public int    getChampionshipPts()       { return championshipPts; }
    public void   setChampionshipPts(int v)  { this.championshipPts=v; }
    public String getContractStart()         { return contractStart; }
    public void   setContractStart(String v) { this.contractStart=v; }
    public String getContractEnd()           { return contractEnd; }
    public void   setContractEnd(String v)   { this.contractEnd=v; }
    public double getSalary()                { return salary; }
    public void   setSalary(double v)        { this.salary=v; }
    public String getDriverStatus()          { return driverStatus; }
    public void   setDriverStatus(String v)  { this.driverStatus=v; }
}
