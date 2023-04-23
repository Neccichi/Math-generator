package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class PdfTransparencyGroup extends PdfDictionary {
  public PdfTransparencyGroup() {
    put(PdfName.S, PdfName.TRANSPARENCY);
  }
  
  public void setIsolated(boolean isolated) {
    if (isolated) {
      put(PdfName.I, PdfBoolean.PDFTRUE);
    } else {
      remove(PdfName.I);
    } 
  }
  
  public void setKnockout(boolean knockout) {
    if (knockout) {
      put(PdfName.K, PdfBoolean.PDFTRUE);
    } else {
      remove(PdfName.K);
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfTransparencyGroup.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */