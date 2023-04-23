package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.util.HashMap;

public class PdfPage extends PdfDictionary {
  private static final String[] boxStrings = new String[] { "crop", "trim", "art", "bleed" };
  
  private static final PdfName[] boxNames = new PdfName[] { PdfName.CROPBOX, PdfName.TRIMBOX, PdfName.ARTBOX, PdfName.BLEEDBOX };
  
  public static final PdfNumber PORTRAIT = new PdfNumber(0);
  
  public static final PdfNumber LANDSCAPE = new PdfNumber(90);
  
  public static final PdfNumber INVERTEDPORTRAIT = new PdfNumber(180);
  
  public static final PdfNumber SEASCAPE = new PdfNumber(270);
  
  PdfRectangle mediaBox;
  
  PdfPage(PdfRectangle mediaBox, HashMap<String, PdfRectangle> boxSize, PdfDictionary resources, int rotate) {
    super(PAGE);
    this.mediaBox = mediaBox;
    put(PdfName.MEDIABOX, mediaBox);
    put(PdfName.RESOURCES, resources);
    if (rotate != 0)
      put(PdfName.ROTATE, new PdfNumber(rotate)); 
    for (int k = 0; k < boxStrings.length; k++) {
      PdfObject rect = boxSize.get(boxStrings[k]);
      if (rect != null)
        put(boxNames[k], rect); 
    } 
  }
  
  PdfPage(PdfRectangle mediaBox, HashMap<String, PdfRectangle> boxSize, PdfDictionary resources) {
    this(mediaBox, boxSize, resources, 0);
  }
  
  public boolean isParent() {
    return false;
  }
  
  void add(PdfIndirectReference contents) {
    put(PdfName.CONTENTS, contents);
  }
  
  PdfRectangle rotateMediaBox() {
    this.mediaBox = this.mediaBox.rotate();
    put(PdfName.MEDIABOX, this.mediaBox);
    return this.mediaBox;
  }
  
  PdfRectangle getMediaBox() {
    return this.mediaBox;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPage.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */