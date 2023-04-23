package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class GrayColor extends ExtendedColor {
  private static final long serialVersionUID = -6571835680819282746L;
  
  private float gray;
  
  public static final GrayColor GRAYBLACK = new GrayColor(0.0F);
  
  public static final GrayColor GRAYWHITE = new GrayColor(1.0F);
  
  public GrayColor(int intGray) {
    this(intGray / 255.0F);
  }
  
  public GrayColor(float floatGray) {
    super(1, floatGray, floatGray, floatGray);
    this.gray = normalize(floatGray);
  }
  
  public float getGray() {
    return this.gray;
  }
  
  public boolean equals(Object obj) {
    return (obj instanceof GrayColor && ((GrayColor)obj).gray == this.gray);
  }
  
  public int hashCode() {
    return Float.floatToIntBits(this.gray);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\GrayColor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */