package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.DocListener;
import com.mycompany.boniuk_math.com.itextpdf.text.DocWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.ImgJBIG2;
import com.mycompany.boniuk_math.com.itextpdf.text.ImgWMF;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection.PdfCollection;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.events.PdfPageEventForwarder;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfAnnotations;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfDocumentActions;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfEncryptionSettings;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfPageActions;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfRunDirection;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfVersion;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfViewerPreferences;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfXConformance;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal.PdfVersionImp;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal.PdfXConformanceImp;
import com.mycompany.boniuk_math.com.itextpdf.text.xml.xmp.XmpWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class PdfWriter extends DocWriter implements PdfViewerPreferences, PdfEncryptionSettings, PdfVersion, PdfDocumentActions, PdfPageActions, PdfXConformance, PdfRunDirection, PdfAnnotations {
  public static final int GENERATION_MAX = 65535;
  
  protected PdfDocument pdf;
  
  protected PdfContentByte directContent;
  
  protected PdfContentByte directContentUnder;
  
  protected PdfBody body;
  
  protected PdfDictionary extraCatalog;
  
  protected PdfPages root;
  
  protected ArrayList<PdfIndirectReference> pageReferences;
  
  protected int currentPageNumber;
  
  protected PdfName tabs;
  
  protected PdfDictionary pageDictEntries;
  
  private PdfPageEvent pageEvent;
  
  protected int prevxref;
  
  protected List<HashMap<String, Object>> newBookmarks;
  
  public static final char VERSION_1_2 = '2';
  
  public static final char VERSION_1_3 = '3';
  
  public static final char VERSION_1_4 = '4';
  
  public static final char VERSION_1_5 = '5';
  
  public static final char VERSION_1_6 = '6';
  
  public static final char VERSION_1_7 = '7';
  
  public static class PdfBody {
    private static final int OBJSINSTREAM = 200;
    
    private final TreeSet<PdfCrossReference> xrefs;
    
    private int refnum;
    
    private int position;
    
    private final PdfWriter writer;
    
    private ByteBuffer index;
    
    private ByteBuffer streamObjects;
    
    private int currentObjNum;
    
    static class PdfCrossReference implements Comparable<PdfCrossReference> {
      private final int type;
      
      private final int offset;
      
      private final int refnum;
      
      private final int generation;
      
      PdfCrossReference(int refnum, int offset, int generation) {
        this.type = 0;
        this.offset = offset;
        this.refnum = refnum;
        this.generation = generation;
      }
      
      PdfCrossReference(int refnum, int offset) {
        this.type = 1;
        this.offset = offset;
        this.refnum = refnum;
        this.generation = 0;
      }
      
      PdfCrossReference(int type, int refnum, int offset, int generation) {
        this.type = type;
        this.offset = offset;
        this.refnum = refnum;
        this.generation = generation;
      }
      
      int getRefnum() {
        return this.refnum;
      }
      
      public void toPdf(OutputStream os) throws IOException {
        StringBuffer off = (new StringBuffer("0000000000")).append(this.offset);
        off.delete(0, off.length() - 10);
        StringBuffer gen = (new StringBuffer("00000")).append(this.generation);
        gen.delete(0, gen.length() - 5);
        off.append(' ').append(gen).append((this.generation == 65535) ? " f \n" : " n \n");
        os.write(DocWriter.getISOBytes(off.toString()));
      }
      
      public void toPdf(int midSize, OutputStream os) throws IOException {
        os.write((byte)this.type);
        while (--midSize >= 0)
          os.write((byte)(this.offset >>> 8 * midSize & 0xFF)); 
        os.write((byte)(this.generation >>> 8 & 0xFF));
        os.write((byte)(this.generation & 0xFF));
      }
      
      public int compareTo(PdfCrossReference other) {
        return (this.refnum < other.refnum) ? -1 : ((this.refnum == other.refnum) ? 0 : 1);
      }
      
      public boolean equals(Object obj) {
        if (obj instanceof PdfCrossReference) {
          PdfCrossReference other = (PdfCrossReference)obj;
          return (this.refnum == other.refnum);
        } 
        return false;
      }
      
      public int hashCode() {
        return this.refnum;
      }
    }
    
    private int numObj = 0;
    
    PdfBody(PdfWriter writer) {
      this.xrefs = new TreeSet<PdfCrossReference>();
      this.xrefs.add(new PdfCrossReference(0, 0, 65535));
      this.position = writer.getOs().getCounter();
      this.refnum = 1;
      this.writer = writer;
    }
    
    void setRefnum(int refnum) {
      this.refnum = refnum;
    }
    
    private PdfCrossReference addToObjStm(PdfObject obj, int nObj) throws IOException {
      if (this.numObj >= 200)
        flushObjStm(); 
      if (this.index == null) {
        this.index = new ByteBuffer();
        this.streamObjects = new ByteBuffer();
        this.currentObjNum = getIndirectReferenceNumber();
        this.numObj = 0;
      } 
      int p = this.streamObjects.size();
      int idx = this.numObj++;
      PdfEncryption enc = this.writer.crypto;
      this.writer.crypto = null;
      obj.toPdf(this.writer, this.streamObjects);
      this.writer.crypto = enc;
      this.streamObjects.append(' ');
      this.index.append(nObj).append(' ').append(p).append(' ');
      return new PdfCrossReference(2, nObj, this.currentObjNum, idx);
    }
    
    private void flushObjStm() throws IOException {
      if (this.numObj == 0)
        return; 
      int first = this.index.size();
      this.index.append(this.streamObjects);
      PdfStream stream = new PdfStream(this.index.toByteArray());
      stream.flateCompress(this.writer.getCompressionLevel());
      stream.put(PdfName.TYPE, PdfName.OBJSTM);
      stream.put(PdfName.N, new PdfNumber(this.numObj));
      stream.put(PdfName.FIRST, new PdfNumber(first));
      add(stream, this.currentObjNum);
      this.index = null;
      this.streamObjects = null;
      this.numObj = 0;
    }
    
    PdfIndirectObject add(PdfObject object) throws IOException {
      return add(object, getIndirectReferenceNumber());
    }
    
    PdfIndirectObject add(PdfObject object, boolean inObjStm) throws IOException {
      return add(object, getIndirectReferenceNumber(), inObjStm);
    }
    
    PdfIndirectReference getPdfIndirectReference() {
      return new PdfIndirectReference(0, getIndirectReferenceNumber());
    }
    
    int getIndirectReferenceNumber() {
      int n = this.refnum++;
      this.xrefs.add(new PdfCrossReference(n, 0, 65535));
      return n;
    }
    
    PdfIndirectObject add(PdfObject object, PdfIndirectReference ref) throws IOException {
      return add(object, ref.getNumber());
    }
    
    PdfIndirectObject add(PdfObject object, PdfIndirectReference ref, boolean inObjStm) throws IOException {
      return add(object, ref.getNumber(), inObjStm);
    }
    
    PdfIndirectObject add(PdfObject object, int refNumber) throws IOException {
      return add(object, refNumber, true);
    }
    
    PdfIndirectObject add(PdfObject object, int refNumber, boolean inObjStm) throws IOException {
      if (inObjStm && object.canBeInObjStm() && this.writer.isFullCompression()) {
        PdfCrossReference pdfCrossReference = addToObjStm(object, refNumber);
        PdfIndirectObject pdfIndirectObject = new PdfIndirectObject(refNumber, object, this.writer);
        if (!this.xrefs.add(pdfCrossReference)) {
          this.xrefs.remove(pdfCrossReference);
          this.xrefs.add(pdfCrossReference);
        } 
        return pdfIndirectObject;
      } 
      PdfIndirectObject indirect = new PdfIndirectObject(refNumber, object, this.writer);
      PdfCrossReference pxref = new PdfCrossReference(refNumber, this.position);
      if (!this.xrefs.add(pxref)) {
        this.xrefs.remove(pxref);
        this.xrefs.add(pxref);
      } 
      indirect.writeTo(this.writer.getOs());
      this.position = this.writer.getOs().getCounter();
      return indirect;
    }
    
    int offset() {
      return this.position;
    }
    
    int size() {
      return Math.max(((PdfCrossReference)this.xrefs.last()).getRefnum() + 1, this.refnum);
    }
    
    void writeCrossReferenceTable(OutputStream os, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) throws IOException {
      int refNumber = 0;
      if (this.writer.isFullCompression()) {
        flushObjStm();
        refNumber = getIndirectReferenceNumber();
        this.xrefs.add(new PdfCrossReference(refNumber, this.position));
      } 
      PdfCrossReference entry = this.xrefs.first();
      int first = entry.getRefnum();
      int len = 0;
      ArrayList<Integer> sections = new ArrayList<Integer>();
      for (PdfCrossReference pdfCrossReference : this.xrefs) {
        entry = pdfCrossReference;
        if (first + len == entry.getRefnum()) {
          len++;
          continue;
        } 
        sections.add(Integer.valueOf(first));
        sections.add(Integer.valueOf(len));
        first = entry.getRefnum();
        len = 1;
      } 
      sections.add(Integer.valueOf(first));
      sections.add(Integer.valueOf(len));
      if (this.writer.isFullCompression()) {
        int mid = 4;
        int mask = -16777216;
        for (; mid > 1 && (
          mask & this.position) == 0; mid--)
          mask >>>= 8; 
        ByteBuffer buf = new ByteBuffer();
        for (PdfCrossReference element : this.xrefs) {
          entry = element;
          entry.toPdf(mid, buf);
        } 
        PdfStream xr = new PdfStream(buf.toByteArray());
        buf = null;
        xr.flateCompress(this.writer.getCompressionLevel());
        xr.put(PdfName.SIZE, new PdfNumber(size()));
        xr.put(PdfName.ROOT, root);
        if (info != null)
          xr.put(PdfName.INFO, info); 
        if (encryption != null)
          xr.put(PdfName.ENCRYPT, encryption); 
        if (fileID != null)
          xr.put(PdfName.ID, fileID); 
        xr.put(PdfName.W, new PdfArray(new int[] { 1, mid, 2 }));
        xr.put(PdfName.TYPE, PdfName.XREF);
        PdfArray idx = new PdfArray();
        for (int k = 0; k < sections.size(); k++)
          idx.add(new PdfNumber(((Integer)sections.get(k)).intValue())); 
        xr.put(PdfName.INDEX, idx);
        if (prevxref > 0)
          xr.put(PdfName.PREV, new PdfNumber(prevxref)); 
        PdfEncryption enc = this.writer.crypto;
        this.writer.crypto = null;
        PdfIndirectObject indirect = new PdfIndirectObject(refNumber, xr, this.writer);
        indirect.writeTo(this.writer.getOs());
        this.writer.crypto = enc;
      } else {
        os.write(DocWriter.getISOBytes("xref\n"));
        Iterator<PdfCrossReference> i = this.xrefs.iterator();
        for (int k = 0; k < sections.size(); k += 2) {
          first = ((Integer)sections.get(k)).intValue();
          len = ((Integer)sections.get(k + 1)).intValue();
          os.write(DocWriter.getISOBytes(String.valueOf(first)));
          os.write(DocWriter.getISOBytes(" "));
          os.write(DocWriter.getISOBytes(String.valueOf(len)));
          os.write(10);
          while (len-- > 0) {
            entry = i.next();
            entry.toPdf(os);
          } 
        } 
      } 
    }
  }
  
  static class PdfTrailer extends PdfDictionary {
    int offset;
    
    PdfTrailer(int size, int offset, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) {
      this.offset = offset;
      put(PdfName.SIZE, new PdfNumber(size));
      put(PdfName.ROOT, root);
      if (info != null)
        put(PdfName.INFO, info); 
      if (encryption != null)
        put(PdfName.ENCRYPT, encryption); 
      if (fileID != null)
        put(PdfName.ID, fileID); 
      if (prevxref > 0)
        put(PdfName.PREV, new PdfNumber(prevxref)); 
    }
    
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
      os.write(DocWriter.getISOBytes("trailer\n"));
      super.toPdf((PdfWriter)null, os);
      os.write(DocWriter.getISOBytes("\nstartxref\n"));
      os.write(DocWriter.getISOBytes(String.valueOf(this.offset)));
      os.write(DocWriter.getISOBytes("\n%%EOF\n"));
    }
  }
  
  protected PdfWriter() {
    this.root = new PdfPages(this);
    this.pageReferences = new ArrayList<PdfIndirectReference>();
    this.currentPageNumber = 1;
    this.tabs = null;
    this.pageDictEntries = new PdfDictionary();
    this.prevxref = 0;
    this.pdf_version = new PdfVersionImp();
    this.xmpMetadata = null;
    this.pdfxConformance = new PdfXConformanceImp();
    this.fullCompression = false;
    this.compressionLevel = -1;
    this.documentFonts = new LinkedHashMap<BaseFont, FontDetails>();
    this.fontNumber = 1;
    this.formXObjects = (HashMap)new HashMap<PdfIndirectReference, Object>();
    this.formXObjectsCounter = 1;
    this.readerInstances = new HashMap<PdfReader, PdfReaderInstance>();
    this.documentColors = new HashMap<PdfSpotColor, ColorDetails>();
    this.colorNumber = 1;
    this.documentPatterns = new HashMap<PdfPatternPainter, PdfName>();
    this.patternNumber = 1;
    this.documentShadingPatterns = new HashSet<PdfShadingPattern>();
    this.documentShadings = new HashSet<PdfShading>();
    this.documentExtGState = (HashMap)new HashMap<PdfDictionary, PdfObject>();
    this.documentProperties = (HashMap)new HashMap<Object, PdfObject>();
    this.tagged = false;
    this.documentOCG = new HashSet<PdfOCG>();
    this.documentOCGorder = new ArrayList<PdfOCG>();
    this.OCGRadioGroup = new PdfArray();
    this.OCGLocked = new PdfArray();
    this.spaceCharRatio = 2.5F;
    this.runDirection = 1;
    this.defaultColorspace = new PdfDictionary();
    this.documentSpotPatterns = new HashMap<ColorDetails, ColorDetails>();
    this.imageDictionary = new PdfDictionary();
    this.images = new HashMap<Long, PdfName>();
    this.JBIG2Globals = new HashMap<PdfStream, PdfIndirectReference>();
  }
  
  protected PdfWriter(PdfDocument document, OutputStream os) {
    super(document, os);
    this.root = new PdfPages(this);
    this.pageReferences = new ArrayList<PdfIndirectReference>();
    this.currentPageNumber = 1;
    this.tabs = null;
    this.pageDictEntries = new PdfDictionary();
    this.prevxref = 0;
    this.pdf_version = new PdfVersionImp();
    this.xmpMetadata = null;
    this.pdfxConformance = new PdfXConformanceImp();
    this.fullCompression = false;
    this.compressionLevel = -1;
    this.documentFonts = new LinkedHashMap<BaseFont, FontDetails>();
    this.fontNumber = 1;
    this.formXObjects = (HashMap)new HashMap<PdfIndirectReference, Object>();
    this.formXObjectsCounter = 1;
    this.readerInstances = new HashMap<PdfReader, PdfReaderInstance>();
    this.documentColors = new HashMap<PdfSpotColor, ColorDetails>();
    this.colorNumber = 1;
    this.documentPatterns = new HashMap<PdfPatternPainter, PdfName>();
    this.patternNumber = 1;
    this.documentShadingPatterns = new HashSet<PdfShadingPattern>();
    this.documentShadings = new HashSet<PdfShading>();
    this.documentExtGState = (HashMap)new HashMap<PdfDictionary, PdfObject>();
    this.documentProperties = (HashMap)new HashMap<Object, PdfObject>();
    this.tagged = false;
    this.documentOCG = new HashSet<PdfOCG>();
    this.documentOCGorder = new ArrayList<PdfOCG>();
    this.OCGRadioGroup = new PdfArray();
    this.OCGLocked = new PdfArray();
    this.spaceCharRatio = 2.5F;
    this.runDirection = 1;
    this.defaultColorspace = new PdfDictionary();
    this.documentSpotPatterns = new HashMap<ColorDetails, ColorDetails>();
    this.imageDictionary = new PdfDictionary();
    this.images = new HashMap<Long, PdfName>();
    this.JBIG2Globals = new HashMap<PdfStream, PdfIndirectReference>();
    this.pdf = document;
    this.directContent = new PdfContentByte(this);
    this.directContentUnder = new PdfContentByte(this);
  }
  
  public static PdfWriter getInstance(Document document, OutputStream os) throws DocumentException {
    PdfDocument pdf = new PdfDocument();
    document.addDocListener((DocListener)pdf);
    PdfWriter writer = new PdfWriter(pdf, os);
    pdf.addWriter(writer);
    return writer;
  }
  
  public static PdfWriter getInstance(Document document, OutputStream os, DocListener listener) throws DocumentException {
    PdfDocument pdf = new PdfDocument();
    pdf.addDocListener(listener);
    document.addDocListener((DocListener)pdf);
    PdfWriter writer = new PdfWriter(pdf, os);
    pdf.addWriter(writer);
    return writer;
  }
  
  PdfDocument getPdfDocument() {
    return this.pdf;
  }
  
  public PdfDictionary getInfo() {
    return this.pdf.getInfo();
  }
  
  public float getVerticalPosition(boolean ensureNewLine) {
    return this.pdf.getVerticalPosition(ensureNewLine);
  }
  
  public void setInitialLeading(float leading) throws DocumentException {
    if (this.open)
      throw new DocumentException(MessageLocalization.getComposedMessage("you.can.t.set.the.initial.leading.if.the.document.is.already.open", new Object[0])); 
    this.pdf.setLeading(leading);
  }
  
  public PdfContentByte getDirectContent() {
    if (!this.open)
      throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open", new Object[0])); 
    return this.directContent;
  }
  
  public PdfContentByte getDirectContentUnder() {
    if (!this.open)
      throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open", new Object[0])); 
    return this.directContentUnder;
  }
  
  void resetContent() {
    this.directContent.reset();
    this.directContentUnder.reset();
  }
  
  void addLocalDestinations(TreeMap<String, PdfDocument.Destination> desto) throws IOException {
    for (Map.Entry<String, PdfDocument.Destination> entry : desto.entrySet()) {
      String name = entry.getKey();
      PdfDocument.Destination dest = entry.getValue();
      PdfDestination destination = dest.destination;
      if (dest.reference == null)
        dest.reference = getPdfIndirectReference(); 
      if (destination == null) {
        addToBody(new PdfString("invalid_" + name), dest.reference);
        continue;
      } 
      addToBody(destination, dest.reference);
    } 
  }
  
  public PdfIndirectObject addToBody(PdfObject object) throws IOException {
    PdfIndirectObject iobj = this.body.add(object);
    return iobj;
  }
  
  public PdfIndirectObject addToBody(PdfObject object, boolean inObjStm) throws IOException {
    PdfIndirectObject iobj = this.body.add(object, inObjStm);
    return iobj;
  }
  
  public PdfIndirectObject addToBody(PdfObject object, PdfIndirectReference ref) throws IOException {
    PdfIndirectObject iobj = this.body.add(object, ref);
    return iobj;
  }
  
  public PdfIndirectObject addToBody(PdfObject object, PdfIndirectReference ref, boolean inObjStm) throws IOException {
    PdfIndirectObject iobj = this.body.add(object, ref, inObjStm);
    return iobj;
  }
  
  public PdfIndirectObject addToBody(PdfObject object, int refNumber) throws IOException {
    PdfIndirectObject iobj = this.body.add(object, refNumber);
    return iobj;
  }
  
  public PdfIndirectObject addToBody(PdfObject object, int refNumber, boolean inObjStm) throws IOException {
    PdfIndirectObject iobj = this.body.add(object, refNumber, inObjStm);
    return iobj;
  }
  
  public PdfIndirectReference getPdfIndirectReference() {
    return this.body.getPdfIndirectReference();
  }
  
  int getIndirectReferenceNumber() {
    return this.body.getIndirectReferenceNumber();
  }
  
  OutputStreamCounter getOs() {
    return this.os;
  }
  
  protected PdfDictionary getCatalog(PdfIndirectReference rootObj) {
    PdfDictionary catalog = this.pdf.getCatalog(rootObj);
    if (this.tagged) {
      try {
        getStructureTreeRoot().buildTree();
      } catch (Exception e) {
        throw new ExceptionConverter(e);
      } 
      catalog.put(PdfName.STRUCTTREEROOT, this.structureTreeRoot.getReference());
      PdfDictionary mi = new PdfDictionary();
      mi.put(PdfName.MARKED, PdfBoolean.PDFTRUE);
      if (this.userProperties)
        mi.put(PdfName.USERPROPERTIES, PdfBoolean.PDFTRUE); 
      catalog.put(PdfName.MARKINFO, mi);
    } 
    if (!this.documentOCG.isEmpty()) {
      fillOCProperties(false);
      catalog.put(PdfName.OCPROPERTIES, this.OCProperties);
    } 
    return catalog;
  }
  
  public PdfDictionary getExtraCatalog() {
    if (this.extraCatalog == null)
      this.extraCatalog = new PdfDictionary(); 
    return this.extraCatalog;
  }
  
  public void addPageDictEntry(PdfName key, PdfObject object) {
    this.pageDictEntries.put(key, object);
  }
  
  public PdfDictionary getPageDictEntries() {
    return this.pageDictEntries;
  }
  
  public void resetPageDictEntries() {
    this.pageDictEntries = new PdfDictionary();
  }
  
  public void setLinearPageMode() {
    this.root.setLinearMode(null);
  }
  
  public int reorderPages(int[] order) throws DocumentException {
    return this.root.reorderPages(order);
  }
  
  public PdfIndirectReference getPageReference(int page) {
    PdfIndirectReference ref;
    page--;
    if (page < 0)
      throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("the.page.number.must.be.gt.eq.1", new Object[0])); 
    if (page < this.pageReferences.size()) {
      ref = this.pageReferences.get(page);
      if (ref == null) {
        ref = this.body.getPdfIndirectReference();
        this.pageReferences.set(page, ref);
      } 
    } else {
      int empty = page - this.pageReferences.size();
      for (int k = 0; k < empty; k++)
        this.pageReferences.add(null); 
      ref = this.body.getPdfIndirectReference();
      this.pageReferences.add(ref);
    } 
    return ref;
  }
  
  public int getPageNumber() {
    return this.pdf.getPageNumber();
  }
  
  PdfIndirectReference getCurrentPage() {
    return getPageReference(this.currentPageNumber);
  }
  
  public int getCurrentPageNumber() {
    return this.currentPageNumber;
  }
  
  public void setPageViewport(PdfArray vp) {
    addPageDictEntry(PdfName.VP, vp);
  }
  
  public void setTabs(PdfName tabs) {
    this.tabs = tabs;
  }
  
  public PdfName getTabs() {
    return this.tabs;
  }
  
  PdfIndirectReference add(PdfPage page, PdfContents contents) throws PdfException {
    PdfIndirectObject object;
    if (!this.open)
      throw new PdfException(MessageLocalization.getComposedMessage("the.document.is.not.open", new Object[0])); 
    try {
      object = addToBody(contents);
    } catch (IOException ioe) {
      throw new ExceptionConverter(ioe);
    } 
    page.add(object.getIndirectReference());
    if (this.group != null) {
      page.put(PdfName.GROUP, this.group);
      this.group = null;
    } else if (this.rgbTransparencyBlending) {
      PdfDictionary pp = new PdfDictionary();
      pp.put(PdfName.TYPE, PdfName.GROUP);
      pp.put(PdfName.S, PdfName.TRANSPARENCY);
      pp.put(PdfName.CS, PdfName.DEVICERGB);
      page.put(PdfName.GROUP, pp);
    } 
    this.root.addPage(page);
    this.currentPageNumber++;
    return null;
  }
  
  public void setPageEvent(PdfPageEvent event) {
    if (event == null) {
      this.pageEvent = null;
    } else if (this.pageEvent == null) {
      this.pageEvent = event;
    } else if (this.pageEvent instanceof PdfPageEventForwarder) {
      ((PdfPageEventForwarder)this.pageEvent).addPageEvent(event);
    } else {
      PdfPageEventForwarder forward = new PdfPageEventForwarder();
      forward.addPageEvent(this.pageEvent);
      forward.addPageEvent(event);
      this.pageEvent = (PdfPageEvent)forward;
    } 
  }
  
  public PdfPageEvent getPageEvent() {
    return this.pageEvent;
  }
  
  public void open() {
    super.open();
    try {
      this.pdf_version.writeHeader(this.os);
      this.body = new PdfBody(this);
      if (this.pdfxConformance.isPdfX32002()) {
        PdfDictionary sec = new PdfDictionary();
        sec.put(PdfName.GAMMA, new PdfArray(new float[] { 2.2F, 2.2F, 2.2F }));
        sec.put(PdfName.MATRIX, new PdfArray(new float[] { 0.4124F, 0.2126F, 0.0193F, 0.3576F, 0.7152F, 0.1192F, 0.1805F, 0.0722F, 0.9505F }));
        sec.put(PdfName.WHITEPOINT, new PdfArray(new float[] { 0.9505F, 1.0F, 1.089F }));
        PdfArray arr = new PdfArray(PdfName.CALRGB);
        arr.add(sec);
        setDefaultColorspace(PdfName.DEFAULTRGB, addToBody(arr).getIndirectReference());
      } 
    } catch (IOException ioe) {
      throw new ExceptionConverter(ioe);
    } 
  }
  
  public void close() {
    if (this.open) {
      if (this.currentPageNumber - 1 != this.pageReferences.size())
        throw new RuntimeException("The page " + this.pageReferences.size() + " was requested but the document has only " + (this.currentPageNumber - 1) + " pages."); 
      this.pdf.close();
      try {
        addSharedObjectsToBody();
        for (PdfOCG layer : this.documentOCG)
          addToBody(layer.getPdfObject(), layer.getRef()); 
        PdfIndirectReference rootRef = this.root.writePageTree();
        PdfDictionary catalog = getCatalog(rootRef);
        if (this.xmpMetadata != null) {
          PdfStream xmp = new PdfStream(this.xmpMetadata);
          xmp.put(PdfName.TYPE, PdfName.METADATA);
          xmp.put(PdfName.SUBTYPE, PdfName.XML);
          if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
            PdfArray ar = new PdfArray();
            ar.add(PdfName.CRYPT);
            xmp.put(PdfName.FILTER, ar);
          } 
          catalog.put(PdfName.METADATA, this.body.add(xmp).getIndirectReference());
        } 
        if (isPdfX()) {
          this.pdfxConformance.completeInfoDictionary(getInfo());
          this.pdfxConformance.completeExtraCatalog(getExtraCatalog());
        } 
        if (this.extraCatalog != null)
          catalog.mergeDifferent(this.extraCatalog); 
        writeOutlines(catalog, false);
        PdfIndirectObject indirectCatalog = addToBody(catalog, false);
        PdfIndirectObject infoObj = addToBody(getInfo(), false);
        PdfIndirectReference encryption = null;
        PdfObject fileID = null;
        this.body.flushObjStm();
        if (this.crypto != null) {
          PdfIndirectObject encryptionObject = addToBody(this.crypto.getEncryptionDictionary(), false);
          encryption = encryptionObject.getIndirectReference();
          fileID = this.crypto.getFileID();
        } else {
          fileID = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
        } 
        this.body.writeCrossReferenceTable(this.os, indirectCatalog.getIndirectReference(), infoObj.getIndirectReference(), encryption, fileID, this.prevxref);
        if (this.fullCompression) {
          this.os.write(getISOBytes("startxref\n"));
          this.os.write(getISOBytes(String.valueOf(this.body.offset())));
          this.os.write(getISOBytes("\n%%EOF\n"));
        } else {
          PdfTrailer trailer = new PdfTrailer(this.body.size(), this.body.offset(), indirectCatalog.getIndirectReference(), infoObj.getIndirectReference(), encryption, fileID, this.prevxref);
          trailer.toPdf(this, this.os);
        } 
        super.close();
      } catch (IOException ioe) {
        throw new ExceptionConverter(ioe);
      } 
    } 
  }
  
  protected void addSharedObjectsToBody() throws IOException {
    for (FontDetails details : this.documentFonts.values())
      details.writeFont(this); 
    for (Object[] objs : this.formXObjects.values()) {
      PdfTemplate template = (PdfTemplate)objs[1];
      if (template != null && template.getIndirectReference() instanceof PRIndirectReference)
        continue; 
      if (template != null && template.getType() == 1)
        addToBody(template.getFormXObject(this.compressionLevel), template.getIndirectReference()); 
    } 
    for (PdfReaderInstance element : this.readerInstances.values()) {
      this.currentPdfReaderInstance = element;
      this.currentPdfReaderInstance.writeAllPages();
    } 
    this.currentPdfReaderInstance = null;
    for (ColorDetails color : this.documentColors.values())
      addToBody(color.getSpotColor(this), color.getIndirectReference()); 
    for (PdfPatternPainter pat : this.documentPatterns.keySet())
      addToBody(pat.getPattern(this.compressionLevel), pat.getIndirectReference()); 
    for (PdfShadingPattern shadingPattern : this.documentShadingPatterns)
      shadingPattern.addToBody(); 
    for (PdfShading shading : this.documentShadings)
      shading.addToBody(); 
    for (Map.Entry<PdfDictionary, PdfObject[]> entry : this.documentExtGState.entrySet()) {
      PdfDictionary gstate = entry.getKey();
      PdfObject[] obj = entry.getValue();
      addToBody(gstate, (PdfIndirectReference)obj[1]);
    } 
    for (Map.Entry<Object, PdfObject[]> entry : this.documentProperties.entrySet()) {
      Object prop = entry.getKey();
      PdfObject[] obj = entry.getValue();
      if (prop instanceof PdfLayerMembership) {
        PdfLayerMembership layer = (PdfLayerMembership)prop;
        addToBody(layer.getPdfObject(), layer.getRef());
        continue;
      } 
      if (prop instanceof PdfDictionary && !(prop instanceof PdfLayer))
        addToBody((PdfDictionary)prop, (PdfIndirectReference)obj[1]); 
    } 
  }
  
  public PdfOutline getRootOutline() {
    return this.directContent.getRootOutline();
  }
  
  public void setOutlines(List<HashMap<String, Object>> outlines) {
    this.newBookmarks = outlines;
  }
  
  protected void writeOutlines(PdfDictionary catalog, boolean namedAsNames) throws IOException {
    if (this.newBookmarks == null || this.newBookmarks.isEmpty())
      return; 
    PdfDictionary top = new PdfDictionary();
    PdfIndirectReference topRef = getPdfIndirectReference();
    Object[] kids = SimpleBookmark.iterateOutlines(this, topRef, this.newBookmarks, namedAsNames);
    top.put(PdfName.FIRST, (PdfIndirectReference)kids[0]);
    top.put(PdfName.LAST, (PdfIndirectReference)kids[1]);
    top.put(PdfName.COUNT, new PdfNumber(((Integer)kids[2]).intValue()));
    addToBody(top, topRef);
    catalog.put(PdfName.OUTLINES, topRef);
  }
  
  public static final PdfName PDF_VERSION_1_2 = new PdfName("1.2");
  
  public static final PdfName PDF_VERSION_1_3 = new PdfName("1.3");
  
  public static final PdfName PDF_VERSION_1_4 = new PdfName("1.4");
  
  public static final PdfName PDF_VERSION_1_5 = new PdfName("1.5");
  
  public static final PdfName PDF_VERSION_1_6 = new PdfName("1.6");
  
  public static final PdfName PDF_VERSION_1_7 = new PdfName("1.7");
  
  protected PdfVersionImp pdf_version;
  
  public static final int PageLayoutSinglePage = 1;
  
  public static final int PageLayoutOneColumn = 2;
  
  public static final int PageLayoutTwoColumnLeft = 4;
  
  public static final int PageLayoutTwoColumnRight = 8;
  
  public static final int PageLayoutTwoPageLeft = 16;
  
  public static final int PageLayoutTwoPageRight = 32;
  
  public static final int PageModeUseNone = 64;
  
  public static final int PageModeUseOutlines = 128;
  
  public static final int PageModeUseThumbs = 256;
  
  public static final int PageModeFullScreen = 512;
  
  public static final int PageModeUseOC = 1024;
  
  public static final int PageModeUseAttachments = 2048;
  
  public static final int HideToolbar = 4096;
  
  public static final int HideMenubar = 8192;
  
  public static final int HideWindowUI = 16384;
  
  public static final int FitWindow = 32768;
  
  public static final int CenterWindow = 65536;
  
  public static final int DisplayDocTitle = 131072;
  
  public static final int NonFullScreenPageModeUseNone = 262144;
  
  public static final int NonFullScreenPageModeUseOutlines = 524288;
  
  public static final int NonFullScreenPageModeUseThumbs = 1048576;
  
  public static final int NonFullScreenPageModeUseOC = 2097152;
  
  public static final int DirectionL2R = 4194304;
  
  public static final int DirectionR2L = 8388608;
  
  public static final int PrintScalingNone = 16777216;
  
  public void setPdfVersion(char version) {
    this.pdf_version.setPdfVersion(version);
  }
  
  public void setAtLeastPdfVersion(char version) {
    this.pdf_version.setAtLeastPdfVersion(version);
  }
  
  public void setPdfVersion(PdfName version) {
    this.pdf_version.setPdfVersion(version);
  }
  
  public void addDeveloperExtension(PdfDeveloperExtension de) {
    this.pdf_version.addDeveloperExtension(de);
  }
  
  PdfVersionImp getPdfVersion() {
    return this.pdf_version;
  }
  
  public void setViewerPreferences(int preferences) {
    this.pdf.setViewerPreferences(preferences);
  }
  
  public void addViewerPreference(PdfName key, PdfObject value) {
    this.pdf.addViewerPreference(key, value);
  }
  
  public void setPageLabels(PdfPageLabels pageLabels) {
    this.pdf.setPageLabels(pageLabels);
  }
  
  public void addNamedDestinations(Map<String, String> map, int page_offset) {
    for (Map.Entry<String, String> entry : map.entrySet()) {
      String dest = entry.getValue();
      int page = Integer.parseInt(dest.substring(0, dest.indexOf(" ")));
      PdfDestination destination = new PdfDestination(dest.substring(dest.indexOf(" ") + 1));
      addNamedDestination(entry.getKey(), page + page_offset, destination);
    } 
  }
  
  public void addNamedDestination(String name, int page, PdfDestination dest) {
    dest.addPage(getPageReference(page));
    this.pdf.localDestination(name, dest);
  }
  
  public void addJavaScript(PdfAction js) {
    this.pdf.addJavaScript(js);
  }
  
  public void addJavaScript(String code, boolean unicode) {
    addJavaScript(PdfAction.javaScript(code, this, unicode));
  }
  
  public void addJavaScript(String code) {
    addJavaScript(code, false);
  }
  
  public void addJavaScript(String name, PdfAction js) {
    this.pdf.addJavaScript(name, js);
  }
  
  public void addJavaScript(String name, String code, boolean unicode) {
    addJavaScript(name, PdfAction.javaScript(code, this, unicode));
  }
  
  public void addJavaScript(String name, String code) {
    addJavaScript(name, code, false);
  }
  
  public void addFileAttachment(String description, byte[] fileStore, String file, String fileDisplay) throws IOException {
    addFileAttachment(description, PdfFileSpecification.fileEmbedded(this, file, fileDisplay, fileStore));
  }
  
  public void addFileAttachment(String description, PdfFileSpecification fs) throws IOException {
    this.pdf.addFileAttachment(description, fs);
  }
  
  public void addFileAttachment(PdfFileSpecification fs) throws IOException {
    addFileAttachment((String)null, fs);
  }
  
  public static final PdfName DOCUMENT_CLOSE = PdfName.WC;
  
  public static final PdfName WILL_SAVE = PdfName.WS;
  
  public static final PdfName DID_SAVE = PdfName.DS;
  
  public static final PdfName WILL_PRINT = PdfName.WP;
  
  public static final PdfName DID_PRINT = PdfName.DP;
  
  public static final int SIGNATURE_EXISTS = 1;
  
  public static final int SIGNATURE_APPEND_ONLY = 2;
  
  protected byte[] xmpMetadata;
  
  public static final int PDFXNONE = 0;
  
  public static final int PDFX1A2001 = 1;
  
  public static final int PDFX32002 = 2;
  
  public static final int PDFA1A = 3;
  
  public static final int PDFA1B = 4;
  
  private final PdfXConformanceImp pdfxConformance;
  
  public static final int STANDARD_ENCRYPTION_40 = 0;
  
  public static final int STANDARD_ENCRYPTION_128 = 1;
  
  public static final int ENCRYPTION_AES_128 = 2;
  
  public static final int ENCRYPTION_AES_256 = 3;
  
  static final int ENCRYPTION_MASK = 7;
  
  public static final int DO_NOT_ENCRYPT_METADATA = 8;
  
  public static final int EMBEDDED_FILES_ONLY = 24;
  
  public static final int ALLOW_PRINTING = 2052;
  
  public static final int ALLOW_MODIFY_CONTENTS = 8;
  
  public static final int ALLOW_COPY = 16;
  
  public static final int ALLOW_MODIFY_ANNOTATIONS = 32;
  
  public static final int ALLOW_FILL_IN = 256;
  
  public static final int ALLOW_SCREENREADERS = 512;
  
  public static final int ALLOW_ASSEMBLY = 1024;
  
  public static final int ALLOW_DEGRADED_PRINTING = 4;
  
  @Deprecated
  public static final int AllowPrinting = 2052;
  
  @Deprecated
  public static final int AllowModifyContents = 8;
  
  @Deprecated
  public static final int AllowCopy = 16;
  
  @Deprecated
  public static final int AllowModifyAnnotations = 32;
  
  @Deprecated
  public static final int AllowFillIn = 256;
  
  @Deprecated
  public static final int AllowScreenReaders = 512;
  
  @Deprecated
  public static final int AllowAssembly = 1024;
  
  @Deprecated
  public static final int AllowDegradedPrinting = 4;
  
  @Deprecated
  public static final boolean STRENGTH40BITS = false;
  
  @Deprecated
  public static final boolean STRENGTH128BITS = true;
  
  protected PdfEncryption crypto;
  
  protected boolean fullCompression;
  
  protected int compressionLevel;
  
  protected LinkedHashMap<BaseFont, FontDetails> documentFonts;
  
  protected int fontNumber;
  
  protected HashMap<PdfIndirectReference, Object[]> formXObjects;
  
  protected int formXObjectsCounter;
  
  protected HashMap<PdfReader, PdfReaderInstance> readerInstances;
  
  protected PdfReaderInstance currentPdfReaderInstance;
  
  protected HashMap<PdfSpotColor, ColorDetails> documentColors;
  
  protected int colorNumber;
  
  protected HashMap<PdfPatternPainter, PdfName> documentPatterns;
  
  protected int patternNumber;
  
  protected HashSet<PdfShadingPattern> documentShadingPatterns;
  
  protected HashSet<PdfShading> documentShadings;
  
  protected HashMap<PdfDictionary, PdfObject[]> documentExtGState;
  
  protected HashMap<Object, PdfObject[]> documentProperties;
  
  protected boolean tagged;
  
  protected PdfStructureTreeRoot structureTreeRoot;
  
  protected HashSet<PdfOCG> documentOCG;
  
  protected ArrayList<PdfOCG> documentOCGorder;
  
  protected PdfOCProperties OCProperties;
  
  protected PdfArray OCGRadioGroup;
  
  protected PdfArray OCGLocked;
  
  public void setOpenAction(String name) {
    this.pdf.setOpenAction(name);
  }
  
  public void setOpenAction(PdfAction action) {
    this.pdf.setOpenAction(action);
  }
  
  public void setAdditionalAction(PdfName actionType, PdfAction action) throws DocumentException {
    if (!actionType.equals(DOCUMENT_CLOSE) && !actionType.equals(WILL_SAVE) && !actionType.equals(DID_SAVE) && !actionType.equals(WILL_PRINT) && !actionType.equals(DID_PRINT))
      throw new DocumentException(MessageLocalization.getComposedMessage("invalid.additional.action.type.1", new Object[] { actionType.toString() })); 
    this.pdf.addAdditionalAction(actionType, action);
  }
  
  public void setCollection(PdfCollection collection) {
    setAtLeastPdfVersion('7');
    this.pdf.setCollection(collection);
  }
  
  public PdfAcroForm getAcroForm() {
    return this.pdf.getAcroForm();
  }
  
  public void addAnnotation(PdfAnnotation annot) {
    this.pdf.addAnnotation(annot);
  }
  
  void addAnnotation(PdfAnnotation annot, int page) {
    addAnnotation(annot);
  }
  
  public void addCalculationOrder(PdfFormField annot) {
    this.pdf.addCalculationOrder(annot);
  }
  
  public void setSigFlags(int f) {
    this.pdf.setSigFlags(f);
  }
  
  public void setXmpMetadata(byte[] xmpMetadata) {
    this.xmpMetadata = xmpMetadata;
  }
  
  public void setPageXmpMetadata(byte[] xmpMetadata) throws IOException {
    this.pdf.setXmpMetadata(xmpMetadata);
  }
  
  public void createXmpMetadata() {
    setXmpMetadata(createXmpMetadataBytes());
  }
  
  private byte[] createXmpMetadataBytes() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      XmpWriter xmp = new XmpWriter(baos, this.pdf.getInfo(), this.pdfxConformance.getPDFXConformance());
      xmp.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } 
    return baos.toByteArray();
  }
  
  public void setPDFXConformance(int pdfx) {
    if (this.pdfxConformance.getPDFXConformance() == pdfx)
      return; 
    if (this.pdf.isOpen())
      throw new PdfXConformanceException(MessageLocalization.getComposedMessage("pdfx.conformance.can.only.be.set.before.opening.the.document", new Object[0])); 
    if (this.crypto != null)
      throw new PdfXConformanceException(MessageLocalization.getComposedMessage("a.pdfx.conforming.document.cannot.be.encrypted", new Object[0])); 
    if (pdfx == 3 || pdfx == 4) {
      setPdfVersion('4');
    } else if (pdfx != 0) {
      setPdfVersion('3');
    } 
    this.pdfxConformance.setPDFXConformance(pdfx);
  }
  
  public int getPDFXConformance() {
    return this.pdfxConformance.getPDFXConformance();
  }
  
  public boolean isPdfX() {
    return this.pdfxConformance.isPdfX();
  }
  
  public void setOutputIntents(String outputConditionIdentifier, String outputCondition, String registryName, String info, ICC_Profile colorProfile) throws IOException {
    PdfName intentSubtype;
    getExtraCatalog();
    PdfDictionary out = new PdfDictionary(PdfName.OUTPUTINTENT);
    if (outputCondition != null)
      out.put(PdfName.OUTPUTCONDITION, new PdfString(outputCondition, "UnicodeBig")); 
    if (outputConditionIdentifier != null)
      out.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString(outputConditionIdentifier, "UnicodeBig")); 
    if (registryName != null)
      out.put(PdfName.REGISTRYNAME, new PdfString(registryName, "UnicodeBig")); 
    if (info != null)
      out.put(PdfName.INFO, new PdfString(info, "UnicodeBig")); 
    if (colorProfile != null) {
      PdfStream stream = new PdfICCBased(colorProfile, this.compressionLevel);
      out.put(PdfName.DESTOUTPUTPROFILE, addToBody(stream).getIndirectReference());
    } 
    if (this.pdfxConformance.isPdfA1() || "PDFA/1".equals(outputCondition)) {
      intentSubtype = PdfName.GTS_PDFA1;
    } else {
      intentSubtype = PdfName.GTS_PDFX;
    } 
    out.put(PdfName.S, intentSubtype);
    this.extraCatalog.put(PdfName.OUTPUTINTENTS, new PdfArray(out));
  }
  
  public void setOutputIntents(String outputConditionIdentifier, String outputCondition, String registryName, String info, byte[] destOutputProfile) throws IOException {
    ICC_Profile colorProfile = (destOutputProfile == null) ? null : ICC_Profile.getInstance(destOutputProfile);
    setOutputIntents(outputConditionIdentifier, outputCondition, registryName, info, colorProfile);
  }
  
  public boolean setOutputIntents(PdfReader reader, boolean checkExistence) throws IOException {
    PdfDictionary catalog = reader.getCatalog();
    PdfArray outs = catalog.getAsArray(PdfName.OUTPUTINTENTS);
    if (outs == null)
      return false; 
    if (outs.isEmpty())
      return false; 
    PdfDictionary out = outs.getAsDict(0);
    PdfObject obj = PdfReader.getPdfObject(out.get(PdfName.S));
    if (obj == null || !PdfName.GTS_PDFX.equals(obj))
      return false; 
    if (checkExistence)
      return true; 
    PRStream stream = (PRStream)PdfReader.getPdfObject(out.get(PdfName.DESTOUTPUTPROFILE));
    byte[] destProfile = null;
    if (stream != null)
      destProfile = PdfReader.getStreamBytes(stream); 
    setOutputIntents(getNameString(out, PdfName.OUTPUTCONDITIONIDENTIFIER), getNameString(out, PdfName.OUTPUTCONDITION), getNameString(out, PdfName.REGISTRYNAME), getNameString(out, PdfName.INFO), destProfile);
    return true;
  }
  
  private static String getNameString(PdfDictionary dic, PdfName key) {
    PdfObject obj = PdfReader.getPdfObject(dic.get(key));
    if (obj == null || !obj.isString())
      return null; 
    return ((PdfString)obj).toUnicodeString();
  }
  
  PdfEncryption getEncryption() {
    return this.crypto;
  }
  
  public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType) throws DocumentException {
    if (this.pdf.isOpen())
      throw new DocumentException(MessageLocalization.getComposedMessage("encryption.can.only.be.added.before.opening.the.document", new Object[0])); 
    this.crypto = new PdfEncryption();
    this.crypto.setCryptoMode(encryptionType, 0);
    this.crypto.setupAllKeys(userPassword, ownerPassword, permissions);
  }
  
  public void setEncryption(Certificate[] certs, int[] permissions, int encryptionType) throws DocumentException {
    if (this.pdf.isOpen())
      throw new DocumentException(MessageLocalization.getComposedMessage("encryption.can.only.be.added.before.opening.the.document", new Object[0])); 
    this.crypto = new PdfEncryption();
    if (certs != null)
      for (int i = 0; i < certs.length; i++)
        this.crypto.addRecipient(certs[i], permissions[i]);  
    this.crypto.setCryptoMode(encryptionType, 0);
    this.crypto.getEncryptionDictionary();
  }
  
  @Deprecated
  public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, boolean strength128Bits) throws DocumentException {
    setEncryption(userPassword, ownerPassword, permissions, strength128Bits ? 1 : 0);
  }
  
  @Deprecated
  public void setEncryption(boolean strength, String userPassword, String ownerPassword, int permissions) throws DocumentException {
    setEncryption(getISOBytes(userPassword), getISOBytes(ownerPassword), permissions, strength ? 1 : 0);
  }
  
  @Deprecated
  public void setEncryption(int encryptionType, String userPassword, String ownerPassword, int permissions) throws DocumentException {
    setEncryption(getISOBytes(userPassword), getISOBytes(ownerPassword), permissions, encryptionType);
  }
  
  public boolean isFullCompression() {
    return this.fullCompression;
  }
  
  public void setFullCompression() {
    this.fullCompression = true;
    setAtLeastPdfVersion('5');
  }
  
  public int getCompressionLevel() {
    return this.compressionLevel;
  }
  
  public void setCompressionLevel(int compressionLevel) {
    if (compressionLevel < 0 || compressionLevel > 9) {
      this.compressionLevel = -1;
    } else {
      this.compressionLevel = compressionLevel;
    } 
  }
  
  FontDetails addSimple(BaseFont bf) {
    if (bf.getFontType() == 4)
      return new FontDetails(new PdfName("F" + this.fontNumber++), ((DocumentFont)bf).getIndirectReference(), bf); 
    FontDetails ret = this.documentFonts.get(bf);
    if (ret == null) {
      PdfXConformanceImp.checkPDFXConformance(this, 4, bf);
      ret = new FontDetails(new PdfName("F" + this.fontNumber++), this.body.getPdfIndirectReference(), bf);
      this.documentFonts.put(bf, ret);
    } 
    return ret;
  }
  
  void eliminateFontSubset(PdfDictionary fonts) {
    for (FontDetails element : this.documentFonts.values()) {
      FontDetails ft = element;
      if (fonts.get(ft.getFontName()) != null)
        ft.setSubset(false); 
    } 
  }
  
  PdfName addDirectTemplateSimple(PdfTemplate template, PdfName forcedName) {
    PdfIndirectReference ref = template.getIndirectReference();
    Object[] obj = this.formXObjects.get(ref);
    PdfName name = null;
    try {
      if (obj == null) {
        if (forcedName == null) {
          name = new PdfName("Xf" + this.formXObjectsCounter);
          this.formXObjectsCounter++;
        } else {
          name = forcedName;
        } 
        if (template.getType() == 2) {
          PdfImportedPage ip = (PdfImportedPage)template;
          PdfReader r = ip.getPdfReaderInstance().getReader();
          if (!this.readerInstances.containsKey(r))
            this.readerInstances.put(r, ip.getPdfReaderInstance()); 
          template = null;
        } 
        this.formXObjects.put(ref, new Object[] { name, template });
      } else {
        name = (PdfName)obj[0];
      } 
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
    return name;
  }
  
  public void releaseTemplate(PdfTemplate tp) throws IOException {
    PdfIndirectReference ref = tp.getIndirectReference();
    Object[] objs = this.formXObjects.get(ref);
    if (objs == null || objs[1] == null)
      return; 
    PdfTemplate template = (PdfTemplate)objs[1];
    if (template.getIndirectReference() instanceof PRIndirectReference)
      return; 
    if (template.getType() == 1) {
      addToBody(template.getFormXObject(this.compressionLevel), template.getIndirectReference());
      objs[1] = null;
    } 
  }
  
  public PdfImportedPage getImportedPage(PdfReader reader, int pageNumber) {
    return getPdfReaderInstance(reader).getImportedPage(pageNumber);
  }
  
  protected PdfReaderInstance getPdfReaderInstance(PdfReader reader) {
    PdfReaderInstance inst = this.readerInstances.get(reader);
    if (inst == null) {
      inst = reader.getPdfReaderInstance(this);
      this.readerInstances.put(reader, inst);
    } 
    return inst;
  }
  
  public void freeReader(PdfReader reader) throws IOException {
    this.currentPdfReaderInstance = this.readerInstances.get(reader);
    if (this.currentPdfReaderInstance == null)
      return; 
    this.currentPdfReaderInstance.writeAllPages();
    this.currentPdfReaderInstance = null;
    this.readerInstances.remove(reader);
  }
  
  public int getCurrentDocumentSize() {
    return this.body.offset() + this.body.size() * 20 + 72;
  }
  
  protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
    if (this.currentPdfReaderInstance == null)
      this.currentPdfReaderInstance = getPdfReaderInstance(reader); 
    return this.currentPdfReaderInstance.getNewObjectNumber(number, generation);
  }
  
  RandomAccessFileOrArray getReaderFile(PdfReader reader) {
    return this.currentPdfReaderInstance.getReaderFile();
  }
  
  PdfName getColorspaceName() {
    return new PdfName("CS" + this.colorNumber++);
  }
  
  ColorDetails addSimple(PdfSpotColor spc) {
    ColorDetails ret = this.documentColors.get(spc);
    if (ret == null) {
      ret = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), spc);
      this.documentColors.put(spc, ret);
    } 
    return ret;
  }
  
  PdfName addSimplePattern(PdfPatternPainter painter) {
    PdfName name = this.documentPatterns.get(painter);
    try {
      if (name == null) {
        name = new PdfName("P" + this.patternNumber);
        this.patternNumber++;
        this.documentPatterns.put(painter, name);
      } 
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
    return name;
  }
  
  void addSimpleShadingPattern(PdfShadingPattern shading) {
    if (!this.documentShadingPatterns.contains(shading)) {
      shading.setName(this.patternNumber);
      this.patternNumber++;
      this.documentShadingPatterns.add(shading);
      addSimpleShading(shading.getShading());
    } 
  }
  
  void addSimpleShading(PdfShading shading) {
    if (!this.documentShadings.contains(shading)) {
      this.documentShadings.add(shading);
      shading.setName(this.documentShadings.size());
    } 
  }
  
  PdfObject[] addSimpleExtGState(PdfDictionary gstate) {
    if (!this.documentExtGState.containsKey(gstate)) {
      PdfXConformanceImp.checkPDFXConformance(this, 6, gstate);
      this.documentExtGState.put(gstate, new PdfObject[] { new PdfName("GS" + (this.documentExtGState.size() + 1)), getPdfIndirectReference() });
    } 
    return this.documentExtGState.get(gstate);
  }
  
  PdfObject[] addSimpleProperty(Object prop, PdfIndirectReference refi) {
    if (!this.documentProperties.containsKey(prop)) {
      if (prop instanceof PdfOCG)
        PdfXConformanceImp.checkPDFXConformance(this, 7, null); 
      this.documentProperties.put(prop, new PdfObject[] { new PdfName("Pr" + (this.documentProperties.size() + 1)), refi });
    } 
    return this.documentProperties.get(prop);
  }
  
  boolean propertyExists(Object prop) {
    return this.documentProperties.containsKey(prop);
  }
  
  public void setTagged() {
    if (this.open)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("tagging.must.be.set.before.opening.the.document", new Object[0])); 
    this.tagged = true;
  }
  
  public boolean isTagged() {
    return this.tagged;
  }
  
  public PdfStructureTreeRoot getStructureTreeRoot() {
    if (this.tagged && this.structureTreeRoot == null)
      this.structureTreeRoot = new PdfStructureTreeRoot(this); 
    return this.structureTreeRoot;
  }
  
  public PdfOCProperties getOCProperties() {
    fillOCProperties(true);
    return this.OCProperties;
  }
  
  public void addOCGRadioGroup(ArrayList<PdfLayer> group) {
    PdfArray ar = new PdfArray();
    for (int k = 0; k < group.size(); k++) {
      PdfLayer layer = group.get(k);
      if (layer.getTitle() == null)
        ar.add(layer.getRef()); 
    } 
    if (ar.size() == 0)
      return; 
    this.OCGRadioGroup.add(ar);
  }
  
  public void lockLayer(PdfLayer layer) {
    this.OCGLocked.add(layer.getRef());
  }
  
  private static void getOCGOrder(PdfArray order, PdfLayer layer) {
    if (!layer.isOnPanel())
      return; 
    if (layer.getTitle() == null)
      order.add(layer.getRef()); 
    ArrayList<PdfLayer> children = layer.getChildren();
    if (children == null)
      return; 
    PdfArray kids = new PdfArray();
    if (layer.getTitle() != null)
      kids.add(new PdfString(layer.getTitle(), "UnicodeBig")); 
    for (int k = 0; k < children.size(); k++)
      getOCGOrder(kids, children.get(k)); 
    if (kids.size() > 0)
      order.add(kids); 
  }
  
  private void addASEvent(PdfName event, PdfName category) {
    PdfArray arr = new PdfArray();
    for (PdfOCG element : this.documentOCG) {
      PdfLayer layer = (PdfLayer)element;
      PdfDictionary usage = layer.getAsDict(PdfName.USAGE);
      if (usage != null && usage.get(category) != null)
        arr.add(layer.getRef()); 
    } 
    if (arr.size() == 0)
      return; 
    PdfDictionary d = this.OCProperties.getAsDict(PdfName.D);
    PdfArray arras = d.getAsArray(PdfName.AS);
    if (arras == null) {
      arras = new PdfArray();
      d.put(PdfName.AS, arras);
    } 
    PdfDictionary as = new PdfDictionary();
    as.put(PdfName.EVENT, event);
    as.put(PdfName.CATEGORY, new PdfArray(category));
    as.put(PdfName.OCGS, arr);
    arras.add(as);
  }
  
  protected void fillOCProperties(boolean erase) {
    if (this.OCProperties == null)
      this.OCProperties = new PdfOCProperties(); 
    if (erase) {
      this.OCProperties.remove(PdfName.OCGS);
      this.OCProperties.remove(PdfName.D);
    } 
    if (this.OCProperties.get(PdfName.OCGS) == null) {
      PdfArray pdfArray = new PdfArray();
      for (PdfOCG element : this.documentOCG) {
        PdfLayer layer = (PdfLayer)element;
        pdfArray.add(layer.getRef());
      } 
      this.OCProperties.put(PdfName.OCGS, pdfArray);
    } 
    if (this.OCProperties.get(PdfName.D) != null)
      return; 
    ArrayList<PdfOCG> docOrder = new ArrayList<PdfOCG>(this.documentOCGorder);
    for (Iterator<PdfOCG> it = docOrder.iterator(); it.hasNext(); ) {
      PdfLayer layer = (PdfLayer)it.next();
      if (layer.getParent() != null)
        it.remove(); 
    } 
    PdfArray order = new PdfArray();
    for (PdfOCG element : docOrder) {
      PdfLayer layer = (PdfLayer)element;
      getOCGOrder(order, layer);
    } 
    PdfDictionary d = new PdfDictionary();
    this.OCProperties.put(PdfName.D, d);
    d.put(PdfName.ORDER, order);
    PdfArray gr = new PdfArray();
    for (PdfOCG element : this.documentOCG) {
      PdfLayer layer = (PdfLayer)element;
      if (!layer.isOn())
        gr.add(layer.getRef()); 
    } 
    if (gr.size() > 0)
      d.put(PdfName.OFF, gr); 
    if (this.OCGRadioGroup.size() > 0)
      d.put(PdfName.RBGROUPS, this.OCGRadioGroup); 
    if (this.OCGLocked.size() > 0)
      d.put(PdfName.LOCKED, this.OCGLocked); 
    addASEvent(PdfName.VIEW, PdfName.ZOOM);
    addASEvent(PdfName.VIEW, PdfName.VIEW);
    addASEvent(PdfName.PRINT, PdfName.PRINT);
    addASEvent(PdfName.EXPORT, PdfName.EXPORT);
    d.put(PdfName.LISTMODE, PdfName.VISIBLEPAGES);
  }
  
  void registerLayer(PdfOCG layer) {
    PdfXConformanceImp.checkPDFXConformance(this, 7, null);
    if (layer instanceof PdfLayer) {
      PdfLayer la = (PdfLayer)layer;
      if (la.getTitle() == null) {
        if (!this.documentOCG.contains(layer)) {
          this.documentOCG.add(layer);
          this.documentOCGorder.add(layer);
        } 
      } else {
        this.documentOCGorder.add(layer);
      } 
    } else {
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("only.pdflayer.is.accepted", new Object[0]));
    } 
  }
  
  public Rectangle getPageSize() {
    return this.pdf.getPageSize();
  }
  
  public void setCropBoxSize(Rectangle crop) {
    this.pdf.setCropBoxSize(crop);
  }
  
  public void setBoxSize(String boxName, Rectangle size) {
    this.pdf.setBoxSize(boxName, size);
  }
  
  public Rectangle getBoxSize(String boxName) {
    return this.pdf.getBoxSize(boxName);
  }
  
  public void setPageEmpty(boolean pageEmpty) {
    if (pageEmpty)
      return; 
    this.pdf.setPageEmpty(pageEmpty);
  }
  
  public boolean isPageEmpty() {
    return this.pdf.isPageEmpty();
  }
  
  public static final PdfName PAGE_OPEN = PdfName.O;
  
  public static final PdfName PAGE_CLOSE = PdfName.C;
  
  protected PdfDictionary group;
  
  public static final float SPACE_CHAR_RATIO_DEFAULT = 2.5F;
  
  public static final float NO_SPACE_CHAR_RATIO = 1.0E7F;
  
  private float spaceCharRatio;
  
  public static final int RUN_DIRECTION_DEFAULT = 0;
  
  public static final int RUN_DIRECTION_NO_BIDI = 1;
  
  public static final int RUN_DIRECTION_LTR = 2;
  
  public static final int RUN_DIRECTION_RTL = 3;
  
  protected int runDirection;
  
  protected PdfDictionary defaultColorspace;
  
  protected HashMap<ColorDetails, ColorDetails> documentSpotPatterns;
  
  protected ColorDetails patternColorspaceRGB;
  
  protected ColorDetails patternColorspaceGRAY;
  
  protected ColorDetails patternColorspaceCMYK;
  
  protected PdfDictionary imageDictionary;
  
  private final HashMap<Long, PdfName> images;
  
  protected HashMap<PdfStream, PdfIndirectReference> JBIG2Globals;
  
  private boolean userProperties;
  
  private boolean rgbTransparencyBlending;
  
  public void setPageAction(PdfName actionType, PdfAction action) throws DocumentException {
    if (!actionType.equals(PAGE_OPEN) && !actionType.equals(PAGE_CLOSE))
      throw new DocumentException(MessageLocalization.getComposedMessage("invalid.page.additional.action.type.1", new Object[] { actionType.toString() })); 
    this.pdf.setPageAction(actionType, action);
  }
  
  public void setDuration(int seconds) {
    this.pdf.setDuration(seconds);
  }
  
  public void setTransition(PdfTransition transition) {
    this.pdf.setTransition(transition);
  }
  
  public void setThumbnail(Image image) throws PdfException, DocumentException {
    this.pdf.setThumbnail(image);
  }
  
  public PdfDictionary getGroup() {
    return this.group;
  }
  
  public void setGroup(PdfDictionary group) {
    this.group = group;
  }
  
  public float getSpaceCharRatio() {
    return this.spaceCharRatio;
  }
  
  public void setSpaceCharRatio(float spaceCharRatio) {
    if (spaceCharRatio < 0.001F) {
      this.spaceCharRatio = 0.001F;
    } else {
      this.spaceCharRatio = spaceCharRatio;
    } 
  }
  
  public void setRunDirection(int runDirection) {
    if (runDirection < 1 || runDirection > 3)
      throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.run.direction.1", runDirection)); 
    this.runDirection = runDirection;
  }
  
  public int getRunDirection() {
    return this.runDirection;
  }
  
  public void setUserunit(float userunit) throws DocumentException {
    if (userunit < 1.0F || userunit > 75000.0F)
      throw new DocumentException(MessageLocalization.getComposedMessage("userunit.should.be.a.value.between.1.and.75000", new Object[0])); 
    addPageDictEntry(PdfName.USERUNIT, new PdfNumber(userunit));
    setAtLeastPdfVersion('6');
  }
  
  public PdfDictionary getDefaultColorspace() {
    return this.defaultColorspace;
  }
  
  public void setDefaultColorspace(PdfName key, PdfObject cs) {
    if (cs == null || cs.isNull())
      this.defaultColorspace.remove(key); 
    this.defaultColorspace.put(key, cs);
  }
  
  ColorDetails addSimplePatternColorspace(BaseColor color) {
    int type = ExtendedColor.getType(color);
    if (type == 4 || type == 5)
      throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.tile.pattern.can.not.have.another.pattern.or.shading.as.color", new Object[0])); 
    try {
      ColorDetails details;
      ColorDetails patternDetails;
      switch (type) {
        case 0:
          if (this.patternColorspaceRGB == null) {
            this.patternColorspaceRGB = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
            PdfArray array = new PdfArray(PdfName.PATTERN);
            array.add(PdfName.DEVICERGB);
            addToBody(array, this.patternColorspaceRGB.getIndirectReference());
          } 
          return this.patternColorspaceRGB;
        case 2:
          if (this.patternColorspaceCMYK == null) {
            this.patternColorspaceCMYK = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
            PdfArray array = new PdfArray(PdfName.PATTERN);
            array.add(PdfName.DEVICECMYK);
            addToBody(array, this.patternColorspaceCMYK.getIndirectReference());
          } 
          return this.patternColorspaceCMYK;
        case 1:
          if (this.patternColorspaceGRAY == null) {
            this.patternColorspaceGRAY = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
            PdfArray array = new PdfArray(PdfName.PATTERN);
            array.add(PdfName.DEVICEGRAY);
            addToBody(array, this.patternColorspaceGRAY.getIndirectReference());
          } 
          return this.patternColorspaceGRAY;
        case 3:
          details = addSimple(((SpotColor)color).getPdfSpotColor());
          patternDetails = this.documentSpotPatterns.get(details);
          if (patternDetails == null) {
            patternDetails = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
            PdfArray array = new PdfArray(PdfName.PATTERN);
            array.add(details.getIndirectReference());
            addToBody(array, patternDetails.getIndirectReference());
            this.documentSpotPatterns.put(details, patternDetails);
          } 
          return patternDetails;
      } 
      throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.color.type", new Object[0]));
    } catch (Exception e) {
      throw new RuntimeException(e.getMessage());
    } 
  }
  
  public boolean isStrictImageSequence() {
    return this.pdf.isStrictImageSequence();
  }
  
  public void setStrictImageSequence(boolean strictImageSequence) {
    this.pdf.setStrictImageSequence(strictImageSequence);
  }
  
  public void clearTextWrap() throws DocumentException {
    this.pdf.clearTextWrap();
  }
  
  public PdfName addDirectImageSimple(Image image) throws PdfException, DocumentException {
    return addDirectImageSimple(image, (PdfIndirectReference)null);
  }
  
  public PdfName addDirectImageSimple(Image image, PdfIndirectReference fixedRef) throws PdfException, DocumentException {
    PdfName name;
    if (this.images.containsKey(image.getMySerialId())) {
      name = this.images.get(image.getMySerialId());
    } else {
      if (image.isImgTemplate()) {
        name = new PdfName("img" + this.images.size());
        if (image instanceof ImgWMF)
          try {
            ImgWMF wmf = (ImgWMF)image;
            wmf.readWMF(PdfTemplate.createTemplate(this, 0.0F, 0.0F));
          } catch (Exception e) {
            throw new DocumentException(e);
          }  
      } else {
        PdfIndirectReference dref = image.getDirectReference();
        if (dref != null) {
          PdfName rname = new PdfName("img" + this.images.size());
          this.images.put(image.getMySerialId(), rname);
          this.imageDictionary.put(rname, dref);
          return rname;
        } 
        Image maskImage = image.getImageMask();
        PdfIndirectReference maskRef = null;
        if (maskImage != null) {
          PdfName mname = this.images.get(maskImage.getMySerialId());
          maskRef = getImageReference(mname);
        } 
        PdfImage i = new PdfImage(image, "img" + this.images.size(), maskRef);
        if (image instanceof ImgJBIG2) {
          byte[] globals = ((ImgJBIG2)image).getGlobalBytes();
          if (globals != null) {
            PdfDictionary decodeparms = new PdfDictionary();
            decodeparms.put(PdfName.JBIG2GLOBALS, getReferenceJBIG2Globals(globals));
            i.put(PdfName.DECODEPARMS, decodeparms);
          } 
        } 
        if (image.hasICCProfile()) {
          PdfICCBased icc = new PdfICCBased(image.getICCProfile(), image.getCompressionLevel());
          PdfIndirectReference iccRef = add(icc);
          PdfArray iccArray = new PdfArray();
          iccArray.add(PdfName.ICCBASED);
          iccArray.add(iccRef);
          PdfArray colorspace = i.getAsArray(PdfName.COLORSPACE);
          if (colorspace != null) {
            if (colorspace.size() > 1 && PdfName.INDEXED.equals(colorspace.getPdfObject(0))) {
              colorspace.set(1, iccArray);
            } else {
              i.put(PdfName.COLORSPACE, iccArray);
            } 
          } else {
            i.put(PdfName.COLORSPACE, iccArray);
          } 
        } 
        add(i, fixedRef);
        name = i.name();
      } 
      this.images.put(image.getMySerialId(), name);
    } 
    return name;
  }
  
  PdfIndirectReference add(PdfImage pdfImage, PdfIndirectReference fixedRef) throws PdfException {
    if (!this.imageDictionary.contains(pdfImage.name())) {
      PdfXConformanceImp.checkPDFXConformance(this, 5, pdfImage);
      if (fixedRef instanceof PRIndirectReference) {
        PRIndirectReference r2 = (PRIndirectReference)fixedRef;
        fixedRef = new PdfIndirectReference(0, getNewObjectNumber(r2.getReader(), r2.getNumber(), r2.getGeneration()));
      } 
      try {
        if (fixedRef == null) {
          fixedRef = addToBody(pdfImage).getIndirectReference();
        } else {
          addToBody(pdfImage, fixedRef);
        } 
      } catch (IOException ioe) {
        throw new ExceptionConverter(ioe);
      } 
      this.imageDictionary.put(pdfImage.name(), fixedRef);
      return fixedRef;
    } 
    return (PdfIndirectReference)this.imageDictionary.get(pdfImage.name());
  }
  
  PdfIndirectReference getImageReference(PdfName name) {
    return (PdfIndirectReference)this.imageDictionary.get(name);
  }
  
  protected PdfIndirectReference add(PdfICCBased icc) {
    PdfIndirectObject object;
    try {
      object = addToBody(icc);
    } catch (IOException ioe) {
      throw new ExceptionConverter(ioe);
    } 
    return object.getIndirectReference();
  }
  
  protected PdfIndirectReference getReferenceJBIG2Globals(byte[] content) {
    PdfIndirectObject ref;
    if (content == null)
      return null; 
    for (PdfStream pdfStream : this.JBIG2Globals.keySet()) {
      if (Arrays.equals(content, pdfStream.getBytes()))
        return this.JBIG2Globals.get(pdfStream); 
    } 
    PdfStream stream = new PdfStream(content);
    try {
      ref = addToBody(stream);
    } catch (IOException e) {
      return null;
    } 
    this.JBIG2Globals.put(stream, ref.getIndirectReference());
    return ref.getIndirectReference();
  }
  
  public boolean isUserProperties() {
    return this.userProperties;
  }
  
  public void setUserProperties(boolean userProperties) {
    this.userProperties = userProperties;
  }
  
  public boolean isRgbTransparencyBlending() {
    return this.rgbTransparencyBlending;
  }
  
  public void setRgbTransparencyBlending(boolean rgbTransparencyBlending) {
    this.rgbTransparencyBlending = rgbTransparencyBlending;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */