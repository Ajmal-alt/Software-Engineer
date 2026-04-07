package com.mms.model;

public class LeadActivity {
    private int    activityId;
    private int    leadId;
    private String leadName;
    private String activityType;
    private String description;
    private String outcome;
    private String activityDate;
    private String nextAction;
    private String nextActionDate;
    private String performedByName;

    public int    getActivityId()              { return activityId; }
    public void   setActivityId(int v)         { this.activityId=v; }
    public int    getLeadId()                  { return leadId; }
    public void   setLeadId(int v)             { this.leadId=v; }
    public String getLeadName()                { return leadName; }
    public void   setLeadName(String v)        { this.leadName=v; }
    public String getActivityType()            { return activityType; }
    public void   setActivityType(String v)    { this.activityType=v; }
    public String getDescription()             { return description; }
    public void   setDescription(String v)     { this.description=v; }
    public String getOutcome()                 { return outcome; }
    public void   setOutcome(String v)         { this.outcome=v; }
    public String getActivityDate()            { return activityDate; }
    public void   setActivityDate(String v)    { this.activityDate=v; }
    public String getNextAction()              { return nextAction; }
    public void   setNextAction(String v)      { this.nextAction=v; }
    public String getNextActionDate()          { return nextActionDate; }
    public void   setNextActionDate(String v)  { this.nextActionDate=v; }
    public String getPerformedByName()         { return performedByName; }
    public void   setPerformedByName(String v) { this.performedByName=v; }
}
