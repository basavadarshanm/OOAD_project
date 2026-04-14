package com.onlinebanking.model;

public class User {
    private long id;
    private String username;
    private String password;
    private String email;
    private String name;

    public User(long id, String username, String password, String email, String name) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
    }

    public long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public String getName() { return name; }

    public void setEmail(String email) { this.email = email; }
    public void setName(String name) { this.name = name; }
}
