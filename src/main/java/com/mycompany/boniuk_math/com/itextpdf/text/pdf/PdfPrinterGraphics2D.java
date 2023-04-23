package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.awt.print.PrinterGraphics;
import java.awt.print.PrinterJob;

public class PdfPrinterGraphics2D extends PdfGraphics2D implements PrinterGraphics {
  private PrinterJob printerJob;
  
  public PdfPrinterGraphics2D(PdfContentByte cb, float width, float height, FontMapper fontMapper, boolean onlyShapes, boolean convertImagesToJPEG, float quality, PrinterJob printerJob) {
    super(cb, width, height, fontMapper, onlyShapes, convertImagesToJPEG, quality);
    this.printerJob = printerJob;
  }
  
  public PrinterJob getPrinterJob() {
    return this.printerJob;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPrinterGraphics2D.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */