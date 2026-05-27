module org.cs2 {
        requires javafx.controls;
        requires javafx.fxml;
        requires org.apache.poi.ooxml;
        requires fr.opensagres.poi.xwpf.converter.pdf;
        requires javafx.swing;
        requires java.desktop;
        requires fr.opensagres.poi.xwpf.converter.core;
    requires org.apache.pdfbox;
    // This opens your package so JavaFX can read your Controller classes
        opens org.cs2 to javafx.fxml;

        // This allows JavaFX to run your main application
        exports org.cs2;
}