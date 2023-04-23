package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class PdfBorderArray extends PdfArray {
  public PdfBorderArray(float hRadius, float vRadius, float width) {
    this(hRadius, vRadius, width, null);
  }
  
  public PdfBorderArray(float hRadius, float vRadius, float width, PdfDashPattern dash) {
    super(new PdfNumber(hRadius));
    add(new PdfNumber(vRadius));
    add(new PdfNumber(width));
    if (dash != null)
      add(dash); 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfBorderArray.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */