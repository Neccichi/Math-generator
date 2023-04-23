package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;

public class PdfBoolean extends PdfObject {
  public static final PdfBoolean PDFTRUE = new PdfBoolean(true);
  
  public static final PdfBoolean PDFFALSE = new PdfBoolean(false);
  
  public static final String TRUE = "true";
  
  public static final String FALSE = "false";
  
  private boolean value;
  
  public PdfBoolean(boolean value) {
    super(1);
    if (value) {
      setContent("true");
    } else {
      setContent("false");
    } 
    this.value = value;
  }
  
  public PdfBoolean(String value) throws BadPdfFormatException {
    super(1, value);
    if (value.equals("true")) {
      this.value = true;
    } else if (value.equals("false")) {
      this.value = false;
    } else {
      throw new BadPdfFormatException(MessageLocalization.getComposedMessage("the.value.has.to.be.true.of.false.instead.of.1", new Object[] { value }));
    } 
  }
  
  public boolean booleanValue() {
    return this.value;
  }
  
  public String toString() {
    return this.value ? "true" : "false";
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfBoolean.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */