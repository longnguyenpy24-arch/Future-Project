module org.cs {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.apache.pdfbox;
    requires javafx.swing;

    opens org.cs2 to javafx.fxml;
    exports org.cs2;
}