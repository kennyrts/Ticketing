package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dbconnect.UtilDB;

public class Enfant {
    private int id;
    private int ageMax;
    private double reduction;

    // Constructors
    public Enfant() {}

    public Enfant(int id, int ageMax, double reduction) {
        this.id = id;
        this.ageMax = ageMax;
        this.reduction = reduction;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAgeMax() { return ageMax; }
    public void setAgeMax(int ageMax) { this.ageMax = ageMax; }
    public double getReduction() { return reduction; }
    public void setReduction(double reduction) { this.reduction = reduction; }

    // Get current child discount rule (most recent entry)
    public static Enfant getCurrentRule() throws Exception {
        String query = "SELECT * FROM enfant ORDER BY id DESC LIMIT 1";
        
        try (Connection connection = UtilDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                Enfant enfant = new Enfant();
                enfant.setId(rs.getInt("id"));
                enfant.setAgeMax(rs.getInt("age_max"));
                enfant.setReduction(rs.getDouble("reduction"));
                return enfant;
            }
        }
        return null; // No rule found
    }

    // Get all child discount rules
    public static List<Enfant> getAll() throws Exception {
        List<Enfant> enfants = new ArrayList<>();
        String query = "SELECT * FROM enfant ORDER BY id DESC";
        
        try (Connection connection = UtilDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Enfant enfant = new Enfant();
                enfant.setId(rs.getInt("id"));
                enfant.setAgeMax(rs.getInt("age_max"));
                enfant.setReduction(rs.getDouble("reduction"));
                enfants.add(enfant);
            }
        }
        return enfants;
    }

    // Insert a new child discount rule
    public static int insert(int ageMax, double reduction) throws Exception {
        String query = "INSERT INTO enfant (age_max, reduction) VALUES (?, ?) RETURNING id";
        
        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, ageMax);
            pstmt.setDouble(2, reduction);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
} 