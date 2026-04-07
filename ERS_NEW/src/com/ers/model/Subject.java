package com.ers.model;

public class Subject {
    private int    subjectId;
    private String subjectCode;
    private String subjectName;
    private String department;
    private int    credits;
    private String subjectType;
    private String status;

    public int    getSubjectId()           { return subjectId; }
    public void   setSubjectId(int v)      { this.subjectId = v; }
    public String getSubjectCode()         { return subjectCode; }
    public void   setSubjectCode(String v) { this.subjectCode = v; }
    public String getSubjectName()         { return subjectName; }
    public void   setSubjectName(String v) { this.subjectName = v; }
    public String getDepartment()          { return department; }
    public void   setDepartment(String v)  { this.department = v; }
    public int    getCredits()             { return credits; }
    public void   setCredits(int v)        { this.credits = v; }
    public String getSubjectType()         { return subjectType; }
    public void   setSubjectType(String v) { this.subjectType = v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status = v; }
}
