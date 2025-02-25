<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.VolSearch" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Results - Airline Ticketing</title>
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
        
        .create-btn {
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        
        .create-btn:hover {
            background-color: #45a049;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            background-color: #f5f5f5;
            font-weight: bold;
        }
        
        tr:hover {
            background-color: #f9f9f9;
        }
        
        .action-links a {
            margin-right: 10px;
            text-decoration: none;
            color: #2196F3;
        }
        
        .action-links a:hover {
            text-decoration: underline;
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
        /* Copy existing styles from vol_list.jsp */
        .highlight {
            background-color: #e8f5e9;
        }
        
        .promo-price {
            color: #4caf50;
            font-weight: bold;
        }
        
        .original-price {
            text-decoration: line-through;
            color: #999;
            font-size: 0.9em;
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
            <a href="vol_type_siege_form" class="menu-item">Promotions</a>
            <a href="vol_search_form" class="menu-item active">Search</a>
        </div>
        <div class="user-info">
            <div>Logged in as: <%= session.getAttribute("nom") %></div>
            <a href="logout" class="logout-btn">Logout</a>
        </div>
    </div>

    <div class="content">
        <div class="container">
            <div class="header">
                <h2>Search Results</h2>
                <div>
                    <a href="vol_search_form" class="create-btn">New Search</a>
                </div>
            </div>

            <table>
                <thead>
                    <tr>
                        <th>Flight</th>
                        <th>From</th>
                        <th>To</th>
                        <th>Departure</th>
                        <th>Aircraft</th>
                        <th>Seat Type</th>
                        <th>Price</th>
                        <th>Promo Seats</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    List<VolSearch> results = (List<VolSearch>) request.getAttribute("results");
                    if (results != null && !results.isEmpty()) {
                        for (VolSearch vol : results) {
                            boolean hasPromo = vol.getPourcentagePromo() > 0;
                    %>
                    <tr class="<%= hasPromo ? "highlight" : "" %>">
                        <td><%= vol.getId() %></td>
                        <td><%= vol.getVilleDepartNom() %></td>
                        <td><%= vol.getVilleArriveeNom() %></td>
                        <td><%= sdf.format(vol.getHeureDepart()) %></td>
                        <td><%= vol.getAvionModele() %></td>
                        <td><%= vol.getTypeSiegeNom() %></td>
                        <td>
                            <% if (hasPromo) { %>
                                <span class="promo-price"><%= String.format("%.2f", vol.getPrixPromo()) %></span>
                                <span class="original-price"><%= String.format("%.2f", vol.getPrix()) %></span>
                            <% } else { %>
                                <%= String.format("%.2f", vol.getPrix()) %>
                            <% } %>
                        </td>
                        <td>
                            <% if (hasPromo) { %>
                                <%= vol.getNombreSiegesPromo() %> 
                                (<%= String.format("%.0f", vol.getPourcentagePromo()) %>% off)
                            <% } else { %>
                                -
                            <% } %>
                        </td>
                    </tr>
                    <%
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="8" style="text-align: center;">No flights found matching your criteria</td>
                    </tr>
                    <%
                    }
                    %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html> 