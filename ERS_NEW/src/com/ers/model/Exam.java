package com.ers.model;

public class Exam {
    private int    examId;
    private String examCode;
    private String examName;
    private String examType;
    private String academicYear;
    private int    semester;
    private String department;
    private String regStartDate;
    private String regEndDate;
    private String examStartDate;
    private String examEndDate;
    private double feePerSubject;
    private int    maxSubjects;
    private String status;

    public int    getExamId()               { return examId; }
    public void   setExamId(int v)          { this.examId = v; }
    public String getExamCode()             { return examCode; }
    public void   setExamCode(String v)     { this.examCode = v; }
    public String getExamName()             { return examName; }
    public void   setExamName(String v)     { this.examName = v; }
    public String getExamType()             { return examType; }
    public void   setExamType(String v)     { this.examType = v; }
    public String getAcademicYear()         { return academicYear; }
    public void   setAcademicYear(String v) { this.academicYear = v; }
    public int    getSemester()             { return semester; }
    public void   setSemester(int v)        { this.semester = v; }
    public String getDepartment()           { return department; }
    public void   setDepartment(String v)   { this.department = v; }
    public String getRegStartDate()         { return regStartDate; }
    public void   setRegStartDate(String v) { this.regStartDate = v; }
    public String getRegEndDate()           { return regEndDate; }
    public void   setRegEndDate(String v)   { this.regEndDate = v; }
    public String getExamStartDate()        { return examStartDate; }
    public void   setExamStartDate(String v){ this.examStartDate = v; }
    public String getExamEndDate()          { return examEndDate; }
    public void   setExamEndDate(String v)  { this.examEndDate = v; }
    public double getFeePerSubject()        { return feePerSubject; }
    public void   setFeePerSubject(double v){ this.feePerSubject = v; }
    public int    getMaxSubjects()          { return maxSubjects; }
    public void   setMaxSubjects(int v)     { this.maxSubjects = v; }
    public String getStatus()               { return status; }
    public void   setStatus(String v)       { this.status = v; }
}
