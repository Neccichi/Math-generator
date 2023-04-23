package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class SpotColor extends ExtendedColor {
  private static final long serialVersionUID = -6257004582113248079L;
  
  PdfSpotColor spot;
  
  float tint;
  
  public SpotColor(PdfSpotColor spot, float tint) {
    super(3, (spot.getAlternativeCS().getRed() / 255.0F - 1.0F) * tint + 1.0F, (spot.getAlternativeCS().getGreen() / 255.0F - 1.0F) * tint + 1.0F, (spot.getAlternativeCS().getBlue() / 255.0F - 1.0F) * tint + 1.0F);
    this.spot = spot;
    this.tint = tint;
  }
  
  public PdfSpotColor getPdfSpotColor() {
    return this.spot;
  }
  
  public float getTint() {
    return this.tint;
  }
  
  public boolean equals(Object obj) {
    return (this == obj);
  }
  
  public int hashCode() {
    return this.spot.hashCode() ^ Float.floatToIntBits(this.tint);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\SpotColor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */