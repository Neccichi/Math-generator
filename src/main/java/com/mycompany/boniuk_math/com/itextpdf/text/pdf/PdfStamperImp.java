package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.exceptions.BadPasswordException;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection.PdfCollection;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal.PdfViewerPreferencesImp;
import com.mycompany.boniuk_math.com.itextpdf.text.xml.xmp.XmpReader;
import com.mycompany.boniuk_math.com.itextpdf.text.xml.xmp.XmpWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.xml.sax.SAXException;

class PdfStamperImp extends PdfWriter {
  HashMap<PdfReader, IntHashtable> readers2intrefs = new HashMap<PdfReader, IntHashtable>();
  
  HashMap<PdfReader, RandomAccessFileOrArray> readers2file = new HashMap<PdfReader, RandomAccessFileOrArray>();
  
  RandomAccessFileOrArray file;
  
  PdfReader reader;
  
  IntHashtable myXref = new IntHashtable();
  
  HashMap<PdfDictionary, PageStamp> pagesToContent = new HashMap<PdfDictionary, PageStamp>();
  
  boolean closed = false;
  
  private boolean rotateContents = true;
  
  protected AcroFields acroFields;
  
  protected boolean flat = false;
  
  protected boolean flatFreeText = false;
  
  protected int[] namePtr = new int[] { 0 };
  
  protected HashSet<String> partialFlattening = new HashSet<String>();
  
  protected boolean useVp = false;
  
  protected PdfViewerPreferencesImp viewerPreferences = new PdfViewerPreferencesImp();
  
  protected HashSet<PdfTemplate> fieldTemplates = new HashSet<PdfTemplate>();
  
  protected boolean fieldsAdded = false;
  
  protected int sigFlags = 0;
  
  protected boolean append;
  
  protected IntHashtable marked;
  
  protected int initialXrefSize;
  
  protected PdfAction openAction;
  
  PdfStamperImp(PdfReader reader, OutputStream os, char pdfVersion, boolean append) throws DocumentException, IOException {
    super(new PdfDocument(), os);
    if (!reader.isOpenedWithFullPermissions())
      throw new BadPasswordException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password", new Object[0])); 
    if (reader.isTampered())
      throw new DocumentException(MessageLocalization.getComposedMessage("the.original.document.was.reused.read.it.again.from.file", new Object[0])); 
    reader.setTampered(true);
    this.reader = reader;
    this.file = reader.getSafeFile();
    this.append = append;
    if (append) {
      if (reader.isRebuilt())
        throw new DocumentException(MessageLocalization.getComposedMessage("append.mode.requires.a.document.without.errors.even.if.recovery.was.possible", new Object[0])); 
      if (reader.isEncrypted())
        this.crypto = new PdfEncryption(reader.getDecrypt()); 
      this.pdf_version.setAppendmode(true);
      this.file.reOpen();
      byte[] buf = new byte[8192];
      int n;
      while ((n = this.file.read(buf)) > 0)
        this.os.write(buf, 0, n); 
      this.file.close();
      this.prevxref = reader.getLastXref();
      reader.setAppendable(true);
    } else if (pdfVersion == '\000') {
      setPdfVersion(reader.getPdfVersion());
    } else {
      setPdfVersion(pdfVersion);
    } 
    open();
    this.pdf.addWriter(this);
    if (append) {
      this.body.setRefnum(reader.getXrefSize());
      this.marked = new IntHashtable();
      if (reader.isNewXrefType())
        this.fullCompression = true; 
      if (reader.isHybridXref())
        this.fullCompression = false; 
    } 
    this.initialXrefSize = reader.getXrefSize();
  }
  
  void close(Map<String, String> moreInfo) throws IOException {
    if (this.closed)
      return; 
    if (this.useVp) {
      this.reader.setViewerPreferences(this.viewerPreferences);
      markUsed(this.reader.getTrailer().get(PdfName.ROOT));
    } 
    if (this.flat)
      flatFields(); 
    if (this.flatFreeText)
      flatFreeTextFields(); 
    addFieldResources();
    PdfDictionary catalog = this.reader.getCatalog();
    PdfDictionary pages = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.PAGES));
    pages.put(PdfName.ITXT, new PdfString(Document.getRelease()));
    markUsed(pages);
    PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), this.reader.getCatalog());
    if (this.acroFields != null && this.acroFields.getXfa().isChanged()) {
      markUsed(acroForm);
      if (!this.flat)
        this.acroFields.getXfa().setXfa(this); 
    } 
    if (this.sigFlags != 0 && 
      acroForm != null) {
      acroForm.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
      markUsed(acroForm);
      markUsed(catalog);
    } 
    this.closed = true;
    addSharedObjectsToBody();
    setOutlines();
    setJavaScript();
    addFileAttachments();
    if (this.openAction != null)
      catalog.put(PdfName.OPENACTION, this.openAction); 
    if (this.pdf.pageLabels != null)
      catalog.put(PdfName.PAGELABELS, this.pdf.pageLabels.getDictionary(this)); 
    if (!this.documentOCG.isEmpty()) {
      fillOCProperties(false);
      PdfDictionary ocdict = catalog.getAsDict(PdfName.OCPROPERTIES);
      if (ocdict == null) {
        this.reader.getCatalog().put(PdfName.OCPROPERTIES, this.OCProperties);
      } else {
        ocdict.put(PdfName.OCGS, this.OCProperties.get(PdfName.OCGS));
        PdfDictionary ddict = ocdict.getAsDict(PdfName.D);
        if (ddict == null) {
          ddict = new PdfDictionary();
          ocdict.put(PdfName.D, ddict);
        } 
        ddict.put(PdfName.ORDER, this.OCProperties.getAsDict(PdfName.D).get(PdfName.ORDER));
        ddict.put(PdfName.RBGROUPS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.RBGROUPS));
        ddict.put(PdfName.OFF, this.OCProperties.getAsDict(PdfName.D).get(PdfName.OFF));
        ddict.put(PdfName.AS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.AS));
      } 
    } 
    int skipInfo = -1;
    PdfObject oInfo = this.reader.getTrailer().get(PdfName.INFO);
    PRIndirectReference iInfo = null;
    PdfDictionary oldInfo = null;
    if (oInfo instanceof PRIndirectReference)
      iInfo = (PRIndirectReference)oInfo; 
    if (iInfo != null) {
      oldInfo = (PdfDictionary)PdfReader.getPdfObject(iInfo);
    } else if (oInfo instanceof PdfDictionary) {
      oldInfo = (PdfDictionary)oInfo;
    } 
    String producer = null;
    if (iInfo != null)
      skipInfo = iInfo.getNumber(); 
    if (oldInfo != null && oldInfo.get(PdfName.PRODUCER) != null)
      producer = oldInfo.getAsString(PdfName.PRODUCER).toUnicodeString(); 
    if (producer == null) {
      producer = Document.getVersion();
    } else if (producer.indexOf(Document.getProduct()) == -1) {
      StringBuffer buf = new StringBuffer(producer);
      buf.append("; modified using ");
      buf.append(Document.getVersion());
      producer = buf.toString();
    } 
    PdfIndirectReference info = null;
    PdfDictionary newInfo = new PdfDictionary();
    if (oldInfo != null)
      for (PdfName element : oldInfo.getKeys()) {
        PdfName key = element;
        PdfObject value = PdfReader.getPdfObject(oldInfo.get(key));
        newInfo.put(key, value);
      }  
    if (moreInfo != null)
      for (Map.Entry<String, String> entry : moreInfo.entrySet()) {
        String key = entry.getKey();
        PdfName keyName = new PdfName(key);
        String value = entry.getValue();
        if (value == null) {
          newInfo.remove(keyName);
          continue;
        } 
        newInfo.put(keyName, new PdfString(value, "UnicodeBig"));
      }  
    PdfDate date = new PdfDate();
    newInfo.put(PdfName.MODDATE, date);
    newInfo.put(PdfName.PRODUCER, new PdfString(producer, "UnicodeBig"));
    if (this.append) {
      if (iInfo == null) {
        info = addToBody(newInfo, false).getIndirectReference();
      } else {
        info = addToBody(newInfo, iInfo.getNumber(), false).getIndirectReference();
      } 
    } else {
      info = addToBody(newInfo, false).getIndirectReference();
    } 
    byte[] altMetadata = null;
    PdfObject xmpo = PdfReader.getPdfObject(catalog.get(PdfName.METADATA));
    if (xmpo != null && xmpo.isStream()) {
      altMetadata = PdfReader.getStreamBytesRaw((PRStream)xmpo);
      PdfReader.killIndirect(catalog.get(PdfName.METADATA));
    } 
    if (this.xmpMetadata != null)
      altMetadata = this.xmpMetadata; 
    if (altMetadata != null) {
      PdfStream pdfStream;
      try {
        XmpReader xmpr;
        if (moreInfo == null) {
          xmpr = new XmpReader(altMetadata);
          if (!xmpr.replaceNode("http://ns.adobe.com/pdf/1.3/", "Producer", producer) && !xmpr.replaceDescriptionAttribute("http://ns.adobe.com/pdf/1.3/", "Producer", producer))
            xmpr.add("rdf:Description", "http://ns.adobe.com/pdf/1.3/", "pdf:Producer", producer); 
          if (!xmpr.replaceNode("http://ns.adobe.com/xap/1.0/", "ModifyDate", date.getW3CDate()) && !xmpr.replaceDescriptionAttribute("http://ns.adobe.com/xap/1.0/", "ModifyDate", date.getW3CDate()))
            xmpr.add("rdf:Description", "http://ns.adobe.com/xap/1.0/", "xmp:ModifyDate", date.getW3CDate()); 
          if (xmpr.replaceNode("http://ns.adobe.com/xap/1.0/", "MetadataDate", date.getW3CDate()) || !xmpr.replaceDescriptionAttribute("http://ns.adobe.com/xap/1.0/", "MetadataDate", date.getW3CDate()));
        } else {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          try {
            XmpWriter xmpw = new XmpWriter(baos, newInfo, getPDFXConformance());
            xmpw.close();
          } catch (IOException ioe) {
            ioe.printStackTrace();
          } 
          xmpr = new XmpReader(baos.toByteArray());
        } 
        pdfStream = new PdfStream(xmpr.serializeDoc());
      } catch (SAXException e) {
        pdfStream = new PdfStream(altMetadata);
      } catch (IOException e) {
        pdfStream = new PdfStream(altMetadata);
      } 
      pdfStream.put(PdfName.TYPE, PdfName.METADATA);
      pdfStream.put(PdfName.SUBTYPE, PdfName.XML);
      if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
        PdfArray ar = new PdfArray();
        ar.add(PdfName.CRYPT);
        pdfStream.put(PdfName.FILTER, ar);
      } 
      if (this.append && xmpo != null) {
        this.body.add(pdfStream, xmpo.getIndRef());
      } else {
        catalog.put(PdfName.METADATA, this.body.add(pdfStream).getIndirectReference());
        markUsed(catalog);
      } 
    } 
    try {
      this.file.reOpen();
      alterContents();
      int rootN = ((PRIndirectReference)this.reader.trailer.get(PdfName.ROOT)).getNumber();
      if (this.append) {
        int[] keys = this.marked.getKeys();
        int k;
        for (k = 0; k < keys.length; k++) {
          int j = keys[k];
          PdfObject obj = this.reader.getPdfObjectRelease(j);
          if (obj != null && skipInfo != j && j < this.initialXrefSize)
            addToBody(obj, j, (j != rootN)); 
        } 
        for (k = this.initialXrefSize; k < this.reader.getXrefSize(); k++) {
          PdfObject obj = this.reader.getPdfObject(k);
          if (obj != null)
            addToBody(obj, getNewObjectNumber(this.reader, k, 0)); 
        } 
      } else {
        for (int k = 1; k < this.reader.getXrefSize(); k++) {
          PdfObject obj = this.reader.getPdfObjectRelease(k);
          if (obj != null && skipInfo != k)
            addToBody(obj, getNewObjectNumber(this.reader, k, 0), (k != rootN)); 
        } 
      } 
    } finally {
      try {
        this.file.close();
      } catch (Exception e) {}
    } 
    PdfIndirectReference encryption = null;
    PdfObject fileID = null;
    if (this.crypto != null) {
      if (this.append) {
        encryption = this.reader.getCryptoRef();
      } else {
        PdfIndirectObject encryptionObject = addToBody(this.crypto.getEncryptionDictionary(), false);
        encryption = encryptionObject.getIndirectReference();
      } 
      fileID = this.crypto.getFileID();
    } else {
      fileID = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
    } 
    PRIndirectReference iRoot = (PRIndirectReference)this.reader.trailer.get(PdfName.ROOT);
    PdfIndirectReference root = new PdfIndirectReference(0, getNewObjectNumber(this.reader, iRoot.getNumber(), 0));
    this.body.writeCrossReferenceTable(this.os, root, info, encryption, fileID, this.prevxref);
    if (this.fullCompression) {
      this.os.write(getISOBytes("startxref\n"));
      this.os.write(getISOBytes(String.valueOf(this.body.offset())));
      this.os.write(getISOBytes("\n%%EOF\n"));
    } else {
      PdfWriter.PdfTrailer trailer = new PdfWriter.PdfTrailer(this.body.size(), this.body.offset(), root, info, encryption, fileID, this.prevxref);
      trailer.toPdf(this, this.os);
    } 
    this.os.flush();
    if (isCloseStream())
      this.os.close(); 
    this.reader.close();
  }
  
  void applyRotation(PdfDictionary pageN, ByteBuffer out) {
    if (!this.rotateContents)
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
  
  void alterContents() throws IOException {
    for (PageStamp element : this.pagesToContent.values()) {
      PageStamp ps = element;
      PdfDictionary pageN = ps.pageN;
      markUsed(pageN);
      PdfArray ar = null;
      PdfObject content = PdfReader.getPdfObject(pageN.get(PdfName.CONTENTS), pageN);
      if (content == null) {
        ar = new PdfArray();
        pageN.put(PdfName.CONTENTS, ar);
      } else if (content.isArray()) {
        ar = (PdfArray)content;
        markUsed(ar);
      } else if (content.isStream()) {
        ar = new PdfArray();
        ar.add(pageN.get(PdfName.CONTENTS));
        pageN.put(PdfName.CONTENTS, ar);
      } else {
        ar = new PdfArray();
        pageN.put(PdfName.CONTENTS, ar);
      } 
      ByteBuffer out = new ByteBuffer();
      if (ps.under != null) {
        out.append(PdfContents.SAVESTATE);
        applyRotation(pageN, out);
        out.append(ps.under.getInternalBuffer());
        out.append(PdfContents.RESTORESTATE);
      } 
      if (ps.over != null)
        out.append(PdfContents.SAVESTATE); 
      PdfStream stream = new PdfStream(out.toByteArray());
      stream.flateCompress(this.compressionLevel);
      ar.addFirst(addToBody(stream).getIndirectReference());
      out.reset();
      if (ps.over != null) {
        out.append(' ');
        out.append(PdfContents.RESTORESTATE);
        ByteBuffer buf = ps.over.getInternalBuffer();
        out.append(buf.getBuffer(), 0, ps.replacePoint);
        out.append(PdfContents.SAVESTATE);
        applyRotation(pageN, out);
        out.append(buf.getBuffer(), ps.replacePoint, buf.size() - ps.replacePoint);
        out.append(PdfContents.RESTORESTATE);
        stream = new PdfStream(out.toByteArray());
        stream.flateCompress(this.compressionLevel);
        ar.add(addToBody(stream).getIndirectReference());
      } 
      alterResources(ps);
    } 
  }
  
  void alterResources(PageStamp ps) {
    ps.pageN.put(PdfName.RESOURCES, ps.pageResources.getResources());
  }
  
  protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
    IntHashtable ref = this.readers2intrefs.get(reader);
    if (ref != null) {
      int n = ref.get(number);
      if (n == 0) {
        n = getIndirectReferenceNumber();
        ref.put(number, n);
      } 
      return n;
    } 
    if (this.currentPdfReaderInstance == null) {
      if (this.append && number < this.initialXrefSize)
        return number; 
      int n = this.myXref.get(number);
      if (n == 0) {
        n = getIndirectReferenceNumber();
        this.myXref.put(number, n);
      } 
      return n;
    } 
    return this.currentPdfReaderInstance.getNewObjectNumber(number, generation);
  }
  
  RandomAccessFileOrArray getReaderFile(PdfReader reader) {
    if (this.readers2intrefs.containsKey(reader)) {
      RandomAccessFileOrArray raf = this.readers2file.get(reader);
      if (raf != null)
        return raf; 
      return reader.getSafeFile();
    } 
    if (this.currentPdfReaderInstance == null)
      return this.file; 
    return this.currentPdfReaderInstance.getReaderFile();
  }
  
  public void registerReader(PdfReader reader, boolean openFile) throws IOException {
    if (this.readers2intrefs.containsKey(reader))
      return; 
    this.readers2intrefs.put(reader, new IntHashtable());
    if (openFile) {
      RandomAccessFileOrArray raf = reader.getSafeFile();
      this.readers2file.put(reader, raf);
      raf.reOpen();
    } 
  }
  
  public void unRegisterReader(PdfReader reader) {
    if (!this.readers2intrefs.containsKey(reader))
      return; 
    this.readers2intrefs.remove(reader);
    RandomAccessFileOrArray raf = this.readers2file.get(reader);
    if (raf == null)
      return; 
    this.readers2file.remove(reader);
    try {
      raf.close();
    } catch (Exception e) {}
  }
  
  static void findAllObjects(PdfReader reader, PdfObject obj, IntHashtable hits) {
    PRIndirectReference iref;
    PdfArray a;
    int k;
    PdfDictionary dic;
    if (obj == null)
      return; 
    switch (obj.type()) {
      case 10:
        iref = (PRIndirectReference)obj;
        if (reader != iref.getReader())
          return; 
        if (hits.containsKey(iref.getNumber()))
          return; 
        hits.put(iref.getNumber(), 1);
        findAllObjects(reader, PdfReader.getPdfObject(obj), hits);
        return;
      case 5:
        a = (PdfArray)obj;
        for (k = 0; k < a.size(); k++)
          findAllObjects(reader, a.getPdfObject(k), hits); 
        return;
      case 6:
      case 7:
        dic = (PdfDictionary)obj;
        for (PdfName element : dic.getKeys()) {
          PdfName name = element;
          findAllObjects(reader, dic.get(name), hits);
        } 
        return;
    } 
  }
  
  public void addComments(FdfReader fdf) throws IOException {
    if (this.readers2intrefs.containsKey(fdf))
      return; 
    PdfDictionary catalog = fdf.getCatalog();
    catalog = catalog.getAsDict(PdfName.FDF);
    if (catalog == null)
      return; 
    PdfArray annots = catalog.getAsArray(PdfName.ANNOTS);
    if (annots == null || annots.size() == 0)
      return; 
    registerReader(fdf, false);
    IntHashtable hits = new IntHashtable();
    HashMap<String, PdfObject> irt = new HashMap<String, PdfObject>();
    ArrayList<PdfObject> an = new ArrayList<PdfObject>();
    for (int k = 0; k < annots.size(); k++) {
      PdfObject obj = annots.getPdfObject(k);
      PdfDictionary annot = (PdfDictionary)PdfReader.getPdfObject(obj);
      PdfNumber page = annot.getAsNumber(PdfName.PAGE);
      if (page != null && page.intValue() < this.reader.getNumberOfPages()) {
        findAllObjects(fdf, obj, hits);
        an.add(obj);
        if (obj.type() == 10) {
          PdfObject nm = PdfReader.getPdfObject(annot.get(PdfName.NM));
          if (nm != null && nm.type() == 3)
            irt.put(nm.toString(), obj); 
        } 
      } 
    } 
    int[] arhits = hits.getKeys();
    int i;
    for (i = 0; i < arhits.length; i++) {
      int n = arhits[i];
      PdfObject obj = fdf.getPdfObject(n);
      if (obj.type() == 6) {
        PdfObject str = PdfReader.getPdfObject(((PdfDictionary)obj).get(PdfName.IRT));
        if (str != null && str.type() == 3) {
          PdfObject pdfObject = irt.get(str.toString());
          if (pdfObject != null) {
            PdfDictionary dic2 = new PdfDictionary();
            dic2.merge((PdfDictionary)obj);
            dic2.put(PdfName.IRT, pdfObject);
            obj = dic2;
          } 
        } 
      } 
      addToBody(obj, getNewObjectNumber(fdf, n, 0));
    } 
    for (i = 0; i < an.size(); i++) {
      PdfObject obj = an.get(i);
      PdfDictionary annot = (PdfDictionary)PdfReader.getPdfObject(obj);
      PdfNumber page = annot.getAsNumber(PdfName.PAGE);
      PdfDictionary dic = this.reader.getPageN(page.intValue() + 1);
      PdfArray annotsp = (PdfArray)PdfReader.getPdfObject(dic.get(PdfName.ANNOTS), dic);
      if (annotsp == null) {
        annotsp = new PdfArray();
        dic.put(PdfName.ANNOTS, annotsp);
        markUsed(dic);
      } 
      markUsed(annotsp);
      annotsp.add(obj);
    } 
  }
  
  PageStamp getPageStamp(int pageNum) {
    PdfDictionary pageN = this.reader.getPageN(pageNum);
    PageStamp ps = this.pagesToContent.get(pageN);
    if (ps == null) {
      ps = new PageStamp(this, this.reader, pageN);
      this.pagesToContent.put(pageN, ps);
    } 
    return ps;
  }
  
  PdfContentByte getUnderContent(int pageNum) {
    if (pageNum < 1 || pageNum > this.reader.getNumberOfPages())
      return null; 
    PageStamp ps = getPageStamp(pageNum);
    if (ps.under == null)
      ps.under = new StampContent(this, ps); 
    return ps.under;
  }
  
  PdfContentByte getOverContent(int pageNum) {
    if (pageNum < 1 || pageNum > this.reader.getNumberOfPages())
      return null; 
    PageStamp ps = getPageStamp(pageNum);
    if (ps.over == null)
      ps.over = new StampContent(this, ps); 
    return ps.over;
  }
  
  void correctAcroFieldPages(int page) {
    if (this.acroFields == null)
      return; 
    if (page > this.reader.getNumberOfPages())
      return; 
    Map<String, AcroFields.Item> fields = this.acroFields.getFields();
    for (AcroFields.Item item : fields.values()) {
      for (int k = 0; k < item.size(); k++) {
        int p = item.getPage(k).intValue();
        if (p >= page)
          item.forcePage(k, p + 1); 
      } 
    } 
  }
  
  private static void moveRectangle(PdfDictionary dic2, PdfReader r, int pageImported, PdfName key, String name) {
    Rectangle m = r.getBoxSize(pageImported, name);
    if (m == null) {
      dic2.remove(key);
    } else {
      dic2.put(key, new PdfRectangle(m));
    } 
  }
  
  void replacePage(PdfReader r, int pageImported, int pageReplaced) {
    PdfDictionary pageN = this.reader.getPageN(pageReplaced);
    if (this.pagesToContent.containsKey(pageN))
      throw new IllegalStateException(MessageLocalization.getComposedMessage("this.page.cannot.be.replaced.new.content.was.already.added", new Object[0])); 
    PdfImportedPage p = getImportedPage(r, pageImported);
    PdfDictionary dic2 = this.reader.getPageNRelease(pageReplaced);
    dic2.remove(PdfName.RESOURCES);
    dic2.remove(PdfName.CONTENTS);
    moveRectangle(dic2, r, pageImported, PdfName.MEDIABOX, "media");
    moveRectangle(dic2, r, pageImported, PdfName.CROPBOX, "crop");
    moveRectangle(dic2, r, pageImported, PdfName.TRIMBOX, "trim");
    moveRectangle(dic2, r, pageImported, PdfName.ARTBOX, "art");
    moveRectangle(dic2, r, pageImported, PdfName.BLEEDBOX, "bleed");
    dic2.put(PdfName.ROTATE, new PdfNumber(r.getPageRotation(pageImported)));
    PdfContentByte cb = getOverContent(pageReplaced);
    cb.addTemplate(p, 0.0F, 0.0F);
    PageStamp ps = this.pagesToContent.get(pageN);
    ps.replacePoint = ps.over.getInternalBuffer().size();
  }
  
  void insertPage(int pageNumber, Rectangle mediabox) {
    PdfDictionary parent;
    PRIndirectReference parentRef;
    Rectangle media = new Rectangle(mediabox);
    int rotation = media.getRotation() % 360;
    PdfDictionary page = new PdfDictionary(PdfName.PAGE);
    PdfDictionary resources = new PdfDictionary();
    PdfArray procset = new PdfArray();
    procset.add(PdfName.PDF);
    procset.add(PdfName.TEXT);
    procset.add(PdfName.IMAGEB);
    procset.add(PdfName.IMAGEC);
    procset.add(PdfName.IMAGEI);
    resources.put(PdfName.PROCSET, procset);
    page.put(PdfName.RESOURCES, resources);
    page.put(PdfName.ROTATE, new PdfNumber(rotation));
    page.put(PdfName.MEDIABOX, new PdfRectangle(media, rotation));
    PRIndirectReference pref = this.reader.addPdfObject(page);
    if (pageNumber > this.reader.getNumberOfPages()) {
      PdfDictionary lastPage = this.reader.getPageNRelease(this.reader.getNumberOfPages());
      parentRef = (PRIndirectReference)lastPage.get(PdfName.PARENT);
      parentRef = new PRIndirectReference(this.reader, parentRef.getNumber());
      parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
      PdfArray kids = (PdfArray)PdfReader.getPdfObject(parent.get(PdfName.KIDS), parent);
      kids.add(pref);
      markUsed(kids);
      this.reader.pageRefs.insertPage(pageNumber, pref);
    } else {
      if (pageNumber < 1)
        pageNumber = 1; 
      PdfDictionary firstPage = this.reader.getPageN(pageNumber);
      PRIndirectReference firstPageRef = this.reader.getPageOrigRef(pageNumber);
      this.reader.releasePage(pageNumber);
      parentRef = (PRIndirectReference)firstPage.get(PdfName.PARENT);
      parentRef = new PRIndirectReference(this.reader, parentRef.getNumber());
      parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
      PdfArray kids = (PdfArray)PdfReader.getPdfObject(parent.get(PdfName.KIDS), parent);
      int len = kids.size();
      int num = firstPageRef.getNumber();
      for (int k = 0; k < len; k++) {
        PRIndirectReference cur = (PRIndirectReference)kids.getPdfObject(k);
        if (num == cur.getNumber()) {
          kids.add(k, pref);
          break;
        } 
      } 
      if (len == kids.size())
        throw new RuntimeException(MessageLocalization.getComposedMessage("internal.inconsistence", new Object[0])); 
      markUsed(kids);
      this.reader.pageRefs.insertPage(pageNumber, pref);
      correctAcroFieldPages(pageNumber);
    } 
    page.put(PdfName.PARENT, parentRef);
    while (parent != null) {
      markUsed(parent);
      PdfNumber count = (PdfNumber)PdfReader.getPdfObjectRelease(parent.get(PdfName.COUNT));
      parent.put(PdfName.COUNT, new PdfNumber(count.intValue() + 1));
      parent = parent.getAsDict(PdfName.PARENT);
    } 
  }
  
  boolean isRotateContents() {
    return this.rotateContents;
  }
  
  void setRotateContents(boolean rotateContents) {
    this.rotateContents = rotateContents;
  }
  
  boolean isContentWritten() {
    return (this.body.size() > 1);
  }
  
  AcroFields getAcroFields() {
    if (this.acroFields == null)
      this.acroFields = new AcroFields(this.reader, this); 
    return this.acroFields;
  }
  
  void setFormFlattening(boolean flat) {
    this.flat = flat;
  }
  
  void setFreeTextFlattening(boolean flat) {
    this.flatFreeText = flat;
  }
  
  boolean partialFormFlattening(String name) {
    getAcroFields();
    if (this.acroFields.getXfa().isXfaPresent())
      throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("partial.form.flattening.is.not.supported.with.xfa.forms", new Object[0])); 
    if (!this.acroFields.getFields().containsKey(name))
      return false; 
    this.partialFlattening.add(name);
    return true;
  }
  
  void flatFields() {
    if (this.append)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("field.flattening.is.not.supported.in.append.mode", new Object[0])); 
    getAcroFields();
    Map<String, AcroFields.Item> fields = this.acroFields.getFields();
    if (this.fieldsAdded && this.partialFlattening.isEmpty())
      for (String s : fields.keySet())
        this.partialFlattening.add(s);  
    PdfDictionary acroForm = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
    PdfArray acroFds = null;
    if (acroForm != null)
      acroFds = (PdfArray)PdfReader.getPdfObject(acroForm.get(PdfName.FIELDS), acroForm); 
    for (Map.Entry<String, AcroFields.Item> entry : fields.entrySet()) {
      String name = entry.getKey();
      if (!this.partialFlattening.isEmpty() && !this.partialFlattening.contains(name))
        continue; 
      AcroFields.Item item = entry.getValue();
      for (int k = 0; k < item.size(); k++) {
        PdfDictionary merged = item.getMerged(k);
        PdfNumber ff = merged.getAsNumber(PdfName.F);
        int flags = 0;
        if (ff != null)
          flags = ff.intValue(); 
        int page = item.getPage(k).intValue();
        PdfDictionary appDic = merged.getAsDict(PdfName.AP);
        if (appDic != null && (flags & 0x4) != 0 && (flags & 0x2) == 0) {
          PdfObject obj = appDic.get(PdfName.N);
          PdfAppearance app = null;
          if (obj != null) {
            PdfObject objReal = PdfReader.getPdfObject(obj);
            if (obj instanceof PdfIndirectReference && !obj.isIndirect()) {
              app = new PdfAppearance((PdfIndirectReference)obj);
            } else if (objReal instanceof PdfStream) {
              ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
              app = new PdfAppearance((PdfIndirectReference)obj);
            } else if (objReal != null && objReal.isDictionary()) {
              PdfName as = merged.getAsName(PdfName.AS);
              if (as != null) {
                PdfIndirectReference iref = (PdfIndirectReference)((PdfDictionary)objReal).get(as);
                if (iref != null) {
                  app = new PdfAppearance(iref);
                  if (iref.isIndirect()) {
                    objReal = PdfReader.getPdfObject(iref);
                    ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                  } 
                } 
              } 
            } 
          } 
          if (app != null) {
            Rectangle box = PdfReader.getNormalizedRectangle(merged.getAsArray(PdfName.RECT));
            PdfContentByte cb = getOverContent(page);
            cb.setLiteral("Q ");
            cb.addTemplate(app, box.getLeft(), box.getBottom());
            cb.setLiteral("q ");
          } 
        } 
        if (!this.partialFlattening.isEmpty()) {
          PdfDictionary pageDic = this.reader.getPageN(page);
          PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
          if (annots != null) {
            for (int idx = 0; idx < annots.size(); idx++) {
              PdfObject ran = annots.getPdfObject(idx);
              if (ran.isIndirect()) {
                PdfObject ran2 = item.getWidgetRef(k);
                if (ran2.isIndirect())
                  if (((PRIndirectReference)ran).getNumber() == ((PRIndirectReference)ran2).getNumber()) {
                    annots.remove(idx--);
                    PRIndirectReference wdref = (PRIndirectReference)ran2;
                    while (true) {
                      PdfDictionary wd = (PdfDictionary)PdfReader.getPdfObject(wdref);
                      PRIndirectReference parentRef = (PRIndirectReference)wd.get(PdfName.PARENT);
                      PdfReader.killIndirect(wdref);
                      if (parentRef == null) {
                        for (int i = 0; i < acroFds.size(); i++) {
                          PdfObject h = acroFds.getPdfObject(i);
                          if (h.isIndirect() && ((PRIndirectReference)h).getNumber() == wdref.getNumber()) {
                            acroFds.remove(i);
                            i--;
                          } 
                        } 
                        break;
                      } 
                      PdfDictionary parent = (PdfDictionary)PdfReader.getPdfObject(parentRef);
                      PdfArray kids = parent.getAsArray(PdfName.KIDS);
                      for (int fr = 0; fr < kids.size(); fr++) {
                        PdfObject h = kids.getPdfObject(fr);
                        if (h.isIndirect() && ((PRIndirectReference)h).getNumber() == wdref.getNumber()) {
                          kids.remove(fr);
                          fr--;
                        } 
                      } 
                      if (!kids.isEmpty())
                        break; 
                      wdref = parentRef;
                    } 
                  }  
              } 
            } 
            if (annots.isEmpty()) {
              PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
              pageDic.remove(PdfName.ANNOTS);
            } 
          } 
        } 
      } 
    } 
    if (!this.fieldsAdded && this.partialFlattening.isEmpty()) {
      for (int page = 1; page <= this.reader.getNumberOfPages(); page++) {
        PdfDictionary pageDic = this.reader.getPageN(page);
        PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
        if (annots != null) {
          for (int idx = 0; idx < annots.size(); idx++) {
            PdfObject annoto = annots.getDirectObject(idx);
            if (!(annoto instanceof PdfIndirectReference) || annoto.isIndirect())
              if (!annoto.isDictionary() || PdfName.WIDGET.equals(((PdfDictionary)annoto).get(PdfName.SUBTYPE))) {
                annots.remove(idx);
                idx--;
              }  
          } 
          if (annots.isEmpty()) {
            PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
            pageDic.remove(PdfName.ANNOTS);
          } 
        } 
      } 
      eliminateAcroformObjects();
    } 
  }
  
  void eliminateAcroformObjects() {
    PdfObject acro = this.reader.getCatalog().get(PdfName.ACROFORM);
    if (acro == null)
      return; 
    PdfDictionary acrodic = (PdfDictionary)PdfReader.getPdfObject(acro);
    this.reader.killXref(acrodic.get(PdfName.XFA));
    acrodic.remove(PdfName.XFA);
    PdfObject iFields = acrodic.get(PdfName.FIELDS);
    if (iFields != null) {
      PdfDictionary kids = new PdfDictionary();
      kids.put(PdfName.KIDS, iFields);
      sweepKids(kids);
      PdfReader.killIndirect(iFields);
      acrodic.put(PdfName.FIELDS, new PdfArray());
    } 
    acrodic.remove(PdfName.SIGFLAGS);
  }
  
  void sweepKids(PdfObject obj) {
    PdfObject oo = PdfReader.killIndirect(obj);
    if (oo == null || !oo.isDictionary())
      return; 
    PdfDictionary dic = (PdfDictionary)oo;
    PdfArray kids = (PdfArray)PdfReader.killIndirect(dic.get(PdfName.KIDS));
    if (kids == null)
      return; 
    for (int k = 0; k < kids.size(); k++)
      sweepKids(kids.getPdfObject(k)); 
  }
  
  private void flatFreeTextFields() {
    if (this.append)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("freetext.flattening.is.not.supported.in.append.mode", new Object[0])); 
    for (int page = 1; page <= this.reader.getNumberOfPages(); page++) {
      PdfDictionary pageDic = this.reader.getPageN(page);
      PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
      if (annots != null) {
        int idx;
        for (idx = 0; idx < annots.size(); idx++) {
          PdfObject annoto = annots.getDirectObject(idx);
          if (!(annoto instanceof PdfIndirectReference) || annoto.isIndirect()) {
            PdfDictionary annDic = (PdfDictionary)annoto;
            if (((PdfName)annDic.get(PdfName.SUBTYPE)).equals(PdfName.FREETEXT)) {
              PdfNumber ff = annDic.getAsNumber(PdfName.F);
              int flags = (ff != null) ? ff.intValue() : 0;
              if ((flags & 0x4) != 0 && (flags & 0x2) == 0) {
                PdfObject obj1 = annDic.get(PdfName.AP);
                if (obj1 != null) {
                  PdfDictionary appDic = (obj1 instanceof PdfIndirectReference) ? (PdfDictionary)PdfReader.getPdfObject(obj1) : (PdfDictionary)obj1;
                  PdfObject obj = appDic.get(PdfName.N);
                  PdfAppearance app = null;
                  PdfObject objReal = PdfReader.getPdfObject(obj);
                  if (obj instanceof PdfIndirectReference && !obj.isIndirect()) {
                    app = new PdfAppearance((PdfIndirectReference)obj);
                  } else if (objReal instanceof PdfStream) {
                    ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                    app = new PdfAppearance((PdfIndirectReference)obj);
                  } else if (objReal.isDictionary()) {
                    PdfName as_p = appDic.getAsName(PdfName.AS);
                    if (as_p != null) {
                      PdfIndirectReference iref = (PdfIndirectReference)((PdfDictionary)objReal).get(as_p);
                      if (iref != null) {
                        app = new PdfAppearance(iref);
                        if (iref.isIndirect()) {
                          objReal = PdfReader.getPdfObject(iref);
                          ((PdfDictionary)objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                        } 
                      } 
                    } 
                  } 
                  if (app != null) {
                    Rectangle box = PdfReader.getNormalizedRectangle(annDic.getAsArray(PdfName.RECT));
                    PdfContentByte cb = getOverContent(page);
                    cb.setLiteral("Q ");
                    cb.addTemplate(app, box.getLeft(), box.getBottom());
                    cb.setLiteral("q ");
                  } 
                } 
              } 
            } 
          } 
        } 
        for (idx = 0; idx < annots.size(); idx++) {
          PdfDictionary annot = annots.getAsDict(idx);
          if (annot != null)
            if (PdfName.FREETEXT.equals(annot.get(PdfName.SUBTYPE))) {
              annots.remove(idx);
              idx--;
            }  
        } 
        if (annots.isEmpty()) {
          PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
          pageDic.remove(PdfName.ANNOTS);
        } 
      } 
    } 
  }
  
  public PdfIndirectReference getPageReference(int page) {
    PdfIndirectReference ref = this.reader.getPageOrigRef(page);
    if (ref == null)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.page.number.1", page)); 
    return ref;
  }
  
  public void addAnnotation(PdfAnnotation annot) {
    throw new RuntimeException(MessageLocalization.getComposedMessage("unsupported.in.this.context.use.pdfstamper.addannotation", new Object[0]));
  }
  
  void addDocumentField(PdfIndirectReference ref) {
    PdfDictionary catalog = this.reader.getCatalog();
    PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
    if (acroForm == null) {
      acroForm = new PdfDictionary();
      catalog.put(PdfName.ACROFORM, acroForm);
      markUsed(catalog);
    } 
    PdfArray fields = (PdfArray)PdfReader.getPdfObject(acroForm.get(PdfName.FIELDS), acroForm);
    if (fields == null) {
      fields = new PdfArray();
      acroForm.put(PdfName.FIELDS, fields);
      markUsed(acroForm);
    } 
    if (!acroForm.contains(PdfName.DA)) {
      acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
      markUsed(acroForm);
    } 
    fields.add(ref);
    markUsed(fields);
  }
  
  void addFieldResources() throws IOException {
    if (this.fieldTemplates.isEmpty())
      return; 
    PdfDictionary catalog = this.reader.getCatalog();
    PdfDictionary acroForm = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
    if (acroForm == null) {
      acroForm = new PdfDictionary();
      catalog.put(PdfName.ACROFORM, acroForm);
      markUsed(catalog);
    } 
    PdfDictionary dr = (PdfDictionary)PdfReader.getPdfObject(acroForm.get(PdfName.DR), acroForm);
    if (dr == null) {
      dr = new PdfDictionary();
      acroForm.put(PdfName.DR, dr);
      markUsed(acroForm);
    } 
    markUsed(dr);
    for (PdfTemplate template : this.fieldTemplates)
      PdfFormField.mergeResources(dr, (PdfDictionary)template.getResources(), this); 
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
    if (acroForm.get(PdfName.DA) == null) {
      acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
      markUsed(acroForm);
    } 
  }
  
  void expandFields(PdfFormField field, ArrayList<PdfAnnotation> allAnnots) {
    allAnnots.add(field);
    ArrayList<PdfFormField> kids = field.getKids();
    if (kids != null)
      for (int k = 0; k < kids.size(); k++)
        expandFields(kids.get(k), allAnnots);  
  }
  
  void addAnnotation(PdfAnnotation annot, PdfDictionary pageN) {
    try {
      ArrayList<PdfAnnotation> allAnnots = new ArrayList<PdfAnnotation>();
      if (annot.isForm()) {
        this.fieldsAdded = true;
        getAcroFields();
        PdfFormField field = (PdfFormField)annot;
        if (field.getParent() != null)
          return; 
        expandFields(field, allAnnots);
      } else {
        allAnnots.add(annot);
      } 
      for (int k = 0; k < allAnnots.size(); k++) {
        annot = allAnnots.get(k);
        if (annot.getPlaceInPage() > 0)
          pageN = this.reader.getPageN(annot.getPlaceInPage()); 
        if (annot.isForm()) {
          if (!annot.isUsed()) {
            HashSet<PdfTemplate> templates = annot.getTemplates();
            if (templates != null)
              this.fieldTemplates.addAll(templates); 
          } 
          PdfFormField field = (PdfFormField)annot;
          if (field.getParent() == null)
            addDocumentField(field.getIndirectReference()); 
        } 
        if (annot.isAnnotation()) {
          PdfObject pdfobj = PdfReader.getPdfObject(pageN.get(PdfName.ANNOTS), pageN);
          PdfArray annots = null;
          if (pdfobj == null || !pdfobj.isArray()) {
            annots = new PdfArray();
            pageN.put(PdfName.ANNOTS, annots);
            markUsed(pageN);
          } else {
            annots = (PdfArray)pdfobj;
          } 
          annots.add(annot.getIndirectReference());
          markUsed(annots);
          if (!annot.isUsed()) {
            PdfRectangle rect = (PdfRectangle)annot.get(PdfName.RECT);
            if (rect != null && (rect.left() != 0.0F || rect.right() != 0.0F || rect.top() != 0.0F || rect.bottom() != 0.0F)) {
              int rotation = this.reader.getPageRotation(pageN);
              Rectangle pageSize = this.reader.getPageSizeWithRotation(pageN);
              switch (rotation) {
                case 90:
                  annot.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.top(), rect.right(), pageSize.getTop() - rect.bottom(), rect.left()));
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
          addToBody(annot, annot.getIndirectReference());
        } 
      } 
    } catch (IOException e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  void addAnnotation(PdfAnnotation annot, int page) {
    annot.setPage(page);
    addAnnotation(annot, this.reader.getPageN(page));
  }
  
  private void outlineTravel(PRIndirectReference outline) {
    while (outline != null) {
      PdfDictionary outlineR = (PdfDictionary)PdfReader.getPdfObjectRelease(outline);
      PRIndirectReference first = (PRIndirectReference)outlineR.get(PdfName.FIRST);
      if (first != null)
        outlineTravel(first); 
      PdfReader.killIndirect(outlineR.get(PdfName.DEST));
      PdfReader.killIndirect(outlineR.get(PdfName.A));
      PdfReader.killIndirect(outline);
      outline = (PRIndirectReference)outlineR.get(PdfName.NEXT);
    } 
  }
  
  void deleteOutlines() {
    PdfDictionary catalog = this.reader.getCatalog();
    PRIndirectReference outlines = (PRIndirectReference)catalog.get(PdfName.OUTLINES);
    if (outlines == null)
      return; 
    outlineTravel(outlines);
    PdfReader.killIndirect(outlines);
    catalog.remove(PdfName.OUTLINES);
    markUsed(catalog);
  }
  
  void setJavaScript() throws IOException {
    HashMap<String, PdfObject> djs = this.pdf.getDocumentLevelJS();
    if (djs.isEmpty())
      return; 
    PdfDictionary catalog = this.reader.getCatalog();
    PdfDictionary names = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
    if (names == null) {
      names = new PdfDictionary();
      catalog.put(PdfName.NAMES, names);
      markUsed(catalog);
    } 
    markUsed(names);
    PdfDictionary tree = PdfNameTree.writeTree(djs, this);
    names.put(PdfName.JAVASCRIPT, addToBody(tree).getIndirectReference());
  }
  
  void addFileAttachments() throws IOException {
    HashMap<String, PdfObject> fs = this.pdf.getDocumentFileAttachment();
    if (fs.isEmpty())
      return; 
    PdfDictionary catalog = this.reader.getCatalog();
    PdfDictionary names = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
    if (names == null) {
      names = new PdfDictionary();
      catalog.put(PdfName.NAMES, names);
      markUsed(catalog);
    } 
    markUsed(names);
    HashMap<String, PdfObject> old = PdfNameTree.readTree((PdfDictionary)PdfReader.getPdfObjectRelease(names.get(PdfName.EMBEDDEDFILES)));
    for (Map.Entry<String, PdfObject> entry : fs.entrySet()) {
      String name = entry.getKey();
      int k = 0;
      StringBuilder nn = new StringBuilder(name);
      while (old.containsKey(nn.toString())) {
        k++;
        nn.append(" ").append(k);
      } 
      old.put(nn.toString(), entry.getValue());
    } 
    PdfDictionary tree = PdfNameTree.writeTree(old, this);
    PdfObject oldEmbeddedFiles = names.get(PdfName.EMBEDDEDFILES);
    if (oldEmbeddedFiles != null)
      PdfReader.killIndirect(oldEmbeddedFiles); 
    names.put(PdfName.EMBEDDEDFILES, addToBody(tree).getIndirectReference());
  }
  
  void makePackage(PdfCollection collection) {
    PdfDictionary catalog = this.reader.getCatalog();
    catalog.put(PdfName.COLLECTION, (PdfObject)collection);
  }
  
  void setOutlines() throws IOException {
    if (this.newBookmarks == null)
      return; 
    deleteOutlines();
    if (this.newBookmarks.isEmpty())
      return; 
    PdfDictionary catalog = this.reader.getCatalog();
    boolean namedAsNames = (catalog.get(PdfName.DESTS) != null);
    writeOutlines(catalog, namedAsNames);
    markUsed(catalog);
  }
  
  public void setViewerPreferences(int preferences) {
    this.useVp = true;
    this.viewerPreferences.setViewerPreferences(preferences);
  }
  
  public void addViewerPreference(PdfName key, PdfObject value) {
    this.useVp = true;
    this.viewerPreferences.addViewerPreference(key, value);
  }
  
  public void setSigFlags(int f) {
    this.sigFlags |= f;
  }
  
  public void setPageAction(PdfName actionType, PdfAction action) throws PdfException {
    throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page", new Object[0]));
  }
  
  void setPageAction(PdfName actionType, PdfAction action, int page) throws PdfException {
    if (!actionType.equals(PAGE_OPEN) && !actionType.equals(PAGE_CLOSE))
      throw new PdfException(MessageLocalization.getComposedMessage("invalid.page.additional.action.type.1", new Object[] { actionType.toString() })); 
    PdfDictionary pg = this.reader.getPageN(page);
    PdfDictionary aa = (PdfDictionary)PdfReader.getPdfObject(pg.get(PdfName.AA), pg);
    if (aa == null) {
      aa = new PdfDictionary();
      pg.put(PdfName.AA, aa);
      markUsed(pg);
    } 
    aa.put(actionType, action);
    markUsed(aa);
  }
  
  public void setDuration(int seconds) {
    throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page", new Object[0]));
  }
  
  public void setTransition(PdfTransition transition) {
    throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page", new Object[0]));
  }
  
  void setDuration(int seconds, int page) {
    PdfDictionary pg = this.reader.getPageN(page);
    if (seconds < 0) {
      pg.remove(PdfName.DUR);
    } else {
      pg.put(PdfName.DUR, new PdfNumber(seconds));
    } 
    markUsed(pg);
  }
  
  void setTransition(PdfTransition transition, int page) {
    PdfDictionary pg = this.reader.getPageN(page);
    if (transition == null) {
      pg.remove(PdfName.TRANS);
    } else {
      pg.put(PdfName.TRANS, transition.getTransitionDictionary());
    } 
    markUsed(pg);
  }
  
  protected void markUsed(PdfObject obj) {
    if (this.append && obj != null) {
      PRIndirectReference ref = null;
      if (obj.type() == 10) {
        ref = (PRIndirectReference)obj;
      } else {
        ref = obj.getIndRef();
      } 
      if (ref != null)
        this.marked.put(ref.getNumber(), 1); 
    } 
  }
  
  protected void markUsed(int num) {
    if (this.append)
      this.marked.put(num, 1); 
  }
  
  boolean isAppend() {
    return this.append;
  }
  
  public void setAdditionalAction(PdfName actionType, PdfAction action) throws PdfException {
    if (!actionType.equals(DOCUMENT_CLOSE) && !actionType.equals(WILL_SAVE) && !actionType.equals(DID_SAVE) && !actionType.equals(WILL_PRINT) && !actionType.equals(DID_PRINT))
      throw new PdfException(MessageLocalization.getComposedMessage("invalid.additional.action.type.1", new Object[] { actionType.toString() })); 
    PdfDictionary aa = this.reader.getCatalog().getAsDict(PdfName.AA);
    if (aa == null) {
      if (action == null)
        return; 
      aa = new PdfDictionary();
      this.reader.getCatalog().put(PdfName.AA, aa);
    } 
    markUsed(aa);
    if (action == null) {
      aa.remove(actionType);
    } else {
      aa.put(actionType, action);
    } 
  }
  
  public void setOpenAction(PdfAction action) {
    this.openAction = action;
  }
  
  public void setOpenAction(String name) {
    throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("open.actions.by.name.are.not.supported", new Object[0]));
  }
  
  public void setThumbnail(Image image) {
    throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.setthumbnail", new Object[0]));
  }
  
  void setThumbnail(Image image, int page) throws PdfException, DocumentException {
    PdfIndirectReference thumb = getImageReference(addDirectImageSimple(image));
    this.reader.resetReleasePage();
    PdfDictionary dic = this.reader.getPageN(page);
    dic.put(PdfName.THUMB, thumb);
    this.reader.resetReleasePage();
  }
  
  public PdfContentByte getDirectContentUnder() {
    throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.getundercontent.or.pdfstamper.getovercontent", new Object[0]));
  }
  
  public PdfContentByte getDirectContent() {
    throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.getundercontent.or.pdfstamper.getovercontent", new Object[0]));
  }
  
  protected void readOCProperties() {
    if (!this.documentOCG.isEmpty())
      return; 
    PdfDictionary dict = this.reader.getCatalog().getAsDict(PdfName.OCPROPERTIES);
    if (dict == null)
      return; 
    PdfArray ocgs = dict.getAsArray(PdfName.OCGS);
    HashMap<String, PdfLayer> ocgmap = new HashMap<String, PdfLayer>();
    for (Iterator<PdfObject> i = ocgs.listIterator(); i.hasNext(); ) {
      PdfIndirectReference ref = (PdfIndirectReference)i.next();
      PdfLayer layer = new PdfLayer(null);
      layer.setRef(ref);
      layer.setOnPanel(false);
      layer.merge((PdfDictionary)PdfReader.getPdfObject(ref));
      ocgmap.put(ref.toString(), layer);
    } 
    PdfDictionary d = dict.getAsDict(PdfName.D);
    PdfArray off = d.getAsArray(PdfName.OFF);
    if (off != null)
      for (Iterator<PdfObject> iterator = off.listIterator(); iterator.hasNext(); ) {
        PdfIndirectReference ref = (PdfIndirectReference)iterator.next();
        PdfLayer layer = ocgmap.get(ref.toString());
        layer.setOn(false);
      }  
    PdfArray order = d.getAsArray(PdfName.ORDER);
    if (order != null)
      addOrder((PdfLayer)null, order, ocgmap); 
    this.documentOCG.addAll(ocgmap.values());
    this.OCGRadioGroup = d.getAsArray(PdfName.RBGROUPS);
    if (this.OCGRadioGroup == null)
      this.OCGRadioGroup = new PdfArray(); 
    this.OCGLocked = d.getAsArray(PdfName.LOCKED);
    if (this.OCGLocked == null)
      this.OCGLocked = new PdfArray(); 
  }
  
  private void addOrder(PdfLayer parent, PdfArray arr, Map<String, PdfLayer> ocgmap) {
    for (int i = 0; i < arr.size(); i++) {
      PdfObject obj = arr.getPdfObject(i);
      if (obj.isIndirect()) {
        PdfLayer layer = ocgmap.get(obj.toString());
        layer.setOnPanel(true);
        registerLayer(layer);
        if (parent != null)
          parent.addChild(layer); 
        if (arr.size() > i + 1 && arr.getPdfObject(i + 1).isArray()) {
          i++;
          addOrder(layer, (PdfArray)arr.getPdfObject(i), ocgmap);
        } 
      } else if (obj.isArray()) {
        PdfArray sub = (PdfArray)obj;
        if (sub.isEmpty())
          return; 
        obj = sub.getPdfObject(0);
        if (obj.isString()) {
          PdfLayer layer = new PdfLayer(obj.toString());
          layer.setOnPanel(true);
          registerLayer(layer);
          if (parent != null)
            parent.addChild(layer); 
          PdfArray array = new PdfArray();
          for (Iterator<PdfObject> j = sub.listIterator(); j.hasNext();)
            array.add(j.next()); 
          addOrder(layer, array, ocgmap);
        } else {
          addOrder(parent, (PdfArray)obj, ocgmap);
        } 
      } 
    } 
  }
  
  public Map<String, PdfLayer> getPdfLayers() {
    if (this.documentOCG.isEmpty())
      readOCProperties(); 
    HashMap<String, PdfLayer> map = new HashMap<String, PdfLayer>();
    for (PdfOCG pdfOCG : this.documentOCG) {
      String key;
      PdfLayer layer = (PdfLayer)pdfOCG;
      if (layer.getTitle() == null) {
        key = layer.getAsString(PdfName.NAME).toString();
      } else {
        key = layer.getTitle();
      } 
      if (map.containsKey(key)) {
        int seq = 2;
        String tmp = key + "(" + seq + ")";
        while (map.containsKey(tmp)) {
          seq++;
          tmp = key + "(" + seq + ")";
        } 
        key = tmp;
      } 
      map.put(key, layer);
    } 
    return map;
  }
  
  static class PageStamp {
    PdfDictionary pageN;
    
    StampContent under;
    
    StampContent over;
    
    PageResources pageResources;
    
    int replacePoint = 0;
    
    PageStamp(PdfStamperImp stamper, PdfReader reader, PdfDictionary pageN) {
      this.pageN = pageN;
      this.pageResources = new PageResources();
      PdfDictionary resources = pageN.getAsDict(PdfName.RESOURCES);
      this.pageResources.setOriginalResources(resources, stamper.namePtr);
    }
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfStamperImp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */