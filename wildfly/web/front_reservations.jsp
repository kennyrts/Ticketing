<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Reservation" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Reservations - Airline Booking</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
        }
        
        .sidebar {
            height: 100vh;
            width: 250px;
            position: fixed;
            top: 0;
            left: 0;
            background-color: #333;
            padding-top: 20px;
            color: white;
        }
        
        .sidebar-header {
            padding: 20px;
            text-align: center;
            border-bottom: 1px solid #444;
        }
        
        .sidebar-menu {
            padding: 20px 0;
        }
        
        .menu-item {
            padding: 15px 25px;
            display: block;
            color: white;
            text-decoration: none;
            transition: all 0.3s;
        }
        
        .menu-item:hover {
            background-color: #444;
            padding-left: 30px;
        }
        
        .menu-item.active {
            background-color: #4CAF50;
        }
        
        .content {
            margin-left: 250px;
            padding: 20px;
        }
        
        .container {
            width: 95%;
            margin: 20px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .new-search-btn {
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }
        
        .new-search-btn:hover {
            background-color: #45a049;
        }
        
        .reservation-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            display: grid;
            grid-template-columns: 3fr 2fr 2fr;
            gap: 20px;
            align-items: center;
            background-color: white;
        }
        
        .reservation-card.promo {
            border-left: 4px solid #4CAF50;
        }
        
        .flight-info {
            display: grid;
            gap: 10px;
        }
        
        .cities {
            font-size: 1.2em;
            font-weight: bold;
        }
        
        .departure-time {
            color: #666;
        }
        
        .reservation-details {
            text-align: center;
            padding: 10px;
            background-color: #f5f5f5;
            border-radius: 4px;
        }
        
        .price-info {
            text-align: right;
        }
        
        .price {
            font-size: 1.5em;
            font-weight: bold;
            color: #4CAF50;
        }
        
        .cancel-btn {
            background-color: #d32f2f;
            color: white;
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            margin-top: 10px;
            width: 100%;
            transition: background-color 0.3s;
        }
        
        .cancel-btn:hover {
            background-color: #b71c1c;
        }
        
        .cancel-btn:disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
        
        .deadline-info {
            color: #666;
            font-size: 0.9em;
            margin-top: 5px;
        }
        
        .deadline-warning {
            color: #d32f2f;
        }
        
        .promo-tag {
            display: inline-block;
            padding: 4px 8px;
            background-color: #4CAF50;
            color: white;
            border-radius: 4px;
            font-size: 0.8em;
            margin-top: 5px;
        }
        
        .user-info {
            padding: 20px;
            border-top: 1px solid #444;
            position: absolute;
            bottom: 0;
            width: 100%;
            box-sizing: border-box;
        }
        
        .logout-btn {
            background-color: #d32f2f;
            color: white;
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            text-decoration: none;
            text-align: center;
            display: block;
            margin-top: 10px;
        }
        
        .logout-btn:hover {
            background-color: #b71c1c;
        }
        
        .no-reservations {
            text-align: center;
            padding: 40px;
            color: #666;
            font-size: 1.2em;
        }
        
        .reservation-photo {
            margin-top: 10px;
        }
        
        /* ===== NOUVEAUX STYLES AMÉLIORÉS ===== */
        .export-buttons {
            margin-bottom: 20px;
            padding: 15px;
            background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
            border-radius: 8px;
            border: 1px solid #e0e6ed;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .export-info {
            display: flex;
            align-items: center;
            color: #555;
            font-weight: 500;
        }
        
        .export-info span:first-child {
            margin-right: 8px;
            font-size: 1.2em;
        }
        
        .export-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 600;
            font-size: 14px;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
            border: none;
        }
        
        .export-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }
        
        .export-btn span {
            margin-right: 8px;
            font-size: 16px;
        }
        
        .action-buttons {
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-top: 15px;
        }
        
        .btn-group {
            display: flex;
            gap: 8px;
        }
        
        .btn {
            padding: 8px 16px;
            border: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 100px;
        }
        
        .btn span {
            margin-right: 6px;
            font-size: 14px;
        }
        
        .btn-cancel {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
        }
        
        .btn-cancel:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
        }
        
        .btn-cancel:disabled {
            background: #ccc;
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }
        
        .btn-pdf {
            background: linear-gradient(135deg, #ffa726 0%, #fb8c00 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(255, 167, 38, 0.3);
        }
        
        .btn-pdf:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(255, 167, 38, 0.4);
        }
        
        .btn-csv {
            background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);
            color: white;
            box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
        }
        
        .btn-csv:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(76, 175, 80, 0.4);
        }
        
        .btn-disabled {
            background: linear-gradient(135deg, #bdbdbd 0%, #9e9e9e 100%);
            color: #666;
            cursor: not-allowed;
        }
        
        .reservation-status {
            display: flex;
            align-items: center;
            margin-top: 10px;
            padding: 8px 12px;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 500;
        }
        
        .status-active {
            background-color: #e8f5e8;
            color: #2e7d32;
            border: 1px solid #c8e6c9;
        }
        
        .status-expired {
            background-color: #ffebee;
            color: #c62828;
            border: 1px solid #ffcdd2;
        }
        
        .status-icon {
            margin-right: 6px;
            font-size: 14px;
        }
    </style>
</head>
<body>    
    <div class="sidebar">        
        <div class="sidebar-header">
            <h2>User Panel</h2>
        </div>
        <div class="sidebar-menu">                        
            <a href="front_vol_search_form" class="menu-item">Search</a>            
            <a href="front_reservations" class="menu-item active">My Reservations</a>            
        </div>
        <div class="user-info">
            <div>Logged in as: <%= session.getAttribute("nom") %></div>
            <a href="logout" class="logout-btn">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="container">
            <div class="header">
                <h2>My Reservations</h2>
                <a href="front_vol_search_form" class="new-search-btn">Book New Flight</a>
            </div>

            <% 
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm");
            List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");
            if (reservations != null && !reservations.isEmpty()) {
            %>
            <!-- Export buttons améliorés -->
            <div class="export-buttons">
                <div class="export-info">
                    <span>📊</span>
                    <span>Export your reservations for records</span>
                </div>
                <div style="display: flex; gap: 10px;">
                    <a href="front_reservations_pdf" class="export-btn">
                        <span>📄</span>
                        Export All to PDF
                    </a>
                    <a href="front_reservations_csv" class="export-btn" style="background: linear-gradient(135deg, #4caf50 0%, #45a049 100%);">
                        <span>📊</span>
                        Export All to CSV
                    </a>
                </div>
            </div>
            <%
                for (Reservation res : reservations) {
            %>
            <div class="reservation-card <%= res.isEstPromo() ? "promo" : "" %>">
                <div class="flight-info">
                    <div class="cities">
                        <%= res.getVilleDepart() %> → <%= res.getVilleArrivee() %>
                    </div>
                    <div class="departure-time">
                        <%= sdf.format(res.getHeureDepart()) %>
                    </div>
                    <div>
                        <%= res.getAvion() %> | <%= res.getTypeSiege() %>
                    </div>
                </div>
                
                <div class="reservation-details">
                    <div>Booked on: <%= sdf.format(res.getDateReservation()) %></div>
                    <% if (res.isEstPromo()) { %>
                        <div class="promo-tag">Promo Fare</div>
                    <% } %>
                    <% if (res.getPhoto() != null) { %>
                        <div class="reservation-photo">
                            <img src="uploads/reservations/<%= res.getPhoto() %>" 
                                alt="Boarding Pass/ID" style="max-width: 200px; margin-top: 10px;">
                        </div>
                    <% } %>
                </div>
                
                <div class="price-info">
                    <div class="price">
                        <%= String.format("%.2f", res.getPrix()) %> €
                    </div>
                    
                    <!-- Statut de réservation amélioré -->
                    <% if (res.getLimiteAnnulation() != null) { %>
                        <div class="reservation-status <%= res.isEstAnnulable() ? "status-active" : "status-expired" %>">
                            <span class="status-icon"><%= res.isEstAnnulable() ? "✅" : "⏰" %></span>
                            <div>
                                <div style="font-weight: 600;">
                                    <%= res.isEstAnnulable() ? "Cancellable" : "Non-cancellable" %>
                                </div>
                                <div style="font-size: 11px;">
                                    Until: <%= sdf.format(res.getLimiteAnnulation()) %>
                                </div>
                            </div>
                        </div>
                    <% } %>
                    
                    <!-- Boutons d'action restructurés -->
                    <div class="action-buttons">
                        <div class="btn-group">
                            <% if (res.isEstAnnulable()) { %>
                                <form action="front_reservation_annuler" method="post" 
                                      onsubmit="return confirm('Are you sure you want to cancel this reservation?')" 
                                      style="display: inline;">
                                    <input type="hidden" name="reservation_id" value="<%= res.getId() %>">
                                    <button type="submit" class="btn btn-cancel">
                                        <span>❌</span>
                                        Cancel
                                    </button>
                                </form>
                            <% } else { %>
                                <button class="btn btn-disabled" disabled>
                                    <span>🚫</span>
                                    Cannot Cancel
                                </button>
                            <% } %>
                            
                            <a href="front_reservation_pdf?reservation_id=<%= res.getId() %>" 
                               class="btn btn-pdf">
                                <span>📄</span>
                                Export PDF
                            </a>
                            
                            <a href="front_reservation_csv?reservation_id=<%= res.getId() %>" 
                               class="btn btn-csv">
                                <span>📊</span>
                                Export CSV
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            } else {
            %>
            <div class="no-reservations">
                <p>You don't have any reservations yet.</p>
                <p>Start by searching for available flights!</p>
            </div>
            <%
            }
            %>
        </div>
    </div>
</body>
</html>