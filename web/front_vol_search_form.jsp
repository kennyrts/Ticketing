<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Ville" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Flights - Airline Booking</title>
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
            width: 800px;
            margin: 20px auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        
        .search-container {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        .date-range, .price-range {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }
        
        .form-group select, .form-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 16px;
        }
        
        .submit-btn {
            background-color: #4CAF50;
            color: white;
            padding: 15px 30px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
            width: 100%;
            transition: background-color 0.3s;
        }
        
        .submit-btn:hover {
            background-color: #45a049;
        }
        
        .error-message {
            color: #d32f2f;
            background-color: #fde7e7;
            padding: 12px;
            border-radius: 4px;
            margin-bottom: 20px;
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
                <h2>Search Flights</h2>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <form action="front_vol_search" method="post">
                <div class="search-container">
                    <div class="form-group">
                        <label for="ville_depart">Departure City:</label>
                        <select id="ville_depart" name="ville_depart_id">
                            <option value="">Any</option>
                            <% 
                            List<Ville> villes = (List<Ville>) request.getAttribute("villes");
                            if (villes != null) {
                                for (Ville ville : villes) {
                            %>
                                <option value="<%= ville.getId() %>">
                                    <%= ville.getNom() %>
                                </option>
                            <%
                                }
                            }
                            %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="ville_arrivee">Arrival City:</label>
                        <select id="ville_arrivee" name="ville_arrivee_id">
                            <option value="">Any</option>
                            <% 
                            if (villes != null) {
                                for (Ville ville : villes) {
                            %>
                                <option value="<%= ville.getId() %>">
                                    <%= ville.getNom() %>
                                </option>
                            <%
                                }
                            }
                            %>
                        </select>
                    </div>

                    <div class="form-group date-range">
                        <div>
                            <label for="date_debut">From Date:</label>
                            <input type="datetime-local" id="date_debut" name="date_debut">
                        </div>
                        <div>
                            <label for="date_fin">To Date:</label>
                            <input type="datetime-local" id="date_fin" name="date_fin">
                        </div>
                    </div>

                    <div class="form-group price-range">
                        <div>
                            <label for="prix_min">Min Price:</label>
                            <input type="number" id="prix_min" name="prix_min" min="0" step="0.01">
                        </div>
                        <div>
                            <label for="prix_max">Max Price:</label>
                            <input type="number" id="prix_max" name="prix_max" min="0" step="0.01">
                        </div>
                    </div>
                </div>

                <button type="submit" class="submit-btn">Search Flights</button>
            </form>
        </div>
    </div>

    <script>
        // Prevent selecting same city for departure and arrival
        document.getElementById('ville_depart').addEventListener('change', function() {
            const villeArrivee = document.getElementById('ville_arrivee');
            const selectedDeparture = this.value;
            
            Array.from(villeArrivee.options).forEach(option => {
                option.disabled = option.value === selectedDeparture && selectedDeparture !== '';
            });
        });

        // Set minimum date to today
        const now = new Date();
        now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
        const nowString = now.toISOString().slice(0,16);
        document.getElementById('date_debut').min = nowString;
        document.getElementById('date_fin').min = nowString;

        // Update return date minimum when departure date changes
        document.getElementById('date_debut').addEventListener('change', function() {
            document.getElementById('date_fin').min = this.value;
        });
    </script>
</body>
</html> 