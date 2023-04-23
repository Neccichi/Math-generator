package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.CMapAwareDocumentFont;

public class GraphicsState {
  Matrix ctm;
  
  float characterSpacing;
  
  float wordSpacing;
  
  float horizontalScaling;
  
  float leading;
  
  CMapAwareDocumentFont font;
  
  float fontSize;
  
  int renderMode;
  
  float rise;
  
  boolean knockout;
  
  public GraphicsState() {
    this.ctm = new Matrix();
    this.characterSpacing = 0.0F;
    this.wordSpacing = 0.0F;
    this.horizontalScaling = 1.0F;
    this.leading = 0.0F;
    this.font = null;
    this.fontSize = 0.0F;
    this.renderMode = 0;
    this.rise = 0.0F;
    this.knockout = true;
  }
  
  public GraphicsState(GraphicsState source) {
    this.ctm = source.ctm;
    this.characterSpacing = source.characterSpacing;
    this.wordSpacing = source.wordSpacing;
    this.horizontalScaling = source.horizontalScaling;
    this.leading = source.leading;
    this.font = source.font;
    this.fontSize = source.fontSize;
    this.renderMode = source.renderMode;
    this.rise = source.rise;
    this.knockout = source.knockout;
  }
  
  public Matrix getCtm() {
    return this.ctm;
  }
  
  public float getCharacterSpacing() {
    return this.characterSpacing;
  }
  
  public float getWordSpacing() {
    return this.wordSpacing;
  }
  
  public float getHorizontalScaling() {
    return this.horizontalScaling;
  }
  
  public float getLeading() {
    return this.leading;
  }
  
  public CMapAwareDocumentFont getFont() {
    return this.font;
  }
  
  public float getFontSize() {
    return this.fontSize;
  }
  
  public int getRenderMode() {
    return this.renderMode;
  }
  
  public float getRise() {
    return this.rise;
  }
  
  public boolean isKnockout() {
    return this.knockout;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\GraphicsState.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */