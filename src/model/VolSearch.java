package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import dbconnect.UtilDB;

public class VolSearch {
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
    private double prix;  // Base price for the flight
    private double prixPromo;  // Promotional price if applicable
    private int nombreSiegesPromo;  // Number of promotional seats
    private double pourcentagePromo;  // Discount percentage
    private int typeSiegeId;  // ID of the seat type
    private String typeSiegeNom;  // Name of the seat type (Business/Economy)

    // Constructors
    public VolSearch() {}

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
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public double getPrixPromo() { return prixPromo; }
    public void setPrixPromo(double prixPromo) { this.prixPromo = prixPromo; }
    public int getNombreSiegesPromo() { return nombreSiegesPromo; }
    public void setNombreSiegesPromo(int nombreSiegesPromo) { this.nombreSiegesPromo = nombreSiegesPromo; }
    public double getPourcentagePromo() { return pourcentagePromo; }
    public void setPourcentagePromo(double pourcentagePromo) { this.pourcentagePromo = pourcentagePromo; }
    public int getTypeSiegeId() { return typeSiegeId; }
    public void setTypeSiegeId(int typeSiegeId) { this.typeSiegeId = typeSiegeId; }
    public String getTypeSiegeNom() { return typeSiegeNom; }
    public void setTypeSiegeNom(String typeSiegeNom) { this.typeSiegeNom = typeSiegeNom; }

    /**
     * Search flights based on criteria
     * @param villeDepartId Departure city ID (optional, 0 for any)
     * @param villeArriveeId Arrival city ID (optional, 0 for any)
     * @param dateDebut Start date (optional, null for any)
     * @param dateFin End date (optional, null for any)
     * @param prixMin Minimum price (optional, 0 for any)
     * @param prixMax Maximum price (optional, 0 for any)
     * @return List of matching flights with their details
     * @throws Exception if search fails
     */
    public static List<VolSearch> search(int villeDepartId, int villeArriveeId, 
                                        Timestamp dateDebut, Timestamp dateFin,
                                        double prixMin, double prixMax) throws Exception {
        List<VolSearch> results = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT DISTINCT v.*, " +
            "a.modele as avion_modele, " +
            "vd.nom as ville_depart_nom, " +
            "va.nom as ville_arrivee_nom, " +
            "vts.prix, " +
            "vts.nombre_sieges_promo, " +
            "vts.pourcentage_promo, " +
            "vts.type_siege_id, " +
            "ts.nom as type_siege_nom, " +
            "CASE WHEN vts.pourcentage_promo > 0 " +
            "     THEN vts.prix * (1 - vts.pourcentage_promo / 100) " +
            "     ELSE vts.prix " +
            "END as prix_promo " +
            "FROM vol v " +
            "JOIN avion a ON v.avion_id = a.id " +
            "JOIN ville vd ON v.ville_depart_id = vd.id " +
            "JOIN ville va ON v.ville_arrivee_id = va.id " +
            "LEFT JOIN vol_type_siege vts ON v.id = vts.vol_id " +
            "LEFT JOIN type_siege ts ON vts.type_siege_id = ts.id " +
            "WHERE 1=1"
        );
        
        List<Object> params = new ArrayList<>();
        
        // Add conditions based on provided criteria
        if (villeDepartId > 0) {
            query.append(" AND v.ville_depart_id = ?");
            params.add(villeDepartId);
        }
        
        if (villeArriveeId > 0) {
            query.append(" AND v.ville_arrivee_id = ?");
            params.add(villeArriveeId);
        }
        
        if (dateDebut != null) {
            query.append(" AND v.heure_depart >= ?");
            params.add(dateDebut);
        }
        
        if (dateFin != null) {
            query.append(" AND v.heure_depart <= ?");
            params.add(dateFin);
        }
        
        if (prixMin > 0) {
            query.append(" AND (vts.prix >= ? OR vts.prix IS NULL)");
            params.add(prixMin);
        }
        
        if (prixMax > 0) {
            query.append(" AND (vts.prix <= ? OR vts.prix IS NULL)");
            params.add(prixMax);
        }
        
        // Add order by clause
        query.append(" ORDER BY v.heure_depart");

        try (Connection connection = UtilDB.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(query.toString())) {
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) param);
                } else if (param instanceof Timestamp) {
                    pstmt.setTimestamp(i + 1, (Timestamp) param);
                }
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    VolSearch vol = new VolSearch();
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
                    vol.setPrix(rs.getDouble("prix"));
                    vol.setNombreSiegesPromo(rs.getInt("nombre_sieges_promo"));
                    vol.setPourcentagePromo(rs.getDouble("pourcentage_promo"));
                    vol.setPrixPromo(rs.getDouble("prix_promo"));
                    vol.setTypeSiegeId(rs.getInt("type_siege_id"));
                    vol.setTypeSiegeNom(rs.getString("type_siege_nom"));
                    results.add(vol);
                }
            }
        }
        
        return results;
    }
} 