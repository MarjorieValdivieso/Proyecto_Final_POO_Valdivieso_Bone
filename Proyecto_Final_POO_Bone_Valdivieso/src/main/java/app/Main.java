package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Belleza Elegante - Iniciar Sesión");
            stage.show();

        } catch (IOException e) {
            System.err.println("Error crítico No se pudo cargar el archivo FXML. Verifica la ruta o la estructura.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}