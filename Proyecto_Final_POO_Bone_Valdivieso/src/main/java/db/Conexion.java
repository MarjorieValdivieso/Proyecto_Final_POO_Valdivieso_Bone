package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String URL = "jdbc:postgresql://ep-flat-king-av1cwkd4-pooler.c-11.us-east-1.aws.neon.tech/Peluqueria?sslmode=require";
    private static final String USER = "neondb_owner";
    private static final String PASSWORD = "npg_hguPje6U4lVo";

    public static Connection conectar() {
        Connection con = null;

        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos Peluqueria en Neon");
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }

        return con;
    }
}