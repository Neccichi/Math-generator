package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PRAcroForm extends PdfDictionary {
  ArrayList<FieldInformation> fields;
  
  ArrayList<PdfDictionary> stack;
  
  HashMap<String, FieldInformation> fieldByName;
  
  PdfReader reader;
  
  public static class FieldInformation {
    String name;
    
    PdfDictionary info;
    
    PRIndirectReference ref;
    
    FieldInformation(String name, PdfDictionary info, PRIndirectReference ref) {
      this.name = name;
      this.info = info;
      this.ref = ref;
    }
    
    public String getName() {
      return this.name;
    }
    
    public PdfDictionary getInfo() {
      return this.info;
    }
    
    public PRIndirectReference getRef() {
      return this.ref;
    }
  }
  
  public PRAcroForm(PdfReader reader) {
    this.reader = reader;
    this.fields = new ArrayList<FieldInformation>();
    this.fieldByName = new HashMap<String, FieldInformation>();
    this.stack = new ArrayList<PdfDictionary>();
  }
  
  public int size() {
    return this.fields.size();
  }
  
  public ArrayList<FieldInformation> getFields() {
    return this.fields;
  }
  
  public FieldInformation getField(String name) {
    return this.fieldByName.get(name);
  }
  
  public PRIndirectReference getRefByName(String name) {
    FieldInformation fi = this.fieldByName.get(name);
    if (fi == null)
      return null; 
    return fi.getRef();
  }
  
  public void readAcroForm(PdfDictionary root) {
    if (root == null)
      return; 
    this.hashMap = root.hashMap;
    pushAttrib(root);
    PdfArray fieldlist = (PdfArray)PdfReader.getPdfObjectRelease(root.get(PdfName.FIELDS));
    iterateFields(fieldlist, null, null);
  }
  
  protected void iterateFields(PdfArray fieldlist, PRIndirectReference fieldDict, String title) {
    for (Iterator<PdfObject> it = fieldlist.listIterator(); it.hasNext(); ) {
      PRIndirectReference ref = (PRIndirectReference)it.next();
      PdfDictionary dict = (PdfDictionary)PdfReader.getPdfObjectRelease(ref);
      PRIndirectReference myFieldDict = fieldDict;
      String myTitle = title;
      PdfString tField = (PdfString)dict.get(PdfName.T);
      boolean isFieldDict = (tField != null);
      if (isFieldDict) {
        myFieldDict = ref;
        if (title == null) {
          myTitle = tField.toString();
        } else {
          myTitle = title + '.' + tField.toString();
        } 
      } 
      PdfArray kids = (PdfArray)dict.get(PdfName.KIDS);
      if (kids != null) {
        pushAttrib(dict);
        iterateFields(kids, myFieldDict, myTitle);
        this.stack.remove(this.stack.size() - 1);
        continue;
      } 
      if (myFieldDict != null) {
        PdfDictionary mergedDict = this.stack.get(this.stack.size() - 1);
        if (isFieldDict)
          mergedDict = mergeAttrib(mergedDict, dict); 
        mergedDict.put(PdfName.T, new PdfString(myTitle));
        FieldInformation fi = new FieldInformation(myTitle, mergedDict, myFieldDict);
        this.fields.add(fi);
        this.fieldByName.put(myTitle, fi);
      } 
    } 
  }
  
  protected PdfDictionary mergeAttrib(PdfDictionary parent, PdfDictionary child) {
    PdfDictionary targ = new PdfDictionary();
    if (parent != null)
      targ.putAll(parent); 
    for (PdfName element : child.getKeys()) {
      PdfName key = element;
      if (key.equals(PdfName.DR) || key.equals(PdfName.DA) || key.equals(PdfName.Q) || key.equals(PdfName.FF) || key.equals(PdfName.DV) || key.equals(PdfName.V) || key.equals(PdfName.FT) || key.equals(PdfName.F))
        targ.put(key, child.get(key)); 
    } 
    return targ;
  }
  
  protected void pushAttrib(PdfDictionary dict) {
    PdfDictionary dic = null;
    if (!this.stack.isEmpty())
      dic = this.stack.get(this.stack.size() - 1); 
    dic = mergeAttrib(dic, dict);
    this.stack.add(dic);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PRAcroForm.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */