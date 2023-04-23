package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.factories.GreekAlphabetFactory;

public class GreekList extends List {
  public GreekList() {
    super(true);
    setGreekFont();
  }
  
  public GreekList(int symbolIndent) {
    super(true, symbolIndent);
    setGreekFont();
  }
  
  public GreekList(boolean greeklower, int symbolIndent) {
    super(true, symbolIndent);
    this.lowercase = greeklower;
    setGreekFont();
  }
  
  protected void setGreekFont() {
    float fontsize = this.symbol.getFont().getSize();
    this.symbol.setFont(FontFactory.getFont("Symbol", fontsize, 0));
  }
  
  public boolean add(Element o) {
    if (o instanceof ListItem) {
      ListItem item = (ListItem)o;
      Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
      chunk.setAttributes(this.symbol.getAttributes());
      chunk.append(GreekAlphabetFactory.getString(this.first + this.list.size(), this.lowercase));
      chunk.append(this.postSymbol);
      item.setListSymbol(chunk);
      item.setIndentationLeft(this.symbolIndent, this.autoindent);
      item.setIndentationRight(0.0F);
      this.list.add(item);
    } else if (o instanceof List) {
      List nested = (List)o;
      nested.setIndentationLeft(nested.getIndentationLeft() + this.symbolIndent);
      this.first--;
      return this.list.add(nested);
    } 
    return false;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\GreekList.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */