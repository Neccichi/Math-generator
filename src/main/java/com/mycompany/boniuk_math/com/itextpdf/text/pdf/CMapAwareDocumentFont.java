package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.fonts.cmaps.CMap;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.fonts.cmaps.CMapParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class CMapAwareDocumentFont extends DocumentFont {
  private PdfDictionary fontDic;
  
  private int spaceWidth;
  
  private CMap toUnicodeCmap;
  
  private char[] cidbyte2uni;
  
  public CMapAwareDocumentFont(PRIndirectReference refFont) {
    super(refFont);
    this.fontDic = (PdfDictionary)PdfReader.getPdfObjectRelease(refFont);
    processToUnicode();
    processUni2Byte();
    this.spaceWidth = super.getWidth(32);
    if (this.spaceWidth == 0)
      this.spaceWidth = computeAverageWidth(); 
  }
  
  private void processToUnicode() {
    PdfObject toUni = PdfReader.getPdfObjectRelease(this.fontDic.get(PdfName.TOUNICODE));
    if (toUni instanceof PRStream)
      try {
        byte[] touni = PdfReader.getStreamBytes((PRStream)toUni);
        CMapParser cmapParser = new CMapParser();
        this.toUnicodeCmap = cmapParser.parse(new ByteArrayInputStream(touni));
      } catch (IOException e) {} 
  }
  
  private void processUni2Byte() {
    IntHashtable uni2byte = getUni2Byte();
    int[] e = uni2byte.toOrderedKeys();
    if (e.length == 0)
      return; 
    this.cidbyte2uni = new char[256];
    for (int k = 0; k < e.length; k++) {
      int n = uni2byte.get(e[k]);
      if (n < 256 && this.cidbyte2uni[n] == '\000')
        this.cidbyte2uni[n] = (char)e[k]; 
    } 
    IntHashtable diffmap = getDiffmap();
    if (diffmap != null) {
      e = diffmap.toOrderedKeys();
      for (int i = 0; i < e.length; i++) {
        int n = diffmap.get(e[i]);
        if (n < 256)
          this.cidbyte2uni[n] = (char)e[i]; 
      } 
    } 
  }
  
  private int computeAverageWidth() {
    int count = 0;
    int total = 0;
    for (int i = 0; i < this.widths.length; i++) {
      if (this.widths[i] != 0) {
        total += this.widths[i];
        count++;
      } 
    } 
    return (count != 0) ? (total / count) : 0;
  }
  
  public int getWidth(int char1) {
    if (char1 == 32)
      return this.spaceWidth; 
    return super.getWidth(char1);
  }
  
  private String decodeSingleCID(byte[] bytes, int offset, int len) {
    if (this.toUnicodeCmap != null) {
      if (offset + len > bytes.length)
        throw new ArrayIndexOutOfBoundsException(MessageLocalization.getComposedMessage("invalid.index.1", offset + len)); 
      String s = this.toUnicodeCmap.lookup(bytes, offset, len);
      if (s != null)
        return s; 
      if (len != 1 || this.cidbyte2uni == null)
        return null; 
    } 
    if (len == 1)
      return new String(this.cidbyte2uni, 0xFF & bytes[offset], 1); 
    throw new Error("Multi-byte glyphs not implemented yet");
  }
  
  public String decode(byte[] cidbytes, int offset, int len) {
    StringBuffer sb = new StringBuffer();
    for (int i = offset; i < offset + len; i++) {
      String rslt = decodeSingleCID(cidbytes, i, 1);
      if (rslt == null && i < offset + len - 1) {
        rslt = decodeSingleCID(cidbytes, i, 2);
        i++;
      } 
      sb.append(rslt);
    } 
    return sb.toString();
  }
  
  public String encode(byte[] bytes, int offset, int len) {
    return decode(bytes, offset, len);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\CMapAwareDocumentFont.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */