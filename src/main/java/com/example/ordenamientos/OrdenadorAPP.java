package com.example.ordenamientos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

// Esta clase es la encargada de iniciar toda la aplicacion de ordenamientos
public class OrdenadorAPP extends Application {

    // Este metodo es llamado al iniciar la aplicacion y el primero en ejecutarse
    @Override
    public void start(Stage escenario) throws IOException {

        URL fxmlLocation = OrdenadorAPP.class.getResource("/Ordenador.fxml");

        if (fxmlLocation == null) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation);
        Scene escena = new Scene(fxmlLoader.load(), 1200, 800);

        escenario.setTitle("Practica 6 - Algoritmos de Ordenamiento");
        escenario.setScene(escena);
        escenario.setMinHeight(800);
        escenario.setMinWidth(1200);
        escenario.show();
    }

    // El metodo que lanza la aplicacion
    public static void main(String[] args) {
        launch();
    }
}