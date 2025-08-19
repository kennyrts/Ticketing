package controllers;

import mg.itu.prom16.util.ModelView;
import mg.itu.prom16.util.MySession;
import mg.itu.prom16.util.UploadedFile;
import mg.itu.prom16.annotations.Auth;
import mg.itu.prom16.annotations.Controller;
import mg.itu.prom16.annotations.FileUpload;
import mg.itu.prom16.annotations.Get;
import mg.itu.prom16.annotations.Url;
import mg.itu.prom16.annotations.Post;
import mg.itu.prom16.annotations.Param;
import mg.itu.prom16.annotations.PdfExport;
import model.Reservation;
import model.Ville;
import model.VolDetails;
import java.sql.Timestamp;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import model.Enfant;

@Controller
@Auth("user")
public class FrontOfficeController {
    
    @Get
    @Url("front_vol_search_form")
    public ModelView volSearchForm() throws Exception {
        ModelView mv = new ModelView();
        mv.setUrl("front_vol_search_form.jsp");
        
        // Get all cities for the search form
        mv.addObject("villes", Ville.getAll());
        
        return mv;
    }

    @Post
    @Url("front_vol_search")
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
            List<VolDetails> results = VolDetails.search(depId, arrId, startDate, endDate, minPrice, maxPrice);

            // Set results and return to results page
            mv.setUrl("front_vol_search_results.jsp");
            mv.addObject("results", results);
            mv.addObject("villes", Ville.getAll());  // For filtering options
        } catch (Exception e) {
            mv.setUrl("front_vol_search_form.jsp");
            mv.addObject("error", "Error during search: " + e.getMessage());
            try {
                mv.addObject("villes", Ville.getAll());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mv;
    }    

    @Post
    @Url("front_vol_reserver")
    public ModelView reserverVol(@Param(name = "vol_type_siege_id") String volTypeSiegeId,
                                @Param(name = "vol_id") String volId,
                                @Param(name = "type_siege_id") String typeSiegeId,
                                @Param(name = "prix") String prixStr,
                                @Param(name = "est_promo") String estPromoStr,
                                @Param(name = "age") String ageStr,
                                @FileUpload(name = "photo") UploadedFile photo,
                                MySession session) {
        ModelView mv = new ModelView();
        
        try {
            int userId = (int) session.get("id");
            int volIdInt = Integer.parseInt(volId);
            int typeSiegeIdInt = Integer.parseInt(typeSiegeId);
            double prix = Double.parseDouble(prixStr);
            boolean estPromo = Boolean.parseBoolean(estPromoStr);
            int age = Integer.parseInt(ageStr);
            
            // Appliquer la réduction enfant si applicable
            Enfant regleEnfant = Enfant.getCurrentRule();
            if (regleEnfant != null && age <= regleEnfant.getAgeMax()) {
                // Calculer le nouveau prix avec la réduction enfant
                double reductionEnfant = regleEnfant.getReduction() / 100.0;
                prix = prix * (1 - reductionEnfant);
            }
            
            // Traiter la photo
            String photoPath = null;
            if (photo != null && !photo.getFileName().isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + photo.getFileName();
                // Create directories if they don't exist
                File uploadDir = new File("uploads/reservations");
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Save the file using InputStream
                File destFile = new File(uploadDir, fileName);
                try (InputStream in = photo.getInputStream();
                    FileOutputStream out = new FileOutputStream(destFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
                photoPath = "reservations/" + fileName;
            }            
            
            // Insérer la réservation
            Reservation.insert(volIdInt, userId, typeSiegeIdInt, estPromo, prix, photoPath);
            
            mv.setUrl("front_reservations");
            mv.addObject("success", "Réservation effectuée avec succès!");
            
        } catch (Exception e) {
            e.printStackTrace();
            mv.setUrl("front_vol_search_form.jsp");
            mv.addObject("error", "Erreur lors de la réservation: " + e.getMessage());
        }
        
        return mv;
    }

    @Get
    @Url("front_reservations")
    public ModelView reservations(MySession session) {
        ModelView mv = new ModelView();
        try {
            // Get user ID from session
            int userId = (int) session.get("id");
            
            // Get user's reservations
            List<Reservation> reservations = Reservation.getByIdUtilisateur(userId);
            
            // Convert photos to base64
            for (Reservation res : reservations) {
                if (res.getPhoto() != null) {
                    File file = new File("D:/ITU/L3/S5/Nouveau dossier/wildfly-10.0.0.Final/bin/uploads/" + res.getPhoto());
                    if (file.exists()) {
                        byte[] fileContent = Files.readAllBytes(file.toPath());
                        String base64 = Base64.getEncoder().encodeToString(fileContent);
                        res.setPhoto("data:image/jpeg;base64," + base64);
                    }
                }
            }
            
            mv.setUrl("front_reservations.jsp");
            mv.addObject("reservations", reservations);
        } catch (Exception e) {
            mv.setUrl("front_vol_search_form");
            mv.addObject("error", "Failed to load reservations: " + e.getMessage());
            try {
                mv.addObject("villes", Ville.getAll());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mv;
    }

    @Post
    @Url("front_reservation_annuler")
    public ModelView annulerReservation(@Param(name = "reservation_id") String reservationId,
                                      MySession session) {
        ModelView mv = new ModelView();
        try {
            // Get user ID from session
            int userId = (int) session.get("id");
            
            // Delete reservation
            boolean success = Reservation.delete(Integer.parseInt(reservationId), userId);
            
            // Redirect back to reservations page
            mv.setUrl("front_reservations");
            mv.addObject("success", "Reservation cancelled successfully");
        } catch (Exception e) {
            mv.setUrl("front_reservations");
            mv.addObject("error", "Failed to cancel reservation: " + e.getMessage());
        }
        return mv;
    }

    @Get
    @PdfExport(filename = "reservation", title = "Détails de la Réservation", forceDownload = true)
    @Url("front_reservation_pdf")
    public ModelView exportReservationPdf(@Param(name = "reservation_id") String reservationId,
                                         MySession session) {
        ModelView mv = new ModelView();
        try {
            // Get user ID from session to ensure security
            int userId = (int) session.get("id");
            int resId = Integer.parseInt(reservationId);
            
            // Get the specific reservation for this user
            List<Reservation> userReservations = Reservation.getByIdUtilisateur(userId);
            Reservation reservation = null;
            
            for (Reservation res : userReservations) {
                if (res.getId() == resId) {
                    reservation = res;
                    break;
                }
            }
            
            if (reservation == null) {
                throw new Exception("Réservation non trouvée ou accès non autorisé");
            }
            
            // Get user information
            String userName = (String) session.get("nom");
            String userLogin = (String) session.get("login");
            
            // Prepare data for PDF export
            mv.addObject("reservation", reservation);
            mv.addObject("userName", userName);
            mv.addObject("userLogin", userLogin);
            mv.addObject("exportDate", new java.util.Date().toString());
            mv.addObject("reservationId", resId);
            
            // Add reservation details
            mv.addObject("volId", reservation.getVolId());
            mv.addObject("typeSiegeId", reservation.getTypeSiegeId());
            mv.addObject("prix", reservation.getPrix());
            mv.addObject("estPromo", reservation.isEstPromo() ? "Oui" : "Non");
            
        } catch (Exception e) {
            // If error, return to reservations page with error message
            mv.setUrl("front_reservations");
            mv.addObject("error", "Erreur lors de l'export PDF: " + e.getMessage());
            try {
                int userId = (int) session.get("id");
                mv.addObject("reservations", Reservation.getByIdUtilisateur(userId));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mv;
    }

    @Get
    @PdfExport(filename = "mes_reservations", title = "Mes Réservations", forceDownload = true)
    @Url("front_reservations_pdf")
    public ModelView exportAllReservationsPdf(MySession session) {
        ModelView mv = new ModelView();
        try {
            // Get user ID from session
            int userId = (int) session.get("id");
            String userName = (String) session.get("nom");
            String userLogin = (String) session.get("login");
            
            // Get user's reservations
            List<Reservation> reservations = Reservation.getByIdUtilisateur(userId);
            
            // Prepare data for PDF export
            mv.addObject("reservations", reservations);
            mv.addObject("userName", userName);
            mv.addObject("userLogin", userLogin);
            mv.addObject("exportDate", new java.util.Date().toString());
            mv.addObject("totalReservations", reservations.size());
            
            // Calculate total amount
            double totalAmount = 0;
            for (Reservation res : reservations) {
                totalAmount += res.getPrix();
            }
            mv.addObject("totalAmount", totalAmount);
            
        } catch (Exception e) {
            // If error, return to reservations page with error message
            mv.setUrl("front_reservations");
            mv.addObject("error", "Erreur lors de l'export PDF: " + e.getMessage());
            try {
                int userId = (int) session.get("id");
                mv.addObject("reservations", Reservation.getByIdUtilisateur(userId));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return mv;
    }
} 