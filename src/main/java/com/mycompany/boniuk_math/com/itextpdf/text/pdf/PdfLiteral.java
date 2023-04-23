package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class PdfLiteral extends PdfObject {
  private int position;
  
  public PdfLiteral(String text) {
    super(0, text);
  }
  
  public PdfLiteral(byte[] b) {
    super(0, b);
  }
  
  public PdfLiteral(int size) {
    super(0, (byte[])null);
    this.bytes = new byte[size];
    Arrays.fill(this.bytes, (byte)32);
  }
  
  public PdfLiteral(int type, String text) {
    super(type, text);
  }
  
  public PdfLiteral(int type, byte[] b) {
    super(type, b);
  }
  
  public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
    if (os instanceof OutputStreamCounter)
      this.position = ((OutputStreamCounter)os).getCounter(); 
    super.toPdf(writer, os);
  }
  
  public int getPosition() {
    return this.position;
  }
  
  public int getPosLength() {
    if (this.bytes != null)
      return this.bytes.length; 
    return 0;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfLiteral.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */