package model;

import dbconnect.UtilDB;

/**
 * Model class representing a user in the system.
 * Maps to the 'utilisateur' table in the database.
 */
public class User {
    private int id;
    private String login;
    private String nom;
    private String role;    

    // Default constructor
    public User() {
    }

    // Constructor with all fields except id
    public User(String login, String nom, String role, String mdp) {
        this.login = login;
        this.nom = nom;
        this.role = role;        
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", nom='" + nom + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    // Note: mdp is intentionally excluded from toString() for security reasons

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Authenticates a user with the given login and password.
     * @param login The user's login
     * @param mdp The user's password
     * @param role The user's role
     * @return User object if authentication successful, null otherwise
     * @throws Exception if database error occurs
     */
    public static User login(String login, String mdp, String role) throws Exception {
        String query = "SELECT * FROM utilisateur WHERE login = '" + login + "' AND mdp = '" + mdp + "' AND role = '" + role + "'";
        
        try (java.sql.Connection connection = UtilDB.getConnection();
             java.sql.Statement stmt = connection.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setLogin(rs.getString("login"));
                user.setNom(rs.getString("nom"));
                user.setRole(rs.getString("role"));
                return user;
            }
            return null;
        }
    }    
}