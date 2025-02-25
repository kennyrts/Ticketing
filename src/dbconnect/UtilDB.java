package dbconnect;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class UtilDB {    

    private static final String POSTGTESQL_URL = "jdbc:postgresql://localhost:5432/ticketing";
    private static final String POSTGREUSERNAME = "postgres";
    private static final String POSTGREPASSWORD = "Dbamanager1"; 

    public static Connection getConnection(){        
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(POSTGTESQL_URL, POSTGREUSERNAME, POSTGREPASSWORD);
            // System.out.println("OK");            
            } catch (ClassNotFoundException e) {
            System.out.println("Erreur lors du chargement du driver JDBC : " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
        return connection;
    }    
    public static void main(String[] args) {
        Connection connection = getConnection();
    }
}
