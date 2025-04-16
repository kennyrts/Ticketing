<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Vol" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Flight List - Airline Ticketing</title>
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
    </style>
</head>
<body>
    <div class="sidebar">
        <div class="sidebar-header">
            <h2>Admin Panel</h2>
        </div>
        <div class="sidebar-menu">            
            <a href="vol_list" class="menu-item active">CRUD</a>
            <a href="vol_type_siege_form" class="menu-item">Promotions</a>
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
                <h2>Flight List</h2>
                <div>
                    <a href="vol_form" class="create-btn">Create New Flight</a>
                </div>
            </div>

            <% if (request.getAttribute("success") != null) { %>
                <div class="success-message">
                    <%= request.getAttribute("success") %>
                </div>
            <% } %>

            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Aircraft</th>
                        <th>From</th>
                        <th>To</th>
                        <th>Departure Time</th>
                        <th>Reservation Deadline</th>
                        <th>Cancellation Deadline</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
                    if (vols != null) {
                        for (Vol vol : vols) {
                    %>
                    <tr>
                        <td><%= vol.getId() %></td>
                        <td><%= vol.getAvionModele() %></td>
                        <td><%= vol.getVilleDepartNom() %></td>
                        <td><%= vol.getVilleArriveeNom() %></td>
                        <td><%= sdf.format(vol.getHeureDepart()) %></td>
                        <td><%= vol.getHeuresAvantReservation() %> hours</td>
                        <td><%= vol.getHeuresAvantAnnulation() %> hours</td>
                        <td class="action-links">
                            <a href="vol_edit?id=<%= vol.getId() %>">Edit</a>
                            <a href="vol_delete?id=<%= vol.getId() %>" 
                               onclick="return confirm('Are you sure you want to delete this flight?')">Delete</a>
                        </td>
                    </tr>
                    <%
                        }
                    }
                    %>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html> 