package com.mycompany.boniuk_math.com.itextpdf.text.html.simpleparser;

import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.Element;
import com.mycompany.boniuk_math.com.itextpdf.text.ElementListener;
import com.mycompany.boniuk_math.com.itextpdf.text.Phrase;
import com.mycompany.boniuk_math.com.itextpdf.text.TextElementArray;
import com.mycompany.boniuk_math.com.itextpdf.text.html.HtmlUtilities;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPCell;
import java.util.List;

public class CellWrapper implements TextElementArray {
  private final PdfPCell cell;
  
  private float width;
  
  private boolean percentage;
  
  public CellWrapper(String tag, ChainedProperties chain) {
    this.cell = createPdfPCell(tag, chain);
    String value = chain.getProperty("width");
    if (value != null) {
      value = value.trim();
      if (value.endsWith("%")) {
        this.percentage = true;
        value = value.substring(0, value.length() - 1);
      } 
      this.width = Float.parseFloat(value);
    } 
  }
  
  public PdfPCell createPdfPCell(String tag, ChainedProperties chain) {
    PdfPCell cell = new PdfPCell((Phrase)null);
    String value = chain.getProperty("colspan");
    if (value != null)
      cell.setColspan(Integer.parseInt(value)); 
    value = chain.getProperty("rowspan");
    if (value != null)
      cell.setRowspan(Integer.parseInt(value)); 
    if (tag.equals("th"))
      cell.setHorizontalAlignment(1); 
    value = chain.getProperty("align");
    if (value != null)
      cell.setHorizontalAlignment(HtmlUtilities.alignmentValue(value)); 
    value = chain.getProperty("valign");
    cell.setVerticalAlignment(5);
    if (value != null)
      cell.setVerticalAlignment(HtmlUtilities.alignmentValue(value)); 
    value = chain.getProperty("border");
    float border = 0.0F;
    if (value != null)
      border = Float.parseFloat(value); 
    cell.setBorderWidth(border);
    value = chain.getProperty("cellpadding");
    if (value != null)
      cell.setPadding(Float.parseFloat(value)); 
    cell.setUseDescender(true);
    value = chain.getProperty("bgcolor");
    cell.setBackgroundColor(HtmlUtilities.decodeColor(value));
    return cell;
  }
  
  public PdfPCell getCell() {
    return this.cell;
  }
  
  public float getWidth() {
    return this.width;
  }
  
  public boolean isPercentage() {
    return this.percentage;
  }
  
  public boolean add(Element o) {
    this.cell.addElement(o);
    return true;
  }
  
  public List<Chunk> getChunks() {
    return null;
  }
  
  public boolean isContent() {
    return false;
  }
  
  public boolean isNestable() {
    return false;
  }
  
  public boolean process(ElementListener listener) {
    return false;
  }
  
  public int type() {
    return 0;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\html\simpleparser\CellWrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */