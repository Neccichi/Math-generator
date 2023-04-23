package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public interface HyphenationEvent {
  String getHyphenSymbol();
  
  String getHyphenatedWordPre(String paramString, BaseFont paramBaseFont, float paramFloat1, float paramFloat2);
  
  String getHyphenatedWordPost();
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\HyphenationEvent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */