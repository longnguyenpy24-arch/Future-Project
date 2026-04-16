package org.cs2;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TabCustomClass {
    private static int colorCounter = 0;
    private static final String[] colorArr = {"theme-1", "theme-2","theme-3","theme-4","theme-5","theme-6","theme-7","theme-8", "theme-9", "theme-10"};
    private final Tab tab;
    private final String colorStyleClass;
    private String citation;
    private ScrollPane scrollPane;

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
        // USE THIS TO FILL COLOR OF STACKPANE OF EVIDENCE
    }

    public Tab getTab(){
        return tab;
    }

    public String getCitation() {
        return citation;
        // USE THIS TO LABEL EVIDENCE
    }
    private void createPdf(VBox mainContent) throws IOException {
        new PdfModule(mainContent, mainContent.getScene().getWindow(), this, scrollPane);
    }

    private void createDocX(VBox mainContent){
        new DocX(mainContent, this);
    }

    void initializeTabContent() {
        scrollPane = new ScrollPane();
        VBox mainContent = new VBox();
        scrollPane.setContent(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        mainContent.getStyleClass().addAll(getColorStyleClass(), "verticalBoxTabContent");
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
        newDocX.setOnAction(_ -> createDocX(mainContent));

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
            String newText = change.getText();
            if (change.isAdded()){
                updateCitation.setText("Add Citation");
            }
            if (newText.matches("[0-9 \\w]*")){
                return change;
            }
            return null;
        }));

        updateCitation.setOnAction(_ -> {
            if(updateCitation.getText().equals("Add Citation")){
                citation = citationInput.getText();
                citationInput.clear();
                updateCitation.setText("Delete Citation");
            }
            else {
                updateCitation.setText("Add Citation");
                citation = "";
            }
        });

        citationBox.getChildren().addAll(citationLabel, citationInput);
        mainContent.getChildren().addAll(citationBox, updateCitation);
    }
}