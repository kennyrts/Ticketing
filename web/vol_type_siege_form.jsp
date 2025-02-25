<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Vol" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Seat Type - Airline Ticketing</title>
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
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        
        .form-group {
            margin-bottom: 15px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        .form-group input, .form-group select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        
        .submit-btn {
            width: 100%;
            padding: 10px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 20px;
        }
        
        .submit-btn:hover {
            background-color: #45a049;
        }
        
        .error-message {
            color: red;
            margin-bottom: 15px;
        }

        .success-message {
            color: green;
            margin-bottom: 15px;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .back-btn {
            padding: 8px 15px;
            background-color: #2196F3;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }

        .back-btn:hover {
            background-color: #1976D2;
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
            width: 100%;
            padding: 10px;
            background-color: #f44336;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            text-align: center;
            display: block;
            margin-top: 10px;
        }
        
        .logout-btn:hover {
            background-color: #d32f2f;
        }
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>Admin Panel</h2>
        </div>
        <div class="sidebar-menu">            
            <a href="vol_list" class="menu-item">CRUD</a>
            <a href="vol_type_siege_form" class="menu-item active">Promotions</a>
            <a href="vol_search_form" class="menu-item">Search</a>            
        </div>
        <div class="user-info">
            <div>Logged in as: <%= session.getAttribute("nom") %></div>
            <a href="logout" class="logout-btn">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="container">
            <div class="header">
                <h2>Add Flight Promotions</h2>
                <div>
                    <a href="vol_list" class="back-btn">Back to Flights</a>
                </div>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="success-message">
                    <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <form action="vol_type_siege_create" method="post">
                <div class="form-group">
                    <label for="vol">Flight:</label>
                    <select id="vol" name="vol_id" required>
                        <option value="">Select a flight</option>
                        <% 
                        List<Vol> vols = (List<Vol>) request.getAttribute("vols");
                        if (vols != null) {
                            for (Vol vol : vols) {
                        %>
                            <option value="<%= vol.getId() %>">
                                Flight <%= vol.getId() %> - 
                                <%= vol.getVilleDepartNom() %> to <%= vol.getVilleArriveeNom() %> 
                                (<%= vol.getHeureDepart() %>)
                            </option>
                        <%
                            }
                        }
                        %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="type_siege">Seat Type:</label>
                    <select id="type_siege" name="type_siege_id" required>
                        <option value="">Select seat type</option>
                        <option value="1">Business</option>
                        <option value="2">Economy</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="prix">Base Price:</label>
                    <input type="number" id="prix" name="prix" min="0" step="0.01" required>
                </div>

                <div class="form-group">
                    <label for="nombre_sieges_promo">Number of Promotional Seats:</label>
                    <input type="number" id="nombre_sieges_promo" name="nombre_sieges_promo" 
                           min="0" value="0" required>
                </div>

                <div class="form-group">
                    <label for="pourcentage_promo">Discount Percentage:</label>
                    <input type="number" id="pourcentage_promo" name="pourcentage_promo" 
                           min="0" max="100" value="0" required>
                    <small style="color: #666; display: block; margin-top: 5px;">
                        Enter a value between 0 and 100. Example: 25 for a 25% discount.
                    </small>
                </div>

                <button type="submit" class="submit-btn">Add Seat Configuration</button>
            </form>
        </div>
    </div>

    <script>
        // Add validation for promotion fields
        document.getElementById('pourcentage_promo').addEventListener('input', function() {
            let value = parseInt(this.value);
            if (value < 0) this.value = 0;
            if (value > 100) this.value = 100;
        });

        document.getElementById('nombre_sieges_promo').addEventListener('input', function() {
            if (this.value < 0) this.value = 0;
        });
    </script>
</body>
</html> 