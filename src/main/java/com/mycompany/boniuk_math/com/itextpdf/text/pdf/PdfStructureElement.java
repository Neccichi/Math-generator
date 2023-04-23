package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;

public class PdfStructureElement extends PdfDictionary {
  private PdfStructureElement parent;
  
  private PdfStructureTreeRoot top;
  
  private PdfIndirectReference reference;
  
  public PdfStructureElement(PdfStructureElement parent, PdfName structureType) {
    this.top = parent.top;
    init(parent, structureType);
    this.parent = parent;
    put(PdfName.P, parent.reference);
    put(PdfName.TYPE, PdfName.STRUCTELEM);
  }
  
  public PdfStructureElement(PdfStructureTreeRoot parent, PdfName structureType) {
    this.top = parent;
    init(parent, structureType);
    put(PdfName.P, parent.getReference());
    put(PdfName.TYPE, PdfName.STRUCTELEM);
  }
  
  private void init(PdfDictionary parent, PdfName structureType) {
    PdfObject kido = parent.get(PdfName.K);
    PdfArray kids = null;
    if (kido != null && !kido.isArray())
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.parent.has.already.another.function", new Object[0])); 
    if (kido == null) {
      kids = new PdfArray();
      parent.put(PdfName.K, kids);
    } else {
      kids = (PdfArray)kido;
    } 
    kids.add(this);
    put(PdfName.S, structureType);
    this.reference = this.top.getWriter().getPdfIndirectReference();
  }
  
  public PdfDictionary getParent() {
    return this.parent;
  }
  
  void setPageMark(int page, int mark) {
    if (mark >= 0)
      put(PdfName.K, new PdfNumber(mark)); 
    this.top.setPageMark(page, this.reference);
  }
  
  public PdfIndirectReference getReference() {
    return this.reference;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfStructureElement.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */