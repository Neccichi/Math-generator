package com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfBoolean;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDate;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfNumber;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfString;

public class PdfCollectionField extends PdfDictionary {
  public static final int TEXT = 0;
  
  public static final int DATE = 1;
  
  public static final int NUMBER = 2;
  
  public static final int FILENAME = 3;
  
  public static final int DESC = 4;
  
  public static final int MODDATE = 5;
  
  public static final int CREATIONDATE = 6;
  
  public static final int SIZE = 7;
  
  protected int fieldType;
  
  public PdfCollectionField(String name, int type) {
    super(PdfName.COLLECTIONFIELD);
    put(PdfName.N, (PdfObject)new PdfString(name, "UnicodeBig"));
    this.fieldType = type;
    switch (type) {
      default:
        put(PdfName.SUBTYPE, (PdfObject)PdfName.S);
        return;
      case 1:
        put(PdfName.SUBTYPE, (PdfObject)PdfName.D);
        return;
      case 2:
        put(PdfName.SUBTYPE, (PdfObject)PdfName.N);
        return;
      case 3:
        put(PdfName.SUBTYPE, (PdfObject)PdfName.F);
        return;
      case 4:
        put(PdfName.SUBTYPE, (PdfObject)PdfName.DESC);
        return;
      case 5:
        put(PdfName.SUBTYPE, (PdfObject)PdfName.MODDATE);
        return;
      case 6:
        put(PdfName.SUBTYPE, (PdfObject)PdfName.CREATIONDATE);
        return;
      case 7:
        break;
    } 
    put(PdfName.SUBTYPE, (PdfObject)PdfName.SIZE);
  }
  
  public void setOrder(int i) {
    put(PdfName.O, (PdfObject)new PdfNumber(i));
  }
  
  public void setVisible(boolean visible) {
    put(PdfName.V, (PdfObject)new PdfBoolean(visible));
  }
  
  public void setEditable(boolean editable) {
    put(PdfName.E, (PdfObject)new PdfBoolean(editable));
  }
  
  public boolean isCollectionItem() {
    switch (this.fieldType) {
      case 0:
      case 1:
      case 2:
        return true;
    } 
    return false;
  }
  
  public PdfObject getValue(String v) {
    switch (this.fieldType) {
      case 0:
        return (PdfObject)new PdfString(v, "UnicodeBig");
      case 1:
        return (PdfObject)new PdfDate(PdfDate.decode(v));
      case 2:
        return (PdfObject)new PdfNumber(v);
    } 
    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("1.is.not.an.acceptable.value.for.the.field.2", new Object[] { v, get(PdfName.N).toString() }));
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\collection\PdfCollectionField.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */