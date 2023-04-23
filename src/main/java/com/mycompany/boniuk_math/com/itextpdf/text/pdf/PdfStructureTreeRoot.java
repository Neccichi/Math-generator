package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.io.IOException;
import java.util.HashMap;

public class PdfStructureTreeRoot extends PdfDictionary {
  private HashMap<Integer, PdfObject> parentTree = new HashMap<Integer, PdfObject>();
  
  private PdfIndirectReference reference;
  
  private PdfWriter writer;
  
  PdfStructureTreeRoot(PdfWriter writer) {
    super(PdfName.STRUCTTREEROOT);
    this.writer = writer;
    this.reference = writer.getPdfIndirectReference();
  }
  
  public void mapRole(PdfName used, PdfName standard) {
    PdfDictionary rm = (PdfDictionary)get(PdfName.ROLEMAP);
    if (rm == null) {
      rm = new PdfDictionary();
      put(PdfName.ROLEMAP, rm);
    } 
    rm.put(used, standard);
  }
  
  public PdfWriter getWriter() {
    return this.writer;
  }
  
  public PdfIndirectReference getReference() {
    return this.reference;
  }
  
  void setPageMark(int page, PdfIndirectReference struc) {
    Integer i = Integer.valueOf(page);
    PdfArray ar = (PdfArray)this.parentTree.get(i);
    if (ar == null) {
      ar = new PdfArray();
      this.parentTree.put(i, ar);
    } 
    ar.add(struc);
  }
  
  private void nodeProcess(PdfDictionary struc, PdfIndirectReference reference) throws IOException {
    PdfObject obj = struc.get(PdfName.K);
    if (obj != null && obj.isArray() && !((PdfArray)obj).getPdfObject(0).isNumber()) {
      PdfArray ar = (PdfArray)obj;
      for (int k = 0; k < ar.size(); k++) {
        PdfStructureElement e = (PdfStructureElement)ar.getAsDict(k);
        ar.set(k, e.getReference());
        nodeProcess(e, e.getReference());
      } 
    } 
    if (reference != null)
      this.writer.addToBody(struc, reference); 
  }
  
  void buildTree() throws IOException {
    HashMap<Integer, PdfIndirectReference> numTree = new HashMap<Integer, PdfIndirectReference>();
    for (Integer i : this.parentTree.keySet()) {
      PdfArray ar = (PdfArray)this.parentTree.get(i);
      numTree.put(i, this.writer.addToBody(ar).getIndirectReference());
    } 
    PdfDictionary dicTree = PdfNumberTree.writeTree(numTree, this.writer);
    if (dicTree != null)
      put(PdfName.PARENTTREE, this.writer.addToBody(dicTree).getIndirectReference()); 
    nodeProcess(this, this.reference);
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfStructureTreeRoot.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */