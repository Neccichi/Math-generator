package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

public interface RenderListener {
  void beginTextBlock();
  
  void renderText(TextRenderInfo paramTextRenderInfo);
  
  void endTextBlock();
  
  void renderImage(ImageRenderInfo paramImageRenderInfo);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\RenderListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */