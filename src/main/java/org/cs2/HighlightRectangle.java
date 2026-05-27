package org.cs2;

class HighlightRectangle{
    final double screenX;
    final double screenY;
    final double screenW;
    final double screenH;
    final int pageIndex;
    final String extractedText;
    public HighlightRectangle(double screenX, double screenY, double screenW, double screenH, int pageIndex, String extractedText, HighlightGroup group){
        this.screenX = screenX;
        this.screenY = screenY;
        this.screenW = screenW;
        this.screenH = screenH;
        this.pageIndex = pageIndex;
        this.extractedText = extractedText;
        group.highlightRectangles.add(this);
    }
}
