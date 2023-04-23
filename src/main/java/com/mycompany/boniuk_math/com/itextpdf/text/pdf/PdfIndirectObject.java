package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PdfIndirectObject {
  protected int number;
  
  protected int generation = 0;
  
  static final byte[] STARTOBJ = DocWriter.getISOBytes(" obj\n");
  
  static final byte[] ENDOBJ = DocWriter.getISOBytes("\nendobj\n");
  
  static final int SIZEOBJ = STARTOBJ.length + ENDOBJ.length;
  
  PdfObject object;
  
  PdfWriter writer;
  
  PdfIndirectObject(int number, PdfObject object, PdfWriter writer) {
    this(number, 0, object, writer);
  }
  
  PdfIndirectObject(PdfIndirectReference ref, PdfObject object, PdfWriter writer) {
    this(ref.getNumber(), ref.getGeneration(), object, writer);
  }
  
  PdfIndirectObject(int number, int generation, PdfObject object, PdfWriter writer) {
    this.writer = writer;
    this.number = number;
    this.generation = generation;
    this.object = object;
    PdfEncryption crypto = null;
    if (writer != null)
      crypto = writer.getEncryption(); 
    if (crypto != null)
      crypto.setHashKey(number, generation); 
  }
  
  public PdfIndirectReference getIndirectReference() {
    return new PdfIndirectReference(this.object.type(), this.number, this.generation);
  }
  
  void writeTo(OutputStream os) throws IOException {
    os.write(DocWriter.getISOBytes(String.valueOf(this.number)));
    os.write(32);
    os.write(DocWriter.getISOBytes(String.valueOf(this.generation)));
    os.write(STARTOBJ);
    this.object.toPdf(this.writer, os);
    os.write(ENDOBJ);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfIndirectObject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */