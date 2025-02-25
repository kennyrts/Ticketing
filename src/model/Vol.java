package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dbconnect.UtilDB;

public class Vol {
    private int id;
    private int avionId;
    private int villeDepartId;
    private int villeArriveeId;
    private Timestamp heureDepart;
    private int heuresAvantReservation;
    private int heuresAvantAnnulation;
    private String avionModele;
    private String villeDepartNom;
    private String villeArriveeNom;

    // Constructors
    public Vol() {}

    public Vol(int avionId, int villeDepartId, int villeArriveeId, 
               Timestamp heureDepart, int heuresAvantReservation, int heuresAvantAnnulation) {
        this.avionId = avionId;
        this.villeDepartId = villeDepartId;
        this.villeArriveeId = villeArriveeId;
        this.heureDepart = heureDepart;
        this.heuresAvantReservation = heuresAvantReservation;
        this.heuresAvantAnnulation = heuresAvantAnnulation;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAvionId() { return avionId; }
    public void setAvionId(int avionId) { this.avionId = avionId; }
    public int getVilleDepartId() { return villeDepartId; }
    public void setVilleDepartId(int villeDepartId) { this.villeDepartId = villeDepartId; }
    public int getVilleArriveeId() { return villeArriveeId; }
    public void setVilleArriveeId(int villeArriveeId) { this.villeArriveeId = villeArriveeId; }
    public Timestamp getHeureDepart() { return heureDepart; }
    public void setHeureDepart(Timestamp heureDepart) { this.heureDepart = heureDepart; }
    public int getHeuresAvantReservation() { return heuresAvantReservation; }
    public void setHeuresAvantReservation(int heuresAvantReservation) { this.heuresAvantReservation = heuresAvantReservation; }
    public int getHeuresAvantAnnulation() { return heuresAvantAnnulation; }
    public void setHeuresAvantAnnulation(int heuresAvantAnnulation) { this.heuresAvantAnnulation = heuresAvantAnnulation; }
    public String getAvionModele() { return avionModele; }
    public void setAvionModele(String avionModele) { this.avionModele = avionModele; }
    public String getVilleDepartNom() { return villeDepartNom; }
    public void setVilleDepartNom(String villeDepartNom) { this.villeDepartNom = villeDepartNom; }
    public String getVilleArriveeNom() { return villeArriveeNom; }
    public void setVilleArriveeNom(String villeArriveeNom) { this.villeArriveeNom = villeArriveeNom; }

    /**
     * Insert a new flight into the database
     * @param avionId Aircraft ID
     * @param villeDepartId Departure city ID
     * @param villeArriveeId Arrival city ID
     * @param heureDepart Departure time
     * @param heuresAvantReservation Hours before reservation deadline
     * @param heuresAvantAnnulation Hours before cancellation deadline
     * @return The ID of the newly inserted flight
     * @throws Exception if insertion fails
     */
    public static int insert(int avionId, int villeDepartId, int villeArriveeId,
                           Timestamp heureDepart, int heuresAvantReservation, 
                           int heuresAvantAnnulation) throws Exception {
        
        String query = "INSERT INTO vol (avion_id, ville_depart_id, ville_arrivee_id, " +
                      "heure_depart, heures_avant_reservation, heures_avant_annulation) " +
                      "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, avionId);
            pstmt.setInt(2, villeDepartId);
            pstmt.setInt(3, villeArriveeId);
            pstmt.setTimestamp(4, heureDepart);
            pstmt.setInt(5, heuresAvantReservation);
            pstmt.setInt(6, heuresAvantAnnulation);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new Exception("Failed to get inserted flight ID");
            }
        }
    }

    public static List<Vol> getAll() throws Exception {
        List<Vol> vols = new ArrayList<>();
        String query = "SELECT v.*, " +
                      "a.modele as avion_modele, " +
                      "vd.nom as ville_depart_nom, " +
                      "va.nom as ville_arrivee_nom " +
                      "FROM vol v " +
                      "JOIN avion a ON v.avion_id = a.id " +
                      "JOIN ville vd ON v.ville_depart_id = vd.id " +
                      "JOIN ville va ON v.ville_arrivee_id = va.id " +
                      "ORDER BY v.heure_depart DESC";
        
        try (Connection connection = UtilDB.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Vol vol = new Vol();
                vol.setId(rs.getInt("id"));
                vol.setAvionId(rs.getInt("avion_id"));
                vol.setVilleDepartId(rs.getInt("ville_depart_id"));
                vol.setVilleArriveeId(rs.getInt("ville_arrivee_id"));
                vol.setHeureDepart(rs.getTimestamp("heure_depart"));
                vol.setHeuresAvantReservation(rs.getInt("heures_avant_reservation"));
                vol.setHeuresAvantAnnulation(rs.getInt("heures_avant_annulation"));
                // Add additional fields for display
                vol.setAvionModele(rs.getString("avion_modele"));
                vol.setVilleDepartNom(rs.getString("ville_depart_nom"));
                vol.setVilleArriveeNom(rs.getString("ville_arrivee_nom"));
                vols.add(vol);
            }
        }
        return vols;
    }

    /**
     * Delete a flight from the database by its ID
     * @param id The ID of the flight to delete
     * @return true if deletion was successful, false if flight was not found
     * @throws Exception if deletion fails
     */
    public static boolean delete(int id) throws Exception {
        String query = "DELETE FROM vol WHERE id = ?";
        
        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            
            return rowsAffected > 0;
        }
    }

    /**
     * Get a flight by its ID
     * @param id The ID of the flight to retrieve
     * @return Vol object if found, null if not found
     * @throws Exception if database error occurs
     */
    public static Vol getById(int id) throws Exception {
        String query = "SELECT v.*, " +
                      "a.modele as avion_modele, " +
                      "vd.nom as ville_depart_nom, " +
                      "va.nom as ville_arrivee_nom " +
                      "FROM vol v " +
                      "JOIN avion a ON v.avion_id = a.id " +
                      "JOIN ville vd ON v.ville_depart_id = vd.id " +
                      "JOIN ville va ON v.ville_arrivee_id = va.id " +
                      "WHERE v.id = ?";
        
        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Vol vol = new Vol();
                    vol.setId(rs.getInt("id"));
                    vol.setAvionId(rs.getInt("avion_id"));
                    vol.setVilleDepartId(rs.getInt("ville_depart_id"));
                    vol.setVilleArriveeId(rs.getInt("ville_arrivee_id"));
                    vol.setHeureDepart(rs.getTimestamp("heure_depart"));
                    vol.setHeuresAvantReservation(rs.getInt("heures_avant_reservation"));
                    vol.setHeuresAvantAnnulation(rs.getInt("heures_avant_annulation"));
                    vol.setAvionModele(rs.getString("avion_modele"));
                    vol.setVilleDepartNom(rs.getString("ville_depart_nom"));
                    vol.setVilleArriveeNom(rs.getString("ville_arrivee_nom"));
                    return vol;
                }
                return null;
            }
        }
    }

    /**
     * Update an existing flight in the database
     * @param id The ID of the flight to update
     * @param avionId Aircraft ID
     * @param villeDepartId Departure city ID
     * @param villeArriveeId Arrival city ID
     * @param heureDepart Departure time
     * @param heuresAvantReservation Hours before reservation deadline
     * @param heuresAvantAnnulation Hours before cancellation deadline
     * @return true if update was successful, false if flight was not found
     * @throws Exception if update fails
     */
    public static boolean update(int id, int avionId, int villeDepartId, int villeArriveeId,
                               Timestamp heureDepart, int heuresAvantReservation, 
                               int heuresAvantAnnulation) throws Exception {
        
        String query = "UPDATE vol SET avion_id = ?, ville_depart_id = ?, " +
                      "ville_arrivee_id = ?, heure_depart = ?, " +
                      "heures_avant_reservation = ?, heures_avant_annulation = ? " +
                      "WHERE id = ?";

        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, avionId);
            pstmt.setInt(2, villeDepartId);
            pstmt.setInt(3, villeArriveeId);
            pstmt.setTimestamp(4, heureDepart);
            pstmt.setInt(5, heuresAvantReservation);
            pstmt.setInt(6, heuresAvantAnnulation);
            pstmt.setInt(7, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
