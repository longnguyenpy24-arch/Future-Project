package org.cs2;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;

public class HelloController {
    @FXML
    private HBox initialInputTextBox;
    @FXML
    private HBox changeTextBox;
    @FXML
    private TextFlow Notes;
    @FXML
    private TextArea inputText;
    @FXML
    private TextArea nameOfTab;
    @FXML
    private TabPane tabPane;
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

    private final ArrayList<StackPane> textPanes = new ArrayList<>();
    private Tab placeHolderTab;
    private StackPane clickedPane = null;
    private String savedString = "";

    private void switchView(HBox boxShowing, HBox boxHiding){
        boxShowing.setVisible(false);
        boxShowing.setManaged(false);
        boxHiding.setVisible(true);
        boxHiding.setManaged(true);
    }
    private void switchTexts(boolean switchBack){
        if (switchBack){
            inputText.setText(savedString);
        }
        else{
            savedString = inputText.getText();
            inputText.setText(((Text)clickedPane.getChildren().getFirst()).getText());
        }
    }

    private void updateClickedPane(StackPane current) {
        if (clickedPane != null){
            clickedPane.getStyleClass().remove("clickedText");
        }
        clickedPane = current;
        clickedPane.getStyleClass().add("clickedText");
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

    @FXML
    private void initialize(){
        select = tabPane.getSelectionModel();
        changeTextBox.setVisible(false);
        changeTextBox.setManaged(false);
        placeHolderTab = new Tab(" ");
        placeHolderTab.setClosable(false);

        Platform.runLater(() -> Notes.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE && changeTextBox.isVisible()){
                    switchView(changeTextBox, initialInputTextBox);
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
                if (changeTextBox.isVisible()){
                    switchView(changeTextBox, initialInputTextBox);
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

    private void addLineBreak(){
        if (!textPanes.isEmpty()){
            Text text = (Text) textPanes.getLast().getChildren().getFirst();
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
        if (event.getSource() == lineBreakSubmitButton){
            addLineBreak();
        }
        else if(!inputText.getText().isEmpty()){
            Text input = new Text(inputText.getText());
            StackPane pane = new StackPane();
            pane.getStyleClass().add("paneStyle");
            pane.getChildren().add(input);
            inputText.clear();

            if(event.getSource() == commentarySubmitButton){
                input.getStyleClass().add("commentary");
                pane.getStyleClass().add("commentaryPane");
            }
            else if(event.getSource() == titleSubmitButton){
                input.getStyleClass().add("title");
                pane.getStyleClass().add("titlePane");
            }
            else if(event.getSource() == headerSubmitButton){
                input.getStyleClass().add("header");
                pane.getStyleClass().add("headerPane");
            }
            input.setOnMouseEntered(_ -> pane.getStyleClass().add("hoverText"));
            input.setOnMouseExited(_ -> pane.getStyleClass().remove("hoverText"));
            input.setOnMouseClicked(_ -> {
                updateClickedPane(pane);
                switchView(initialInputTextBox, changeTextBox);
                switchTexts(false);
            });

            Notes.getChildren().add(pane);
            textPanes.add(pane);

            StackPane breakPane = new StackPane();
            breakPane.prefWidthProperty().bind(Notes.widthProperty());
            Notes.getChildren().add(breakPane);
            Platform.runLater(() -> inputText.requestFocus());
        }
    }

    @FXML
    private void submitText() {
        ((Text) clickedPane.getChildren().getFirst()).setText(inputText.getText());
        switchTexts(true);
        switchView(changeTextBox, initialInputTextBox);
        clickedPane.getStyleClass().remove("clickedText");
    }

    @FXML
    private void deleteText() {
        Notes.getChildren().remove(clickedPane);
        clickedPane = null;
        switchTexts(true);
        switchView(changeTextBox, initialInputTextBox);
    }
}