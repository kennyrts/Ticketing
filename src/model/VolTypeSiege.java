package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import dbconnect.UtilDB;

public class VolTypeSiege {
    private int id;
    private int volId;
    private int typeSiegeId;
    private double prix;
    private int nombreSiegesPromo;
    private double pourcentagePromo;
    private String typeSiegeNom;  // For display purposes

    // Constructors
    public VolTypeSiege() {}

    public VolTypeSiege(int volId, int typeSiegeId, double prix, 
                       int nombreSiegesPromo, double pourcentagePromo) {
        this.volId = volId;
        this.typeSiegeId = typeSiegeId;
        this.prix = prix;
        this.nombreSiegesPromo = nombreSiegesPromo;
        this.pourcentagePromo = pourcentagePromo;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public int getTypeSiegeId() { return typeSiegeId; }
    public void setTypeSiegeId(int typeSiegeId) { this.typeSiegeId = typeSiegeId; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public int getNombreSiegesPromo() { return nombreSiegesPromo; }
    public void setNombreSiegesPromo(int nombreSiegesPromo) { this.nombreSiegesPromo = nombreSiegesPromo; }
    public double getPourcentagePromo() { return pourcentagePromo; }
    public void setPourcentagePromo(double pourcentagePromo) { this.pourcentagePromo = pourcentagePromo; }
    public String getTypeSiegeNom() { return typeSiegeNom; }
    public void setTypeSiegeNom(String typeSiegeNom) { this.typeSiegeNom = typeSiegeNom; }

    /**
     * Insert a new seat type configuration for a flight
     * @param volId Flight ID
     * @param typeSiegeId Seat type ID
     * @param prix Base price
     * @param nombreSiegesPromo Number of promotional seats
     * @param pourcentagePromo Discount percentage
     * @return The ID of the newly inserted record
     * @throws Exception if insertion fails
     */
    public static int insert(int volId, int typeSiegeId, double prix, 
                           int nombreSiegesPromo, double pourcentagePromo) throws Exception {
        String query = "INSERT INTO vol_type_siege (vol_id, type_siege_id, prix, " +
                      "nombre_sieges_promo, pourcentage_promo) " +
                      "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, volId);
            pstmt.setInt(2, typeSiegeId);
            pstmt.setDouble(3, prix);
            pstmt.setInt(4, nombreSiegesPromo);
            pstmt.setDouble(5, pourcentagePromo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new Exception("Failed to get inserted seat type configuration ID");
            }
        }
    }

    /**
     * Get all seat type configurations for a specific flight
     * @param volId Flight ID
     * @return List of seat type configurations
     * @throws Exception if query fails
     */
    public static java.util.List<VolTypeSiege> getByVolId(int volId) throws Exception {
        java.util.List<VolTypeSiege> configs = new java.util.ArrayList<>();
        String query = "SELECT vts.*, ts.nom as type_siege_nom " +
                      "FROM vol_type_siege vts " +
                      "JOIN type_siege ts ON vts.type_siege_id = ts.id " +
                      "WHERE vts.vol_id = ?";

        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, volId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    VolTypeSiege config = new VolTypeSiege();
                    config.setId(rs.getInt("id"));
                    config.setVolId(rs.getInt("vol_id"));
                    config.setTypeSiegeId(rs.getInt("type_siege_id"));
                    config.setPrix(rs.getDouble("prix"));
                    config.setNombreSiegesPromo(rs.getInt("nombre_sieges_promo"));
                    config.setPourcentagePromo(rs.getDouble("pourcentage_promo"));
                    config.setTypeSiegeNom(rs.getString("type_siege_nom"));
                    configs.add(config);
                }
            }
        }
        return configs;
    }

    /**
     * Check if a seat type configuration already exists for a flight
     * @param volId Flight ID
     * @param typeSiegeId Seat type ID
     * @return true if configuration exists, false otherwise
     * @throws Exception if query fails
     */
    public static boolean exists(int volId, int typeSiegeId) throws Exception {
        String query = "SELECT 1 FROM vol_type_siege WHERE vol_id = ? AND type_siege_id = ?";
        
        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            pstmt.setInt(1, volId);
            pstmt.setInt(2, typeSiegeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
} 