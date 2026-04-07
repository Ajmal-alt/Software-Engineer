package com.ers.model;

public class Result {
    private int     resultId;
    private int     registrationId;
    private int     studentId;
    private String  studentName;
    private String  rollNumber;
    private int     examId;
    private String  examName;
    private int     subjectId;
    private String  subjectCode;
    private String  subjectName;
    private double  marksObtained;
    private int     maxMarks;
    private int     passMarks;
    private String  grade;
    private String  resultStatus;
    private boolean published;

    public int     getResultId()              { return resultId; }
    public void    setResultId(int v)         { this.resultId = v; }
    public int     getRegistrationId()        { return registrationId; }
    public void    setRegistrationId(int v)   { this.registrationId = v; }
    public int     getStudentId()             { return studentId; }
    public void    setStudentId(int v)        { this.studentId = v; }
    public String  getStudentName()           { return studentName; }
    public void    setStudentName(String v)   { this.studentName = v; }
    public String  getRollNumber()            { return rollNumber; }
    public void    setRollNumber(String v)    { this.rollNumber = v; }
    public int     getExamId()                { return examId; }
    public void    setExamId(int v)           { this.examId = v; }
    public String  getExamName()              { return examName; }
    public void    setExamName(String v)      { this.examName = v; }
    public int     getSubjectId()             { return subjectId; }
    public void    setSubjectId(int v)        { this.subjectId = v; }
    public String  getSubjectCode()           { return subjectCode; }
    public void    setSubjectCode(String v)   { this.subjectCode = v; }
    public String  getSubjectName()           { return subjectName; }
    public void    setSubjectName(String v)   { this.subjectName = v; }
    public double  getMarksObtained()         { return marksObtained; }
    public void    setMarksObtained(double v) { this.marksObtained = v; }
    public int     getMaxMarks()              { return maxMarks; }
    public void    setMaxMarks(int v)         { this.maxMarks = v; }
    public int     getPassMarks()             { return passMarks; }
    public void    setPassMarks(int v)        { this.passMarks = v; }
    public String  getGrade()                 { return grade; }
    public void    setGrade(String v)         { this.grade = v; }
    public String  getResultStatus()          { return resultStatus; }
    public void    setResultStatus(String v)  { this.resultStatus = v; }
    public boolean isPublished()              { return published; }
    public void    setPublished(boolean v)    { this.published = v; }
}
