package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import java.awt.geom.Rectangle2D;

public class TextMarginFinder implements RenderListener {
  private Rectangle2D.Float textRectangle = null;
  
  public void renderText(TextRenderInfo renderInfo) {
    if (this.textRectangle == null) {
      this.textRectangle = renderInfo.getDescentLine().getBoundingRectange();
    } else {
      this.textRectangle.add(renderInfo.getDescentLine().getBoundingRectange());
    } 
    this.textRectangle.add(renderInfo.getAscentLine().getBoundingRectange());
  }
  
  public float getLlx() {
    return this.textRectangle.x;
  }
  
  public float getLly() {
    return this.textRectangle.y;
  }
  
  public float getUrx() {
    return this.textRectangle.x + this.textRectangle.width;
  }
  
  public float getUry() {
    return this.textRectangle.y + this.textRectangle.height;
  }
  
  public float getWidth() {
    return this.textRectangle.width;
  }
  
  public float getHeight() {
    return this.textRectangle.height;
  }
  
  public void beginTextBlock() {}
  
  public void endTextBlock() {}
  
  public void renderImage(ImageRenderInfo renderInfo) {}
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\TextMarginFinder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */