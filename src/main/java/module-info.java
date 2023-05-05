module com.chip8.chip8 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;

    opens com.chip8.chip8 to javafx.fxml;
    exports com.chip8.chip8;
}