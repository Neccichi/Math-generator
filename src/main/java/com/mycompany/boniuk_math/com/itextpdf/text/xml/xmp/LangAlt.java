package com.mycompany.boniuk_math.com.itextpdf.text.xml.xmp;

import java.util.Enumeration;
import java.util.Properties;

public class LangAlt extends Properties {
  private static final long serialVersionUID = 4396971487200843099L;
  
  public static final String DEFAULT = "x-default";
  
  public LangAlt(String defaultValue) {
    addLanguage("x-default", defaultValue);
  }
  
  public LangAlt() {}
  
  public void addLanguage(String language, String value) {
    setProperty(language, XmpSchema.escape(value));
  }
  
  protected void process(StringBuffer buf, Object lang) {
    buf.append("<rdf:li xml:lang=\"");
    buf.append(lang);
    buf.append("\" >");
    buf.append(get(lang));
    buf.append("</rdf:li>");
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("<rdf:Alt>");
    for (Enumeration<?> e = propertyNames(); e.hasMoreElements();)
      process(sb, e.nextElement()); 
    sb.append("</rdf:Alt>");
    return sb.toString();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\xmp\LangAlt.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */