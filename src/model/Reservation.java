package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import dbconnect.UtilDB;

public class Reservation {
    private int id;
    private int volId;
    private int utilisateurId;
    private int typeSiegeId;
    private boolean estPromo;
    private Timestamp dateReservation;    
    private double prix;
    
    // Additional fields for display
    private String villeDepart;
    private String villeArrivee;
    private Timestamp heureDepart;
    private String typeSiege;
    private String avion;

    // Add to class attributes
    private boolean estAnnulable;
    private Timestamp limiteAnnulation;

    // Constructors
    public Reservation() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public int getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(int utilisateurId) { this.utilisateurId = utilisateurId; }
    public int getTypeSiegeId() { return typeSiegeId; }
    public void setTypeSiegeId(int typeSiegeId) { this.typeSiegeId = typeSiegeId; }
    public boolean isEstPromo() { return estPromo; }
    public void setEstPromo(boolean estPromo) { this.estPromo = estPromo; }
    public Timestamp getDateReservation() { return dateReservation; }
    public void setDateReservation(Timestamp dateReservation) { this.dateReservation = dateReservation; }    
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }
    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }
    public Timestamp getHeureDepart() { return heureDepart; }
    public void setHeureDepart(Timestamp heureDepart) { this.heureDepart = heureDepart; }
    public String getTypeSiege() { return typeSiege; }
    public void setTypeSiege(String typeSiege) { this.typeSiege = typeSiege; }
    public String getAvion() { return avion; }
    public void setAvion(String avion) { this.avion = avion; }
    public boolean isEstAnnulable() { return estAnnulable; }
    public void setEstAnnulable(boolean estAnnulable) { this.estAnnulable = estAnnulable; }
    public Timestamp getLimiteAnnulation() { return limiteAnnulation; }
    public void setLimiteAnnulation(Timestamp limiteAnnulation) { this.limiteAnnulation = limiteAnnulation; }

    /**
     * Insert a new reservation
     * @param volId Flight ID
     * @param utilisateurId User ID
     * @param typeSiegeId Seat type ID
     * @param estPromo Is promotional seat
     * @param prix Price paid
     * @return The ID of the newly inserted reservation
     * @throws Exception if insertion fails
     */
    public static int insert(int volId, int utilisateurId, int typeSiegeId, 
                           boolean estPromo, double prix) throws Exception {
        String query = "INSERT INTO reservation (vol_id, utilisateur_id, type_siege_id, " +
                      "est_promo, date_reservation, prix_paye) " +
                      "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?) RETURNING id";

        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, volId);
            pstmt.setInt(2, utilisateurId);
            pstmt.setInt(3, typeSiegeId);
            pstmt.setBoolean(4, estPromo);
            pstmt.setDouble(5, prix);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new Exception("Failed to insert reservation");
            }
        }
    }

    /**
     * Get all reservations for a user with flight details
     * @param utilisateurId User ID
     * @return List of reservations with flight details
     * @throws Exception if query fails
     */
    public static List<Reservation> getByIdUtilisateur(int utilisateurId) throws Exception {
        List<Reservation> reservations = new ArrayList<>();
        String query = 
            "SELECT r.*, v.heure_depart, " +
            "vd.nom as ville_depart, va.nom as ville_arrivee, " +
            "ts.nom as type_siege, a.modele as avion, " +
            "(v.heure_depart - (v.heures_avant_annulation || ' hours')::interval) as limite_annulation, " +
            "CURRENT_TIMESTAMP < (v.heure_depart - (v.heures_avant_annulation || ' hours')::interval) as est_annulable " +
            "FROM reservation r " +
            "JOIN vol v ON r.vol_id = v.id " +
            "JOIN ville vd ON v.ville_depart_id = vd.id " +
            "JOIN ville va ON v.ville_arrivee_id = va.id " +
            "JOIN type_siege ts ON r.type_siege_id = ts.id " +
            "JOIN avion a ON v.avion_id = a.id " +
            "WHERE r.utilisateur_id = ? " +
            "ORDER BY r.date_reservation DESC";

        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, utilisateurId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = new Reservation();
                    reservation.setId(rs.getInt("id"));
                    reservation.setVolId(rs.getInt("vol_id"));
                    reservation.setUtilisateurId(rs.getInt("utilisateur_id"));
                    reservation.setTypeSiegeId(rs.getInt("type_siege_id"));
                    reservation.setEstPromo(rs.getBoolean("est_promo"));
                    reservation.setDateReservation(rs.getTimestamp("date_reservation"));
                    reservation.setPrix(rs.getDouble("prix_paye"));
                    
                    // Additional flight details
                    reservation.setVilleDepart(rs.getString("ville_depart"));
                    reservation.setVilleArrivee(rs.getString("ville_arrivee"));
                    reservation.setHeureDepart(rs.getTimestamp("heure_depart"));
                    reservation.setTypeSiege(rs.getString("type_siege"));
                    reservation.setAvion(rs.getString("avion"));
                    
                    // Update cancellation details
                    reservation.setLimiteAnnulation(rs.getTimestamp("limite_annulation"));
                    reservation.setEstAnnulable(rs.getBoolean("est_annulable"));
                    
                    reservations.add(reservation);
                }
            }
        }
        return reservations;
    }

    /**
     * Delete a reservation
     * @param id Reservation ID
     * @param utilisateurId User ID (for security check)
     * @return true if deletion was successful
     * @throws Exception if deletion fails or if reservation doesn't belong to user
     */
    public static boolean delete(int id, int utilisateurId) throws Exception {
        String query = "DELETE FROM reservation WHERE id = ? AND utilisateur_id = ?";
        
        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            pstmt.setInt(2, utilisateurId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new Exception("Reservation not found or doesn't belong to user");
            }
            
            return true;
        }
    }
} 