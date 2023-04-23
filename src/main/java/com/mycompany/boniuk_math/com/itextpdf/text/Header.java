package com.mycompany.boniuk_math.com.itextpdf.text;

public class Header extends Meta {
  private StringBuffer name;
  
  public Header(String name, String content) {
    super(0, content);
    this.name = new StringBuffer(name);
  }
  
  public String getName() {
    return this.name.toString();
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\Header.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */