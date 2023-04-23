package com.mycompany.boniuk_math.com.itextpdf.text.xml.xmp;

import java.util.ArrayList;

public class XmpArray extends ArrayList<String> {
  private static final long serialVersionUID = 5722854116328732742L;
  
  public static final String UNORDERED = "rdf:Bag";
  
  public static final String ORDERED = "rdf:Seq";
  
  public static final String ALTERNATIVE = "rdf:Alt";
  
  protected String type;
  
  public XmpArray(String type) {
    this.type = type;
  }
  
  public String toString() {
    StringBuffer buf = new StringBuffer("<");
    buf.append(this.type);
    buf.append('>');
    for (String string : this) {
      String s = string;
      buf.append("<rdf:li>");
      buf.append(XmpSchema.escape(s));
      buf.append("</rdf:li>");
    } 
    buf.append("</");
    buf.append(this.type);
    buf.append('>');
    return buf.toString();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\xmp\XmpArray.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */