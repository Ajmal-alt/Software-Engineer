package com.spms.model;

public class Department {
    private int    departmentId;
    private String departmentName;
    private String departmentHead;
    private String location;

    public int    getDepartmentId()           { return departmentId; }
    public void   setDepartmentId(int v)      { this.departmentId = v; }
    public String getDepartmentName()         { return departmentName; }
    public void   setDepartmentName(String v) { this.departmentName = v; }
    public String getDepartmentHead()         { return departmentHead; }
    public void   setDepartmentHead(String v) { this.departmentHead = v; }
    public String getLocation()               { return location; }
    public void   setLocation(String v)       { this.location = v; }
}
