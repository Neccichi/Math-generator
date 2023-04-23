package com.mycompany.boniuk_math.com.itextpdf.text.xml.simpleparser.handler;

import com.mycompany.boniuk_math.com.itextpdf.text.xml.simpleparser.NewLineHandler;

public class NeverNewLineHandler implements NewLineHandler {
  public boolean isNewLineTag(String tag) {
    return false;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\simpleparser\handler\NeverNewLineHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */