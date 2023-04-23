package com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec.wmf;

public class MetaObject {
  public static final int META_NOT_SUPPORTED = 0;
  
  public static final int META_PEN = 1;
  
  public static final int META_BRUSH = 2;
  
  public static final int META_FONT = 3;
  
  public int type = 0;
  
  public MetaObject() {}
  
  public MetaObject(int type) {
    this.type = type;
  }
  
  public int getType() {
    return this.type;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\codec\wmf\MetaObject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */