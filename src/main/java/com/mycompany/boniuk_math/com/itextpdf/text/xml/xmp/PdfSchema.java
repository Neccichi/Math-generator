package com.mycompany.boniuk_math.com.itextpdf.text.xml.xmp;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;

public class PdfSchema extends XmpSchema {
  private static final long serialVersionUID = -1541148669123992185L;
  
  public static final String DEFAULT_XPATH_ID = "pdf";
  
  public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/pdf/1.3/";
  
  public static final String KEYWORDS = "pdf:Keywords";
  
  public static final String VERSION = "pdf:PDFVersion";
  
  public static final String PRODUCER = "pdf:Producer";
  
  public PdfSchema() {
    super("xmlns:pdf=\"http://ns.adobe.com/pdf/1.3/\"");
    addProducer(Document.getVersion());
  }
  
  public void addKeywords(String keywords) {
    setProperty("pdf:Keywords", keywords);
  }
  
  public void addProducer(String producer) {
    setProperty("pdf:Producer", producer);
  }
  
  public void addVersion(String version) {
    setProperty("pdf:PDFVersion", version);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\xmp\PdfSchema.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */