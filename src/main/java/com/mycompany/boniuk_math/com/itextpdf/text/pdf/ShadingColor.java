package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class ShadingColor extends ExtendedColor {
  private static final long serialVersionUID = 4817929454941328671L;
  
  PdfShadingPattern shadingPattern;
  
  public ShadingColor(PdfShadingPattern shadingPattern) {
    super(5, 0.5F, 0.5F, 0.5F);
    this.shadingPattern = shadingPattern;
  }
  
  public PdfShadingPattern getPdfShadingPattern() {
    return this.shadingPattern;
  }
  
  public boolean equals(Object obj) {
    return (this == obj);
  }
  
  public int hashCode() {
    return this.shadingPattern.hashCode();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\ShadingColor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */