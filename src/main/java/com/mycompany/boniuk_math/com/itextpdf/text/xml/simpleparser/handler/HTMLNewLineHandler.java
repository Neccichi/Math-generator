package com.mycompany.boniuk_math.com.itextpdf.text.xml.simpleparser.handler;

import com.mycompany.boniuk_math.com.itextpdf.text.xml.simpleparser.NewLineHandler;
import java.util.HashSet;
import java.util.Set;

public class HTMLNewLineHandler implements NewLineHandler {
  private final Set<String> newLineTags = new HashSet<String>();
  
  public HTMLNewLineHandler() {
    this.newLineTags.add("p");
    this.newLineTags.add("blockquote");
    this.newLineTags.add("br");
  }
  
  public boolean isNewLineTag(String tag) {
    return this.newLineTags.contains(tag);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\xml\simpleparser\handler\HTMLNewLineHandler.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */