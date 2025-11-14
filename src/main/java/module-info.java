module com.example.ordenamientos {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ordenamientos to javafx.fxml;
    exports com.example.ordenamientos;
}