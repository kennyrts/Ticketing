<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="model.Ville" %>
<%@ page import="model.Avion" %>
<%@ page import="model.Vol" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${vol != null ? 'Edit' : 'Create'} Flight - Airline Ticketing</title>
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
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            margin-right: 10px;
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
                <h2>${vol != null ? 'Edit' : 'Create'} Flight</h2>
                <div>
                    <a href="vol_list" class="back-btn">Back to List</a>
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

            <%
                Vol vol = (Vol) request.getAttribute("vol");
                String formAction = vol != null ? "vol_update" : "vol_create";
                
                // Format datetime for the input field if editing
                String departureDatetime = "";
                if (vol != null && vol.getHeureDepart() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    departureDatetime = sdf.format(vol.getHeureDepart());
                }
            %>

            <form action="<%= formAction %>" method="post">
                <% if (vol != null) { %>
                    <input type="hidden" name="id" value="<%= vol.getId() %>">
                <% } %>

                <div class="form-group">
                    <label for="avion">Aircraft:</label>
                    <select id="avion" name="avion_id" required>
                        <option value="">Select an aircraft</option>
                        <% 
                        List<Avion> avions = (List<Avion>) request.getAttribute("avions");
                        if (avions != null) {
                            for (Avion avion : avions) {
                        %>
                            <option value="<%= avion.getId() %>" 
                                    <%= (vol != null && vol.getAvionId() == avion.getId()) ? "selected" : "" %>>
                                <%= avion.getModele() %> (<%= avion.getDateFabrication() %>)
                            </option>
                        <%
                            }
                        }
                        %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="ville_depart">Departure City:</label>
                    <select id="ville_depart" name="ville_depart_id" required>
                        <option value="">Select departure city</option>
                        <% 
                        List<Ville> villes = (List<Ville>) request.getAttribute("villes");
                        if (villes != null) {
                            for (Ville ville : villes) {
                        %>
                            <option value="<%= ville.getId() %>"
                                    <%= (vol != null && vol.getVilleDepartId() == ville.getId()) ? "selected" : "" %>>
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
                    <select id="ville_arrivee" name="ville_arrivee_id" required>
                        <option value="">Select arrival city</option>
                        <% 
                        if (villes != null) {
                            for (Ville ville : villes) {
                        %>
                            <option value="<%= ville.getId() %>"
                                    <%= (vol != null && vol.getVilleArriveeId() == ville.getId()) ? "selected" : "" %>>
                                <%= ville.getNom() %>
                            </option>
                        <%
                            }
                        }
                        %>
                    </select>
                </div>

                <div class="form-group">
                    <label for="heure_depart">Departure Time:</label>
                    <input type="datetime-local" id="heure_depart" name="heure_depart" 
                           value="<%= departureDatetime %>" required>
                </div>

                <div class="form-group">
                    <label for="heures_avant_reservation">Hours Before Reservation Deadline:</label>
                    <input type="number" id="heures_avant_reservation" name="heures_avant_reservation" 
                           min="1" value="<%= vol != null ? vol.getHeuresAvantReservation() : "" %>" required>
                </div>

                <div class="form-group">
                    <label for="heures_avant_annulation">Hours Before Cancellation Deadline:</label>
                    <input type="number" id="heures_avant_annulation" name="heures_avant_annulation" 
                           min="1" value="<%= vol != null ? vol.getHeuresAvantAnnulation() : "" %>" required>
                </div>

                <button type="submit" class="submit-btn">
                    ${vol != null ? 'Update' : 'Create'} Flight
                </button>
            </form>
        </div>
    </div>

    <script>
        // Set minimum datetime-local to current time for new flights
        if (!document.getElementById('heure_depart').value) {
            const now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            document.getElementById('heure_depart').min = now.toISOString().slice(0,16);
        }

        // Add event listener to prevent selecting same city for departure and arrival
        document.getElementById('ville_depart').addEventListener('change', function() {
            const villeArrivee = document.getElementById('ville_arrivee');
            const selectedDeparture = this.value;
            
            Array.from(villeArrivee.options).forEach(option => {
                option.disabled = option.value === selectedDeparture;
            });
        });
    </script>
</body>
</html> 