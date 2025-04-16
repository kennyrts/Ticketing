package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dbconnect.UtilDB;

public class Ville {
    private int id;
    private String nom;

    // Constructors
    public Ville() {}

    public Ville(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public static List<Ville> getAll() throws Exception {
        List<Ville> villes = new ArrayList<>();
        String query = "SELECT * FROM ville ORDER BY nom";
        
        try (Connection connection = UtilDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Ville ville = new Ville();
                ville.setId(rs.getInt("id"));
                ville.setNom(rs.getString("nom"));
                villes.add(ville);
            }
        }
        return villes;
    }
} 