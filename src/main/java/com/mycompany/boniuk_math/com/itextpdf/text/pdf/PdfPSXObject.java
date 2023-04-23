package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.io.IOException;

public class PdfPSXObject extends PdfTemplate {
  protected PdfPSXObject() {}
  
  public PdfPSXObject(PdfWriter wr) {
    super(wr);
  }
  
  PdfStream getFormXObject(int compressionLevel) throws IOException {
    PdfStream s = new PdfStream(this.content.toByteArray());
    s.put(PdfName.TYPE, PdfName.XOBJECT);
    s.put(PdfName.SUBTYPE, PdfName.PS);
    s.flateCompress(compressionLevel);
    return s;
  }
  
  public PdfContentByte getDuplicate() {
    PdfPSXObject tpl = new PdfPSXObject();
    tpl.writer = this.writer;
    tpl.pdf = this.pdf;
    tpl.thisReference = this.thisReference;
    tpl.pageResources = this.pageResources;
    tpl.separator = this.separator;
    return tpl;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfPSXObject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */