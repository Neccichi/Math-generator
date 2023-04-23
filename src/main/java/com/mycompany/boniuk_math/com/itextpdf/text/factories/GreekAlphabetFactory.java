package com.mycompany.boniuk_math.com.itextpdf.text.factories;

import com.mycompany.boniuk_math.com.itextpdf.text.SpecialSymbol;

public class GreekAlphabetFactory {
  public static final String getString(int index) {
    return getString(index, true);
  }
  
  public static final String getLowerCaseString(int index) {
    return getString(index);
  }
  
  public static final String getUpperCaseString(int index) {
    return getString(index).toUpperCase();
  }
  
  public static final String getString(int index, boolean lowercase) {
    if (index < 1)
      return ""; 
    index--;
    int bytes = 1;
    int start = 0;
    int symbols = 24;
    while (index >= symbols + start) {
      bytes++;
      start += symbols;
      symbols *= 24;
    } 
    int c = index - start;
    char[] value = new char[bytes];
    while (bytes > 0) {
      bytes--;
      value[bytes] = (char)(c % 24);
      if (value[bytes] > '\020')
        value[bytes] = (char)(value[bytes] + 1); 
      value[bytes] = (char)(value[bytes] + (lowercase ? 945 : 913));
      value[bytes] = SpecialSymbol.getCorrespondingSymbol(value[bytes]);
      c /= 24;
    } 
    return String.valueOf(value);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\factories\GreekAlphabetFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */