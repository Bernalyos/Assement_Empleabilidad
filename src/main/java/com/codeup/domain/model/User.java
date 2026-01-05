package com.codeup.domain.model;

import java.util.UUID;

public class User{

    private UUID id;
    private String username;
      private String email;
    private String password;
   

    public User(UUID id, String username, String email, String password)
    {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;   

    }

    public UUID getId() {return id;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getPassword() {return password;   }
    

    public void setId(UUID id) {this.id = id;}
    public void setUsername(String username) {this.username = username;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
}