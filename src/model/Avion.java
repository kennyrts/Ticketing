package model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import dbconnect.UtilDB;

public class Avion {
    private int id;
    private String modele;
    private Date dateFabrication;

    // Constructors
    public Avion() {}

    public Avion(int id, String modele, Date dateFabrication) {
        this.id = id;
        this.modele = modele;
        this.dateFabrication = dateFabrication;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }
    public Date getDateFabrication() { return dateFabrication; }
    public void setDateFabrication(Date dateFabrication) { this.dateFabrication = dateFabrication; }

    public static List<Avion> getAll() throws Exception {
        List<Avion> avions = new ArrayList<>();
        String query = "SELECT * FROM avion ORDER BY modele";
        
        try (Connection connection = UtilDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Avion avion = new Avion();
                avion.setId(rs.getInt("id"));
                avion.setModele(rs.getString("modele"));
                avion.setDateFabrication(rs.getDate("date_fabrication"));
                avions.add(avion);
            }
        }
        return avions;
    }
} 