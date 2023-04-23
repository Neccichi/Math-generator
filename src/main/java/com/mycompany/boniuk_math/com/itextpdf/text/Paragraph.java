package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.api.Indentable;
import com.mycompany.boniuk_math.com.itextpdf.text.api.Spaceable;
import java.util.List;

public class Paragraph extends Phrase implements Indentable, Spaceable {
  private static final long serialVersionUID = 7852314969733375514L;
  
  protected int alignment = -1;
  
  protected float multipliedLeading = 0.0F;
  
  protected float indentationLeft;
  
  protected float indentationRight;
  
  private float firstLineIndent = 0.0F;
  
  protected float spacingBefore;
  
  protected float spacingAfter;
  
  private float extraParagraphSpace = 0.0F;
  
  protected boolean keeptogether = false;
  
  public Paragraph(float leading) {
    super(leading);
  }
  
  public Paragraph(Chunk chunk) {
    super(chunk);
  }
  
  public Paragraph(float leading, Chunk chunk) {
    super(leading, chunk);
  }
  
  public Paragraph(String string) {
    super(string);
  }
  
  public Paragraph(String string, Font font) {
    super(string, font);
  }
  
  public Paragraph(float leading, String string) {
    super(leading, string);
  }
  
  public Paragraph(float leading, String string, Font font) {
    super(leading, string, font);
  }
  
  public Paragraph(Phrase phrase) {
    super(phrase);
    if (phrase instanceof Paragraph) {
      Paragraph p = (Paragraph)phrase;
      setAlignment(p.alignment);
      setLeading(phrase.getLeading(), p.multipliedLeading);
      setIndentationLeft(p.getIndentationLeft());
      setIndentationRight(p.getIndentationRight());
      setFirstLineIndent(p.getFirstLineIndent());
      setSpacingAfter(p.getSpacingAfter());
      setSpacingBefore(p.getSpacingBefore());
      setExtraParagraphSpace(p.getExtraParagraphSpace());
    } 
  }
  
  public int type() {
    return 12;
  }
  
  public boolean add(Element o) {
    if (o instanceof List) {
      List list = (List)o;
      list.setIndentationLeft(list.getIndentationLeft() + this.indentationLeft);
      list.setIndentationRight(this.indentationRight);
      return super.add(list);
    } 
    if (o instanceof Image) {
      addSpecial(o);
      return true;
    } 
    if (o instanceof Paragraph) {
      super.add(o);
      List<Chunk> chunks = getChunks();
      if (!chunks.isEmpty()) {
        Chunk tmp = chunks.get(chunks.size() - 1);
        super.add(new Chunk("\n", tmp.getFont()));
      } else {
        super.add(Chunk.NEWLINE);
      } 
      return true;
    } 
    return super.add(o);
  }
  
  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }
  
  public void setLeading(float fixedLeading) {
    this.leading = fixedLeading;
    this.multipliedLeading = 0.0F;
  }
  
  public void setMultipliedLeading(float multipliedLeading) {
    this.leading = 0.0F;
    this.multipliedLeading = multipliedLeading;
  }
  
  public void setLeading(float fixedLeading, float multipliedLeading) {
    this.leading = fixedLeading;
    this.multipliedLeading = multipliedLeading;
  }
  
  public void setIndentationLeft(float indentation) {
    this.indentationLeft = indentation;
  }
  
  public void setIndentationRight(float indentation) {
    this.indentationRight = indentation;
  }
  
  public void setFirstLineIndent(float firstLineIndent) {
    this.firstLineIndent = firstLineIndent;
  }
  
  public void setSpacingBefore(float spacing) {
    this.spacingBefore = spacing;
  }
  
  public void setSpacingAfter(float spacing) {
    this.spacingAfter = spacing;
  }
  
  public void setKeepTogether(boolean keeptogether) {
    this.keeptogether = keeptogether;
  }
  
  public boolean getKeepTogether() {
    return this.keeptogether;
  }
  
  public int getAlignment() {
    return this.alignment;
  }
  
  public float getMultipliedLeading() {
    return this.multipliedLeading;
  }
  
  public float getTotalLeading() {
    float m = (this.font == null) ? (12.0F * this.multipliedLeading) : this.font.getCalculatedLeading(this.multipliedLeading);
    if (m > 0.0F && !hasLeading())
      return m; 
    return getLeading() + m;
  }
  
  public float getIndentationLeft() {
    return this.indentationLeft;
  }
  
  public float getIndentationRight() {
    return this.indentationRight;
  }
  
  public float getFirstLineIndent() {
    return this.firstLineIndent;
  }
  
  public float getSpacingBefore() {
    return this.spacingBefore;
  }
  
  public float getSpacingAfter() {
    return this.spacingAfter;
  }
  
  public float getExtraParagraphSpace() {
    return this.extraParagraphSpace;
  }
  
  public void setExtraParagraphSpace(float extraParagraphSpace) {
    this.extraParagraphSpace = extraParagraphSpace;
  }
  
  @Deprecated
  public float spacingBefore() {
    return getSpacingBefore();
  }
  
  @Deprecated
  public float spacingAfter() {
    return this.spacingAfter;
  }
  
  public Paragraph() {}
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\Paragraph.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */