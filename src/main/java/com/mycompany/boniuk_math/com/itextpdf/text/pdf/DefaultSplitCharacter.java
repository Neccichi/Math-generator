package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.SplitCharacter;

public class DefaultSplitCharacter implements SplitCharacter {
  public static final SplitCharacter DEFAULT = new DefaultSplitCharacter();
  
  public boolean isSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck) {
    char c = getCurrentCharacter(current, cc, ck);
    if (c <= ' ' || c == '-' || c == '‐')
      return true; 
    if (c < ' ')
      return false; 
    return ((c >= ' ' && c <= '​') || (c >= '⺀' && c < '힠') || (c >= '豈' && c < 'ﬀ') || (c >= '︰' && c < '﹐') || (c >= '｡' && c < 'ﾠ'));
  }
  
  protected char getCurrentCharacter(int current, char[] cc, PdfChunk[] ck) {
    if (ck == null)
      return cc[current]; 
    return (char)ck[Math.min(current, ck.length - 1)].getUnicodeEquivalent(cc[current]);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\DefaultSplitCharacter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */