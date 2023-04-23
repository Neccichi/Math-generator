package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfNumber;

public class MarkedContentInfo {
  private final PdfName tag;
  
  private final PdfDictionary dictionary;
  
  public MarkedContentInfo(PdfName tag, PdfDictionary dictionary) {
    this.tag = tag;
    this.dictionary = (dictionary != null) ? dictionary : new PdfDictionary();
  }
  
  public PdfName getTag() {
    return this.tag;
  }
  
  public boolean hasMcid() {
    return this.dictionary.contains(PdfName.MCID);
  }
  
  public int getMcid() {
    PdfNumber id = this.dictionary.getAsNumber(PdfName.MCID);
    if (id == null)
      throw new IllegalStateException("MarkedContentInfo does not contain MCID"); 
    return id.intValue();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\MarkedContentInfo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */