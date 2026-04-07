package com.ers.model;

public class ExamSubject {
    private int    id;
    private int    examId;
    private String examName;
    private int    subjectId;
    private String subjectCode;
    private String subjectName;
    private String examDate;
    private String examTime;
    private int    durationMins;
    private int    maxMarks;
    private int    passMarks;
    private String venue;

    public int    getId()                  { return id; }
    public void   setId(int v)             { this.id = v; }
    public int    getExamId()              { return examId; }
    public void   setExamId(int v)         { this.examId = v; }
    public String getExamName()            { return examName; }
    public void   setExamName(String v)    { this.examName = v; }
    public int    getSubjectId()           { return subjectId; }
    public void   setSubjectId(int v)      { this.subjectId = v; }
    public String getSubjectCode()         { return subjectCode; }
    public void   setSubjectCode(String v) { this.subjectCode = v; }
    public String getSubjectName()         { return subjectName; }
    public void   setSubjectName(String v) { this.subjectName = v; }
    public String getExamDate()            { return examDate; }
    public void   setExamDate(String v)    { this.examDate = v; }
    public String getExamTime()            { return examTime; }
    public void   setExamTime(String v)    { this.examTime = v; }
    public int    getDurationMins()        { return durationMins; }
    public void   setDurationMins(int v)   { this.durationMins = v; }
    public int    getMaxMarks()            { return maxMarks; }
    public void   setMaxMarks(int v)       { this.maxMarks = v; }
    public int    getPassMarks()           { return passMarks; }
    public void   setPassMarks(int v)      { this.passMarks = v; }
    public String getVenue()               { return venue; }
    public void   setVenue(String v)       { this.venue = v; }
}
