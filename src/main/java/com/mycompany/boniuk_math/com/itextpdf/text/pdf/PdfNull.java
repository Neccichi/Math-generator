package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class PdfNull extends PdfObject {
  public static final PdfNull PDFNULL = new PdfNull();
  
  private static final String CONTENT = "null";
  
  public PdfNull() {
    super(8, "null");
  }
  
  public String toString() {
    return "null";
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfNull.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */