package org.cs2;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public abstract class PdfManager {
    double pressedX;
    double pressedY;
    PDDocument document;
    protected final ArrayList<StackPane> wrapperList = new ArrayList<>();
    TabCustomClass tabCustomClass;
    void initialize(VBox mainContent, ScrollPane scrollPane, TabCustomClass tabCustomClass, File file) throws IOException {
        tabCustomClass.initializeDocumentContent(mainContent);
        this.tabCustomClass = tabCustomClass;

        // NOTE: We do not close this document at the end of the constructor anymore.
        document = PDDocument.load(file);
        TabCustomClass.getUnclosedDocuments().add(document);
        PDFRenderer renderer = new PDFRenderer(document);
        Thread thread = new Thread(() -> {
            WordCoordinateStripper stripper;
            try {
                stripper = new WordCoordinateStripper();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                // 1. Render image for visuals
                BufferedImage img;
                try {
                    img = renderer.renderImageWithDPI(i, 96);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Image fxImage = SwingFXUtils.toFXImage(img, null);
                img.flush();

                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                try {
                    stripper.documentWords.clear();
                    stripper.getText(document);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                int finalI = i;
                Platform.runLater(() -> {
                    ImageView iv = new ImageView(fxImage);
                    StackPane wrapper = new StackPane();

                    Rectangle selectionRect = new Rectangle();
                    selectionRect.setFill(Color.rgb(255, 255, 0, 0.3));
                    selectionRect.setStroke(Color.GOLD);
                    selectionRect.setVisible(false);
                    selectionRect.setManaged(false);
                    selectionRect.setMouseTransparent(true);

                    wrapper.setAlignment(javafx.geometry.Pos.TOP_LEFT);

                    iv.setPreserveRatio(true);
                    iv.setSmooth(true);

                    wrapper.getChildren().addAll(iv, selectionRect);
                    wrapperList.add(wrapper);
                    iv.fitWidthProperty().bind(scrollPane.widthProperty().subtract(20));
                    mainContent.getChildren().add(wrapper);


                    PDPage page = document.getPage(finalI);
                    float nativePdfWidth = page.getCropBox().getWidth();
                    float nativePdfHeight = page.getCropBox().getHeight();

                    iv.setOnMouseEntered(_ -> iv.setCursor(Cursor.CROSSHAIR));
                    iv.setOnMouseExited(_ -> iv.setCursor(Cursor.DEFAULT));
                    iv.setOnMousePressed(event -> {
                        Highlights.holdingShift = event.isShiftDown();
                        this.pressedX = event.getX();
                        this.pressedY = event.getY();

                    });

                    iv.setOnMouseDragged(event -> {
                        if (event.getButton() == MouseButton.PRIMARY){
                            double screenStartX = Math.min(pressedX, event.getX());
                            double screenStartY = Math.min(pressedY, event.getY());
                            double screenWidth = Math.abs(event.getX() - pressedX);
                            double screenHeight = Math.abs(event.getY() - pressedY);
                            selectionRect.setX(screenStartX);
                            selectionRect.setY(screenStartY);
                            selectionRect.setWidth(screenWidth);
                            selectionRect.setHeight(screenHeight);
                            selectionRect.setVisible(true);
                        }

                    });

                    iv.setOnMouseReleased(event -> {
                        if (event.getButton() == MouseButton.PRIMARY){
                            selectionRect.setVisible(false);
                            // Ignore if it was just a quick click without dragging
                            if (Math.abs(event.getX() - pressedX) < 5 && Math.abs(event.getY() - pressedY) < 5) {
                                return;
                            }

                            try {
                                // Calculate scale using native PDF sizes
                                double scaleX = iv.getBoundsInLocal().getWidth() / nativePdfWidth;
                                double scaleY = iv.getBoundsInLocal().getHeight() / nativePdfHeight;

                                double screenStartX = Math.min(pressedX, event.getX());
                                double screenStartY = Math.min(pressedY, event.getY());
                                double screenWidth = Math.abs(event.getX() - pressedX);
                                double screenHeight = Math.abs(event.getY() - pressedY);
                                // Convert screen coordinates to native PDF coordinates
                                double pdfStartX = screenStartX / scaleX;
                                double pdfStartY = screenStartY / scaleY;
                                double pdfWidth = screenWidth / scaleX;
                                double pdfHeight = screenHeight / scaleY;

                                String extracted = extractTextFromRegion(page, pdfStartX, pdfStartY, pdfWidth, pdfHeight);
                                if (!extracted.trim().isEmpty()) {
                                    UIDesign.drawRectangle(screenStartX, screenStartY, screenWidth, screenHeight, finalI, extracted, this);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Highlights.holdingShift = false;
                    });
                });
            }
        });
        thread.start();

    }
    // Updated to accept the specific page and the scaled native coordinates
    private String extractTextFromRegion(PDPage page, double x, double y, double width, double height) throws IOException {
        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);

        Rectangle2D region = new Rectangle2D.Double(x, y, width, height);

        stripper.addRegion("draggedRegion", region);

        // Extract the text
        stripper.extractRegions(page);
        return stripper.getTextForRegion("draggedRegion").trim();
    }
}
