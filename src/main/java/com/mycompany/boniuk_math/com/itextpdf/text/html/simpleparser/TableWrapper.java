package com.mycompany.boniuk_math.com.itextpdf.text.html.simpleparser;

import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.Element;
import com.mycompany.boniuk_math.com.itextpdf.text.ElementListener;
import com.mycompany.boniuk_math.com.itextpdf.text.html.HtmlUtilities;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPCell;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPTable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableWrapper implements Element {
  private final Map<String, String> styles = new HashMap<String, String>();
  
  private final List<List<PdfPCell>> rows = new ArrayList<List<PdfPCell>>();
  
  private float[] colWidths;
  
  public TableWrapper(Map<String, String> attrs) {
    this.styles.putAll(attrs);
  }
  
  public void addRow(List<PdfPCell> row) {
    if (row != null) {
      Collections.reverse(row);
      this.rows.add(row);
      row = null;
    } 
  }
  
  public void setColWidths(float[] colWidths) {
    this.colWidths = colWidths;
  }
  
  public PdfPTable createTable() {
    if (this.rows.isEmpty())
      return new PdfPTable(1); 
    int ncol = 0;
    for (PdfPCell pc : this.rows.get(0))
      ncol += pc.getColspan(); 
    PdfPTable table = new PdfPTable(ncol);
    String width = this.styles.get("width");
    if (width == null) {
      table.setWidthPercentage(100.0F);
    } else if (width.endsWith("%")) {
      table.setWidthPercentage(Float.parseFloat(width.substring(0, width.length() - 1)));
    } else {
      table.setTotalWidth(Float.parseFloat(width));
      table.setLockedWidth(true);
    } 
    String alignment = this.styles.get("align");
    int align = 0;
    if (alignment != null)
      align = HtmlUtilities.alignmentValue(alignment); 
    table.setHorizontalAlignment(align);
    try {
      if (this.colWidths != null)
        table.setWidths(this.colWidths); 
    } catch (Exception e) {}
    for (List<PdfPCell> col : this.rows) {
      for (PdfPCell pc : col)
        table.addCell(pc); 
    } 
    return table;
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


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\html\simpleparser\TableWrapper.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */