package com.mycompany.boniuk_math.com.itextpdf.text.html.simpleparser;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.Map;

public interface HTMLTagProcessor {
  void startElement(HTMLWorker paramHTMLWorker, String paramString, Map<String, String> paramMap) throws DocumentException, IOException;
  
  void endElement(HTMLWorker paramHTMLWorker, String paramString) throws DocumentException;
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\html\simpleparser\HTMLTagProcessor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */