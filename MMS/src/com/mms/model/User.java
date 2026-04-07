package com.mms.model;

public abstract class User {
    private int userId; private String username; private String password;
    private String email; private String role; private boolean status;

    public User() {}
    public User(int userId, String username, String email, String role) {
        this.userId=userId; this.username=username; this.email=email; this.role=role; this.status=true;
    }
    public abstract void displayDashboard();

    public int     getUserId()          { return userId; }
    public void    setUserId(int v)     { this.userId=v; }
    public String  getUsername()        { return username; }
    public void    setUsername(String v){ this.username=v; }
    public String  getPassword()        { return password; }
    public void    setPassword(String v){ this.password=v; }
    public String  getEmail()           { return email; }
    public void    setEmail(String v)   { this.email=v; }
    public String  getRole()            { return role; }
    public void    setRole(String v)    { this.role=v; }
    public boolean isStatus()           { return status; }
    public void    setStatus(boolean v) { this.status=v; }
}
