package com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;

public class PdfCollectionSchema extends PdfDictionary {
  public PdfCollectionSchema() {
    super(PdfName.COLLECTIONSCHEMA);
  }
  
  public void addField(String name, PdfCollectionField field) {
    put(new PdfName(name), (PdfObject)field);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\collection\PdfCollectionSchema.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */