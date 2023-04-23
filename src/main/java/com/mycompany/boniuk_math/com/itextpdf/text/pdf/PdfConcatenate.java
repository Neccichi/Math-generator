package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.io.OutputStream;

public class PdfConcatenate {
  public PdfConcatenate(OutputStream os) throws DocumentException {
    this(os, false);
  }
  
  protected Document document = new Document();
  
  protected PdfCopy copy;
  
  public PdfConcatenate(OutputStream os, boolean smart) throws DocumentException {
    if (smart) {
      this.copy = new PdfSmartCopy(this.document, os);
    } else {
      this.copy = new PdfCopy(this.document, os);
    } 
  }
  
  public int addPages(PdfReader reader) throws DocumentException, IOException {
    open();
    int n = reader.getNumberOfPages();
    for (int i = 1; i <= n; i++) {
      System.out.println(i);
      this.copy.addPage(this.copy.getImportedPage(reader, i));
    } 
    this.copy.freeReader(reader);
    return n;
  }
  
  public PdfCopy getWriter() {
    return this.copy;
  }
  
  public void open() {
    if (!this.document.isOpen())
      this.document.open(); 
  }
  
  public void close() {
    this.document.close();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfConcatenate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */