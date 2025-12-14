package com.example.pokedam.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String username; // PK: El nombre de usuario será único

    public String password;

    // Constructor vacío necesario para Room
    public User() {}

    public User(@NonNull String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y Setters estándar
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}