package org.cs2;

import java.util.ArrayList;
import java.util.HashMap;

public class HighlightStorage{
    final ArrayList<HighlightStorageFile> filesArrayList = new ArrayList<>();
    final HashMap<String, HighlightGroup> evidenceReference = new HashMap<>();
    final HashMap<HighlightGroup, String> idReference = new HashMap<>();

}

