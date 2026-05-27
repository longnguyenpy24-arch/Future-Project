package org.cs2;

import java.util.ArrayList;

public class HighlightStorageFile {
    final ArrayList<HighlightGroup> highlightGroups = new ArrayList<>();
    final String PdfPath;
    String citation;

    public HighlightStorageFile(String PdfPath, String citation, HighlightStorage storage){
        this.PdfPath = PdfPath;
        this.citation = citation;
        storage.filesArrayList.add(this);
    }

    void cleanUp(){
        for (int i = 0; i < highlightGroups.size(); i++){
            if (highlightGroups.get(i).highlightRectangles.isEmpty()){
                highlightGroups.remove(i);
                i--;
            }
        }
    }
    // Link highlight groups with the evidence panes

}
