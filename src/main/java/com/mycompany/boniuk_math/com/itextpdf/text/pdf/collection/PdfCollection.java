package com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfString;

public class PdfCollection extends PdfDictionary {
  public static final int DETAILS = 0;
  
  public static final int TILE = 1;
  
  public static final int HIDDEN = 2;
  
  public static final int CUSTOM = 3;
  
  public PdfCollection(int type) {
    super(PdfName.COLLECTION);
    switch (type) {
      case 1:
        put(PdfName.VIEW, (PdfObject)PdfName.T);
        return;
      case 2:
        put(PdfName.VIEW, (PdfObject)PdfName.H);
        return;
      case 3:
        put(PdfName.VIEW, (PdfObject)PdfName.C);
        return;
    } 
    put(PdfName.VIEW, (PdfObject)PdfName.D);
  }
  
  public void setInitialDocument(String description) {
    put(PdfName.D, (PdfObject)new PdfString(description, null));
  }
  
  public void setSchema(PdfCollectionSchema schema) {
    put(PdfName.SCHEMA, (PdfObject)schema);
  }
  
  public PdfCollectionSchema getSchema() {
    return (PdfCollectionSchema)get(PdfName.SCHEMA);
  }
  
  public void setSort(PdfCollectionSort sort) {
    put(PdfName.SORT, (PdfObject)sort);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\collection\PdfCollection.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */