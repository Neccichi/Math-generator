package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocListener;
import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class PdfCopy extends PdfWriter {
  protected HashMap<RefKey, IndirectReferences> indirects;
  
  protected HashMap<PdfReader, HashMap<RefKey, IndirectReferences>> indirectMap;
  
  static class IndirectReferences {
    PdfIndirectReference theRef;
    
    boolean hasCopied;
    
    IndirectReferences(PdfIndirectReference ref) {
      this.theRef = ref;
      this.hasCopied = false;
    }
    
    void setCopied() {
      this.hasCopied = true;
    }
    
    boolean getCopied() {
      return this.hasCopied;
    }
    
    PdfIndirectReference getRef() {
      return this.theRef;
    }
  }
  
  protected int currentObjectNum = 1;
  
  protected PdfReader reader;
  
  protected PdfIndirectReference acroForm;
  
  protected int[] namePtr = new int[] { 0 };
  
  private boolean rotateContents = true;
  
  protected PdfArray fieldArray;
  
  protected HashSet<PdfTemplate> fieldTemplates;
  
  protected static class RefKey {
    int num;
    
    int gen;
    
    RefKey(int num, int gen) {
      this.num = num;
      this.gen = gen;
    }
    
    RefKey(PdfIndirectReference ref) {
      this.num = ref.getNumber();
      this.gen = ref.getGeneration();
    }
    
    RefKey(PRIndirectReference ref) {
      this.num = ref.getNumber();
      this.gen = ref.getGeneration();
    }
    
    public int hashCode() {
      return (this.gen << 16) + this.num;
    }
    
    public boolean equals(Object o) {
      if (!(o instanceof RefKey))
        return false; 
      RefKey other = (RefKey)o;
      return (this.gen == other.gen && this.num == other.num);
    }
    
    public String toString() {
      return Integer.toString(this.num) + ' ' + this.gen;
    }
  }
  
  public PdfCopy(Document document, OutputStream os) throws DocumentException {
    super(new PdfDocument(), os);
    document.addDocListener((DocListener)this.pdf);
    this.pdf.addWriter(this);
    this.indirectMap = new HashMap<PdfReader, HashMap<RefKey, IndirectReferences>>();
  }
  
  public boolean isRotateContents() {
    return this.rotateContents;
  }
  
  public void setRotateContents(boolean rotateContents) {
    this.rotateContents = rotateContents;
  }
  
  public PdfImportedPage getImportedPage(PdfReader reader, int pageNumber) {
    if (this.currentPdfReaderInstance != null) {
      if (this.currentPdfReaderInstance.getReader() != reader) {
        try {
          this.currentPdfReaderInstance.getReader().close();
          this.currentPdfReaderInstance.getReaderFile().close();
        } catch (IOException ioe) {}
        this.currentPdfReaderInstance = getPdfReaderInstance(reader);
      } 
    } else {
      this.currentPdfReaderInstance = getPdfReaderInstance(reader);
    } 
    return this.currentPdfReaderInstance.getImportedPage(pageNumber);
  }
  
  protected PdfIndirectReference copyIndirect(PRIndirectReference in) throws IOException, BadPdfFormatException {
    PdfIndirectReference theRef;
    RefKey key = new RefKey(in);
    IndirectReferences iRef = this.indirects.get(key);
    if (iRef != null) {
      theRef = iRef.getRef();
      if (iRef.getCopied())
        return theRef; 
    } else {
      theRef = this.body.getPdfIndirectReference();
      iRef = new IndirectReferences(theRef);
      this.indirects.put(key, iRef);
    } 
    PdfObject obj = PdfReader.getPdfObjectRelease(in);
    if (obj != null && obj.isDictionary()) {
      PdfObject type = PdfReader.getPdfObjectRelease(((PdfDictionary)obj).get(PdfName.TYPE));
      if (type != null && PdfName.PAGE.equals(type))
        return theRef; 
    } 
    iRef.setCopied();
    obj = copyObject(obj);
    addToBody(obj, theRef);
    return theRef;
  }
  
  protected PdfDictionary copyDictionary(PdfDictionary in) throws IOException, BadPdfFormatException {
    PdfDictionary out = new PdfDictionary();
    PdfObject type = PdfReader.getPdfObjectRelease(in.get(PdfName.TYPE));
    for (PdfName element : in.getKeys()) {
      PdfName key = element;
      PdfObject value = in.get(key);
      if (type != null && PdfName.PAGE.equals(type)) {
        if (!key.equals(PdfName.B) && !key.equals(PdfName.PARENT))
          out.put(key, copyObject(value)); 
        continue;
      } 
      out.put(key, copyObject(value));
    } 
    return out;
  }
  
  protected PdfStream copyStream(PRStream in) throws IOException, BadPdfFormatException {
    PRStream out = new PRStream(in, null);
    for (PdfName element : in.getKeys()) {
      PdfName key = element;
      PdfObject value = in.get(key);
      out.put(key, copyObject(value));
    } 
    return out;
  }
  
  protected PdfArray copyArray(PdfArray in) throws IOException, BadPdfFormatException {
    PdfArray out = new PdfArray();
    for (Iterator<PdfObject> i = in.listIterator(); i.hasNext(); ) {
      PdfObject value = i.next();
      out.add(copyObject(value));
    } 
    return out;
  }
  
  protected PdfObject copyObject(PdfObject in) throws IOException, BadPdfFormatException {
    if (in == null)
      return PdfNull.PDFNULL; 
    switch (in.type) {
      case 6:
        return copyDictionary((PdfDictionary)in);
      case 10:
        return copyIndirect((PRIndirectReference)in);
      case 5:
        return copyArray((PdfArray)in);
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 8:
        return in;
      case 7:
        return copyStream((PRStream)in);
    } 
    if (in.type < 0) {
      String lit = ((PdfLiteral)in).toString();
      if (lit.equals("true") || lit.equals("false"))
        return new PdfBoolean(lit); 
      return new PdfLiteral(lit);
    } 
    System.out.println("CANNOT COPY type " + in.type);
    return null;
  }
  
  protected int setFromIPage(PdfImportedPage iPage) {
    int pageNum = iPage.getPageNumber();
    PdfReaderInstance inst = this.currentPdfReaderInstance = iPage.getPdfReaderInstance();
    this.reader = inst.getReader();
    setFromReader(this.reader);
    return pageNum;
  }
  
  protected void setFromReader(PdfReader reader) {
    this.reader = reader;
    this.indirects = this.indirectMap.get(reader);
    if (this.indirects == null) {
      this.indirects = new HashMap<RefKey, IndirectReferences>();
      this.indirectMap.put(reader, this.indirects);
      PdfDictionary catalog = reader.getCatalog();
      PRIndirectReference ref = null;
      PdfObject o = catalog.get(PdfName.ACROFORM);
      if (o == null || o.type() != 10)
        return; 
      ref = (PRIndirectReference)o;
      if (this.acroForm == null)
        this.acroForm = this.body.getPdfIndirectReference(); 
      this.indirects.put(new RefKey(ref), new IndirectReferences(this.acroForm));
    } 
  }
  
  public void addPage(PdfImportedPage iPage) throws IOException, BadPdfFormatException {
    int pageNum = setFromIPage(iPage);
    PdfDictionary thePage = this.reader.getPageN(pageNum);
    PRIndirectReference origRef = this.reader.getPageOrigRef(pageNum);
    this.reader.releasePage(pageNum);
    RefKey key = new RefKey(origRef);
    IndirectReferences iRef = this.indirects.get(key);
    if (iRef != null && !iRef.getCopied()) {
      this.pageReferences.add(iRef.getRef());
      iRef.setCopied();
    } 
    PdfIndirectReference pageRef = getCurrentPage();
    if (iRef == null) {
      iRef = new IndirectReferences(pageRef);
      this.indirects.put(key, iRef);
    } 
    iRef.setCopied();
    PdfDictionary newPage = copyDictionary(thePage);
    this.root.addPage(newPage);
    iPage.setCopied();
    this.currentPageNumber++;
  }
  
  public void addPage(Rectangle rect, int rotation) {
    PdfRectangle mediabox = new PdfRectangle(rect, rotation);
    PageResources resources = new PageResources();
    PdfPage page = new PdfPage(mediabox, new HashMap<String, PdfRectangle>(), resources.getResources(), 0);
    page.put(PdfName.TABS, getTabs());
    this.root.addPage(page);
    this.currentPageNumber++;
  }
  
  public void copyAcroForm(PdfReader reader) throws IOException, BadPdfFormatException {
    PdfIndirectReference myRef;
    setFromReader(reader);
    PdfDictionary catalog = reader.getCatalog();
    PRIndirectReference hisRef = null;
    PdfObject o = catalog.get(PdfName.ACROFORM);
    if (o != null && o.type() == 10)
      hisRef = (PRIndirectReference)o; 
    if (hisRef == null)
      return; 
    RefKey key = new RefKey(hisRef);
    IndirectReferences iRef = this.indirects.get(key);
    if (iRef != null) {
      this.acroForm = myRef = iRef.getRef();
    } else {
      this.acroForm = myRef = this.body.getPdfIndirectReference();
      iRef = new IndirectReferences(myRef);
      this.indirects.put(key, iRef);
    } 
    if (!iRef.getCopied()) {
      iRef.setCopied();
      PdfDictionary theForm = copyDictionary((PdfDictionary)PdfReader.getPdfObject(hisRef));
      addToBody(theForm, myRef);
    } 
  }
  
  protected PdfDictionary getCatalog(PdfIndirectReference rootObj) {
    try {
      PdfDictionary theCat = this.pdf.getCatalog(rootObj);
      if (this.fieldArray == null) {
        if (this.acroForm != null)
          theCat.put(PdfName.ACROFORM, this.acroForm); 
      } else {
        addFieldResources(theCat);
      } 
      return theCat;
    } catch (IOException e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  private void addFieldResources(PdfDictionary catalog) throws IOException {
    if (this.fieldArray == null)
      return; 
    PdfDictionary acroForm = new PdfDictionary();
    catalog.put(PdfName.ACROFORM, acroForm);
    acroForm.put(PdfName.FIELDS, this.fieldArray);
    acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
    if (this.fieldTemplates.isEmpty())
      return; 
    PdfDictionary dr = new PdfDictionary();
    acroForm.put(PdfName.DR, dr);
    for (PdfTemplate template : this.fieldTemplates)
      PdfFormField.mergeResources(dr, (PdfDictionary)template.getResources()); 
    PdfDictionary fonts = dr.getAsDict(PdfName.FONT);
    if (fonts == null) {
      fonts = new PdfDictionary();
      dr.put(PdfName.FONT, fonts);
    } 
    if (!fonts.contains(PdfName.HELV)) {
      PdfDictionary dic = new PdfDictionary(PdfName.FONT);
      dic.put(PdfName.BASEFONT, PdfName.HELVETICA);
      dic.put(PdfName.ENCODING, PdfName.WIN_ANSI_ENCODING);
      dic.put(PdfName.NAME, PdfName.HELV);
      dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
      fonts.put(PdfName.HELV, addToBody(dic).getIndirectReference());
    } 
    if (!fonts.contains(PdfName.ZADB)) {
      PdfDictionary dic = new PdfDictionary(PdfName.FONT);
      dic.put(PdfName.BASEFONT, PdfName.ZAPFDINGBATS);
      dic.put(PdfName.NAME, PdfName.ZADB);
      dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
      fonts.put(PdfName.ZADB, addToBody(dic).getIndirectReference());
    } 
  }
  
  public void close() {
    if (this.open) {
      PdfReaderInstance ri = this.currentPdfReaderInstance;
      this.pdf.close();
      super.close();
      if (ri != null)
        try {
          ri.getReader().close();
          ri.getReaderFile().close();
        } catch (IOException ioe) {} 
    } 
  }
  
  public PdfIndirectReference add(PdfOutline outline) {
    return null;
  }
  
  public void addAnnotation(PdfAnnotation annot) {}
  
  PdfIndirectReference add(PdfPage page, PdfContents contents) throws PdfException {
    return null;
  }
  
  public void freeReader(PdfReader reader) throws IOException {
    this.indirectMap.remove(reader);
    if (this.currentPdfReaderInstance != null && 
      this.currentPdfReaderInstance.getReader() == reader) {
      try {
        this.currentPdfReaderInstance.getReader().close();
        this.currentPdfReaderInstance.getReaderFile().close();
      } catch (IOException ioe) {}
      this.currentPdfReaderInstance = null;
    } 
    super.freeReader(reader);
  }
  
  public PageStamp createPageStamp(PdfImportedPage iPage) {
    int pageNum = iPage.getPageNumber();
    PdfReader reader = iPage.getPdfReaderInstance().getReader();
    PdfDictionary pageN = reader.getPageN(pageNum);
    return new PageStamp(reader, pageN, this);
  }
  
  public static class PageStamp {
    PdfDictionary pageN;
    
    PdfCopy.StampContent under;
    
    PdfCopy.StampContent over;
    
    PageResources pageResources;
    
    PdfReader reader;
    
    PdfCopy cstp;
    
    PageStamp(PdfReader reader, PdfDictionary pageN, PdfCopy cstp) {
      this.pageN = pageN;
      this.reader = reader;
      this.cstp = cstp;
    }
    
    public PdfContentByte getUnderContent() {
      if (this.under == null) {
        if (this.pageResources == null) {
          this.pageResources = new PageResources();
          PdfDictionary resources = this.pageN.getAsDict(PdfName.RESOURCES);
          this.pageResources.setOriginalResources(resources, this.cstp.namePtr);
        } 
        this.under = new PdfCopy.StampContent(this.cstp, this.pageResources);
      } 
      return this.under;
    }
    
    public PdfContentByte getOverContent() {
      if (this.over == null) {
        if (this.pageResources == null) {
          this.pageResources = new PageResources();
          PdfDictionary resources = this.pageN.getAsDict(PdfName.RESOURCES);
          this.pageResources.setOriginalResources(resources, this.cstp.namePtr);
        } 
        this.over = new PdfCopy.StampContent(this.cstp, this.pageResources);
      } 
      return this.over;
    }
    
    public void alterContents() throws IOException {
      if (this.over == null && this.under == null)
        return; 
      PdfArray ar = null;
      PdfObject content = PdfReader.getPdfObject(this.pageN.get(PdfName.CONTENTS), this.pageN);
      if (content == null) {
        ar = new PdfArray();
        this.pageN.put(PdfName.CONTENTS, ar);
      } else if (content.isArray()) {
        ar = (PdfArray)content;
      } else if (content.isStream()) {
        ar = new PdfArray();
        ar.add(this.pageN.get(PdfName.CONTENTS));
        this.pageN.put(PdfName.CONTENTS, ar);
      } else {
        ar = new PdfArray();
        this.pageN.put(PdfName.CONTENTS, ar);
      } 
      ByteBuffer out = new ByteBuffer();
      if (this.under != null) {
        out.append(PdfContents.SAVESTATE);
        applyRotation(this.pageN, out);
        out.append(this.under.getInternalBuffer());
        out.append(PdfContents.RESTORESTATE);
      } 
      if (this.over != null)
        out.append(PdfContents.SAVESTATE); 
      PdfStream stream = new PdfStream(out.toByteArray());
      stream.flateCompress(this.cstp.getCompressionLevel());
      PdfIndirectReference ref1 = this.cstp.addToBody(stream).getIndirectReference();
      ar.addFirst(ref1);
      out.reset();
      if (this.over != null) {
        out.append(' ');
        out.append(PdfContents.RESTORESTATE);
        out.append(PdfContents.SAVESTATE);
        applyRotation(this.pageN, out);
        out.append(this.over.getInternalBuffer());
        out.append(PdfContents.RESTORESTATE);
        stream = new PdfStream(out.toByteArray());
        stream.flateCompress(this.cstp.getCompressionLevel());
        ar.add(this.cstp.addToBody(stream).getIndirectReference());
      } 
      this.pageN.put(PdfName.RESOURCES, this.pageResources.getResources());
    }
    
    void applyRotation(PdfDictionary pageN, ByteBuffer out) {
      if (!this.cstp.rotateContents)
        return; 
      Rectangle page = this.reader.getPageSizeWithRotation(pageN);
      int rotation = page.getRotation();
      switch (rotation) {
        case 90:
          out.append(PdfContents.ROTATE90);
          out.append(page.getTop());
          out.append(' ').append('0').append(PdfContents.ROTATEFINAL);
          break;
        case 180:
          out.append(PdfContents.ROTATE180);
          out.append(page.getRight());
          out.append(' ');
          out.append(page.getTop());
          out.append(PdfContents.ROTATEFINAL);
          break;
        case 270:
          out.append(PdfContents.ROTATE270);
          out.append('0').append(' ');
          out.append(page.getRight());
          out.append(PdfContents.ROTATEFINAL);
          break;
      } 
    }
    
    private void addDocumentField(PdfIndirectReference ref) {
      if (this.cstp.fieldArray == null)
        this.cstp.fieldArray = new PdfArray(); 
      this.cstp.fieldArray.add(ref);
    }
    
    private void expandFields(PdfFormField field, ArrayList<PdfAnnotation> allAnnots) {
      allAnnots.add(field);
      ArrayList<PdfFormField> kids = field.getKids();
      if (kids != null)
        for (PdfFormField f : kids)
          expandFields(f, allAnnots);  
    }
    
    public void addAnnotation(PdfAnnotation annot) {
      try {
        ArrayList<PdfAnnotation> allAnnots = new ArrayList<PdfAnnotation>();
        if (annot.isForm()) {
          PdfFormField field = (PdfFormField)annot;
          if (field.getParent() != null)
            return; 
          expandFields(field, allAnnots);
          if (this.cstp.fieldTemplates == null)
            this.cstp.fieldTemplates = new HashSet<PdfTemplate>(); 
        } else {
          allAnnots.add(annot);
        } 
        for (int k = 0; k < allAnnots.size(); k++) {
          annot = allAnnots.get(k);
          if (annot.isForm()) {
            if (!annot.isUsed()) {
              HashSet<PdfTemplate> templates = annot.getTemplates();
              if (templates != null)
                this.cstp.fieldTemplates.addAll(templates); 
            } 
            PdfFormField field = (PdfFormField)annot;
            if (field.getParent() == null)
              addDocumentField(field.getIndirectReference()); 
          } 
          if (annot.isAnnotation()) {
            PdfObject pdfobj = PdfReader.getPdfObject(this.pageN.get(PdfName.ANNOTS), this.pageN);
            PdfArray annots = null;
            if (pdfobj == null || !pdfobj.isArray()) {
              annots = new PdfArray();
              this.pageN.put(PdfName.ANNOTS, annots);
            } else {
              annots = (PdfArray)pdfobj;
            } 
            annots.add(annot.getIndirectReference());
            if (!annot.isUsed()) {
              PdfRectangle rect = (PdfRectangle)annot.get(PdfName.RECT);
              if (rect != null && (rect.left() != 0.0F || rect.right() != 0.0F || rect.top() != 0.0F || rect.bottom() != 0.0F)) {
                int rotation = this.reader.getPageRotation(this.pageN);
                Rectangle pageSize = this.reader.getPageSizeWithRotation(this.pageN);
                switch (rotation) {
                  case 90:
                    annot.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.bottom(), rect.left(), pageSize.getTop() - rect.top(), rect.right()));
                    break;
                  case 180:
                    annot.put(PdfName.RECT, new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                    break;
                  case 270:
                    annot.put(PdfName.RECT, new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                    break;
                } 
              } 
            } 
          } 
          if (!annot.isUsed()) {
            annot.setUsed();
            this.cstp.addToBody(annot, annot.getIndirectReference());
          } 
        } 
      } catch (IOException e) {
        throw new ExceptionConverter(e);
      } 
    }
  }
  
  public static class StampContent extends PdfContentByte {
    PageResources pageResources;
    
    StampContent(PdfWriter writer, PageResources pageResources) {
      super(writer);
      this.pageResources = pageResources;
    }
    
    public PdfContentByte getDuplicate() {
      return new StampContent(this.writer, this.pageResources);
    }
    
    PageResources getPageResources() {
      return this.pageResources;
    }
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfCopy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */