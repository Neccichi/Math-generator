package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.Phrase;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.util.ArrayList;
import java.util.Iterator;

public class VerticalText {
  public static final int NO_MORE_TEXT = 1;
  
  public static final int NO_MORE_COLUMN = 2;
  
  protected ArrayList<PdfChunk> chunks = new ArrayList<PdfChunk>();
  
  protected PdfContentByte text;
  
  protected int alignment = 0;
  
  protected int currentChunkMarker = -1;
  
  protected PdfChunk currentStandbyChunk;
  
  protected String splittedChunkText;
  
  protected float leading;
  
  protected float startX;
  
  protected float startY;
  
  protected int maxLines;
  
  protected float height;
  
  private Float curCharSpace;
  
  public void addText(Phrase phrase) {
    for (Chunk c : phrase.getChunks())
      this.chunks.add(new PdfChunk(c, null)); 
  }
  
  public void addText(Chunk chunk) {
    this.chunks.add(new PdfChunk(chunk, null));
  }
  
  public void setVerticalLayout(float startX, float startY, float height, int maxLines, float leading) {
    this.startX = startX;
    this.startY = startY;
    this.height = height;
    this.maxLines = maxLines;
    setLeading(leading);
  }
  
  public void setLeading(float leading) {
    this.leading = leading;
  }
  
  public float getLeading() {
    return this.leading;
  }
  
  protected PdfLine createLine(float width) {
    if (this.chunks.isEmpty())
      return null; 
    this.splittedChunkText = null;
    this.currentStandbyChunk = null;
    PdfLine line = new PdfLine(0.0F, width, this.alignment, 0.0F);
    for (this.currentChunkMarker = 0; this.currentChunkMarker < this.chunks.size(); this.currentChunkMarker++) {
      PdfChunk original = this.chunks.get(this.currentChunkMarker);
      String total = original.toString();
      this.currentStandbyChunk = line.add(original);
      if (this.currentStandbyChunk != null) {
        this.splittedChunkText = original.toString();
        original.setValue(total);
        return line;
      } 
    } 
    return line;
  }
  
  protected void shortenChunkArray() {
    if (this.currentChunkMarker < 0)
      return; 
    if (this.currentChunkMarker >= this.chunks.size()) {
      this.chunks.clear();
      return;
    } 
    PdfChunk split = this.chunks.get(this.currentChunkMarker);
    split.setValue(this.splittedChunkText);
    this.chunks.set(this.currentChunkMarker, this.currentStandbyChunk);
    for (int j = this.currentChunkMarker - 1; j >= 0; j--)
      this.chunks.remove(j); 
  }
  
  public int go() {
    return go(false);
  }
  
  public int go(boolean simulate) {
    boolean dirty = false;
    PdfContentByte graphics = null;
    if (this.text != null) {
      graphics = this.text.getDuplicate();
    } else if (!simulate) {
      throw new NullPointerException(MessageLocalization.getComposedMessage("verticaltext.go.with.simulate.eq.eq.false.and.text.eq.eq.null", new Object[0]));
    } 
    int status = 0;
    while (true) {
      if (this.maxLines <= 0) {
        status = 2;
        if (this.chunks.isEmpty())
          status |= 0x1; 
        break;
      } 
      if (this.chunks.isEmpty()) {
        status = 1;
        break;
      } 
      PdfLine line = createLine(this.height);
      if (!simulate && !dirty) {
        this.text.beginText();
        dirty = true;
      } 
      shortenChunkArray();
      if (!simulate) {
        this.text.setTextMatrix(this.startX, this.startY - line.indentLeft());
        writeLine(line, this.text, graphics);
      } 
      this.maxLines--;
      this.startX -= this.leading;
    } 
    if (dirty) {
      this.text.endText();
      this.text.add(graphics);
    } 
    return status;
  }
  
  public VerticalText(PdfContentByte text) {
    this.curCharSpace = Float.valueOf(0.0F);
    this.text = text;
  }
  
  void writeLine(PdfLine line, PdfContentByte text, PdfContentByte graphics) {
    PdfFont currentFont = null;
    for (Iterator<PdfChunk> j = line.iterator(); j.hasNext(); ) {
      PdfChunk chunk = j.next();
      if (chunk.font().compareTo(currentFont) != 0) {
        currentFont = chunk.font();
        text.setFontAndSize(currentFont.getFont(), currentFont.size());
      } 
      BaseColor color = chunk.color();
      Float charSpace = (Float)chunk.getAttribute("CHAR_SPACING");
      if (charSpace != null && !this.curCharSpace.equals(charSpace)) {
        this.curCharSpace = Float.valueOf(charSpace.floatValue());
        text.setCharacterSpacing(this.curCharSpace.floatValue());
      } 
      if (color != null)
        text.setColorFill(color); 
      text.showText(chunk.toString());
      if (color != null)
        text.resetRGBColorFill(); 
    } 
  }
  
  public void setOrigin(float startX, float startY) {
    this.startX = startX;
    this.startY = startY;
  }
  
  public float getOriginX() {
    return this.startX;
  }
  
  public float getOriginY() {
    return this.startY;
  }
  
  public int getMaxLines() {
    return this.maxLines;
  }
  
  public void setMaxLines(int maxLines) {
    this.maxLines = maxLines;
  }
  
  public float getHeight() {
    return this.height;
  }
  
  public void setHeight(float height) {
    this.height = height;
  }
  
  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }
  
  public int getAlignment() {
    return this.alignment;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\VerticalText.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */