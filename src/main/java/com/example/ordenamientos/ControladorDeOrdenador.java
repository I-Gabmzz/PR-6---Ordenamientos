package com.example.ordenamientos;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

// Se declara la creacion de la clase que controla la interfaz y se encarga como tal de ser el cerebro de la aplicacion
public class ControladorDeOrdenador implements Initializable {
    // Variable que funciona como un almacenamiento y guarda resultado de cada ordenamiento
    private record ResultadoOrdenamiento(String algoritmo, long tiempo, String nota) {}

    private LectorDeDatos lectorDeDatos; // Instancia de la clase que sabe como leer y obtener los datos del CSV
    private String[] encabezadosOriginales; // Almacena los nombres originales de las columnas del CSV
    private List<String[]> datosCompletos; // Almacena todos los datos leidos del CSV
    private final Map<String, String> traducciones = new HashMap<>(); // Un mapa para traducir los nombres de las columnas

    // Atributos de apoyo (Rutas y colores)
    private final String RUTA_CSV = "/weatherHistory.csv";
    private final String RUTA_IMG_TITULAR = "/climaTitular.png";
    private final String[] COLORES_GRAFICA = {
            "#3366CC", "#DC3912", "#FF9900", "#109618", "#990099", "#0099C6", "#DD4477"
    };

    @FXML private VBox panelSuperiorBanner; // El panel superior que contiene la imagen del banner
    @FXML private ImageView imagenTitular; // La imagen que se muestra como titular o banner
    @FXML private TableView<Map<String, String>> tablaDatos; // La tabla donde se muestran los datos cargados del CSV
    @FXML private BarChart<String, Number> graficaResultados; // La grafica de barras que muestra los tiempos de ejecucion
    @FXML private TextArea areaResultadosTerminal; // El area de texto que funciona como una terminal de resultados
    @FXML private ComboBox<String> selectorColumna; // El menu desplegable para elegir la columna a ordenar

    @FXML private CheckBox checkboxQuickSort; // Checkbox para Quicksort
    @FXML private CheckBox checkboxMergeSort; // Checkbox para Mergesort
    @FXML private CheckBox checkboxShellSort; // Checkbox para Shell Sort
    @FXML private CheckBox checkboxSeleccion; // Checkbox para Seleccion Directa
    @FXML private CheckBox checkboxRadixSort; // Checkbox para Radix Sort
    @FXML private CheckBox checkboxArraysSort; // Checkbox para Arrays.sort()
    @FXML private CheckBox checkboxParallelSort; // Checkbox para Arrays.parallelSort()

    @FXML private Button botonOrdenar; // El boton que inicia el proceso de ordenamiento
    @FXML private Button botonCerrar; // El boton que cierra la aplicacion

    // Este metodo se encarga de inicializar la aplicacion y de presentarle al usuario toda la interfaz.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.lectorDeDatos = new LectorDeDatos();
        crearMapaTraducciones();

        cargarImagenTitular();
        cargarDatosAutomaticamente();

        selectorColumna.getSelectionModel().selectedItemProperty().addListener(
                (observable, valorAntiguo, valorNuevo) -> botonOrdenar.setDisable(valorNuevo == null)
        );

        if (panelSuperiorBanner != null) {
            imagenTitular.fitWidthProperty().bind(panelSuperiorBanner.widthProperty());
        }
    }

    // Controlador para el boton cerrar
    @FXML
    void manejarBotonCerrar(ActionEvent evento) {
        Stage escenario = (Stage) botonCerrar.getScene().getWindow();
        escenario.close();
    }

    // Este metodo se encarga de realizar una traduccion del archivo CSV, esto en las columnas para que el usuario tenga un mejor manejo
    private void crearMapaTraducciones() {
        traducciones.put("Formatted Date", "Fecha");
        traducciones.put("Summary", "Resumen");
        traducciones.put("Precip Type", "Tipo de Precipitacion");
        traducciones.put("Temperature (C)", "Temperatura (C)");
        traducciones.put("Apparent Temperature (C)", "Sensacion Termica (C)");
        traducciones.put("Humidity", "Humedad");
        traducciones.put("Wind Speed (km/h)", "Velocidad Viento (km/h)");
        traducciones.put("Wind Bearing (degrees)", "Direccion Viento (grados)");
        traducciones.put("Visibility (km)", "Visibilidad (km)");
        traducciones.put("Loud Cover", "Cubierta Sonora");
        traducciones.put("Pressure (millibars)", "Presion (milibares)");
        traducciones.put("Daily Summary", "Resumen Diario");
    }

    // Este metodo se encarga de cargar la imagen o banner de la interfaz
    private void cargarImagenTitular() {
        InputStream streamImagen = OrdenadorAPP.class.getResourceAsStream(RUTA_IMG_TITULAR);
        if (streamImagen != null) {
            imagenTitular.setImage(new Image(streamImagen));
        }
    }

    // Metodo el cual carga automaticamente los datos del CSV al iniciar la app
    private void cargarDatosAutomaticamente() {
        try (InputStream streamCSV = OrdenadorAPP.class.getResourceAsStream(RUTA_CSV)) {
            if (streamCSV == null) {
                throw new IOException("No se pudo encontrar el archivo CSV '" + RUTA_CSV + "'.");
            }

            lectorDeDatos.cargarDatos(streamCSV);
            this.encabezadosOriginales = lectorDeDatos.getEncabezados();
            this.datosCompletos = lectorDeDatos.getDatos();

            rellenarTabView();
            llenarComBox();

            selectorColumna.setDisable(false);
            registrarLog("Datos cargados correctamente desde " + RUTA_CSV);
        } catch (Exception e) {
            registrarLog("Error al cargar " + RUTA_CSV + ": " + e.getMessage());
            selectorColumna.setDisable(true);
            botonOrdenar.setDisable(true);
        }
    }

    // Metodo que llena la TableView con los datos del CSV.
    private void rellenarTabView() {
        tablaDatos.getColumns().clear();
        tablaDatos.getItems().clear();
        ObservableList<Map<String, String>> items = FXCollections.observableArrayList();

        for (String encabezadoOriginal : encabezadosOriginales) {
            String nombreColumnaTraducido = traducciones.getOrDefault(encabezadoOriginal, encabezadoOriginal);
            TableColumn<Map<String, String>, String> columna = new TableColumn<>(nombreColumnaTraducido);

            columna.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(encabezadoOriginal)));
            columna.setPrefWidth(120);
            tablaDatos.getColumns().add(columna);
        }

        for (String[] fila : datosCompletos) {
            Map<String, String> filaMapa = new HashMap<>();
            for (int i = 0; i < encabezadosOriginales.length; i++) {
                if (i < fila.length) {
                    filaMapa.put(encabezadosOriginales[i], fila[i]);
                } else {
                    filaMapa.put(encabezadosOriginales[i], "");
                }
            }
            items.add(filaMapa);
        }

        tablaDatos.setItems(items);
    }

    // Funcion que llena el ComboBox con los nombres de las columnas
    private void llenarComBox() {
        List<String> columnasTraducidas = new ArrayList<>();
        for (String encabezado : encabezadosOriginales) {
            columnasTraducidas.add(traducciones.getOrDefault(encabezado, encabezado));
        }
        selectorColumna.setItems(FXCollections.observableArrayList(columnasTraducidas));
    }

    // Controlador que se ejecuta al hacer clic en el boton de realizar Ordenamiento
    @FXML
    void manejarBotonOrdenar(ActionEvent evento) {
        String columnaTraducida = selectorColumna.getValue();
        if (columnaTraducida == null) {
            mostrarAlerta("Error", "Por favor, selecciona una columna para ordenar.");
            return;
        }

        int indiceSeleccionado = selectorColumna.getSelectionModel().getSelectedIndex();
        String columnaOriginal = encabezadosOriginales[indiceSeleccionado];

        graficaResultados.getData().clear();
        areaResultadosTerminal.clear();
        botonOrdenar.setDisable(true);
        botonOrdenar.setText("Ordenando");

        registrarLog("Iniciando ordenamiento para la columna: " + columnaTraducida);

        Task<List<ResultadoOrdenamiento>> tareaOrdenamiento = new Task<>() {

            // Este metodo contiene el trabajo tras bambalinas de lo que conlleva realizar los ordenamientos, este se ejecuta en segundo plano.
            @Override
            protected List<ResultadoOrdenamiento> call() throws Exception {
                List<ResultadoOrdenamiento> resultados = new ArrayList<>();
                int indiceColumna = Arrays.asList(encabezadosOriginales).indexOf(columnaOriginal);

                double[] datosDouble = obtenerDatosColumnaDouble(indiceColumna);

                if (checkboxQuickSort.isSelected()) {
                    double[] copia = Arrays.copyOf(datosDouble, datosDouble.length);
                    long t0 = System.nanoTime();
                    MetodosDeOrdenamiento.quicksort(copia);
                    long t1 = System.nanoTime();
                    resultados.add(new ResultadoOrdenamiento("Quicksort", t1 - t0, ""));
                }
                if (checkboxMergeSort.isSelected()) {
                    double[] copia = Arrays.copyOf(datosDouble, datosDouble.length);
                    long t0 = System.nanoTime();
                    MetodosDeOrdenamiento.mergesort(copia);
                    long t1 = System.nanoTime();
                    resultados.add(new ResultadoOrdenamiento("Mergesort", t1 - t0, ""));
                }
                if (checkboxShellSort.isSelected()) {
                    double[] copia = Arrays.copyOf(datosDouble, datosDouble.length);
                    long t0 = System.nanoTime();
                    MetodosDeOrdenamiento.shellSort(copia);
                    long t1 = System.nanoTime();
                    resultados.add(new ResultadoOrdenamiento("Shell Sort", t1 - t0, ""));
                }
                if (checkboxSeleccion.isSelected()) {
                    double[] copia = Arrays.copyOf(datosDouble, datosDouble.length);
                    long t0 = System.nanoTime();
                    MetodosDeOrdenamiento.seleccionDirecta(copia);
                    long t1 = System.nanoTime();
                    resultados.add(new ResultadoOrdenamiento("Seleccion Directa", t1 - t0, ""));
                }
                if (checkboxRadixSort.isSelected()) {
                    try {
                        long[] datosLong = obtenerDatosColumnaLong(indiceColumna);
                        long t0 = System.nanoTime();
                        MetodosDeOrdenamiento.radixSort(datosLong);
                        long t1 = System.nanoTime();
                        resultados.add(new ResultadoOrdenamiento("Radix Sort", t1 - t0, ""));
                    } catch (Exception e) {
                        resultados.add(new ResultadoOrdenamiento("Radix Sort", 0, "N/A (Error)"));
                    }
                }
                if (checkboxArraysSort.isSelected()) {
                    double[] copia = Arrays.copyOf(datosDouble, datosDouble.length);
                    long t0 = System.nanoTime();
                    Arrays.sort(copia);
                    long t1 = System.nanoTime();
                    resultados.add(new ResultadoOrdenamiento("Arrays.sort()", t1 - t0, ""));
                }
                if (checkboxParallelSort.isSelected()) {
                    double[] copia = Arrays.copyOf(datosDouble, datosDouble.length);
                    long t0 = System.nanoTime();
                    Arrays.parallelSort(copia);
                    long t1 = System.nanoTime();
                    resultados.add(new ResultadoOrdenamiento("Arrays.parallelSort()", t1 - t0, ""));
                }

                return resultados;
            }
        };

        tareaOrdenamiento.setOnSucceeded(eventoWorker -> {
            mostrarResultadosEnUI(tareaOrdenamiento.getValue());
            botonOrdenar.setDisable(false);
            botonOrdenar.setText("Realizar Ordenamiento");
            mostrarAlerta("Exito", "Proceso de ordenamiento completado.");
        });

        tareaOrdenamiento.setOnFailed(eventoWorker -> {
            mostrarAlerta("Error", "Ocurrio un error durante el ordenamiento: " + tareaOrdenamiento.getException().getMessage());
            eventoWorker.getSource().getException().printStackTrace();
            botonOrdenar.setDisable(false);
            botonOrdenar.setText("Realizar Ordenamiento");
        });

        new Thread(tareaOrdenamiento).start();
    }

    // Este metodo toma la lista de resultados y los muestra en la grafica y en la terminal
    private void mostrarResultadosEnUI(List<ResultadoOrdenamiento> resultados) {
        if (resultados == null || resultados.isEmpty()) {
            registrarLog("No se selecciono ningun algoritmo para ejecutar.");
            return;
        }

        long tiempoMasRapido = resultados.stream()
                .mapToLong(ResultadoOrdenamiento::tiempo)
                .filter(t -> t > 0)
                .min()
                .orElse(0);

        registrarLog("\n--- Resultados del Ordenamiento (" + selectorColumna.getValue() + ") ---");
        registrarLog(String.format("%-22s | %20s ns | %s", "Algoritmo", "Tiempo (ns)", "Detalles"));
        registrarLog("=".repeat(66));

        NumberFormat formateadorNumeros = NumberFormat.getInstance();
        int indiceColor = 0;

        for (ResultadoOrdenamiento res : resultados) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(res.algoritmo);
            XYChart.Data<String, Number> data = new XYChart.Data<>(res.algoritmo, res.tiempo);
            series.getData().add(data);
            graficaResultados.getData().add(series);

            final String color = COLORES_GRAFICA[indiceColor % COLORES_GRAFICA.length];
            data.nodeProperty().addListener((ov, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                }
            });
            indiceColor++;

            String esMasRapido = (res.tiempo > 0 && res.tiempo == tiempoMasRapido) ? "  <-- ESTE ES EL MAS RAPIDO" : "";
            String tiempoFormateado = (res.tiempo > 0) ? formateadorNumeros.format(res.tiempo) : res.nota;

            registrarLog(String.format("%-22s | %20s ns |%s", res.algoritmo, tiempoFormateado, esMasRapido));
        }
    }

    // Se extrae una columna y la convierte a tipo double
    private double[] obtenerDatosColumnaDouble(int indiceColumna) {
        double[] columnaDatos = new double[datosCompletos.size()];
        for (int i = 0; i < datosCompletos.size(); i++) {
            if (datosCompletos.get(i).length > indiceColumna) {
                try {
                    String valor = datosCompletos.get(i)[indiceColumna];
                    columnaDatos[i] = (valor == null || valor.trim().isEmpty()) ? 0.0 : Double.parseDouble(valor);
                } catch (NumberFormatException e) {
                    columnaDatos[i] = 0.0;
                }
            } else {
                columnaDatos[i] = 0.0;
            }
        }
        return columnaDatos;
    }

    // Metodo el cual sirve para convertir una columna de double a long
    private long[] obtenerDatosColumnaLong(int indiceColumna) {
        long[] columnaDatos = new long[datosCompletos.size()];
        for (int i = 0; i < datosCompletos.size(); i++) {
            if (datosCompletos.get(i).length > indiceColumna) {
                try {
                    String valor = datosCompletos.get(i)[indiceColumna];
                    if (valor == null || valor.trim().isEmpty()) {
                        columnaDatos[i] = 0L;
                    } else {
                        columnaDatos[i] = (long) (Double.parseDouble(valor) * 1000);
                    }
                } catch (NumberFormatException e) {
                    columnaDatos[i] = 0L;
                }
            } else {
                columnaDatos[i] = 0L;
            }
        }
        return columnaDatos;
    }

    // Funcion que sirve para imprimir un mensaje en la terminal de la UI
    private void registrarLog(String mensaje) {
        Platform.runLater(() -> areaResultadosTerminal.appendText(mensaje + "\n"));
    }

    // Metodo que sirve para mostrar una ventana de alerta al usuario
    private void mostrarAlerta(String titulo, String contenido) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(contenido);
            alerta.showAndWait();
        });
    }
}