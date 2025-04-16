package controllers;

import mg.itu.prom16.util.ModelView;
import mg.itu.prom16.annotations.Auth;
import mg.itu.prom16.annotations.Controller;
import mg.itu.prom16.annotations.Get;
import mg.itu.prom16.annotations.Url;
import mg.itu.prom16.annotations.Post;
import mg.itu.prom16.annotations.Param;
import model.Ville;
import model.Avion;
import java.sql.Timestamp;
import model.Vol;
import model.VolTypeSiege;
import java.util.List;
import model.VolSearch;
import mg.itu.prom16.annotations.FormObject;

@Controller
@Auth("admin")
public class BackOfficeController {
    
    @Get
    @Url("vol_form")
    public ModelView volForm() throws Exception {
        ModelView mv = new ModelView();
        mv.setUrl("vol_form.jsp");
        
        // Get all cities and aircraft and add them to ModelView
        mv.addObject("villes", Ville.getAll());
        mv.addObject("avions", Avion.getAll());
        
        return mv;
    }

    @Post
    @Url("vol_create")
    public ModelView createVol(@FormObject Vol vol) {
        ModelView mv = new ModelView();
        try {
            // Insert the flight
            int volId = Vol.insert(
                vol.getAvionId(), 
                vol.getVilleDepartId(), 
                vol.getVilleArriveeId(), 
                vol.getHeureDepart(), 
                vol.getHeuresAvantReservation(), 
                vol.getHeuresAvantAnnulation()
            );

            // Redirect back to form with success message
            mv.setUrl("vol_form");
            mv.addObject("success", "Flight created successfully with ID: " + volId);
        } catch (Exception e) {
            mv.setUrl("vol_form");
            mv.addObject("error", "Error creating flight: " + e.getMessage());
            // Re-add the lists for the form
            try {
                mv.addObject("villes", Ville.getAll());
                mv.addObject("avions", Avion.getAll());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mv;
    }

    @Get
    @Url("vol_list")
    public ModelView listVols() throws Exception {
        ModelView mv = new ModelView();
        mv.setUrl("vol_list.jsp");
        mv.addObject("vols", Vol.getAll());
        return mv;
    }

    @Get
    @Url("vol_delete")
    public ModelView deleteVol(@Param(name = "id") String volId) {
        ModelView mv = new ModelView();
        try {
            int id = Integer.parseInt(volId);
            boolean deleted = Vol.delete(id);
            
            mv.setUrl("vol_list");
            if (deleted) {
                mv.addObject("success", "Flight successfully deleted");
            } else {
                mv.addObject("error", "Flight not found");
            }
        } catch (NumberFormatException e) {
            mv.setUrl("vol_list");
            mv.addObject("error", "Invalid flight ID format");
        } catch (Exception e) {
            mv.setUrl("vol_list");
            mv.addObject("error", "Error deleting flight: " + e.getMessage());
        }
        
        // Reload the list of flights
        try {
            mv.addObject("vols", Vol.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return mv;
    }

    @Get
    @Url("vol_edit")
    public ModelView editVol(@Param(name = "id") String volId) {
        ModelView mv = new ModelView();
        try {
            int id = Integer.parseInt(volId);
            Vol vol = Vol.getById(id);
            
            if (vol != null) {
                mv.setUrl("vol_form.jsp");
                mv.addObject("vol", vol);
                // Add lists needed for the form
                mv.addObject("villes", Ville.getAll());
                mv.addObject("avions", Avion.getAll());
            } else {
                mv.setUrl("vol_list");
                mv.addObject("error", "Flight not found");
                mv.addObject("vols", Vol.getAll());
            }
        } catch (NumberFormatException e) {
            mv.setUrl("vol_list");
            mv.addObject("error", "Invalid flight ID format");
            try {
                mv.addObject("vols", Vol.getAll());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            mv.setUrl("vol_list");
            mv.addObject("error", "Error loading flight: " + e.getMessage());
            try {
                mv.addObject("vols", Vol.getAll());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mv;
    }

    @Post
    @Url("vol_update")
    public ModelView updateVol(@Param(name = "id") String volId,
                             @Param(name = "avion_id") String avionId,
                             @Param(name = "ville_depart_id") String villeDepartId,
                             @Param(name = "ville_arrivee_id") String villeArriveeId,
                             @Param(name = "heure_depart") String heureDepart,
                             @Param(name = "heures_avant_reservation") String heuresAvantReservation,
                             @Param(name = "heures_avant_annulation") String heuresAvantAnnulation) {
        ModelView mv = new ModelView();
        try {
            // Convert parameters
            int id = Integer.parseInt(volId);
            int avId = Integer.parseInt(avionId);
            int depId = Integer.parseInt(villeDepartId);
            int arrId = Integer.parseInt(villeArriveeId);
            Timestamp depTime = Timestamp.valueOf(heureDepart.replace("T", " ") + ":00");
            int resHours = Integer.parseInt(heuresAvantReservation);
            int cancelHours = Integer.parseInt(heuresAvantAnnulation);

            // Update the flight
            boolean updated = Vol.update(id, avId, depId, arrId, depTime, resHours, cancelHours);

            if (updated) {
                mv.setUrl("vol_list");
                mv.addObject("success", "Flight updated successfully");
                mv.addObject("vols", Vol.getAll());
            } else {
                mv.setUrl("vol_form");
                mv.addObject("error", "Flight not found");
                mv.addObject("villes", Ville.getAll());
                mv.addObject("avions", Avion.getAll());
            }
        } catch (Exception e) {
            mv.setUrl("vol_form");
            mv.addObject("error", "Error updating flight: " + e.getMessage());
            try {
                mv.addObject("villes", Ville.getAll());
                mv.addObject("avions", Avion.getAll());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mv;
    }

    @Get
    @Url("vol_type_siege_form")
    public ModelView volTypeSiegeForm() throws Exception {
        ModelView mv = new ModelView();
        mv.setUrl("vol_type_siege_form.jsp");
        mv.addObject("vols", Vol.getAll());
        return mv;
    }

    @Post
    @Url("vol_type_siege_create")
    public ModelView createVolTypeSiege(@Param(name = "vol_id") String volId,
                                      @Param(name = "type_siege_id") String typeSiegeId,
                                      @Param(name = "prix") String prix,
                                      @Param(name = "nombre_sieges_promo") String nombreSiegesPromo,
                                      @Param(name = "pourcentage_promo") String pourcentagePromo) {
        ModelView mv = new ModelView();
        try {
            // Convert parameters
            int vId = Integer.parseInt(volId);
            int tsId = Integer.parseInt(typeSiegeId);
            double price = Double.parseDouble(prix);
            int promoSeats = Integer.parseInt(nombreSiegesPromo);
            double promoPercentage = Double.parseDouble(pourcentagePromo);

            // Validate inputs
            if (price < 0) throw new Exception("Price cannot be negative");
            if (promoSeats < 0) throw new Exception("Number of promotional seats cannot be negative");
            if (promoPercentage < 0 || promoPercentage > 100) 
                throw new Exception("Discount percentage must be between 0 and 100");

            // Check if configuration already exists
            if (VolTypeSiege.exists(vId, tsId)) {
                mv.setUrl("vol_type_siege_form");
                mv.addObject("error", "This seat type is already configured for this flight");
                mv.addObject("vols", Vol.getAll());
                return mv;
            }

            // Insert the configuration
            int id = VolTypeSiege.insert(vId, tsId, price, promoSeats, promoPercentage);

            mv.setUrl("vol_type_siege_form");
            mv.addObject("success", "Seat configuration added successfully with ID: " + id);
        } catch (NumberFormatException e) {
            mv.setUrl("vol_type_siege_form");
            mv.addObject("error", "Invalid number format in one or more fields");
        } catch (Exception e) {
            mv.setUrl("vol_type_siege_form");
            mv.addObject("error", "Error creating seat configuration: " + e.getMessage());
        }

        // Reload the flights list
        try {
            mv.addObject("vols", Vol.getAll());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return mv;
    }

    @Get
    @Url("vol_search_form")
    public ModelView volSearchForm() throws Exception {
        ModelView mv = new ModelView();
        mv.setUrl("vol_search_form.jsp");
        mv.addObject("villes", Ville.getAll());
        return mv;
    }

    @Post
    @Url("vol_search")
    public ModelView searchVol(@Param(name = "ville_depart_id") String villeDepartId,
                             @Param(name = "ville_arrivee_id") String villeArriveeId,
                             @Param(name = "date_debut") String dateDebut,
                             @Param(name = "date_fin") String dateFin,
                             @Param(name = "prix_min") String prixMin,
                             @Param(name = "prix_max") String prixMax) {
        ModelView mv = new ModelView();
        try {
            // Convert parameters
            int depId = villeDepartId.isEmpty() ? 0 : Integer.parseInt(villeDepartId);
            int arrId = villeArriveeId.isEmpty() ? 0 : Integer.parseInt(villeArriveeId);
            Timestamp startDate = dateDebut.isEmpty() ? null : Timestamp.valueOf(dateDebut.replace("T", " ") + ":00");
            Timestamp endDate = dateFin.isEmpty() ? null : Timestamp.valueOf(dateFin.replace("T", " ") + ":00");
            double minPrice = prixMin.isEmpty() ? 0 : Double.parseDouble(prixMin);
            double maxPrice = prixMax.isEmpty() ? 0 : Double.parseDouble(prixMax);

            // Execute search
            List<VolSearch> results = VolSearch.search(depId, arrId, startDate, endDate, minPrice, maxPrice);

            // Set results and return to results page
            mv.setUrl("vol_search_results.jsp");
            mv.addObject("results", results);
            mv.addObject("villes", Ville.getAll());  // For filtering options
        } catch (Exception e) {
            mv.setUrl("vol_search_form.jsp");
            mv.addObject("error", "Error during search: " + e.getMessage());
            try {
                mv.addObject("villes", Ville.getAll());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mv;
    }
} 