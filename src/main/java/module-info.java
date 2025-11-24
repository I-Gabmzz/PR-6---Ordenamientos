module com.example.ordenamientos {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.example.ordenamientos to javafx.fxml;
    exports com.example.ordenamientos;
}