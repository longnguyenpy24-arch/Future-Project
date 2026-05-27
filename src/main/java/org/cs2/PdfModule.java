package org.cs2;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.io.IOException;

public class PdfModule extends PdfManager{
    public PdfModule(VBox mainContent, Window window, ScrollPane scrollPane, TabCustomClass tabCustomClass) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        File file = fileChooser.showOpenDialog(window);
        if (file == null) {
            return;
        }
        String folderPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "pdfs";
        File targetFolder = new File(folderPath);
        File pdfFile = new File(targetFolder, file.getName());
        java.nio.file.Files.copy(
                file.toPath(),
                pdfFile.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );
        super.initialize(mainContent, scrollPane, tabCustomClass, pdfFile);
    }

}