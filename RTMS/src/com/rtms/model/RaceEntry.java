package com.rtms.model;

public class RaceEntry {
    private int    entryId;
    private int    eventId;
    private String eventName;
    private String eventDate;
    private String series;
    private int    driverId;
    private String driverName;
    private String carNumber;
    private int    qualifyingPos;
    private String qualifyingTime;
    private int    racePos;
    private String raceTime;
    private String fastestLap;
    private int    lapsCompleted;
    private int    pointsScored;
    private boolean dnf;
    private String dnfReason;
    private String notes;

    public int    getEntryId()              { return entryId; }
    public void   setEntryId(int v)         { this.entryId=v; }
    public int    getEventId()              { return eventId; }
    public void   setEventId(int v)         { this.eventId=v; }
    public String getEventName()            { return eventName; }
    public void   setEventName(String v)    { this.eventName=v; }
    public String getEventDate()            { return eventDate; }
    public void   setEventDate(String v)    { this.eventDate=v; }
    public String getSeries()               { return series; }
    public void   setSeries(String v)       { this.series=v; }
    public int    getDriverId()             { return driverId; }
    public void   setDriverId(int v)        { this.driverId=v; }
    public String getDriverName()           { return driverName; }
    public void   setDriverName(String v)   { this.driverName=v; }
    public String getCarNumber()            { return carNumber; }
    public void   setCarNumber(String v)    { this.carNumber=v; }
    public int    getQualifyingPos()        { return qualifyingPos; }
    public void   setQualifyingPos(int v)   { this.qualifyingPos=v; }
    public String getQualifyingTime()       { return qualifyingTime; }
    public void   setQualifyingTime(String v){ this.qualifyingTime=v; }
    public int    getRacePos()              { return racePos; }
    public void   setRacePos(int v)         { this.racePos=v; }
    public String getRaceTime()             { return raceTime; }
    public void   setRaceTime(String v)     { this.raceTime=v; }
    public String getFastestLap()           { return fastestLap; }
    public void   setFastestLap(String v)   { this.fastestLap=v; }
    public int    getLapsCompleted()        { return lapsCompleted; }
    public void   setLapsCompleted(int v)   { this.lapsCompleted=v; }
    public int    getPointsScored()         { return pointsScored; }
    public void   setPointsScored(int v)    { this.pointsScored=v; }
    public boolean isDnf()                  { return dnf; }
    public void   setDnf(boolean v)         { this.dnf=v; }
    public String getDnfReason()            { return dnfReason; }
    public void   setDnfReason(String v)    { this.dnfReason=v; }
    public String getNotes()                { return notes; }
    public void   setNotes(String v)        { this.notes=v; }
}
