package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.itextpdf.text.error_messages.MessageLocalization;
import java.io.IOException;

public class PdfImportedPage extends PdfTemplate {
  PdfReaderInstance readerInstance;
  
  int pageNumber;
  
  protected boolean toCopy = true;
  
  PdfImportedPage(PdfReaderInstance readerInstance, PdfWriter writer, int pageNumber) {
    this.readerInstance = readerInstance;
    this.pageNumber = pageNumber;
    this.writer = writer;
    this.bBox = readerInstance.getReader().getPageSize(pageNumber);
    setMatrix(1.0F, 0.0F, 0.0F, 1.0F, -this.bBox.getLeft(), -this.bBox.getBottom());
    this.type = 2;
  }
  
  public PdfImportedPage getFromReader() {
    return this;
  }
  
  public int getPageNumber() {
    return this.pageNumber;
  }
  
  public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
    throwError();
  }
  
  public void addTemplate(PdfTemplate template, float a, float b, float c, float d, float e, float f) {
    throwError();
  }
  
  public PdfContentByte getDuplicate() {
    throwError();
    return null;
  }
  
  PdfStream getFormXObject(int compressionLevel) throws IOException {
    return this.readerInstance.getFormXObject(this.pageNumber, compressionLevel);
  }
  
  public void setColorFill(PdfSpotColor sp, float tint) {
    throwError();
  }
  
  public void setColorStroke(PdfSpotColor sp, float tint) {
    throwError();
  }
  
  PdfObject getResources() {
    return this.readerInstance.getResources(this.pageNumber);
  }
  
  public void setFontAndSize(BaseFont bf, float size) {
    throwError();
  }
  
  public void setGroup(PdfTransparencyGroup group) {
    throwError();
  }
  
  void throwError() {
    throw new RuntimeException(MessageLocalization.getComposedMessage("content.can.not.be.added.to.a.pdfimportedpage", new Object[0]));
  }
  
  PdfReaderInstance getPdfReaderInstance() {
    return this.readerInstance;
  }
  
  public boolean isToCopy() {
    return this.toCopy;
  }
  
  public void setCopied() {
    this.toCopy = false;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfImportedPage.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */