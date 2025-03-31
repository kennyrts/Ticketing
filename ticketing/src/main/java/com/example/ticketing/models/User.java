package com.example.ticketing.models;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "login")
    private String username;
    
    @Column(name = "mdp")
    private String password;
    
    @Column(name = "role")
    private String role;
    
    @Column(name = "nom")
    private String nom;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    // Méthode pour vérifier si l'utilisateur est admin
    public boolean isAdmin() { 
        return "admin".equalsIgnoreCase(role);
    }
} 