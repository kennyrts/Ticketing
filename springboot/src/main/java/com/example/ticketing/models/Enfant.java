package com.example.ticketing.models;

import jakarta.persistence.*;

@Entity
@Table(name = "enfant")
public class Enfant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "age_max")
    private Integer ageMax;
    
    @Column(name = "reduction")
    private Double reduction;
    
    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getAgeMax() { return ageMax; }
    public void setAgeMax(Integer ageMax) { this.ageMax = ageMax; }
    
    public Double getReduction() { return reduction; }
    public void setReduction(Double reduction) { this.reduction = reduction; }
} 