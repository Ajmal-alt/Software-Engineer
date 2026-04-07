package com.rtms.model;

public class RaceEvent {
    private int    eventId;
    private String eventCode;
    private String eventName;
    private String series;
    private String circuitName;
    private String city;
    private String country;
    private String eventDate;
    private String qualifyingDate;
    private String practiceDate;
    private int    totalLaps;
    private double circuitLengthKm;
    private double prizeMoney;
    private String status;
    private String notes;

    public int    getEventId()               { return eventId; }
    public void   setEventId(int v)          { this.eventId=v; }
    public String getEventCode()             { return eventCode; }
    public void   setEventCode(String v)     { this.eventCode=v; }
    public String getEventName()             { return eventName; }
    public void   setEventName(String v)     { this.eventName=v; }
    public String getSeries()                { return series; }
    public void   setSeries(String v)        { this.series=v; }
    public String getCircuitName()           { return circuitName; }
    public void   setCircuitName(String v)   { this.circuitName=v; }
    public String getCity()                  { return city; }
    public void   setCity(String v)          { this.city=v; }
    public String getCountry()               { return country; }
    public void   setCountry(String v)       { this.country=v; }
    public String getEventDate()             { return eventDate; }
    public void   setEventDate(String v)     { this.eventDate=v; }
    public String getQualifyingDate()        { return qualifyingDate; }
    public void   setQualifyingDate(String v){ this.qualifyingDate=v; }
    public String getPracticeDate()          { return practiceDate; }
    public void   setPracticeDate(String v)  { this.practiceDate=v; }
    public int    getTotalLaps()             { return totalLaps; }
    public void   setTotalLaps(int v)        { this.totalLaps=v; }
    public double getCircuitLengthKm()       { return circuitLengthKm; }
    public void   setCircuitLengthKm(double v){ this.circuitLengthKm=v; }
    public double getPrizeMoney()            { return prizeMoney; }
    public void   setPrizeMoney(double v)    { this.prizeMoney=v; }
    public String getStatus()                { return status; }
    public void   setStatus(String v)        { this.status=v; }
    public String getNotes()                 { return notes; }
    public void   setNotes(String v)         { this.notes=v; }
}
