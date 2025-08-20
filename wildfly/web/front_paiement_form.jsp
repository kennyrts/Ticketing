<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Reservation" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Paiement - Airline Booking</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
        }
        
        .container {
            max-width: 800px;
            margin: 50px auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .header {
            background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        
        .header h1 {
            margin: 0;
            font-size: 2em;
        }
        
        .content {
            padding: 30px;
        }
        
        .reservation-details {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
        }
        
        .reservation-details h3 {
            margin: 0 0 15px 0;
            color: #333;
        }
        
        .detail-row {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            border-bottom: 1px solid #eee;
        }
        
        .detail-row:last-child {
            border-bottom: none;
        }
        
        .detail-label {
            font-weight: bold;
            color: #666;
        }
        
        .detail-value {
            color: #333;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #333;
        }
        
        .form-group input {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
            box-sizing: border-box;
        }
        
        .form-group input:focus {
            outline: none;
            border-color: #4CAF50;
        }
        
        .button-group {
            display: flex;
            gap: 15px;
            justify-content: center;
            margin-top: 30px;
        }
        
        .btn {
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            transition: background-color 0.3s;
        }
        
        .btn-primary {
            background-color: #4CAF50;
            color: white;
        }
        
        .btn-primary:hover {
            background-color: #45a049;
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        
        .error-message {
            background: #ffebee;
            color: #c62828;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #ffcdd2;
        }
        
        .info-box {
            background: #e3f2fd;
            color: #1565c0;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            border: 1px solid #bbdefb;
        }
        
        .price-highlight {
            background: #e8f5e8;
            color: #2e7d32;
            padding: 15px;
            border-radius: 5px;
            text-align: center;
            font-size: 1.2em;
            font-weight: bold;
            margin: 20px 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>💳 Paiement de Réservation</h1>
        </div>
        
        <div class="content">
            <% 
            Reservation reservation = (Reservation) request.getAttribute("reservation");
            String error = (String) request.getAttribute("error");
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm");
            
            if (error != null) {
            %>
                <div class="error-message">
                    <strong>Erreur:</strong> <%= error %>
                </div>
            <% } %>
            
            <% if (reservation != null) { %>
            <div class="reservation-details">
                <h3>📋 Détails de la réservation</h3>
                <div class="detail-row">
                    <span class="detail-label">Vol:</span>
                    <span class="detail-value"><%= reservation.getVilleDepart() %> → <%= reservation.getVilleArrivee() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Départ:</span>
                    <span class="detail-value"><%= sdf.format(reservation.getHeureDepart()) %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Avion:</span>
                    <span class="detail-value"><%= reservation.getAvion() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Siège:</span>
                    <span class="detail-value"><%= reservation.getTypeSiege() %></span>
                </div>
                <div class="detail-row">
                    <span class="detail-label">Réservé le:</span>
                    <span class="detail-value"><%= sdf.format(reservation.getDateReservation()) %></span>
                </div>
                <% if (reservation.isEstPromo()) { %>
                <div class="detail-row">
                    <span class="detail-label">Tarif:</span>
                    <span class="detail-value" style="color: #4CAF50; font-weight: bold;">🎉 Promotion</span>
                </div>
                <% } %>
            </div>
            
            <div class="price-highlight">
                💰 Prix de la réservation: <%= String.format("%.2f", reservation.getPrix()) %> €
            </div>
            
            <div class="info-box">
                <strong>ℹ️ Information:</strong> Veuillez saisir le montant que vous souhaitez payer et la date de paiement.
            </div>
            
            <form action="front_paiement_traiter" method="post">
                <input type="hidden" name="reservation_id" value="<%= reservation.getId() %>">
                
                <div class="form-group">
                    <label for="montant">💰 Montant à payer (€):</label>
                    <input type="number" 
                           id="montant" 
                           name="montant" 
                           step="0.01" 
                           min="0" 
                           value="<%= String.format("%.2f", reservation.getPrix()) %>"
                           required>
                </div>
                
                <div class="form-group">
                    <label for="date_paiement">📅 Date de paiement:</label>
                    <input type="datetime-local" 
                           id="date_paiement" 
                           name="date_paiement" 
                           required>
                </div>
                
                <div class="button-group">
                    <a href="front_reservations" class="btn btn-secondary">Annuler</a>
                    <button type="submit" class="btn btn-primary">Confirmer le paiement</button>
                </div>
            </form>
            <% } %>
        </div>
    </div>
    
    <script>
        // Définir la date actuelle par défaut
        document.addEventListener('DOMContentLoaded', function() {
            const now = new Date();
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            const day = String(now.getDate()).padStart(2, '0');
            const hours = String(now.getHours()).padStart(2, '0');
            const minutes = String(now.getMinutes()).padStart(2, '0');
            
            const dateTimeString = `${year}-${month}-${day}T${hours}:${minutes}`;
            document.getElementById('date_paiement').value = dateTimeString;
        });
    </script>
</body>
</html>