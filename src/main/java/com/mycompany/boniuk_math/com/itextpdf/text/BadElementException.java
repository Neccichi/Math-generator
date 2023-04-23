package com.mycompany.boniuk_math.com.itextpdf.text;

public class BadElementException extends DocumentException {
  private static final long serialVersionUID = -799006030723822254L;
  
  public BadElementException(Exception ex) {
    super(ex);
  }
  
  BadElementException() {}
  
  public BadElementException(String message) {
    super(message);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\BadElementException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */