package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.api.Indentable;
import com.mycompany.boniuk_math.com.itextpdf.text.factories.RomanAlphabetFactory;
import java.util.ArrayList;

public class List implements TextElementArray, Indentable {
  public static final boolean ORDERED = true;
  
  public static final boolean UNORDERED = false;
  
  public static final boolean NUMERICAL = false;
  
  public static final boolean ALPHABETICAL = true;
  
  public static final boolean UPPERCASE = false;
  
  public static final boolean LOWERCASE = true;
  
  protected ArrayList<Element> list = new ArrayList<Element>();
  
  protected boolean numbered = false;
  
  protected boolean lettered = false;
  
  protected boolean lowercase = false;
  
  protected boolean autoindent = false;
  
  protected boolean alignindent = false;
  
  protected int first = 1;
  
  protected Chunk symbol = new Chunk("- ");
  
  protected String preSymbol = "";
  
  protected String postSymbol = ". ";
  
  protected float indentationLeft = 0.0F;
  
  protected float indentationRight = 0.0F;
  
  protected float symbolIndent = 0.0F;
  
  public List() {
    this(false, false);
  }
  
  public List(float symbolIndent) {
    this.symbolIndent = symbolIndent;
  }
  
  public List(boolean numbered) {
    this(numbered, false);
  }
  
  public List(boolean numbered, boolean lettered) {
    this.numbered = numbered;
    this.lettered = lettered;
    this.autoindent = true;
    this.alignindent = true;
  }
  
  public List(boolean numbered, float symbolIndent) {
    this(numbered, false, symbolIndent);
  }
  
  public List(boolean numbered, boolean lettered, float symbolIndent) {
    this.numbered = numbered;
    this.lettered = lettered;
    this.symbolIndent = symbolIndent;
  }
  
  public boolean process(ElementListener listener) {
    try {
      for (Element element : this.list)
        listener.add(element); 
      return true;
    } catch (DocumentException de) {
      return false;
    } 
  }
  
  public int type() {
    return 14;
  }
  
  public java.util.List<Chunk> getChunks() {
    java.util.List<Chunk> tmp = new ArrayList<Chunk>();
    for (Element element : this.list)
      tmp.addAll(element.getChunks()); 
    return tmp;
  }
  
  public boolean add(String s) {
    if (s != null)
      return add(new ListItem(s)); 
    return false;
  }
  
  public boolean add(Element o) {
    if (o instanceof ListItem) {
      ListItem item = (ListItem)o;
      if (this.numbered || this.lettered) {
        Chunk chunk = new Chunk(this.preSymbol, this.symbol.getFont());
        chunk.setAttributes(this.symbol.getAttributes());
        int index = this.first + this.list.size();
        if (this.lettered) {
          chunk.append(RomanAlphabetFactory.getString(index, this.lowercase));
        } else {
          chunk.append(String.valueOf(index));
        } 
        chunk.append(this.postSymbol);
        item.setListSymbol(chunk);
      } else {
        item.setListSymbol(this.symbol);
      } 
      item.setIndentationLeft(this.symbolIndent, this.autoindent);
      item.setIndentationRight(0.0F);
      return this.list.add(item);
    } 
    if (o instanceof List) {
      List nested = (List)o;
      nested.setIndentationLeft(nested.getIndentationLeft() + this.symbolIndent);
      this.first--;
      return this.list.add(nested);
    } 
    return false;
  }
  
  public void normalizeIndentation() {
    float max = 0.0F;
    for (Element o : this.list) {
      if (o instanceof ListItem)
        max = Math.max(max, ((ListItem)o).getIndentationLeft()); 
    } 
    for (Element o : this.list) {
      if (o instanceof ListItem)
        ((ListItem)o).setIndentationLeft(max); 
    } 
  }
  
  public void setNumbered(boolean numbered) {
    this.numbered = numbered;
  }
  
  public void setLettered(boolean lettered) {
    this.lettered = lettered;
  }
  
  public void setLowercase(boolean uppercase) {
    this.lowercase = uppercase;
  }
  
  public void setAutoindent(boolean autoindent) {
    this.autoindent = autoindent;
  }
  
  public void setAlignindent(boolean alignindent) {
    this.alignindent = alignindent;
  }
  
  public void setFirst(int first) {
    this.first = first;
  }
  
  public void setListSymbol(Chunk symbol) {
    this.symbol = symbol;
  }
  
  public void setListSymbol(String symbol) {
    this.symbol = new Chunk(symbol);
  }
  
  public void setIndentationLeft(float indentation) {
    this.indentationLeft = indentation;
  }
  
  public void setIndentationRight(float indentation) {
    this.indentationRight = indentation;
  }
  
  public void setSymbolIndent(float symbolIndent) {
    this.symbolIndent = symbolIndent;
  }
  
  public ArrayList<Element> getItems() {
    return this.list;
  }
  
  public int size() {
    return this.list.size();
  }
  
  public boolean isEmpty() {
    return this.list.isEmpty();
  }
  
  public float getTotalLeading() {
    if (this.list.size() < 1)
      return -1.0F; 
    ListItem item = (ListItem)this.list.get(0);
    return item.getTotalLeading();
  }
  
  public boolean isNumbered() {
    return this.numbered;
  }
  
  public boolean isLettered() {
    return this.lettered;
  }
  
  public boolean isLowercase() {
    return this.lowercase;
  }
  
  public boolean isAutoindent() {
    return this.autoindent;
  }
  
  public boolean isAlignindent() {
    return this.alignindent;
  }
  
  public int getFirst() {
    return this.first;
  }
  
  public Chunk getSymbol() {
    return this.symbol;
  }
  
  public float getIndentationLeft() {
    return this.indentationLeft;
  }
  
  public float getIndentationRight() {
    return this.indentationRight;
  }
  
  public float getSymbolIndent() {
    return this.symbolIndent;
  }
  
  public boolean isContent() {
    return true;
  }
  
  public boolean isNestable() {
    return true;
  }
  
  public String getPostSymbol() {
    return this.postSymbol;
  }
  
  public void setPostSymbol(String postSymbol) {
    this.postSymbol = postSymbol;
  }
  
  public String getPreSymbol() {
    return this.preSymbol;
  }
  
  public void setPreSymbol(String preSymbol) {
    this.preSymbol = preSymbol;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\List.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */