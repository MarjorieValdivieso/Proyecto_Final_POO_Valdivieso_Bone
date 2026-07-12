package app;

import db.Conexion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // ---- Prueba de conexión a la base de datos ----
        Connection con = Conexion.conectar();
        if (con != null) {
            System.out.println("Conexión exitosa a la base de datos.");
        } else {
            System.out.println(" No se pudo conectar");
        }
        // ------------------------------------------------

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Belleza Elegante - Iniciar Sesión");
            stage.show();

        } catch (IOException e) {
            System.err.println("Error. No se pudo cargar el archivo FXML.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}