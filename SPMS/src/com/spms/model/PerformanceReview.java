package com.spms.model;

public class PerformanceReview {
    private int    reviewId;
    private int    employeeId;
    private String employeeName;
    private int    reviewerId;
    private String reviewerName;
    private String reviewPeriod;
    private String reviewDate;
    private double rating;
    private String comments;
    private String status;

    public int    getReviewId()             { return reviewId; }
    public void   setReviewId(int v)        { this.reviewId = v; }
    public int    getEmployeeId()           { return employeeId; }
    public void   setEmployeeId(int v)      { this.employeeId = v; }
    public String getEmployeeName()         { return employeeName; }
    public void   setEmployeeName(String v) { this.employeeName = v; }
    public int    getReviewerId()           { return reviewerId; }
    public void   setReviewerId(int v)      { this.reviewerId = v; }
    public String getReviewerName()         { return reviewerName; }
    public void   setReviewerName(String v) { this.reviewerName = v; }
    public String getReviewPeriod()         { return reviewPeriod; }
    public void   setReviewPeriod(String v) { this.reviewPeriod = v; }
    public String getReviewDate()           { return reviewDate; }
    public void   setReviewDate(String v)   { this.reviewDate = v; }
    public double getRating()               { return rating; }
    public void   setRating(double v)       { this.rating = v; }
    public String getComments()             { return comments; }
    public void   setComments(String v)     { this.comments = v; }
    public String getStatus()               { return status; }
    public void   setStatus(String v)       { this.status = v; }
}
