package com.mycompany.boniuk_math.com.itextpdf.text.pdf.draw;

import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.Element;
import com.mycompany.boniuk_math.com.itextpdf.text.ElementListener;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfContentByte;
import java.util.ArrayList;
import java.util.List;

public class VerticalPositionMark implements DrawInterface, Element {
  protected DrawInterface drawInterface = null;
  
  protected float offset = 0.0F;
  
  public VerticalPositionMark() {}
  
  public VerticalPositionMark(DrawInterface drawInterface, float offset) {
    this.drawInterface = drawInterface;
    this.offset = offset;
  }
  
  public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
    if (this.drawInterface != null)
      this.drawInterface.draw(canvas, llx, lly, urx, ury, y + this.offset); 
  }
  
  public boolean process(ElementListener listener) {
    try {
      return listener.add(this);
    } catch (DocumentException e) {
      return false;
    } 
  }
  
  public int type() {
    return 55;
  }
  
  public boolean isContent() {
    return true;
  }
  
  public boolean isNestable() {
    return false;
  }
  
  public List<Chunk> getChunks() {
    List<Chunk> list = new ArrayList<Chunk>();
    list.add(new Chunk(this, true));
    return list;
  }
  
  public DrawInterface getDrawInterface() {
    return this.drawInterface;
  }
  
  public void setDrawInterface(DrawInterface drawInterface) {
    this.drawInterface = drawInterface;
  }
  
  public float getOffset() {
    return this.offset;
  }
  
  public void setOffset(float offset) {
    this.offset = offset;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\draw\VerticalPositionMark.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */