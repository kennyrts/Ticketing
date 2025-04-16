package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import dbconnect.UtilDB;

public class VolDetails {
    private int volTypeSiegeId;
    private int volId;
    private Timestamp heureDepart;
    private Timestamp limiteReservation;
    private Timestamp limiteAnnulation;
    private int villeDepartId;
    private String villeDepart;
    private int villeArriveeId;
    private String villeArrivee;
    private int avionId;
    private String avion;
    private int typeSiegeId;
    private String typeSiege;
    private double prix;
    private double prixPromo;
    private int nombreSiegesPromo;
    private double pourcentagePromo;
    private int siegeLibre;
    private int siegeLibrePromo;

    // Constructors
    public VolDetails() {}

    // Getters and Setters
    public int getVolTypeSiegeId() { return volTypeSiegeId; }
    public void setVolTypeSiegeId(int volTypeSiegeId) { this.volTypeSiegeId = volTypeSiegeId; }
    public int getVolId() { return volId; }
    public void setVolId(int volId) { this.volId = volId; }
    public Timestamp getHeureDepart() { return heureDepart; }
    public void setHeureDepart(Timestamp heureDepart) { this.heureDepart = heureDepart; }
    public Timestamp getLimiteReservation() { return limiteReservation; }
    public void setLimiteReservation(Timestamp limiteReservation) { this.limiteReservation = limiteReservation; }
    public Timestamp getLimiteAnnulation() { return limiteAnnulation; }
    public void setLimiteAnnulation(Timestamp limiteAnnulation) { this.limiteAnnulation = limiteAnnulation; }
    public int getVilleDepartId() { return villeDepartId; }
    public void setVilleDepartId(int villeDepartId) { this.villeDepartId = villeDepartId; }
    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }
    public int getVilleArriveeId() { return villeArriveeId; }
    public void setVilleArriveeId(int villeArriveeId) { this.villeArriveeId = villeArriveeId; }
    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }
    public int getAvionId() { return avionId; }
    public void setAvionId(int avionId) { this.avionId = avionId; }
    public String getAvion() { return avion; }
    public void setAvion(String avion) { this.avion = avion; }
    public int getTypeSiegeId() { return typeSiegeId; }
    public void setTypeSiegeId(int typeSiegeId) { this.typeSiegeId = typeSiegeId; }
    public String getTypeSiege() { return typeSiege; }
    public void setTypeSiege(String typeSiege) { this.typeSiege = typeSiege; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public double getPrixPromo() { return prixPromo; }
    public void setPrixPromo(double prixPromo) { this.prixPromo = prixPromo; }
    public int getNombreSiegesPromo() { return nombreSiegesPromo; }
    public void setNombreSiegesPromo(int nombreSiegesPromo) { this.nombreSiegesPromo = nombreSiegesPromo; }
    public double getPourcentagePromo() { return pourcentagePromo; }
    public void setPourcentagePromo(double pourcentagePromo) { this.pourcentagePromo = pourcentagePromo; }
    public int getSiegeLibre() { return siegeLibre; }
    public void setSiegeLibre(int siegeLibre) { this.siegeLibre = siegeLibre; }
    public int getSiegeLibrePromo() { return siegeLibrePromo; }
    public void setSiegeLibrePromo(int siegeLibrePromo) { this.siegeLibrePromo = siegeLibrePromo; }

    /**
     * Get the current applicable price based on promotional seat availability
     * @return The promotional price if promo seats are available, otherwise the regular price
     */
    public double getPrixActuel() {
        // If there are promotional seats available and there is a promotion
        if (siegeLibrePromo > 0 && pourcentagePromo > 0) {
            return prixPromo;
        }
        // Return regular price if no promo seats available or no promotion
        return prix;
    }

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
    public static List<VolDetails> search(int villeDepartId, int villeArriveeId, 
                                        Timestamp dateDebut, Timestamp dateFin,
                                        double prixMin, double prixMax) throws Exception {
        List<VolDetails> results = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM vol_disponibles WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Add conditions based on provided criteria
        if (villeDepartId > 0) {
            query.append(" AND ville_depart_id = ?");
            params.add(villeDepartId);
        }
        
        if (villeArriveeId > 0) {
            query.append(" AND ville_arrivee_id = ?");
            params.add(villeArriveeId);
        }
        
        if (dateDebut != null) {
            query.append(" AND heure_depart >= ?");
            params.add(dateDebut);
        }
        
        if (dateFin != null) {
            query.append(" AND heure_depart <= ?");
            params.add(dateFin);
        }
        
        if (prixMin > 0) {
            query.append(" AND CASE WHEN siege_libre_promo > 0 AND pourcentage_promo > 0 THEN prix_promo ELSE prix END >= ?");
            params.add(prixMin);
        }
        
        if (prixMax > 0) {
            query.append(" AND CASE WHEN siege_libre_promo > 0 AND pourcentage_promo > 0 THEN prix_promo ELSE prix END <= ?");
            params.add(prixMax);
        }
        
        // Add order by clause
        query.append(" ORDER BY heure_depart");

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
                    VolDetails detail = new VolDetails();
                    detail.setVolTypeSiegeId(rs.getInt("vol_type_siege_id"));
                    detail.setVolId(rs.getInt("vol_id"));
                    detail.setHeureDepart(rs.getTimestamp("heure_depart"));
                    detail.setLimiteReservation(rs.getTimestamp("limite_reservation"));
                    detail.setLimiteAnnulation(rs.getTimestamp("limite_annulation"));
                    detail.setVilleDepartId(rs.getInt("ville_depart_id"));
                    detail.setVilleDepart(rs.getString("ville_depart"));
                    detail.setVilleArriveeId(rs.getInt("ville_arrivee_id"));
                    detail.setVilleArrivee(rs.getString("ville_arrivee"));
                    detail.setAvionId(rs.getInt("avion_id"));
                    detail.setAvion(rs.getString("avion"));
                    detail.setTypeSiegeId(rs.getInt("type_siege_id"));
                    detail.setTypeSiege(rs.getString("type_siege"));
                    detail.setPrix(rs.getDouble("prix"));
                    detail.setPrixPromo(rs.getDouble("prix_promo"));
                    detail.setNombreSiegesPromo(rs.getInt("nombre_sieges_promo"));
                    detail.setPourcentagePromo(rs.getDouble("pourcentage_promo"));
                    detail.setSiegeLibre(rs.getInt("siege_libre"));
                    detail.setSiegeLibrePromo(rs.getInt("siege_libre_promo"));
                    results.add(detail);
                }
            }
        }
        
        return results;
    }
} 