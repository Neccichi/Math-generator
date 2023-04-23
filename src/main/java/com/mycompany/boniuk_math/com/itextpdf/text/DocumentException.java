package com.mycompany.boniuk_math.com.itextpdf.text;

public class DocumentException extends Exception {
  private static final long serialVersionUID = -2191131489390840739L;
  
  public DocumentException(Exception ex) {
    super(ex);
  }
  
  public DocumentException() {}
  
  public DocumentException(String message) {
    super(message);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\DocumentException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */