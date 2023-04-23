package com.mycompany.boniuk_math.com.itextpdf.text;

import java.util.List;
import java.util.Properties;

public class MarkedObject implements Element {
  protected Element element;
  
  protected Properties markupAttributes = new Properties();
  
  protected MarkedObject() {
    this.element = null;
  }
  
  public MarkedObject(Element element) {
    this.element = element;
  }
  
  public List<Chunk> getChunks() {
    return this.element.getChunks();
  }
  
  public boolean process(ElementListener listener) {
    try {
      return listener.add(this.element);
    } catch (DocumentException de) {
      return false;
    } 
  }
  
  public int type() {
    return 50;
  }
  
  public boolean isContent() {
    return true;
  }
  
  public boolean isNestable() {
    return true;
  }
  
  public Properties getMarkupAttributes() {
    return this.markupAttributes;
  }
  
  public void setMarkupAttribute(String key, String value) {
    this.markupAttributes.setProperty(key, value);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\MarkedObject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */