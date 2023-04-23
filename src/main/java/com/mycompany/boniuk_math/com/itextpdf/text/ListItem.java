package com.mycompany.boniuk_math.com.itextpdf.text;

import java.util.List;

public class ListItem extends Paragraph {
  private static final long serialVersionUID = 1970670787169329006L;
  
  protected Chunk symbol;
  
  public ListItem() {}
  
  public ListItem(float leading) {
    super(leading);
  }
  
  public ListItem(Chunk chunk) {
    super(chunk);
  }
  
  public ListItem(String string) {
    super(string);
  }
  
  public ListItem(String string, Font font) {
    super(string, font);
  }
  
  public ListItem(float leading, Chunk chunk) {
    super(leading, chunk);
  }
  
  public ListItem(float leading, String string) {
    super(leading, string);
  }
  
  public ListItem(float leading, String string, Font font) {
    super(leading, string, font);
  }
  
  public ListItem(Phrase phrase) {
    super(phrase);
  }
  
  public int type() {
    return 15;
  }
  
  public void setListSymbol(Chunk symbol) {
    if (this.symbol == null) {
      this.symbol = symbol;
      if (this.symbol.getFont().isStandardFont())
        this.symbol.setFont(this.font); 
    } 
  }
  
  public void setIndentationLeft(float indentation, boolean autoindent) {
    if (autoindent) {
      setIndentationLeft(getListSymbol().getWidthPoint());
    } else {
      setIndentationLeft(indentation);
    } 
  }
  
  public void adjustListSymbolFont() {
    List<Chunk> cks = getChunks();
    if (!cks.isEmpty() && this.symbol != null)
      this.symbol.setFont(((Chunk)cks.get(0)).getFont()); 
  }
  
  public Chunk getListSymbol() {
    return this.symbol;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\ListItem.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */