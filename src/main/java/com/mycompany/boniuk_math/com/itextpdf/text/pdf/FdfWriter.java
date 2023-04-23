package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class FdfWriter {
  private static final byte[] HEADER_FDF = DocWriter.getISOBytes("%FDF-1.4\n%âãÏÓ\n");
  
  HashMap<String, Object> fields = new HashMap<String, Object>();
  
  private String file;
  
  public void writeTo(OutputStream os) throws IOException {
    Wrt wrt = new Wrt(os, this);
    wrt.writeTo();
  }
  
  boolean setField(String field, PdfObject value) {
    String s;
    Object<Object, Object> obj;
    HashMap<String, Object> map = this.fields;
    StringTokenizer tk = new StringTokenizer(field, ".");
    if (!tk.hasMoreTokens())
      return false; 
    while (true) {
      s = tk.nextToken();
      obj = (Object<Object, Object>)map.get(s);
      if (tk.hasMoreTokens()) {
        if (obj == null) {
          obj = (Object<Object, Object>)new HashMap<Object, Object>();
          map.put(s, obj);
          map = (HashMap)obj;
          continue;
        } 
        if (obj instanceof HashMap) {
          map = (HashMap)obj;
          continue;
        } 
        return false;
      } 
      break;
    } 
    if (!(obj instanceof HashMap)) {
      map.put(s, value);
      return true;
    } 
    return false;
  }
  
  void iterateFields(HashMap<String, Object> values, HashMap<String, Object> map, String name) {
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String s = entry.getKey();
      Object obj = entry.getValue();
      if (obj instanceof HashMap) {
        iterateFields(values, (HashMap<String, Object>)obj, name + "." + s);
        continue;
      } 
      values.put((name + "." + s).substring(1), obj);
    } 
  }
  
  public boolean removeField(String field) {
    Object obj;
    HashMap<String, Object> map = this.fields;
    StringTokenizer tk = new StringTokenizer(field, ".");
    if (!tk.hasMoreTokens())
      return false; 
    ArrayList<Object> hist = new ArrayList();
    while (true) {
      String s = tk.nextToken();
      obj = map.get(s);
      if (obj == null)
        return false; 
      hist.add(map);
      hist.add(s);
      if (tk.hasMoreTokens()) {
        if (obj instanceof HashMap) {
          map = (HashMap<String, Object>)obj;
          continue;
        } 
        return false;
      } 
      break;
    } 
    if (obj instanceof HashMap)
      return false; 
    for (int k = hist.size() - 2; k >= 0; k -= 2) {
      map = (HashMap<String, Object>)hist.get(k);
      String s = (String)hist.get(k + 1);
      map.remove(s);
      if (!map.isEmpty())
        break; 
    } 
    return true;
  }
  
  public HashMap<String, Object> getFields() {
    HashMap<String, Object> values = new HashMap<String, Object>();
    iterateFields(values, this.fields, "");
    return values;
  }
  
  public String getField(String field) {
    Object obj;
    HashMap<String, Object> map = this.fields;
    StringTokenizer tk = new StringTokenizer(field, ".");
    if (!tk.hasMoreTokens())
      return null; 
    while (true) {
      String s = tk.nextToken();
      obj = map.get(s);
      if (obj == null)
        return null; 
      if (tk.hasMoreTokens()) {
        if (obj instanceof HashMap) {
          map = (HashMap<String, Object>)obj;
          continue;
        } 
        return null;
      } 
      break;
    } 
    if (obj instanceof HashMap)
      return null; 
    if (((PdfObject)obj).isString())
      return ((PdfString)obj).toUnicodeString(); 
    return PdfName.decodeName(obj.toString());
  }
  
  public boolean setFieldAsName(String field, String value) {
    return setField(field, new PdfName(value));
  }
  
  public boolean setFieldAsString(String field, String value) {
    return setField(field, new PdfString(value, "UnicodeBig"));
  }
  
  public boolean setFieldAsAction(String field, PdfAction action) {
    return setField(field, action);
  }
  
  public void setFields(FdfReader fdf) {
    HashMap<String, PdfDictionary> map = fdf.getFields();
    for (Map.Entry<String, PdfDictionary> entry : map.entrySet()) {
      String key = entry.getKey();
      PdfDictionary dic = entry.getValue();
      PdfObject v = dic.get(PdfName.V);
      if (v != null)
        setField(key, v); 
      v = dic.get(PdfName.A);
      if (v != null)
        setField(key, v); 
    } 
  }
  
  public void setFields(PdfReader pdf) {
    setFields(pdf.getAcroFields());
  }
  
  public void setFields(AcroFields af) {
    for (Map.Entry<String, AcroFields.Item> entry : af.getFields().entrySet()) {
      String fn = entry.getKey();
      AcroFields.Item item = entry.getValue();
      PdfDictionary dic = item.getMerged(0);
      PdfObject v = PdfReader.getPdfObjectRelease(dic.get(PdfName.V));
      if (v == null)
        continue; 
      PdfObject ft = PdfReader.getPdfObjectRelease(dic.get(PdfName.FT));
      if (ft == null || PdfName.SIG.equals(ft))
        continue; 
      setField(fn, v);
    } 
  }
  
  public String getFile() {
    return this.file;
  }
  
  public void setFile(String file) {
    this.file = file;
  }
  
  static class Wrt extends PdfWriter {
    private FdfWriter fdf;
    
    Wrt(OutputStream os, FdfWriter fdf) throws IOException {
      super(new PdfDocument(), os);
      this.fdf = fdf;
      this.os.write(FdfWriter.HEADER_FDF);
      this.body = new PdfWriter.PdfBody(this);
    }
    
    void writeTo() throws IOException {
      PdfDictionary dic = new PdfDictionary();
      dic.put(PdfName.FIELDS, calculate(this.fdf.fields));
      if (this.fdf.file != null)
        dic.put(PdfName.F, new PdfString(this.fdf.file, "UnicodeBig")); 
      PdfDictionary fd = new PdfDictionary();
      fd.put(PdfName.FDF, dic);
      PdfIndirectReference ref = addToBody(fd).getIndirectReference();
      this.os.write(getISOBytes("trailer\n"));
      PdfDictionary trailer = new PdfDictionary();
      trailer.put(PdfName.ROOT, ref);
      trailer.toPdf(null, this.os);
      this.os.write(getISOBytes("\n%%EOF\n"));
      this.os.close();
    }
    
    PdfArray calculate(HashMap<String, Object> map) throws IOException {
      PdfArray ar = new PdfArray();
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        String key = entry.getKey();
        Object v = entry.getValue();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.T, new PdfString(key, "UnicodeBig"));
        if (v instanceof HashMap) {
          dic.put(PdfName.KIDS, calculate((HashMap<String, Object>)v));
        } else if (v instanceof PdfAction) {
          dic.put(PdfName.A, (PdfAction)v);
        } else {
          dic.put(PdfName.V, (PdfObject)v);
        } 
        ar.add(dic);
      } 
      return ar;
    }
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\FdfWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */