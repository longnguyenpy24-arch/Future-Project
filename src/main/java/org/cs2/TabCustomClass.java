package org.cs2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.IOException;
import java.util.ArrayList;

public class TabCustomClass {
    private static int colorCounter = 0;
    private static final String[] colorArr = {"theme-1", "theme-2","theme-3","theme-4","theme-5","theme-6","theme-7","theme-8", "theme-9", "theme-10"};
    private final Tab tab;
    private final String colorStyleClass;
    private final StringProperty citation = new SimpleStringProperty("");
    private ScrollPane scrollPane;
    private boolean submitted = false;
    private VBox mainContent;
    private static final ArrayList<PDDocument> unclosedDocuments = new ArrayList<>();

    public static ArrayList<PDDocument> getUnclosedDocuments() {
        return unclosedDocuments;
    }

    public TabCustomClass(String nameOfTab){
        tab = new Tab(nameOfTab);
        tab.setClosable(true);
        colorStyleClass = colorArr[colorCounter];
        tab.getStyleClass().add(colorStyleClass);

        colorCounter = (colorCounter+1) % colorArr.length;
        initializeTabContent();
    }

    public String getColorStyleClass(){
        return colorStyleClass;
    }

    ScrollPane getScrollPane(){
        return scrollPane;
    }

    public Tab getTab(){
        return tab;
    }

    StringProperty getCitation() {
        return citation;
    }
    private void createPdf(VBox mainContent) throws IOException {
        new PdfModule(mainContent, mainContent.getScene().getWindow(), scrollPane, this);
    }

    private void createDocX(VBox mainContent) throws IOException {
        new DocX(mainContent, mainContent.getScene().getWindow(), scrollPane, this);
    }

    void initializeTabContent() {
        scrollPane = new ScrollPane();
        mainContent = new VBox();
        scrollPane.setContent(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        mainContent.getStyleClass().addAll(colorStyleClass, "verticalBoxTabContent");
        HBox start = new HBox();
        Button uploadPdf = new Button("Upload Pdf");
        Button newDocX = new Button("Upload DOCX");

        uploadPdf.setOnAction(_ -> {
            try {
                createPdf(mainContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        newDocX.setOnAction(_ -> {
            try {
                createDocX(mainContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        uploadPdf.getStyleClass().add("styleButton");
        newDocX.getStyleClass().add("styleButton");
        start.getChildren().add(uploadPdf);
        start.getChildren().add(newDocX);

        start.getStyleClass().add("initialTabBox");
        mainContent.getChildren().add(start);
        mainContent.getStyleClass().add("tabContentStyle");
        tab.setContent(scrollPane);
    }

    void initializeDocumentContent(VBox mainContent){
        mainContent.getChildren().clear();

        HBox citationBox = new HBox();
        Label citationLabel = new Label("Optional Citation: ");
        TextArea citationInput = new TextArea();
        Button updateCitation = new Button("Add Citation");

        citationBox.getStyleClass().add("row");
        citationLabel.getStyleClass().add("styleText");
        citationInput.getStyleClass().add("textArea");
        updateCitation.getStyleClass().add("styleButton");

        citationInput.setTextFormatter(new TextFormatter<String> (change -> {
            if (change.getControlNewText().isEmpty() && submitted){
                updateCitation.setText("Delete Citation");
                return change;
            }
            else{
                String newText = change.getText();
                if (change.isAdded()){
                    updateCitation.setText("Add Citation");
                }
                if (newText.matches("[0-9 \\w]*")){
                    return change;
                }
            }
            return null;
        }));
        updateCitation.setOnAction(_ -> {
            if(updateCitation.getText().equals("Add Citation")){
                citation.set(citationInput.getText());
                citationInput.clear();
                updateCitation.setText("Delete Citation");
                submitted = true;
            }
            else {
                updateCitation.setText("Add Citation");
                citation.set("");
                submitted = false;
            }
        });

        citationBox.getChildren().addAll(citationLabel, citationInput);
        mainContent.getChildren().addAll(citationBox, updateCitation);
    }
}