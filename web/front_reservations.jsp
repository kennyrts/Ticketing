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
                </div>
                
                <div class="price-info">
                    <div class="price">
                        <%= String.format("%.2f", res.getPrix()) %> €
                    </div>
                    <% if (res.getLimiteAnnulation() != null) { %>
                        <div class="deadline-info <%= res.isEstAnnulable() ? "" : "deadline-warning" %>">
                            Cancellation deadline:<br>
                            <%= sdf.format(res.getLimiteAnnulation()) %>
                        </div>
                    <% } %>
                    <% if (res.isEstAnnulable()) { %>
                        <form action="front_reservation_annuler" method="post" 
                              onsubmit="return confirm('Are you sure you want to cancel this reservation?');">
                            <input type="hidden" name="reservation_id" value="<%= res.getId() %>">
                            <button type="submit" class="cancel-btn">Cancel Reservation</button>
                        </form>
                    <% } else { %>
                        <div class="deadline-info deadline-warning">
                            Cancellation no longer available
                        </div>
                    <% } %>
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