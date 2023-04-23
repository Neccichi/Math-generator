package com.mycompany.boniuk_math.com.itextpdf.text.xml.xmp;

public class PdfA1Schema extends XmpSchema {
  private static final long serialVersionUID = 5300646133692948168L;
  
  public static final String DEFAULT_XPATH_ID = "pdfaid";
  
  public static final String DEFAULT_XPATH_URI = "http://www.aiim.org/pdfa/ns/id/";
  
  public static final String PART = "pdfaid:part";
  
  public static final String CONFORMANCE = "pdfaid:conformance";
  
  public PdfA1Schema() {
    super("xmlns:pdfaid=\"http://www.aiim.org/pdfa/ns/id/\"");
    addPart("1");
  }
  
  public void addPart(String part) {
    setProperty("pdfaid:part", part);
  }
  
  public void addConformance(String conformance) {
    setProperty("pdfaid:conformance", conformance);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\xmp\PdfA1Schema.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */