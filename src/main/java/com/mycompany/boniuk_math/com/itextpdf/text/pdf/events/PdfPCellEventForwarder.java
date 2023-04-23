package com.mycompany.boniuk_math.com.itextpdf.text.pdf.events;

import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfContentByte;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPCell;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfPCellEvent;
import java.util.ArrayList;

public class PdfPCellEventForwarder implements PdfPCellEvent {
  protected ArrayList<PdfPCellEvent> events = new ArrayList<PdfPCellEvent>();
  
  public void addCellEvent(PdfPCellEvent event) {
    this.events.add(event);
  }
  
  public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
    for (PdfPCellEvent event : this.events)
      event.cellLayout(cell, position, canvases); 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\events\PdfPCellEventForwarder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */