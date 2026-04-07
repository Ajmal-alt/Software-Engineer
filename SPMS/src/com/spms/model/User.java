package com.spms.model;

public abstract class User {
    private int    userId;
    private String username;
    private String password;
    private String email;
    private String role;
    private boolean status;

    public User() {}

    public User(int userId, String username, String email, String role) {
        this.userId   = userId;
        this.username = username;
        this.email    = email;
        this.role     = role;
        this.status   = true;
    }

    public abstract void displayDashboard();

    // Getters & Setters
    public int    getUserId()               { return userId; }
    public void   setUserId(int userId)     { this.userId = userId; }
    public String getUsername()             { return username; }
    public void   setUsername(String u)     { this.username = u; }
    public String getPassword()             { return password; }
    public void   setPassword(String p)     { this.password = p; }
    public String getEmail()                { return email; }
    public void   setEmail(String e)        { this.email = e; }
    public String getRole()                 { return role; }
    public void   setRole(String role)      { this.role = role; }
    public boolean isStatus()              { return status; }
    public void   setStatus(boolean status) { this.status = status; }
}
