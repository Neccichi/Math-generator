package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;

public class PdfRectangle extends NumberArray {
  private float llx = 0.0F;
  
  private float lly = 0.0F;
  
  private float urx = 0.0F;
  
  private float ury = 0.0F;
  
  public PdfRectangle(float llx, float lly, float urx, float ury, int rotation) {
    super(new float[0]);
    if (rotation == 90 || rotation == 270) {
      this.llx = lly;
      this.lly = llx;
      this.urx = ury;
      this.ury = urx;
    } else {
      this.llx = llx;
      this.lly = lly;
      this.urx = urx;
      this.ury = ury;
    } 
    super.add(new PdfNumber(this.llx));
    super.add(new PdfNumber(this.lly));
    super.add(new PdfNumber(this.urx));
    super.add(new PdfNumber(this.ury));
  }
  
  public PdfRectangle(float llx, float lly, float urx, float ury) {
    this(llx, lly, urx, ury, 0);
  }
  
  public PdfRectangle(float urx, float ury, int rotation) {
    this(0.0F, 0.0F, urx, ury, rotation);
  }
  
  public PdfRectangle(float urx, float ury) {
    this(0.0F, 0.0F, urx, ury, 0);
  }
  
  public PdfRectangle(Rectangle rectangle, int rotation) {
    this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), rotation);
  }
  
  public PdfRectangle(Rectangle rectangle) {
    this(rectangle.getLeft(), rectangle.getBottom(), rectangle.getRight(), rectangle.getTop(), 0);
  }
  
  public Rectangle getRectangle() {
    return new Rectangle(left(), bottom(), right(), top());
  }
  
  public boolean add(PdfObject object) {
    return false;
  }
  
  public boolean add(float[] values) {
    return false;
  }
  
  public boolean add(int[] values) {
    return false;
  }
  
  public void addFirst(PdfObject object) {}
  
  public float left() {
    return this.llx;
  }
  
  public float right() {
    return this.urx;
  }
  
  public float top() {
    return this.ury;
  }
  
  public float bottom() {
    return this.lly;
  }
  
  public float left(int margin) {
    return this.llx + margin;
  }
  
  public float right(int margin) {
    return this.urx - margin;
  }
  
  public float top(int margin) {
    return this.ury - margin;
  }
  
  public float bottom(int margin) {
    return this.lly + margin;
  }
  
  public float width() {
    return this.urx - this.llx;
  }
  
  public float height() {
    return this.ury - this.lly;
  }
  
  public PdfRectangle rotate() {
    return new PdfRectangle(this.lly, this.llx, this.ury, this.urx, 0);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfRectangle.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */