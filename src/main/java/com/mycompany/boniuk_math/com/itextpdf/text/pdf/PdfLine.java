package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.ListItem;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfLine {
  protected ArrayList<PdfChunk> line;
  
  protected float left;
  
  protected float width;
  
  protected int alignment;
  
  protected float height;
  
  protected Chunk listSymbol = null;
  
  protected float symbolIndent;
  
  protected boolean newlineSplit = false;
  
  protected float originalWidth;
  
  protected boolean isRTL = false;
  
  PdfLine(float left, float right, int alignment, float height) {
    this.left = left;
    this.width = right - left;
    this.originalWidth = this.width;
    this.alignment = alignment;
    this.height = height;
    this.line = new ArrayList<PdfChunk>();
  }
  
  PdfLine(float left, float originalWidth, float remainingWidth, int alignment, boolean newlineSplit, ArrayList<PdfChunk> line, boolean isRTL) {
    this.left = left;
    this.originalWidth = originalWidth;
    this.width = remainingWidth;
    this.alignment = alignment;
    this.line = line;
    this.newlineSplit = newlineSplit;
    this.isRTL = isRTL;
  }
  
  PdfChunk add(PdfChunk chunk) {
    if (chunk == null || chunk.toString().equals(""))
      return null; 
    PdfChunk overflow = chunk.split(this.width);
    this.newlineSplit = (chunk.isNewlineSplit() || overflow == null);
    if (chunk.isTab()) {
      Object[] tab = (Object[])chunk.getAttribute("TAB");
      float tabPosition = ((Float)tab[1]).floatValue();
      boolean newline = ((Boolean)tab[2]).booleanValue();
      if (newline && tabPosition < this.originalWidth - this.width)
        return chunk; 
      this.width = this.originalWidth - tabPosition;
      chunk.adjustLeft(this.left);
      addToLine(chunk);
    } else if (chunk.length() > 0 || chunk.isImage()) {
      if (overflow != null)
        chunk.trimLastSpace(); 
      this.width -= chunk.width();
      addToLine(chunk);
    } else {
      if (this.line.size() < 1) {
        chunk = overflow;
        overflow = chunk.truncate(this.width);
        this.width -= chunk.width();
        if (chunk.length() > 0) {
          addToLine(chunk);
          return overflow;
        } 
        if (overflow != null)
          addToLine(overflow); 
        return null;
      } 
      this.width += ((PdfChunk)this.line.get(this.line.size() - 1)).trimLastSpace();
    } 
    return overflow;
  }
  
  private void addToLine(PdfChunk chunk) {
    if (chunk.changeLeading && chunk.isImage()) {
      Image img = chunk.getImage();
      float f = img.getScaledHeight() + chunk.getImageOffsetY() + img.getBorderWidthTop() + img.getSpacingBefore();
      if (f > this.height)
        this.height = f; 
    } 
    this.line.add(chunk);
  }
  
  public int size() {
    return this.line.size();
  }
  
  public Iterator<PdfChunk> iterator() {
    return this.line.iterator();
  }
  
  float height() {
    return this.height;
  }
  
  float indentLeft() {
    if (this.isRTL) {
      switch (this.alignment) {
        case 0:
          return this.left + this.width;
        case 1:
          return this.left + this.width / 2.0F;
      } 
      return this.left;
    } 
    if (getSeparatorCount() <= 0)
      switch (this.alignment) {
        case 2:
          return this.left + this.width;
        case 1:
          return this.left + this.width / 2.0F;
      }  
    return this.left;
  }
  
  public boolean hasToBeJustified() {
    return ((this.alignment == 3 || this.alignment == 8) && this.width != 0.0F);
  }
  
  public void resetAlignment() {
    if (this.alignment == 3)
      this.alignment = 0; 
  }
  
  void setExtraIndent(float extra) {
    this.left += extra;
    this.width -= extra;
  }
  
  float widthLeft() {
    return this.width;
  }
  
  int numberOfSpaces() {
    String string = toString();
    int length = string.length();
    int numberOfSpaces = 0;
    for (int i = 0; i < length; i++) {
      if (string.charAt(i) == ' ')
        numberOfSpaces++; 
    } 
    return numberOfSpaces;
  }
  
  public void setListItem(ListItem listItem) {
    this.listSymbol = listItem.getListSymbol();
    this.symbolIndent = listItem.getIndentationLeft();
  }
  
  public Chunk listSymbol() {
    return this.listSymbol;
  }
  
  public float listIndent() {
    return this.symbolIndent;
  }
  
  public String toString() {
    StringBuffer tmp = new StringBuffer();
    for (PdfChunk pdfChunk : this.line)
      tmp.append(pdfChunk.toString()); 
    return tmp.toString();
  }
  
  public int getLineLengthUtf32() {
    int total = 0;
    for (PdfChunk element : this.line)
      total += ((PdfChunk)element).lengthUtf32(); 
    return total;
  }
  
  public boolean isNewlineSplit() {
    return (this.newlineSplit && this.alignment != 8);
  }
  
  public int getLastStrokeChunk() {
    int lastIdx = this.line.size() - 1;
    for (; lastIdx >= 0; lastIdx--) {
      PdfChunk chunk = this.line.get(lastIdx);
      if (chunk.isStroked())
        break; 
    } 
    return lastIdx;
  }
  
  public PdfChunk getChunk(int idx) {
    if (idx < 0 || idx >= this.line.size())
      return null; 
    return this.line.get(idx);
  }
  
  public float getOriginalWidth() {
    return this.originalWidth;
  }
  
  float[] getMaxSize() {
    float normal_leading = 0.0F;
    float image_leading = -10000.0F;
    for (int k = 0; k < this.line.size(); k++) {
      PdfChunk chunk = this.line.get(k);
      if (!chunk.isImage()) {
        normal_leading = Math.max(chunk.font().size(), normal_leading);
      } else {
        Image img = chunk.getImage();
        if (chunk.changeLeading()) {
          float height = img.getScaledHeight() + chunk.getImageOffsetY() + img.getSpacingBefore();
          image_leading = Math.max(height, image_leading);
        } 
      } 
    } 
    return new float[] { normal_leading, image_leading };
  }
  
  boolean isRTL() {
    return this.isRTL;
  }
  
  int getSeparatorCount() {
    int s = 0;
    for (PdfChunk element : this.line) {
      PdfChunk ck = element;
      if (ck.isTab())
        return -1; 
      if (ck.isHorizontalSeparator())
        s++; 
    } 
    return s;
  }
  
  public float getWidthCorrected(float charSpacing, float wordSpacing) {
    float total = 0.0F;
    for (int k = 0; k < this.line.size(); k++) {
      PdfChunk ck = this.line.get(k);
      total += ck.getWidthCorrected(charSpacing, wordSpacing);
    } 
    return total;
  }
  
  public float getAscender() {
    float ascender = 0.0F;
    for (int k = 0; k < this.line.size(); k++) {
      PdfChunk ck = this.line.get(k);
      if (ck.isImage()) {
        ascender = Math.max(ascender, ck.getImage().getScaledHeight() + ck.getImageOffsetY());
      } else {
        PdfFont font = ck.font();
        ascender = Math.max(ascender, font.getFont().getFontDescriptor(1, font.size()));
      } 
    } 
    return ascender;
  }
  
  public float getDescender() {
    float descender = 0.0F;
    for (int k = 0; k < this.line.size(); k++) {
      PdfChunk ck = this.line.get(k);
      if (ck.isImage()) {
        descender = Math.min(descender, ck.getImageOffsetY());
      } else {
        PdfFont font = ck.font();
        descender = Math.min(descender, font.getFont().getFontDescriptor(3, font.size()));
      } 
    } 
    return descender;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfLine.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */