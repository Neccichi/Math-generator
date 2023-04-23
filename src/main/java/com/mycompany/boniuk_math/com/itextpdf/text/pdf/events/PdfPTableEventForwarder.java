package com.mycompany.boniuk_math.com.itextpdf.text.pdf.events;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfContentByte;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPTable;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPTableEvent;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPTableEventSplit;
import java.util.ArrayList;

public class PdfPTableEventForwarder implements PdfPTableEventSplit {
  protected ArrayList<PdfPTableEvent> events = new ArrayList<PdfPTableEvent>();
  
  public void addTableEvent(PdfPTableEvent event) {
    this.events.add(event);
  }
  
  public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) {
    for (PdfPTableEvent event : this.events)
      event.tableLayout(table, widths, heights, headerRows, rowStart, canvases); 
  }
  
  public void splitTable(PdfPTable table) {
    for (PdfPTableEvent event : this.events) {
      if (event instanceof PdfPTableEventSplit)
        ((PdfPTableEventSplit)event).splitTable(table); 
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\events\PdfPTableEventForwarder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */