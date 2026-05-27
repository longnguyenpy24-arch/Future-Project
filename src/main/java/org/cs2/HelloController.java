package org.cs2;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.HashMap;

public class HelloController {
    @FXML
    HBox changeNotesBox;
    @FXML
    private HBox changeHighlightsBox;
    @FXML
    private HBox initialInputTextBox;
    @FXML
    TextFlow Notes;
    @FXML
    TextArea inputText;
    @FXML
    private TextArea nameOfTab;
    @FXML
    TabPane tabPane;
    @FXML
    private Button commentarySubmitButton;
    @FXML
    private Button titleSubmitButton;
    @FXML
    private Button headerSubmitButton;
    @FXML
    private Button lineBreakSubmitButton;
    @FXML
    private SelectionModel<Tab> select;

    final ArrayList<StackPane> textPanes = new ArrayList<>();
    private Tab placeHolderTab;
    StackPane clickedPane = null;
    private String savedString = "";
    private HBox currentHbox;
    final HashMap<StackPane, Highlights> paneMap = new HashMap<>();
    final HighlightStorage storage = new HighlightStorage();

    HBox getCurrentHBox() {
        return currentHbox;
    }
    HBox getInitialInputTextBox() {
        return initialInputTextBox;
    }
    HBox getChangeHighlightsBox(){
        return changeHighlightsBox;
    }

    void switchView(HBox boxHiding){
        currentHbox.setVisible(false);
        currentHbox.setManaged(false);
        boxHiding.setVisible(true);
        boxHiding.setManaged(true);
        currentHbox = boxHiding;
    }
    void switchTexts(boolean switchBack){
        if (switchBack){
            inputText.setText(savedString);
        }
        else{
            savedString = inputText.getText();
            inputText.setText(((Text)clickedPane.getChildren().getFirst()).getText());
        }
    }

    void evidenceSwitchTexts(boolean switchBack) {
        if (switchBack){
            inputText.setText(savedString);
        }
        else{
            savedString = inputText.getText();
            inputText.setText(((Text)((VBox)clickedPane.getChildren().getFirst()).getChildren().getFirst()).getText());
        }
    }
    void updateClickedPane(StackPane current) {
        if (clickedPane != null){
            if (clickedPane.getChildren().getFirst() instanceof VBox vbox){
                updateEvidencePaneBackgroundStatus(true, clickedPane, paneMap.get(clickedPane));
                if (((Text) vbox.getChildren().getFirst()).getText().equals(savedString)){
                    savedString = "";
                    inputText.setText(savedString);
                }
            }
            else{
                if (((Text) clickedPane.getChildren().getFirst()).getText().equals(savedString)){
                    savedString = "";
                    inputText.setText(savedString);
                }
            }
            clickedPane.getStyleClass().remove("clickedText");
        }
        if (current != null){
            current.getStyleClass().add("clickedText");
        }
        clickedPane = current;

    }

    private void updatePlaceHolder() {
        if (tabPane  == null){
            return;
        }
        boolean hasRealTabs = tabPane.getTabs().stream().anyMatch(tab -> tab != placeHolderTab);
        if (!hasRealTabs){
            if (!tabPane.getTabs().contains(placeHolderTab)){
                tabPane.getTabs().add(placeHolderTab);
            }
        }
        else{
            tabPane.getTabs().remove(placeHolderTab);
        }
        if(tabPane.getTabs().isEmpty()){
            tabPane.getTabs().add(placeHolderTab);
        }
    }

    void updateEvidencePaneBackgroundStatus (boolean switchback, StackPane pane, Highlights highlightGroup){
        if (switchback) {
            if (!pane.getStyleClass().contains(highlightGroup.tabCustomClass.getColorStyleClass())){
                pane.getStyleClass().add(highlightGroup.tabCustomClass.getColorStyleClass());
                pane.setOpacity(0.6);
            }
        }
        else {
            if (pane.getStyleClass().contains(highlightGroup.tabCustomClass.getColorStyleClass())){
                pane.getStyleClass().remove(highlightGroup.tabCustomClass.getColorStyleClass());
                pane.setOpacity(1);
            }

        }
    }

    @FXML
    private void initialize(){
        currentHbox = initialInputTextBox;
        select = tabPane.getSelectionModel();
        changeNotesBox.setVisible(false);
        changeNotesBox.setManaged(false);
        changeHighlightsBox.setVisible(false);
        changeHighlightsBox.setManaged(false);
        Highlights.setHelloController(this);

        placeHolderTab = new Tab(" ");
        placeHolderTab.setClosable(false);

        Platform.runLater(() -> Notes.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE && !initialInputTextBox.isVisible()){
                if (changeHighlightsBox.isVisible()) {
                    updateEvidencePaneBackgroundStatus(true, clickedPane, paneMap.get(clickedPane));
                }
                switchView(initialInputTextBox);
                clickedPane.getStyleClass().remove("clickedText");
                clickedPane = null;
                switchTexts(true);
            }
            else if (event.getCode() == KeyCode.BACK_SPACE && clickedPane != null){
                deleteText();
            }
            else if (initialInputTextBox.isVisible()){
                if (event.getCode() == KeyCode.DIGIT1 && event.isControlDown()){
                    titleSubmitButton.fire();
                }
                else if (event.getCode() == KeyCode.DIGIT2 && event.isControlDown()){
                    headerSubmitButton.fire();
                }
                else if (event.getCode() == KeyCode.DIGIT3 && event.isControlDown()){
                    commentarySubmitButton.fire();
                }
                else if (event.getCode() == KeyCode.DIGIT4 && event.isControlDown()){
                    lineBreakSubmitButton.fire();
                }
            }
            else if (event.getCode() == KeyCode.RIGHT){
                int tabIndex = select.getSelectedIndex();
                if (tabIndex >= tabPane.getTabs().size() - 1){
                    tabIndex = 0;
                }
                else {
                    tabIndex++;
                }
                int finalTabIndex = tabIndex;
                select.select(finalTabIndex);
                event.consume();
            }
            else if (event.getCode() == KeyCode.LEFT){
                int tabIndex = select.getSelectedIndex();
                if (tabIndex <= 0){
                    tabIndex = tabPane.getTabs().size() - 1;
                }
                else {
                    tabIndex--;
                }
                int finalTabIndex = tabIndex;
                select.select(finalTabIndex);
                event.consume();
            }
        }));

        Platform.runLater(() -> Notes.getScene().setOnMouseClicked(event  ->{
            if(event.getButton() == MouseButton.SECONDARY){
                if (changeHighlightsBox.isVisible()){
                    updateEvidencePaneBackgroundStatus(true, clickedPane, paneMap.get(clickedPane));
                }
                if (changeNotesBox.isVisible() || changeHighlightsBox.isVisible()){
                    switchView(initialInputTextBox);
                    clickedPane.getStyleClass().remove("clickedText");
                    clickedPane = null;
                    switchTexts(true);
                }
            }
        }));


        tabPane.getTabs().addListener((ListChangeListener<Tab>) _ -> Platform.runLater(this::updatePlaceHolder));
        updatePlaceHolder();

        nameOfTab.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getText();
            if (newText.matches("[\\w0-9 ]*")) {
                return change;
            }
            return null;
        }));}

    @FXML
    private void handleAddTab(){
        if (!nameOfTab.getText().isEmpty()){
            TabCustomClass newTab = new TabCustomClass(nameOfTab.getText());
            nameOfTab.clear();
            tabPane.getTabs().add(newTab.getTab());
            select.select(newTab.getTab());
            nameOfTab.requestFocus();
        }
    }

    void addLineBreak(){
        if (!textPanes.isEmpty()){
            Text text;
            if (textPanes.getLast().getChildren().getFirst() instanceof VBox vbox){
                text = (Text) vbox.getChildren().getFirst();
            }
            else {
                text = (Text) textPanes.getLast().getChildren().getFirst();
            }
            if(text.getText().contains("\n\u200B")){
                text.setText(text.getText() + "\n");
            }
            else{
                text.setText(text.getText() + "\n\u200B");
            }
        }
        Platform.runLater(() -> inputText.requestFocus());
    }

    @FXML
    private void submitInput(ActionEvent event) {
        if (event.getSource() == lineBreakSubmitButton){ UIDesign.submitInputNotes(UIDesign.NotesType.LINE_BREAK_SUBMIT_BUTTON, this); }
        else if (event.getSource() == commentarySubmitButton){ UIDesign.submitInputNotes(UIDesign.NotesType.COMMENTARY_SUBMIT_BUTTON, this); }
        else if (event.getSource() == headerSubmitButton){ UIDesign.submitInputNotes(UIDesign.NotesType.HEADER_SUBMIT_BUTTON, this); }
        else if (event.getSource() == titleSubmitButton){ UIDesign.submitInputNotes(UIDesign.NotesType.TITLE_SUBMIT_BUTTON, this); }
    }
    private static void movePane(int index, StackPane pane, TextFlow Notes) {
        int tempIndex = Notes.getChildren().indexOf(pane);
        Notes.getChildren().remove(pane);
        Notes.getChildren().add(tempIndex + index, pane);
    }
    static void moveElementUp(StackPane pane, int textPaneIndex, TextFlow notes, ArrayList<StackPane> textPane){
        int storageIndex = notes.getChildren().indexOf(pane);
        movePane(-2, pane, notes);
        movePane(1, (StackPane) notes.getChildren().get(storageIndex-1), notes);
        textPane.add(textPaneIndex-1, textPane.get(textPaneIndex));
        textPane.remove(textPaneIndex+1);
    }
    static void moveElementDown(StackPane pane, int textPaneIndex, TextFlow notes, ArrayList<StackPane> textPane){
        int storageIndex = notes.getChildren().indexOf(pane);
        movePane(1, pane, notes);
        movePane(-2, (StackPane) notes.getChildren().get(storageIndex+2), notes);
        textPane.add(textPaneIndex+2, textPane.get(textPaneIndex));
        textPane.remove(textPaneIndex);
    }

    @FXML
    private void submitNotesText() {
        ((Text) clickedPane.getChildren().getFirst()).setText(inputText.getText());
        switchTexts(true);
        switchView(initialInputTextBox);
        clickedPane.getStyleClass().remove("clickedText");
    }

    @FXML
    private void deleteText() {
        if (changeHighlightsBox.isVisible()) {
            paneMap.remove(clickedPane);
        }
        Notes.getChildren().remove(Notes.getChildren().indexOf(clickedPane)+1);
        Notes.getChildren().remove(clickedPane);
        textPanes.remove(clickedPane);
        clickedPane = null;
        switchTexts(true);
        switchView(initialInputTextBox);
    }
    @FXML
    private void submitHighlightsText() {
        ((Text) ((VBox) clickedPane.getChildren().getFirst()).getChildren().getFirst()).setText(inputText.getText());
        switchTexts(true);
        switchView(initialInputTextBox);
        updateEvidencePaneBackgroundStatus(true, clickedPane, paneMap.get(clickedPane));
        clickedPane.getStyleClass().remove("clickedText");
        clickedPane = null;
    }
}