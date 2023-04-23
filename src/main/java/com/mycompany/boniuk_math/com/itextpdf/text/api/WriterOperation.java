package com.mycompany.boniuk_math.com.itextpdf.text.api;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfWriter;

public interface WriterOperation {
  void write(PdfWriter paramPdfWriter, Document paramDocument) throws DocumentException;
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\api\WriterOperation.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */