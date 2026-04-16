package org.cs2;

import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import javafx.embed.swing.SwingFXUtils;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PdfModule {
    private PDDocument document;
    private final ScrollPane scrollPane;
    private double clickedX;
    private double clickedY;
    public PdfModule(VBox mainContent, Window window, TabCustomClass tab, ScrollPane scrollPane) throws IOException {
        this.scrollPane = scrollPane;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        File file = fileChooser.showOpenDialog(window);
        if (file == null){
            return;
        }

        tab.initializeDocumentContent(mainContent);

        document = PDDocument.load(file);
        PDFRenderer renderer = new PDFRenderer(document);
        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage img = renderer.renderImageWithDPI(i, 150);
            StackPane wrapper = new StackPane();
            wrapper.setMouseTransparent(true);
            ImageView iv = new ImageView(SwingFXUtils.toFXImage(img, null));
            iv.setOnMouseClicked(event -> {
                this.clickedX = event.getX();
                this.clickedY = event.getY();
            });
            iv.setOnMouseReleased(event -> {
                try {
                    extractTextFromRegion(event.getX(), event.getY());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            wrapper.getChildren().add(iv);
            iv.fitWidthProperty().bind(mainContent.widthProperty());
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.fitWidthProperty().bind(mainContent.widthProperty().subtract(20));
            mainContent.getChildren().add(wrapper);
        }
        document.close();
    }

    public String extractTextFromRegion(double releasedX, double releasedY) throws IOException {
        int calculatedWidth = 20;
        int calculatedHeight = 50;

        int pageIndex =  (int) (scrollPane.getVvalue() * document.getNumberOfPages());
        PDPage page = document.getPage(pageIndex);
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);

        Rectangle2D region = new Rectangle2D.Double(clickedX, clickedY, calculatedWidth, calculatedHeight);
        stripper.addRegion("draggedRegion", region);

        // Extract the text
        stripper.extractRegions(page);
        return stripper.getTextForRegion("draggedRegion");
    }
}
