package com.mycompany.boniuk_math.com.itextpdf.text.pdf.codec.wmf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import java.io.IOException;

public class MetaPen extends MetaObject {
  public static final int PS_SOLID = 0;
  
  public static final int PS_DASH = 1;
  
  public static final int PS_DOT = 2;
  
  public static final int PS_DASHDOT = 3;
  
  public static final int PS_DASHDOTDOT = 4;
  
  public static final int PS_NULL = 5;
  
  public static final int PS_INSIDEFRAME = 6;
  
  int style = 0;
  
  int penWidth = 1;
  
  BaseColor color = BaseColor.BLACK;
  
  public MetaPen() {
    this.type = 1;
  }
  
  public void init(InputMeta in) throws IOException {
    this.style = in.readWord();
    this.penWidth = in.readShort();
    in.readWord();
    this.color = in.readColor();
  }
  
  public int getStyle() {
    return this.style;
  }
  
  public int getPenWidth() {
    return this.penWidth;
  }
  
  public BaseColor getColor() {
    return this.color;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\codec\wmf\MetaPen.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */