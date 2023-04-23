package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;

public class PdfException extends DocumentException {
  private static final long serialVersionUID = 6767433960955483999L;
  
  public PdfException(Exception ex) {
    super(ex);
  }
  
  PdfException() {}
  
  PdfException(String message) {
    super(message);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */