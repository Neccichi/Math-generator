package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

public abstract class RenderFilter {
  public boolean allowText(TextRenderInfo renderInfo) {
    return true;
  }
  
  public boolean allowImage(ImageRenderInfo renderInfo) {
    return true;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\RenderFilter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */