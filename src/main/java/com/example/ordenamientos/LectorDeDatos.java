package com.example.ordenamientos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

// Esta clase se encarga de leer y procesar el archivo CSV.
public class LectorDeDatos {
    private String[] encabezados; // Almacena los nombres de las columnas leidos de la primera fila del CSV
    private List<String[]> filasDeDatos; // Una lista que almacena cada fila del CSV como un arreglo de Strings

    // Constructor de la clase
    public LectorDeDatos() {
        this.filasDeDatos = new ArrayList<>();
    }

    // Metodo principal que carga y procesa el archivo CSV desde un stream
    public void cargarDatos(InputStream stream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            procesarStream(bufferedReader);
        }
    }

    // Metodo que contiene la logica para leer el stream, es decir, el archivo
    private void procesarStream(BufferedReader bufferedReader) throws IOException {
        this.filasDeDatos.clear();
        this.encabezados = null;

        String linea;

        if ((linea = bufferedReader.readLine()) != null) {
            this.encabezados = linea.split(",");
        } else {
            throw new IOException("El archivo CSV esta vacio.");
        }

        while ((linea = bufferedReader.readLine()) != null) {
            if (!linea.trim().isEmpty()) {
                String[] fila = linea.split(",", -1);
                this.filasDeDatos.add(fila);
            }
        }
    }

    // Se encarga de devolver los encabezados leidos del CSV
    public String[] getEncabezados() {
        return encabezados;
    }

    // Este metodo es el que devuelve todas las filas de datos leidas
    public List<String[]> getDatos() {
        return filasDeDatos;
    }
}