package com.example.ordenamientos;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class OrdenadorAPP extends Application {

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
        escenario.setMinWidth(800);
        escenario.show();
    }

    public static void main(String[] args) {
        launch();
    }
}