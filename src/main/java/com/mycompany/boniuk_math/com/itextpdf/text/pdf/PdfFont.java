package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;

class PdfFont implements Comparable<PdfFont> {
  private BaseFont font;
  
  private float size;
  
  protected Image image;
  
  protected float hScale = 1.0F;
  
  PdfFont(BaseFont bf, float size) {
    this.size = size;
    this.font = bf;
  }
  
  public int compareTo(PdfFont pdfFont) {
    if (this.image != null)
      return 0; 
    if (pdfFont == null)
      return -1; 
    try {
      if (this.font != pdfFont.font)
        return 1; 
      if (size() != pdfFont.size())
        return 2; 
      return 0;
    } catch (ClassCastException cce) {
      return -2;
    } 
  }
  
  float size() {
    if (this.image == null)
      return this.size; 
    return this.image.getScaledHeight();
  }
  
  float width() {
    return width(32);
  }
  
  float width(int character) {
    if (this.image == null)
      return this.font.getWidthPoint(character, this.size) * this.hScale; 
    return this.image.getScaledWidth();
  }
  
  float width(String s) {
    if (this.image == null)
      return this.font.getWidthPoint(s, this.size) * this.hScale; 
    return this.image.getScaledWidth();
  }
  
  BaseFont getFont() {
    return this.font;
  }
  
  void setImage(Image image) {
    this.image = image;
  }
  
  static PdfFont getDefaultFont() {
    try {
      BaseFont bf = BaseFont.createFont("Helvetica", "Cp1252", false);
      return new PdfFont(bf, 12.0F);
    } catch (Exception ee) {
      throw new ExceptionConverter(ee);
    } 
  }
  
  void setHorizontalScaling(float hScale) {
    this.hScale = hScale;
  }
  
  float getHorizontalScaling() {
    return this.hScale;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfFont.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */