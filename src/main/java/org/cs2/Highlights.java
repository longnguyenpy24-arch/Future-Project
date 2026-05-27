package org.cs2;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.ScrollPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Highlights {
    static class Tuple {
        public int pageIndex;
        public String extractedText;
        public  Tuple(int pageIndex, String extractedText) {
            this.pageIndex = pageIndex;
            this.extractedText = extractedText;
        }
    }
    private static int colorIndex;
    final List<Rectangle> highlightBoxes = new ArrayList<>();
    protected static Highlights lastHighlight = null;
    highlightColor currentHighlight;
    protected static boolean holdingShift = false;
    final HashMap<Rectangle, Tuple> map = new HashMap<>();
    private static final double strokeWidthHover = 1.0;
    static final double strokeWidthNormal = 0.0;
    static final double strokeWidthClicked = 3.0;
    private static final double submitStroke = 2.0;
    private static HelloController helloController;
    final TabCustomClass tabCustomClass;
    public Highlights(TabCustomClass tabCustomClass) {
        this.tabCustomClass = tabCustomClass;
        currentHighlight = highlightColor.values()[colorIndex];
        colorIndex++;
        colorIndex %= highlightColor.values().length;
        lastHighlight = this;
    }

    static void setHelloController(HelloController helloController){
        Highlights.helloController = helloController;
    }
    protected void addHighlightBox(Rectangle box, int pageIndex, String extractedText, StackPane wrapper){
        map.put(box, new Tuple(pageIndex, extractedText));
        box.setFocusTraversable(true);
        box.setOnMouseEntered(_ -> {
            if (box.getStrokeWidth() != strokeWidthClicked && box.getStrokeWidth() != submitStroke){
                box.setStrokeWidth(strokeWidthHover);
            }
        });
        box.setOnMouseExited(_ -> {
            if (box.getStrokeWidth() == strokeWidthHover){
                box.setStrokeWidth(strokeWidthNormal);
            }
        });
        box.focusedProperty().addListener((_, _, newValue) -> {
            if (!newValue){
                box.setStrokeWidth(strokeWidthNormal);
            }
        });
        box.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY){
                box.setStrokeWidth(strokeWidthClicked);
                box.requestFocus();
                if (helloController.getCurrentHBox() == helloController.getChangeHighlightsBox()){
                    helloController.switchView(helloController.getInitialInputTextBox());
                }
                helloController.evidenceSwitchTexts(true);
                lastHighlight = this;
            }
            else if (event.getButton() == MouseButton.SECONDARY){
                UIDesign.submitEvidenceNotes(this, tabCustomClass, helloController, box, wrapper);
            }
        });

        if (highlightBoxes.isEmpty()){
            applyStyleAndAdd(box, wrapper, 0);
            return;
        }

        int left = 0;
        int right = highlightBoxes.size();

        while (left < right) {
            int middle = (left + right) / 2;
            Rectangle checking = highlightBoxes.get(middle);
            Tuple boxData = map.get(box);
            Tuple checkData = map.get(checking);

            int comparison;

            // 1. Sort by Page First
            if (boxData.pageIndex != checkData.pageIndex) {
                comparison = Integer.compare(boxData.pageIndex, checkData.pageIndex);
            } else {
                boolean intersectsY = (box.getY() < checking.getY() + checking.getHeight()) &&
                        (checking.getY() < box.getY() + box.getHeight());

                if (intersectsY) {
                    comparison = Double.compare(box.getX(), checking.getX());
                } else {
                    comparison = Double.compare(box.getY(), checking.getY());
                }
            }

            if (comparison > 0) {
                left = middle + 1;
            } else {
                right = middle;
            }
        }
        applyStyleAndAdd(box, wrapper, left);
    }

    static void focusOnImageRegion(ScrollPane scrollPane, Rectangle highlightBox) {
        double contentHeight = scrollPane.getContent().getBoundsInLocal().getHeight();
        double viewportHeight = scrollPane.getViewportBounds().getHeight();
        double boxY = scrollPane.getContent().sceneToLocal(highlightBox.localToScene(highlightBox.getBoundsInLocal())).getMinY();
        double centeredY = boxY - (viewportHeight / 2);
        double vValue = centeredY / (contentHeight - viewportHeight);
        scrollPane.setVvalue(Math.max(0, Math.min(1, vValue)));
    }

    private void applyStyleAndAdd(Rectangle box, StackPane wrapper, int insertIndex) {
        box.setFill(currentHighlight.getFill());
        box.setStroke(currentHighlight.getStroke());
        highlightBoxes.add(insertIndex, box);
        wrapper.getChildren().add(box);
    }

    protected void removeHighlightBox (Rectangle box, StackPane wrapper){
        helloController.paneMap.remove(wrapper);
        helloController.textPanes.remove(wrapper);
        helloController.Notes.getChildren().remove(helloController.Notes.getChildren().indexOf(wrapper)+1);
        wrapper.getChildren().remove(box);
        map.remove(box);
        highlightBoxes.remove(box);
        helloController.updateClickedPane(null);
    }

    public enum highlightColor{
        ELECTRIC_YELLOW(255, 255, 0, 0.3, Color.GOLD),
        NEON_GREEN(57, 255, 20, 0.25, Color.LIMEGREEN),
        BRIGHT_PINK(255, 20, 147, 0.25, Color.DEEPPINK),
        CYAN_BLUE(0, 255, 255, 0.25, Color.DARKCYAN),
        SAFETY_ORANGE(255, 165, 0, 0.3, Color.ORANGERED),

        SOFT_LAVENDER(230, 230, 250, 0.4, Color.MEDIUMPURPLE),
        MINT_LEAF(152, 255, 152, 0.3, Color.MEDIUMSEAGREEN),
        SKY_BLUE(135, 206, 235, 0.3, Color.STEELBLUE),
        PEACH_FUZZ(255, 218, 185, 0.4, Color.CORAL),
        ROSE_QUARTZ(247, 202, 201, 0.4, Color.PALEVIOLETRED),

        ROYAL_NAVY(0, 35, 102, 0.15, Color.ROYALBLUE),
        SLATE_GRAY(112, 128, 144, 0.2, Color.SLATEGRAY),
        FOREST_GREEN(34, 139, 34, 0.2, Color.FORESTGREEN),
        DEEP_CRIMSON(165, 42, 42, 0.15, Color.FIREBRICK),
        RICH_TEAL(0, 128, 128, 0.2, Color.TEAL),

        AMBER_GLOW(255, 191, 0, 0.3, Color.DARKORANGE),
        ELECTRIC_PURPLE(191, 0, 255, 0.2, Color.VIOLET),
        TURQUOISE(64, 224, 208, 0.25, Color.DARKTURQUOISE),
        BLOOD_RED(255, 0, 0, 0.15, Color.RED),
        MIDNIGHT(25, 25, 112, 0.2, Color.NAVY);

        private final int r, g, b;
        private final double opacity;
        private final Color stroke;
        highlightColor(int r, int g, int b, double opacity, Color stroke) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.opacity = opacity;
            this.stroke = stroke;
        }

        public Color getStroke(){
            return stroke;
        }
        public Color getFill() {
            return Color.rgb(r, g, b, opacity);
        }
    }
}