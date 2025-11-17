package com.example.ordenamientos;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LectorDeDatos {
    private String[] encabezados;
    private List<String[]> filasDeDatos;

    public LectorDeDatos() {
        this.filasDeDatos = new ArrayList<>();
    }

    public void cargarDatos(InputStream stream) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            procesarStream(bufferedReader);
        }
    }

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

    public String[] getEncabezados() {
        return encabezados;
    }

    public List<String[]> getDatos() {
        return filasDeDatos;
    }
}