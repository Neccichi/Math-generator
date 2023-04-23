package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Utilities;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

class FontDetails {
  PdfIndirectReference indirectReference;
  
  PdfName fontName;
  
  BaseFont baseFont;
  
  TrueTypeFontUnicode ttu;
  
  CJKFont cjkFont;
  
  byte[] shortTag;
  
  HashMap<Integer, int[]> longTag;
  
  IntHashtable cjkTag;
  
  int fontType;
  
  boolean symbolic;
  
  protected boolean subset = true;
  
  FontDetails(PdfName fontName, PdfIndirectReference indirectReference, BaseFont baseFont) {
    this.fontName = fontName;
    this.indirectReference = indirectReference;
    this.baseFont = baseFont;
    this.fontType = baseFont.getFontType();
    switch (this.fontType) {
      case 0:
      case 1:
        this.shortTag = new byte[256];
        break;
      case 2:
        this.cjkTag = new IntHashtable();
        this.cjkFont = (CJKFont)baseFont;
        break;
      case 3:
        this.longTag = (HashMap)new HashMap<Integer, int>();
        this.ttu = (TrueTypeFontUnicode)baseFont;
        this.symbolic = baseFont.isFontSpecific();
        break;
    } 
  }
  
  PdfIndirectReference getIndirectReference() {
    return this.indirectReference;
  }
  
  PdfName getFontName() {
    return this.fontName;
  }
  
  BaseFont getBaseFont() {
    return this.baseFont;
  }
  
  byte[] convertToBytes(String text) {
    int len, k;
    byte[] b = null;
    switch (this.fontType) {
      case 5:
        return this.baseFont.convertToBytes(text);
      case 0:
      case 1:
        b = this.baseFont.convertToBytes(text);
        len = b.length;
        for (k = 0; k < len; k++)
          this.shortTag[b[k] & 0xFF] = 1; 
        break;
      case 2:
        len = text.length();
        for (k = 0; k < len; k++)
          this.cjkTag.put(this.cjkFont.getCidCode(text.charAt(k)), 0); 
        b = this.baseFont.convertToBytes(text);
        break;
      case 4:
        b = this.baseFont.convertToBytes(text);
        break;
      case 3:
        try {
          len = text.length();
          int[] metrics = null;
          char[] glyph = new char[len];
          int i = 0;
          if (this.symbolic) {
            b = PdfEncodings.convertToBytes(text, "symboltt");
            len = b.length;
            for (int j = 0; j < len; j++) {
              metrics = this.ttu.getMetricsTT(b[j] & 0xFF);
              if (metrics != null) {
                this.longTag.put(Integer.valueOf(metrics[0]), new int[] { metrics[0], metrics[1], this.ttu.getUnicodeDifferences(b[j] & 0xFF) });
                glyph[i++] = (char)metrics[0];
              } 
            } 
          } else {
            for (int j = 0; j < len; j++) {
              int val;
              if (Utilities.isSurrogatePair(text, j)) {
                val = Utilities.convertToUtf32(text, j);
                j++;
              } else {
                val = text.charAt(j);
              } 
              metrics = this.ttu.getMetricsTT(val);
              if (metrics != null) {
                int m0 = metrics[0];
                Integer gl = Integer.valueOf(m0);
                if (!this.longTag.containsKey(gl))
                  this.longTag.put(gl, new int[] { m0, metrics[1], val }); 
                glyph[i++] = (char)m0;
              } 
            } 
          } 
          String s = new String(glyph, 0, i);
          b = s.getBytes("UnicodeBigUnmarked");
        } catch (UnsupportedEncodingException e) {
          throw new ExceptionConverter(e);
        } 
        break;
    } 
    return b;
  }
  
  void writeFont(PdfWriter writer) {
    try {
      int firstChar;
      int lastChar;
      switch (this.fontType) {
        case 5:
          this.baseFont.writeFont(writer, this.indirectReference, null);
          break;
        case 0:
        case 1:
          for (firstChar = 0; firstChar < 256 && 
            this.shortTag[firstChar] == 0; firstChar++);
          for (lastChar = 255; lastChar >= firstChar && 
            this.shortTag[lastChar] == 0; lastChar--);
          if (firstChar > 255) {
            firstChar = 255;
            lastChar = 255;
          } 
          this.baseFont.writeFont(writer, this.indirectReference, new Object[] { Integer.valueOf(firstChar), Integer.valueOf(lastChar), this.shortTag, Boolean.valueOf(this.subset) });
          break;
        case 2:
          this.baseFont.writeFont(writer, this.indirectReference, new Object[] { this.cjkTag });
          break;
        case 3:
          this.baseFont.writeFont(writer, this.indirectReference, new Object[] { this.longTag, Boolean.valueOf(this.subset) });
          break;
      } 
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  public boolean isSubset() {
    return this.subset;
  }
  
  public void setSubset(boolean subset) {
    this.subset = subset;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\FontDetails.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */