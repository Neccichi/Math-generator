package com.mycompany.boniuk_math.com.itextpdf.text.factories;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;

public class RomanAlphabetFactory {
  public static final String getString(int index) {
    if (index < 1)
      throw new NumberFormatException(MessageLocalization.getComposedMessage("you.can.t.translate.a.negative.number.into.an.alphabetical.value", new Object[0])); 
    index--;
    int bytes = 1;
    int start = 0;
    int symbols = 26;
    while (index >= symbols + start) {
      bytes++;
      start += symbols;
      symbols *= 26;
    } 
    int c = index - start;
    char[] value = new char[bytes];
    while (bytes > 0) {
      value[--bytes] = (char)(97 + c % 26);
      c /= 26;
    } 
    return new String(value);
  }
  
  public static final String getLowerCaseString(int index) {
    return getString(index);
  }
  
  public static final String getUpperCaseString(int index) {
    return getString(index).toUpperCase();
  }
  
  public static final String getString(int index, boolean lowercase) {
    if (lowercase)
      return getLowerCaseString(index); 
    return getUpperCaseString(index);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\factories\RomanAlphabetFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */