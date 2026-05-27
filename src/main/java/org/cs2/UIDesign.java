package org.cs2;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static org.cs2.Highlights.strokeWidthClicked;
import static org.cs2.Highlights.strokeWidthNormal;

public class UIDesign {
    public enum NotesType{
        LINE_BREAK_SUBMIT_BUTTON,
        COMMENTARY_SUBMIT_BUTTON,
        TITLE_SUBMIT_BUTTON,
        HEADER_SUBMIT_BUTTON
    }
    static void submitInputNotes(NotesType type, HelloController controller) {
        if (type == NotesType.LINE_BREAK_SUBMIT_BUTTON) {
            controller.addLineBreak();
        } else if (!controller.inputText.getText().isEmpty()) {
            Text input = new Text(controller.inputText.getText());
            StackPane pane = new StackPane();
            pane.setFocusTraversable(true);
            pane.getStyleClass().add("paneStyle");
            pane.getChildren().add(input);
            controller.inputText.clear();

            if (type == NotesType.COMMENTARY_SUBMIT_BUTTON) {
                input.getStyleClass().add("commentary");
                pane.getStyleClass().add("commentaryPane");
            } else if (type == NotesType.TITLE_SUBMIT_BUTTON) {
                input.getStyleClass().add("title");
                pane.getStyleClass().add("titlePane");
            } else if (type == NotesType.HEADER_SUBMIT_BUTTON) {
                input.getStyleClass().add("header");
                pane.getStyleClass().add("headerPane");
            }
            else{
                return;
            }
            input.setOnMouseEntered(_ -> pane.getStyleClass().add("hoverText"));
            input.setOnMouseExited(_ -> pane.getStyleClass().remove("hoverText"));
            input.setOnMouseClicked(_ -> {
                controller.updateClickedPane(pane);
                controller.switchView(controller.changeNotesBox);
                controller.switchTexts(false);
                pane.requestFocus();
            });

            controller.Notes.getChildren().add(pane);
            controller.textPanes.add(pane);

            StackPane breakPane = new StackPane();
            breakPane.prefWidthProperty().bind(controller.Notes.widthProperty());
            controller.Notes.getChildren().add(breakPane);
            Platform.runLater(() -> controller.inputText.requestFocus());

            pane.setOnKeyPressed(keyEvent -> {
                int textPaneIndex = controller.textPanes.indexOf(pane);
                if (keyEvent.getCode() == KeyCode.UP && controller.Notes.getChildren().indexOf(pane) > 0) {
                    HelloController.moveElementUp(pane, textPaneIndex, controller.Notes, controller.textPanes);
                } else if (keyEvent.getCode() == KeyCode.DOWN && textPaneIndex < controller.textPanes.size() - 1) {
                    HelloController.moveElementDown(pane, textPaneIndex, controller.Notes, controller.textPanes);
                }
            });
        }
    }

    static void drawRectangle(double screenX, double screenY, double screenW, double screenH, int pageIndex, String extractedText, PdfManager manager){
        Rectangle rect = new Rectangle(screenX, screenY, screenW, screenH);
        if (Highlights.lastHighlight != null && Highlights.holdingShift){
            Highlights.lastHighlight.addHighlightBox(rect, pageIndex, extractedText, manager.wrapperList.get(pageIndex));
        }
        else{
            Highlights highlights = new Highlights(manager.tabCustomClass);
            highlights.addHighlightBox(rect, pageIndex, extractedText, manager.wrapperList.get(pageIndex));
        }
        rect.setStrokeWidth(0);
        rect.setManaged(false);
    }

    static void submitEvidenceNotes(Highlights highlights, TabCustomClass tabCustomClass, HelloController helloController, Rectangle box, StackPane wrapper){
        Text input = new Text(extractText(highlights));
        VBox evidence = new VBox();
        evidence.setMouseTransparent(true);
        Text citation = new Text(tabCustomClass.getCitation().get());
        evidence.getChildren().addAll(input, citation);
        tabCustomClass.getCitation().addListener((_, _, newValue) -> {
            if (!newValue.isEmpty()){
                citation.setText(newValue);
                citation.setVisible(true);
                citation.setManaged(true);
            }
            else{
                citation.setVisible(false);
                citation.setManaged(false);
            }
        });
        citation.getStyleClass().add("citationText");
        if (!tabCustomClass.getCitation().get().isEmpty()){
            citation.setManaged(true);
            citation.setVisible(true);
        }
        StackPane pane = new StackPane();
        helloController.paneMap.put(pane, highlights);
        pane.getStyleClass().add("paneStyle");
        pane.getChildren().add(evidence);

        input.getStyleClass().add("commentary");
        pane.getStyleClass().add("evidencePane");

        helloController.updateEvidencePaneBackgroundStatus(true, pane, highlights);

        pane.setOnMouseEntered(_ -> {
            if (!pane.getStyleClass().contains("clickedText")){
                pane.getStyleClass().add("hoverText");
                helloController.updateEvidencePaneBackgroundStatus(false, pane, highlights);
            }
        });
        pane.setOnMouseExited(_ -> {
            if (pane.getStyleClass().contains("hoverText")){
                pane.getStyleClass().remove("hoverText");
                helloController.updateEvidencePaneBackgroundStatus(true, pane, highlights);
            }
        });
        pane.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY && helloController.clickedPane != pane){
                helloController.updateClickedPane(pane);
                helloController.switchView(helloController.getChangeHighlightsBox());
                helloController.evidenceSwitchTexts(false);
                pane.requestFocus();
                helloController.updateEvidencePaneBackgroundStatus(false, helloController.clickedPane, highlights);
                pane.getStyleClass().remove("hoverText");

                helloController.tabPane.getSelectionModel().select(tabCustomClass.getTab());
                Highlights.focusOnImageRegion(tabCustomClass.getScrollPane(), highlights.highlightBoxes.getFirst());
            }
            else if (mouseEvent.getButton() == MouseButton.SECONDARY && helloController.getCurrentHBox() == helloController.getChangeHighlightsBox()){
                helloController.switchView(helloController.getInitialInputTextBox());
                helloController.evidenceSwitchTexts(true);
                helloController.updateClickedPane(null);
            }

        });

        helloController.Notes.getChildren().add(pane);
        helloController.textPanes.add(pane);

        StackPane breakPane = new StackPane();
        breakPane.prefWidthProperty().bind(helloController.Notes.widthProperty());
        helloController.Notes.getChildren().add(breakPane);

        pane.setOnKeyPressed(keyEvent -> {
            int textPaneIndex = helloController.textPanes.indexOf(pane);
            if (keyEvent.getCode() == KeyCode.UP && helloController.Notes.getChildren().indexOf(pane) > 0){
                HelloController.moveElementUp(pane, textPaneIndex, helloController.Notes, helloController.textPanes);
            }
            else if (keyEvent.getCode() == KeyCode.DOWN && textPaneIndex < helloController.textPanes.size() - 1){
                HelloController.moveElementDown(pane, textPaneIndex, helloController.Notes, helloController.textPanes);
            }
            else if (keyEvent.getCode() == KeyCode.BACK_SPACE && box.getStrokeWidth() == strokeWidthClicked){
                highlights.removeHighlightBox(box, wrapper);
            }
            else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                box.setStrokeWidth(strokeWidthNormal);
            }
        });
    }

    private static String extractText (Highlights highlights) {
        StringBuilder answer = new StringBuilder();
        for (Rectangle i : highlights.highlightBoxes){
            answer.append(highlights.map.get(i).extractedText).append("\n");
        }
        return answer.toString();
    }

}
