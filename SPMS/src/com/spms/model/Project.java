package com.spms.model;

public class Project {
    private int    projectId;
    private String projectName;
    private String description;
    private String startDate;
    private String endDate;
    private String status;
    private double budget;
    private int    managerId;
    private String managerName;

    public int    getProjectId()            { return projectId; }
    public void   setProjectId(int v)       { this.projectId = v; }
    public String getProjectName()          { return projectName; }
    public void   setProjectName(String v)  { this.projectName = v; }
    public String getDescription()          { return description; }
    public void   setDescription(String v)  { this.description = v; }
    public String getStartDate()            { return startDate; }
    public void   setStartDate(String v)    { this.startDate = v; }
    public String getEndDate()              { return endDate; }
    public void   setEndDate(String v)      { this.endDate = v; }
    public String getStatus()               { return status; }
    public void   setStatus(String v)       { this.status = v; }
    public double getBudget()               { return budget; }
    public void   setBudget(double v)       { this.budget = v; }
    public int    getManagerId()            { return managerId; }
    public void   setManagerId(int v)       { this.managerId = v; }
    public String getManagerName()          { return managerName; }
    public void   setManagerName(String v)  { this.managerName = v; }
}
