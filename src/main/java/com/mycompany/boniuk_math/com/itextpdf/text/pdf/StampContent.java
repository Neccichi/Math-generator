package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

public class StampContent extends PdfContentByte {
  PdfStamperImp.PageStamp ps;
  
  PageResources pageResources;
  
  StampContent(PdfStamperImp stamper, PdfStamperImp.PageStamp ps) {
    super(stamper);
    this.ps = ps;
    this.pageResources = ps.pageResources;
  }
  
  public void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
    ((PdfStamperImp)this.writer).addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action), this.ps.pageN);
  }
  
  public PdfContentByte getDuplicate() {
    return new StampContent((PdfStamperImp)this.writer, this.ps);
  }
  
  PageResources getPageResources() {
    return this.pageResources;
  }
  
  void addAnnotation(PdfAnnotation annot) {
    ((PdfStamperImp)this.writer).addAnnotation(annot, this.ps.pageN);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\StampContent.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */