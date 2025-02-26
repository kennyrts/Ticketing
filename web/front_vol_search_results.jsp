<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.VolDetails" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Flight Search Results - Airline Booking</title>
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
        
        .flight-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            display: grid;
            grid-template-columns: 3fr 2fr 2fr;
            gap: 20px;
            align-items: center;
            transition: transform 0.2s;
        }
        
        .flight-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        
        .flight-card.promo {
            background-color: #e8f5e9;
            border-color: #4CAF50;
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
        
        .seat-info {
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
        
        .original-price {
            text-decoration: line-through;
            color: #999;
            font-size: 0.9em;
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
        
        .no-results {
            text-align: center;
            padding: 40px;
            color: #666;
            font-size: 1.2em;
        }
        
        .reserve-btn {
            background-color: #4CAF50;
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
        
        .reserve-btn:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>User Panel</h2>
        </div>
        <div class="sidebar-menu">                        
            <a href="front_vol_search_form" class="menu-item active">Search</a>            
            <a href="front_reservations" class="menu-item">My Reservations</a>            
        </div>
        <div class="user-info">
            <div>Logged in as: <%= session.getAttribute("nom") %></div>
            <a href="logout" class="logout-btn">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="container">
            <div class="header">
                <h2>Available Flights</h2>
                <a href="front_vol_search_form" class="new-search-btn">New Search</a>
            </div>

            <% 
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy HH:mm");
            List<VolDetails> results = (List<VolDetails>) request.getAttribute("results");
            if (results != null && !results.isEmpty()) {
                for (VolDetails vol : results) {
                    boolean hasPromo = vol.getSiegeLibrePromo() > 0 && vol.getPourcentagePromo() > 0;
            %>
            <div class="flight-card <%= hasPromo ? "promo" : "" %>">
                <div class="flight-info">
                    <div class="cities">
                        <%= vol.getVilleDepart() %> → <%= vol.getVilleArrivee() %>
                    </div>
                    <div class="departure-time">
                        <%= sdf.format(vol.getHeureDepart()) %>
                    </div>
                    <div>
                        <%= vol.getAvion() %> | <%= vol.getTypeSiege() %>
                    </div>
                </div>
                
                <div class="seat-info">
                    <div>Available Seats: <%= vol.getSiegeLibre() %></div>
                    <% if (hasPromo) { %>
                        <div>Promo Seats: <%= vol.getSiegeLibrePromo() %></div>
                    <% } %>
                </div>
                
                <div class="price-info">
                    <div class="price">
                        <%= String.format("%.2f", vol.getPrixActuel()) %> €
                    </div>
                    <% if (hasPromo) { %>
                        <div class="original-price"><%= String.format("%.2f", vol.getPrix()) %> €</div>
                        <div class="promo-tag"><%= String.format("%.0f", vol.getPourcentagePromo()) %>% OFF</div>
                    <% } %>
                    <form action="front_vol_reserver" method="post">
                        <input type="hidden" name="vol_type_siege_id" value="<%= vol.getVolTypeSiegeId() %>">
                        <input type="hidden" name="vol_id" value="<%= vol.getVolId() %>">
                        <input type="hidden" name="type_siege_id" value="<%= vol.getTypeSiegeId() %>">
                        <input type="hidden" name="prix" value="<%= vol.getPrixActuel() %>">
                        <input type="hidden" name="est_promo" value="<%= hasPromo %>">
                        <button type="submit" class="reserve-btn">Book Now</button>
                    </form>
                </div>
            </div>
            <%
                }
            } else {
            %>
            <div class="no-results">
                <p>No flights found matching your criteria.</p>
                <p>Please try different search parameters.</p>
            </div>
            <%
            }
            %>
        </div>
    </div>
</body>
</html> 