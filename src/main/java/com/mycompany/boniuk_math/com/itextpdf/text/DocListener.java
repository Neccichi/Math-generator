package com.mycompany.boniuk_math.com.itextpdf.text;

public interface DocListener extends ElementListener {
  void open();
  
  void close();
  
  boolean newPage();
  
  boolean setPageSize(Rectangle paramRectangle);
  
  boolean setMargins(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);
  
  boolean setMarginMirroring(boolean paramBoolean);
  
  boolean setMarginMirroringTopBottom(boolean paramBoolean);
  
  void setPageCount(int paramInt);
  
  void resetPageCount();
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\DocListener.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */