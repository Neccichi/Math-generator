package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

public class SimpleTextExtractionStrategy implements TextExtractionStrategy {
  private Vector lastStart;
  
  private Vector lastEnd;
  
  private final StringBuffer result = new StringBuffer();
  
  public void beginTextBlock() {}
  
  public void endTextBlock() {}
  
  public String getResultantText() {
    return this.result.toString();
  }
  
  public void renderText(TextRenderInfo renderInfo) {
    boolean firstRender = (this.result.length() == 0);
    boolean hardReturn = false;
    LineSegment segment = renderInfo.getBaseline();
    Vector start = segment.getStartPoint();
    Vector end = segment.getEndPoint();
    if (!firstRender) {
      Vector x0 = start;
      Vector x1 = this.lastStart;
      Vector x2 = this.lastEnd;
      float dist = x2.subtract(x1).cross(x1.subtract(x0)).lengthSquared() / x2.subtract(x1).lengthSquared();
      float sameLineThreshold = 1.0F;
      if (dist > sameLineThreshold)
        hardReturn = true; 
    } 
    if (hardReturn) {
      this.result.append('\n');
    } else if (!firstRender && 
      this.result.charAt(this.result.length() - 1) != ' ' && renderInfo.getText().length() > 0 && renderInfo.getText().charAt(0) != ' ') {
      float spacing = this.lastEnd.subtract(start).length();
      if (spacing > renderInfo.getSingleSpaceWidth() / 2.0F)
        this.result.append(' '); 
    } 
    this.result.append(renderInfo.getText());
    this.lastStart = start;
    this.lastEnd = end;
  }
  
  public void renderImage(ImageRenderInfo renderInfo) {}
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\SimpleTextExtractionStrategy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */