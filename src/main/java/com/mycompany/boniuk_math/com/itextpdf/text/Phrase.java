package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.HyphenationEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Phrase extends ArrayList<Element> implements TextElementArray {
  private static final long serialVersionUID = 2643594602455068231L;
  
  protected float leading = Float.NaN;
  
  protected Font font;
  
  protected HyphenationEvent hyphenation = null;
  
  public Phrase() {
    this(16.0F);
  }
  
  public Phrase(Phrase phrase) {
    addAll(phrase);
    this.leading = phrase.getLeading();
    this.font = phrase.getFont();
    setHyphenation(phrase.getHyphenation());
  }
  
  public Phrase(float leading) {
    this.leading = leading;
    this.font = new Font();
  }
  
  public Phrase(Chunk chunk) {
    super.add(chunk);
    this.font = chunk.getFont();
    setHyphenation(chunk.getHyphenation());
  }
  
  public Phrase(float leading, Chunk chunk) {
    this.leading = leading;
    super.add(chunk);
    this.font = chunk.getFont();
    setHyphenation(chunk.getHyphenation());
  }
  
  public Phrase(String string) {
    this(Float.NaN, string, new Font());
  }
  
  public Phrase(String string, Font font) {
    this(Float.NaN, string, font);
  }
  
  public Phrase(float leading, String string) {
    this(leading, string, new Font());
  }
  
  public Phrase(float leading, String string, Font font) {
    this.leading = leading;
    this.font = font;
    if (string != null && string.length() != 0)
      super.add(new Chunk(string, font)); 
  }
  
  public boolean process(ElementListener listener) {
    try {
      for (Object element : this)
        listener.add((Element)element); 
      return true;
    } catch (DocumentException de) {
      return false;
    } 
  }
  
  public int type() {
    return 11;
  }
  
  public List<Chunk> getChunks() {
    List<Chunk> tmp = new ArrayList<Chunk>();
    for (Element element : this)
      tmp.addAll(element.getChunks()); 
    return tmp;
  }
  
  public boolean isContent() {
    return true;
  }
  
  public boolean isNestable() {
    return true;
  }
  
  public void add(int index, Element element) {
    if (element == null)
      return; 
    try {
      if (element.type() == 10) {
        Chunk chunk = (Chunk)element;
        if (!this.font.isStandardFont())
          chunk.setFont(this.font.difference(chunk.getFont())); 
        if (this.hyphenation != null && chunk.getHyphenation() == null && !chunk.isEmpty())
          chunk.setHyphenation(this.hyphenation); 
        super.add(index, chunk);
      } else if (element.type() == 11 || element.type() == 17 || element.type() == 29 || element.type() == 55 || element.type() == 50 || element.type() == 666) {
        super.add(index, element);
      } else {
        throw new ClassCastException(String.valueOf(element.type()));
      } 
    } catch (ClassCastException cce) {
      throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", new Object[] { cce.getMessage() }));
    } 
  }
  
  public boolean add(String s) {
    if (s == null)
      return false; 
    return super.add(new Chunk(s, this.font));
  }
  
  public boolean add(Element element) {
    if (element == null)
      return false; 
    try {
      Phrase phrase;
      boolean success;
      switch (element.type()) {
        case 10:
          return addChunk((Chunk)element);
        case 11:
        case 12:
          phrase = (Phrase)element;
          success = true;
          for (Object element2 : phrase) {
            Element e = (Element)element2;
            if (e instanceof Chunk) {
              success &= addChunk((Chunk)e);
              continue;
            } 
            success &= add(e);
          } 
          return success;
        case 14:
        case 17:
        case 23:
        case 29:
        case 50:
        case 55:
        case 666:
          return super.add(element);
      } 
      throw new ClassCastException(String.valueOf(element.type()));
    } catch (ClassCastException cce) {
      throw new ClassCastException(MessageLocalization.getComposedMessage("insertion.of.illegal.element.1", new Object[] { cce.getMessage() }));
    } 
  }
  
  public boolean addAll(Collection<? extends Element> collection) {
    for (Element e : collection)
      add(e); 
    return true;
  }
  
  protected boolean addChunk(Chunk chunk) {
    Font f = chunk.getFont();
    String c = chunk.getContent();
    if (this.font != null && !this.font.isStandardFont())
      f = this.font.difference(chunk.getFont()); 
    if (size() > 0 && !chunk.hasAttributes())
      try {
        Chunk previous = (Chunk)get(size() - 1);
        if (!previous.hasAttributes() && (f == null || f.compareTo(previous.getFont()) == 0) && !"".equals(previous.getContent().trim()) && !"".equals(c.trim())) {
          previous.append(c);
          return true;
        } 
      } catch (ClassCastException cce) {} 
    Chunk newChunk = new Chunk(c, f);
    newChunk.setAttributes(chunk.getAttributes());
    if (this.hyphenation != null && newChunk.getHyphenation() == null && !newChunk.isEmpty())
      newChunk.setHyphenation(this.hyphenation); 
    return super.add(newChunk);
  }
  
  protected void addSpecial(Element object) {
    super.add(object);
  }
  
  public void setLeading(float leading) {
    this.leading = leading;
  }
  
  public void setFont(Font font) {
    this.font = font;
  }
  
  public float getLeading() {
    if (Float.isNaN(this.leading) && this.font != null)
      return this.font.getCalculatedLeading(1.5F); 
    return this.leading;
  }
  
  public boolean hasLeading() {
    if (Float.isNaN(this.leading))
      return false; 
    return true;
  }
  
  public Font getFont() {
    return this.font;
  }
  
  public String getContent() {
    StringBuffer buf = new StringBuffer();
    for (Chunk c : getChunks())
      buf.append(c.toString()); 
    return buf.toString();
  }
  
  public boolean isEmpty() {
    Element element;
    switch (size()) {
      case 0:
        return true;
      case 1:
        element = get(0);
        if (element.type() == 10 && ((Chunk)element).isEmpty())
          return true; 
        return false;
    } 
    return false;
  }
  
  public HyphenationEvent getHyphenation() {
    return this.hyphenation;
  }
  
  public void setHyphenation(HyphenationEvent hyphenation) {
    this.hyphenation = hyphenation;
  }
  
  public static final Phrase getInstance(String string) {
    return getInstance(16, string, new Font());
  }
  
  public static final Phrase getInstance(int leading, String string) {
    return getInstance(leading, string, new Font());
  }
  
  public static final Phrase getInstance(int leading, String string, Font font) {
    Phrase p = new Phrase(true);
    p.setLeading(leading);
    p.font = font;
    if (font.getFamily() != Font.FontFamily.SYMBOL && font.getFamily() != Font.FontFamily.ZAPFDINGBATS && font.getBaseFont() == null) {
      int index;
      while ((index = SpecialSymbol.index(string)) > -1) {
        if (index > 0) {
          String firstPart = string.substring(0, index);
          p.add(new Chunk(firstPart, font));
          string = string.substring(index);
        } 
        Font symbol = new Font(Font.FontFamily.SYMBOL, font.getSize(), font.getStyle(), font.getColor());
        StringBuffer buf = new StringBuffer();
        buf.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
        string = string.substring(1);
        while (SpecialSymbol.index(string) == 0) {
          buf.append(SpecialSymbol.getCorrespondingSymbol(string.charAt(0)));
          string = string.substring(1);
        } 
        p.add(new Chunk(buf.toString(), symbol));
      } 
    } 
    if (string != null && string.length() != 0)
      p.add(new Chunk(string, font)); 
    return p;
  }
  
  private Phrase(boolean dummy) {}
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\Phrase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */