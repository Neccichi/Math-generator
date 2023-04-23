package com.mycompany.boniuk_math.com.itextpdf.text.factories;

public class RomanNumberFactory {
  private static class RomanDigit {
    public char digit;
    
    public int value;
    
    public boolean pre;
    
    RomanDigit(char digit, int value, boolean pre) {
      this.digit = digit;
      this.value = value;
      this.pre = pre;
    }
  }
  
  private static final RomanDigit[] roman = new RomanDigit[] { new RomanDigit('m', 1000, false), new RomanDigit('d', 500, false), new RomanDigit('c', 100, true), new RomanDigit('l', 50, false), new RomanDigit('x', 10, true), new RomanDigit('v', 5, false), new RomanDigit('i', 1, true) };
  
  public static final String getString(int index) {
    StringBuffer buf = new StringBuffer();
    if (index < 0) {
      buf.append('-');
      index = -index;
    } 
    if (index > 3000) {
      buf.append('|');
      buf.append(getString(index / 1000));
      buf.append('|');
      index -= index / 1000 * 1000;
    } 
    int pos = 0;
    while (true) {
      RomanDigit dig = roman[pos];
      while (index >= dig.value) {
        buf.append(dig.digit);
        index -= dig.value;
      } 
      if (index <= 0)
        break; 
      int j = pos;
      while (!(roman[++j]).pre);
      if (index + (roman[j]).value >= dig.value) {
        buf.append((roman[j]).digit).append(dig.digit);
        index -= dig.value - (roman[j]).value;
      } 
      pos++;
    } 
    return buf.toString();
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


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\factories\RomanNumberFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */