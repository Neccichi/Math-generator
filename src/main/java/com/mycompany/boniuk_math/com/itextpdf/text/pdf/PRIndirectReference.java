package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.io.IOException;
import java.io.OutputStream;

public class PRIndirectReference extends PdfIndirectReference {
  protected PdfReader reader;
  
  PRIndirectReference(PdfReader reader, int number, int generation) {
    this.type = 10;
    this.number = number;
    this.generation = generation;
    this.reader = reader;
  }
  
  PRIndirectReference(PdfReader reader, int number) {
    this(reader, number, 0);
  }
  
  public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
    int n = writer.getNewObjectNumber(this.reader, this.number, this.generation);
    os.write(PdfEncodings.convertToBytes(n + " 0 R", (String)null));
  }
  
  public PdfReader getReader() {
    return this.reader;
  }
  
  public void setNumber(int number, int generation) {
    this.number = number;
    this.generation = generation;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PRIndirectReference.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */