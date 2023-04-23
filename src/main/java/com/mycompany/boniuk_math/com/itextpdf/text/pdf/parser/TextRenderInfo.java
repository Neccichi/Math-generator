package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.CMapAwareDocumentFont;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.DocumentFont;
import java.util.ArrayList;
import java.util.Collection;

public class TextRenderInfo {
  private final String text;
  
  private final Matrix textToUserSpaceTransformMatrix;
  
  private final GraphicsState gs;
  
  private final Collection<MarkedContentInfo> markedContentInfos;
  
  TextRenderInfo(String text, GraphicsState gs, Matrix textMatrix, Collection<MarkedContentInfo> markedContentInfo) {
    this.text = text;
    this.textToUserSpaceTransformMatrix = textMatrix.multiply(gs.ctm);
    this.gs = gs;
    this.markedContentInfos = new ArrayList<MarkedContentInfo>(markedContentInfo);
  }
  
  public String getText() {
    return this.text;
  }
  
  public boolean hasMcid(int mcid) {
    for (MarkedContentInfo info : this.markedContentInfos) {
      if (info.hasMcid() && 
        info.getMcid() == mcid)
        return true; 
    } 
    return false;
  }
  
  float getUnscaledWidth() {
    return getStringWidth(this.text);
  }
  
  public LineSegment getBaseline() {
    return getUnscaledBaselineWithOffset(0.0F).transformBy(this.textToUserSpaceTransformMatrix);
  }
  
  public LineSegment getAscentLine() {
    float ascent = this.gs.getFont().getFontDescriptor(1, this.gs.getFontSize());
    return getUnscaledBaselineWithOffset(ascent).transformBy(this.textToUserSpaceTransformMatrix);
  }
  
  public LineSegment getDescentLine() {
    float descent = this.gs.getFont().getFontDescriptor(3, this.gs.getFontSize());
    return getUnscaledBaselineWithOffset(descent).transformBy(this.textToUserSpaceTransformMatrix);
  }
  
  private LineSegment getUnscaledBaselineWithOffset(float yOffset) {
    return new LineSegment(new Vector(0.0F, yOffset, 1.0F), new Vector(getUnscaledWidth(), yOffset, 1.0F));
  }
  
  public DocumentFont getFont() {
    return (DocumentFont)this.gs.getFont();
  }
  
  public float getSingleSpaceWidth() {
    LineSegment textSpace = new LineSegment(new Vector(0.0F, 0.0F, 1.0F), new Vector(getUnscaledFontSpaceWidth(), 0.0F, 1.0F));
    LineSegment userSpace = textSpace.transformBy(this.textToUserSpaceTransformMatrix);
    return userSpace.getLength();
  }
  
  public int getTextRenderMode() {
    return this.gs.renderMode;
  }
  
  private float getUnscaledFontSpaceWidth() {
    char charToUse = ' ';
    if (this.gs.font.getWidth(charToUse) == 0)
      charToUse = ' '; 
    return getStringWidth(String.valueOf(charToUse));
  }
  
  private float getStringWidth(String string) {
    CMapAwareDocumentFont cMapAwareDocumentFont = this.gs.font;
    char[] chars = string.toCharArray();
    float totalWidth = 0.0F;
    for (int i = 0; i < chars.length; i++) {
      float w = cMapAwareDocumentFont.getWidth(chars[i]) / 1000.0F;
      float wordSpacing = (chars[i] == ' ') ? this.gs.wordSpacing : 0.0F;
      totalWidth += (w * this.gs.fontSize + this.gs.characterSpacing + wordSpacing) * this.gs.horizontalScaling;
    } 
    return totalWidth;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\TextRenderInfo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */