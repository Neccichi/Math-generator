package com.mycompany.boniuk_math.com.itextpdf.text.pdf.hyphenation;

import java.util.ArrayList;

public interface PatternConsumer {
  void addClass(String paramString);
  
  void addException(String paramString, ArrayList<Object> paramArrayList);
  
  void addPattern(String paramString1, String paramString2);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\hyphenation\PatternConsumer.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */