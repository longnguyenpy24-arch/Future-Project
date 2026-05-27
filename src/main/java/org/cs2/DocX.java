package org.cs2;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;

public class DocX extends PdfManager{
    public DocX(VBox mainContent, Window window, ScrollPane scrollPane, TabCustomClass tabCustomClass){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open DOCX");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("DOCX Files", "*.docx")
        );
        File file = fileChooser.showOpenDialog(window);
        if (file == null) {
            return;
        }
        String pdfPath = file.getName().substring(0, file.getName().indexOf(".")) + ".pdf";
        try{
            String folderPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "pdfs";
            File targetFolder = new File(folderPath);

            InputStream is = new FileInputStream(file);
            XWPFDocument document = new XWPFDocument(is);
            File pdfFile = new File(targetFolder, pdfPath);
            OutputStream out = new FileOutputStream(pdfFile);
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(document, out, options);
            super.initialize(mainContent, scrollPane, tabCustomClass, pdfFile);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
}