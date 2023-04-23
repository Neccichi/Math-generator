package com.mycompany.boniuk_math.com.itextpdf.text.xml.simpleparser;

import java.util.Map;

public interface SimpleXMLDocHandler {
  void startElement(String paramString, Map<String, String> paramMap);
  
  void endElement(String paramString);
  
  void startDocument();
  
  void endDocument();
  
  void text(String paramString);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\simpleparser\SimpleXMLDocHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */