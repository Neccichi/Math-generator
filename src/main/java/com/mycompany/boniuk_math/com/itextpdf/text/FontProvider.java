package com.mycompany.boniuk_math.com.itextpdf.text;

public interface FontProvider {
  boolean isRegistered(String paramString);
  
  Font getFont(String paramString1, String paramString2, boolean paramBoolean, float paramFloat, int paramInt, BaseColor paramBaseColor);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\FontProvider.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */