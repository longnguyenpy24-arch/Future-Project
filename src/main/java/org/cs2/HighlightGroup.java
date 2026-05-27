package org.cs2;

import java.util.ArrayList;
import java.util.UUID;

public class HighlightGroup{
    final ArrayList<HighlightRectangle> highlightRectangles = new ArrayList<>();
    final String uniqueId;

    public HighlightGroup(HighlightStorageFile fileStorage, HighlightStorage storage){
        fileStorage.highlightGroups.add(this);
        uniqueId = UUID.randomUUID().toString();
        storage.idReference.put(this, uniqueId);
        storage.evidenceReference.put(uniqueId, this);
    }

    void removeHighlightRectangle(HighlightRectangle highlightRectangle){
        highlightRectangles.remove(highlightRectangle);
    }

}
