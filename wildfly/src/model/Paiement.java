package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import dbconnect.UtilDB;

public class Paiement {
    private int id;
    private int reservationId;
    private double montant;
    private Timestamp datePaiement;
    private Timestamp dateCreation;

    // Constructeurs
    public Paiement() {}

    public Paiement(int reservationId, double montant, Timestamp datePaiement) {
        this.reservationId = reservationId;
        this.montant = montant;
        this.datePaiement = datePaiement;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public Timestamp getDatePaiement() { return datePaiement; }
    public void setDatePaiement(Timestamp datePaiement) { this.datePaiement = datePaiement; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    /**
     * Insérer un nouveau paiement
     */
    public static int insert(int reservationId, double montant, Timestamp datePaiement) throws Exception {
        String query = "INSERT INTO paiement (reservation_id, montant, date_paiement) VALUES (?, ?, ?) RETURNING id";
        
        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, reservationId);
            pstmt.setDouble(2, montant);
            pstmt.setTimestamp(3, datePaiement);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new Exception("Échec de l'insertion du paiement");
            }
        }
    }

    /**
     * Vérifier si une réservation a déjà un paiement
     */
    public static boolean reservationEstPayee(int reservationId) throws Exception {
        String query = "SELECT COUNT(*) FROM paiement WHERE reservation_id = ?";
        
        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, reservationId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    /**
     * Récupérer un paiement par ID de réservation
     */
    public static Paiement getByReservationId(int reservationId) throws Exception {
        String query = "SELECT * FROM paiement WHERE reservation_id = ?";
        
        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, reservationId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Paiement paiement = new Paiement();
                    paiement.setId(rs.getInt("id"));
                    paiement.setReservationId(rs.getInt("reservation_id"));
                    paiement.setMontant(rs.getDouble("montant"));
                    paiement.setDatePaiement(rs.getTimestamp("date_paiement"));
                    paiement.setDateCreation(rs.getTimestamp("date_creation"));
                    return paiement;
                }
                return null;
            }
        }
    }
}