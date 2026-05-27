package org.cs2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Rectangle2D;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class WordCoordinateStripper extends PDFTextStripper {
    public List<WordBox> documentWords = new ArrayList<>();

    public WordCoordinateStripper() throws IOException {
    }

    protected void writeString(String string, List<TextPosition> textPositions) {
        String[] wordsInLine = string.split(" ");
        int charIndex = 0;

        for(String word : wordsInLine) {
            if (!word.trim().isEmpty()) {
                float minX = Float.MAX_VALUE;
                float minY = Float.MAX_VALUE;
                float maxX = Float.MIN_VALUE;
                float maxY = Float.MIN_VALUE;

                for(int i = 0; i < word.length() && charIndex < textPositions.size(); ++i) {
                    TextPosition pos = textPositions.get(charIndex++);
                    minX = Math.min(minX, pos.getXDirAdj());
                    minY = Math.min(minY, pos.getYDirAdj());
                    maxX = Math.max(maxX, pos.getXDirAdj() + pos.getWidthDirAdj());
                    maxY = Math.max(maxY, pos.getYDirAdj() + pos.getHeightDir());
                }

                ++charIndex;

                if (minX != Float.MAX_VALUE && minY != Float.MAX_VALUE) {

                    // 2. Ensure width and height are at least 0
                    double width = Math.max(0, maxX - minX);
                    double height = Math.max(0, maxY - minY);

                    // 3. Only add if it's a "real" box
                    if (width > 0 && height > 0) {
                        documentWords.add(new WordBox(word, new javafx.geometry.Rectangle2D(minX, minY, width, height)));
                    }
                }
            }
        }

    }

    public static class WordBox {
        public String text;
        public Rectangle2D bounds;

        public WordBox(String text, Rectangle2D bounds) {
            this.text = text;
            this.bounds = bounds;
        }
    }
}
