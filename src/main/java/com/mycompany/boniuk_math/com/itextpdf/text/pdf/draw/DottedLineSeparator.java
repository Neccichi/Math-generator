package com.mycompany.boniuk_math.com.itextpdf.text.pdf.draw;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfContentByte;

public class DottedLineSeparator extends LineSeparator {
  protected float gap = 5.0F;
  
  public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
    canvas.saveState();
    canvas.setLineWidth(this.lineWidth);
    canvas.setLineCap(1);
    canvas.setLineDash(0.0F, this.gap, this.gap / 2.0F);
    drawLine(canvas, llx, urx, y);
    canvas.restoreState();
  }
  
  public float getGap() {
    return this.gap;
  }
  
  public void setGap(float gap) {
    this.gap = gap;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\draw\DottedLineSeparator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */