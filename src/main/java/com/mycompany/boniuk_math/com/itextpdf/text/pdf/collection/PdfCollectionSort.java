package com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfArray;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfBoolean;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;

public class PdfCollectionSort extends PdfDictionary {
  public PdfCollectionSort(String key) {
    super(PdfName.COLLECTIONSORT);
    put(PdfName.S, (PdfObject)new PdfName(key));
  }
  
  public PdfCollectionSort(String[] keys) {
    super(PdfName.COLLECTIONSORT);
    PdfArray array = new PdfArray();
    for (int i = 0; i < keys.length; i++)
      array.add((PdfObject)new PdfName(keys[i])); 
    put(PdfName.S, (PdfObject)array);
  }
  
  public void setSortOrder(boolean ascending) {
    PdfObject o = get(PdfName.S);
    if (o instanceof PdfName) {
      put(PdfName.A, (PdfObject)new PdfBoolean(ascending));
    } else {
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.have.to.define.a.boolean.array.for.this.collection.sort.dictionary", new Object[0]));
    } 
  }
  
  public void setSortOrder(boolean[] ascending) {
    PdfObject o = get(PdfName.S);
    if (o instanceof PdfArray) {
      if (((PdfArray)o).size() != ascending.length)
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.number.of.booleans.in.this.array.doesn.t.correspond.with.the.number.of.fields", new Object[0])); 
      PdfArray array = new PdfArray();
      for (int i = 0; i < ascending.length; i++)
        array.add((PdfObject)new PdfBoolean(ascending[i])); 
      put(PdfName.A, (PdfObject)array);
    } else {
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("you.need.a.single.boolean.for.this.collection.sort.dictionary", new Object[0]));
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\collection\PdfCollectionSort.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */