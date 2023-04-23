package com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.NoSuchElementException;

public class PolylineShapeIterator implements PathIterator {
  protected PolylineShape poly;
  
  protected AffineTransform affine;
  
  protected int index;
  
  PolylineShapeIterator(PolylineShape l, AffineTransform at) {
    this.poly = l;
    this.affine = at;
  }
  
  public int currentSegment(double[] coords) {
    if (isDone())
      throw new NoSuchElementException(MessageLocalization.getComposedMessage("line.iterator.out.of.bounds", new Object[0])); 
    int type = (this.index == 0) ? 0 : 1;
    coords[0] = this.poly.x[this.index];
    coords[1] = this.poly.y[this.index];
    if (this.affine != null)
      this.affine.transform(coords, 0, coords, 0, 1); 
    return type;
  }
  
  public int currentSegment(float[] coords) {
    if (isDone())
      throw new NoSuchElementException(MessageLocalization.getComposedMessage("line.iterator.out.of.bounds", new Object[0])); 
    int type = (this.index == 0) ? 0 : 1;
    coords[0] = this.poly.x[this.index];
    coords[1] = this.poly.y[this.index];
    if (this.affine != null)
      this.affine.transform(coords, 0, coords, 0, 1); 
    return type;
  }
  
  public int getWindingRule() {
    return 1;
  }
  
  public boolean isDone() {
    return (this.index >= this.poly.np);
  }
  
  public void next() {
    this.index++;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\internal\PolylineShapeIterator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */