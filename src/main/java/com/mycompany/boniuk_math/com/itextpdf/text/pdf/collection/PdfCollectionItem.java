package com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDate;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfNumber;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfString;
import java.util.Calendar;

public class PdfCollectionItem extends PdfDictionary {
  PdfCollectionSchema schema;
  
  public PdfCollectionItem(PdfCollectionSchema schema) {
    super(PdfName.COLLECTIONITEM);
    this.schema = schema;
  }
  
  public void addItem(String key, String value) {
    PdfName fieldname = new PdfName(key);
    PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
    put(fieldname, field.getValue(value));
  }
  
  public void addItem(String key, PdfString value) {
    PdfName fieldname = new PdfName(key);
    PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
    if (field.fieldType == 0)
      put(fieldname, (PdfObject)value); 
  }
  
  public void addItem(String key, PdfDate d) {
    PdfName fieldname = new PdfName(key);
    PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
    if (field.fieldType == 1)
      put(fieldname, (PdfObject)d); 
  }
  
  public void addItem(String key, PdfNumber n) {
    PdfName fieldname = new PdfName(key);
    PdfCollectionField field = (PdfCollectionField)this.schema.get(fieldname);
    if (field.fieldType == 2)
      put(fieldname, (PdfObject)n); 
  }
  
  public void addItem(String key, Calendar c) {
    addItem(key, new PdfDate(c));
  }
  
  public void addItem(String key, int i) {
    addItem(key, new PdfNumber(i));
  }
  
  public void addItem(String key, float f) {
    addItem(key, new PdfNumber(f));
  }
  
  public void addItem(String key, double d) {
    addItem(key, new PdfNumber(d));
  }
  
  public void setPrefix(String key, String prefix) {
    PdfName fieldname = new PdfName(key);
    PdfObject o = get(fieldname);
    if (o == null)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.must.set.a.value.before.adding.a.prefix", new Object[0])); 
    PdfDictionary dict = new PdfDictionary(PdfName.COLLECTIONSUBITEM);
    dict.put(PdfName.D, o);
    dict.put(PdfName.P, (PdfObject)new PdfString(prefix, "UnicodeBig"));
    put(fieldname, (PdfObject)dict);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\collection\PdfCollectionItem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */