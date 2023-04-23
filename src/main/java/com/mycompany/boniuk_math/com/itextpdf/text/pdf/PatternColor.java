package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class PatternColor extends ExtendedColor {
  private static final long serialVersionUID = -1185448552860615964L;
  
  PdfPatternPainter painter;
  
  public PatternColor(PdfPatternPainter painter) {
    super(4, 0.5F, 0.5F, 0.5F);
    this.painter = painter;
  }
  
  public PdfPatternPainter getPainter() {
    return this.painter;
  }
  
  public boolean equals(Object obj) {
    return (this == obj);
  }
  
  public int hashCode() {
    return this.painter.hashCode();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PatternColor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */