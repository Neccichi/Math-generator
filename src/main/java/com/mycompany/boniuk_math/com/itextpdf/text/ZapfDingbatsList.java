package com.mycompany.boniuk_math.com.itextpdf.text;

public class ZapfDingbatsList extends List {
  protected int zn;
  
  public ZapfDingbatsList(int zn) {
    super(true);
    this.zn = zn;
    float fontsize = this.symbol.getFont().getSize();
    this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
    this.postSymbol = " ";
  }
  
  public ZapfDingbatsList(int zn, int symbolIndent) {
    super(true, symbolIndent);
    this.zn = zn;
    float fontsize = this.symbol.getFont().getSize();
    this.symbol.setFont(FontFactory.getFont("ZapfDingbats", fontsize, 0));
    this.postSymbol = " ";
  }
  
  public void setCharNumber(int zn) {
    this.zn = zn;
  }
  
  public int getCharNumber() {
    return this.zn;
  }
  
  public boolean add(Element o) {
    if (o instanceof ListItem) {
      ListItem item = (ListItem)o;
      Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
      chunk.setAttributes(this.symbol.getAttributes());
      chunk.append(String.valueOf((char)this.zn));
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


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\ZapfDingbatsList.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */