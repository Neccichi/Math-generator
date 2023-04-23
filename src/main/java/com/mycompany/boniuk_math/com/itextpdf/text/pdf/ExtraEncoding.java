package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public interface ExtraEncoding {
  byte[] charToByte(String paramString1, String paramString2);
  
  byte[] charToByte(char paramChar, String paramString);
  
  String byteToChar(byte[] paramArrayOfbyte, String paramString);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\ExtraEncoding.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */