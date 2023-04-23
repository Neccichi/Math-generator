package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Anchor;
import com.mycompany.boniuk_math.com.itextpdf.text.Annotation;
import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.Document;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.Element;
import com.mycompany.boniuk_math.com.itextpdf.text.ElementListener;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Font;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.List;
import com.mycompany.boniuk_math.com.itextpdf.text.ListItem;
import com.mycompany.boniuk_math.com.itextpdf.text.MarkedObject;
import com.mycompany.boniuk_math.com.itextpdf.text.MarkedSection;
import com.mycompany.boniuk_math.com.itextpdf.text.Meta;
import com.mycompany.boniuk_math.com.itextpdf.text.Paragraph;
import com.mycompany.boniuk_math.com.itextpdf.text.Phrase;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.Section;
import com.mycompany.boniuk_math.com.itextpdf.text.api.WriterOperation;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.collection.PdfCollection;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.draw.DrawInterface;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal.PdfAnnotationsImp;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal.PdfViewerPreferencesImp;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class PdfDocument extends Document {
  protected PdfWriter writer;
  
  protected PdfContentByte text;
  
  protected PdfContentByte graphics;
  
  protected float leading;
  
  protected int alignment;
  
  protected float currentHeight;
  
  protected boolean isSectionTitle;
  
  protected int leadingCount;
  
  protected PdfAction anchorAction;
  
  protected int textEmptySize;
  
  protected float nextMarginLeft;
  
  protected float nextMarginRight;
  
  protected float nextMarginTop;
  
  protected float nextMarginBottom;
  
  protected boolean firstPageEvent;
  
  protected PdfLine line;
  
  protected ArrayList<PdfLine> lines;
  
  protected int lastElementType;
  
  static final String hangingPunctuation = ".,;:'";
  
  protected Indentation indentation;
  
  protected PdfInfo info;
  
  protected PdfOutline rootOutline;
  
  protected PdfOutline currentOutline;
  
  protected PdfViewerPreferencesImp viewerPreferences;
  
  protected PdfPageLabels pageLabels;
  
  protected TreeMap<String, Destination> localDestinations;
  
  int jsCounter;
  
  protected HashMap<String, PdfObject> documentLevelJS;
  
  public static class PdfInfo extends PdfDictionary {
    PdfInfo() {
      addProducer();
      addCreationDate();
    }
    
    PdfInfo(String author, String title, String subject) {
      this();
      addTitle(title);
      addSubject(subject);
      addAuthor(author);
    }
    
    void addTitle(String title) {
      put(PdfName.TITLE, new PdfString(title, "UnicodeBig"));
    }
    
    void addSubject(String subject) {
      put(PdfName.SUBJECT, new PdfString(subject, "UnicodeBig"));
    }
    
    void addKeywords(String keywords) {
      put(PdfName.KEYWORDS, new PdfString(keywords, "UnicodeBig"));
    }
    
    void addAuthor(String author) {
      put(PdfName.AUTHOR, new PdfString(author, "UnicodeBig"));
    }
    
    void addCreator(String creator) {
      put(PdfName.CREATOR, new PdfString(creator, "UnicodeBig"));
    }
    
    void addProducer() {
      put(PdfName.PRODUCER, new PdfString(Document.getVersion()));
    }
    
    void addCreationDate() {
      PdfString date = new PdfDate();
      put(PdfName.CREATIONDATE, date);
      put(PdfName.MODDATE, date);
    }
    
    void addkey(String key, String value) {
      if (key.equals("Producer") || key.equals("CreationDate"))
        return; 
      put(new PdfName(key), new PdfString(value, "UnicodeBig"));
    }
  }
  
  static class PdfCatalog extends PdfDictionary {
    PdfWriter writer;
    
    PdfCatalog(PdfIndirectReference pages, PdfWriter writer) {
      super(CATALOG);
      this.writer = writer;
      put(PdfName.PAGES, pages);
    }
    
    void addNames(TreeMap<String, PdfDocument.Destination> localDestinations, HashMap<String, PdfObject> documentLevelJS, HashMap<String, PdfObject> documentFileAttachment, PdfWriter writer) {
      if (localDestinations.isEmpty() && documentLevelJS.isEmpty() && documentFileAttachment.isEmpty())
        return; 
      try {
        PdfDictionary names = new PdfDictionary();
        if (!localDestinations.isEmpty()) {
          PdfArray ar = new PdfArray();
          for (Map.Entry<String, PdfDocument.Destination> entry : localDestinations.entrySet()) {
            String name = entry.getKey();
            PdfDocument.Destination dest = entry.getValue();
            if (dest.destination == null)
              continue; 
            PdfIndirectReference ref = dest.reference;
            ar.add(new PdfString(name, null));
            ar.add(ref);
          } 
          if (ar.size() > 0) {
            PdfDictionary dests = new PdfDictionary();
            dests.put(PdfName.NAMES, ar);
            names.put(PdfName.DESTS, writer.addToBody(dests).getIndirectReference());
          } 
        } 
        if (!documentLevelJS.isEmpty()) {
          PdfDictionary tree = PdfNameTree.writeTree(documentLevelJS, writer);
          names.put(PdfName.JAVASCRIPT, writer.addToBody(tree).getIndirectReference());
        } 
        if (!documentFileAttachment.isEmpty())
          names.put(PdfName.EMBEDDEDFILES, writer.addToBody(PdfNameTree.writeTree(documentFileAttachment, writer)).getIndirectReference()); 
        if (names.size() > 0)
          put(PdfName.NAMES, writer.addToBody(names).getIndirectReference()); 
      } catch (IOException e) {
        throw new ExceptionConverter(e);
      } 
    }
    
    void setOpenAction(PdfAction action) {
      put(PdfName.OPENACTION, action);
    }
    
    void setAdditionalActions(PdfDictionary actions) {
      try {
        put(PdfName.AA, this.writer.addToBody(actions).getIndirectReference());
      } catch (Exception e) {
        throw new ExceptionConverter(e);
      } 
    }
  }
  
  public PdfDocument() {
    this.leading = 0.0F;
    this.alignment = 0;
    this.currentHeight = 0.0F;
    this.isSectionTitle = false;
    this.leadingCount = 0;
    this.anchorAction = null;
    this.firstPageEvent = true;
    this.line = null;
    this.lines = new ArrayList<PdfLine>();
    this.lastElementType = -1;
    this.indentation = new Indentation();
    this.info = new PdfInfo();
    this.viewerPreferences = new PdfViewerPreferencesImp();
    this.localDestinations = new TreeMap<String, Destination>();
    this.documentLevelJS = new HashMap<String, PdfObject>();
    this.documentFileAttachment = new HashMap<String, PdfObject>();
    this.nextPageSize = null;
    this.thisBoxSize = new HashMap<String, PdfRectangle>();
    this.boxSize = new HashMap<String, PdfRectangle>();
    this.pageEmpty = true;
    this.pageAA = null;
    this.strictImageSequence = false;
    this.imageEnd = -1.0F;
    this.imageWait = null;
    addProducer();
    addCreationDate();
  }
  
  public void addWriter(PdfWriter writer) throws DocumentException {
    if (this.writer == null) {
      this.writer = writer;
      this.annotationsImp = new PdfAnnotationsImp(writer);
      return;
    } 
    throw new DocumentException(MessageLocalization.getComposedMessage("you.can.only.add.a.writer.to.a.pdfdocument.once", new Object[0]));
  }
  
  public float getLeading() {
    return this.leading;
  }
  
  void setLeading(float leading) {
    this.leading = leading;
  }
  
  public boolean add(Element element) throws DocumentException {
    if (this.writer != null && this.writer.isPaused())
      return false; 
    try {
      PdfChunk chunk;
      Anchor anchor;
      Annotation annot;
      Paragraph paragraph;
      Section section;
      List list;
      ListItem listItem;
      Rectangle rectangle;
      PdfPTable ptable;
      MultiColumnText multiText;
      DrawInterface zh;
      MarkedObject mo;
      PdfChunk overflow;
      String url;
      Rectangle rect;
      PdfPageEvent pageEvent;
      float height;
      PdfAnnotation an;
      boolean hasTitle;
      switch (element.type()) {
        case 0:
          this.info.addkey(((Meta)element).getName(), ((Meta)element).getContent());
          this.lastElementType = element.type();
          return true;
        case 1:
          this.info.addTitle(((Meta)element).getContent());
          this.lastElementType = element.type();
          return true;
        case 2:
          this.info.addSubject(((Meta)element).getContent());
          this.lastElementType = element.type();
          return true;
        case 3:
          this.info.addKeywords(((Meta)element).getContent());
          this.lastElementType = element.type();
          return true;
        case 4:
          this.info.addAuthor(((Meta)element).getContent());
          this.lastElementType = element.type();
          return true;
        case 7:
          this.info.addCreator(((Meta)element).getContent());
          this.lastElementType = element.type();
          return true;
        case 5:
          this.info.addProducer();
          this.lastElementType = element.type();
          return true;
        case 6:
          this.info.addCreationDate();
          this.lastElementType = element.type();
          return true;
        case 10:
          if (this.line == null)
            carriageReturn(); 
          chunk = new PdfChunk((Chunk)element, this.anchorAction);
          while ((overflow = this.line.add(chunk)) != null) {
            carriageReturn();
            chunk = overflow;
            chunk.trimFirstSpace();
          } 
          this.pageEmpty = false;
          if (chunk.isAttribute("NEWPAGE"))
            newPage(); 
          this.lastElementType = element.type();
          return true;
        case 17:
          this.leadingCount++;
          anchor = (Anchor)element;
          url = anchor.getReference();
          this.leading = anchor.getLeading();
          if (url != null)
            this.anchorAction = new PdfAction(url); 
          element.process((ElementListener)this);
          this.anchorAction = null;
          this.leadingCount--;
          this.lastElementType = element.type();
          return true;
        case 29:
          if (this.line == null)
            carriageReturn(); 
          annot = (Annotation)element;
          rect = new Rectangle(0.0F, 0.0F);
          if (this.line != null)
            rect = new Rectangle(annot.llx(indentRight() - this.line.widthLeft()), annot.ury(indentTop() - this.currentHeight - 20.0F), annot.urx(indentRight() - this.line.widthLeft() + 20.0F), annot.lly(indentTop() - this.currentHeight)); 
          an = PdfAnnotationsImp.convertAnnotation(this.writer, annot, rect);
          this.annotationsImp.addPlainAnnotation(an);
          this.pageEmpty = false;
          this.lastElementType = element.type();
          return true;
        case 11:
          this.leadingCount++;
          this.leading = ((Phrase)element).getLeading();
          element.process((ElementListener)this);
          this.leadingCount--;
          this.lastElementType = element.type();
          return true;
        case 12:
          this.leadingCount++;
          paragraph = (Paragraph)element;
          addSpacing(paragraph.getSpacingBefore(), this.leading, paragraph.getFont());
          this.alignment = paragraph.getAlignment();
          this.leading = paragraph.getTotalLeading();
          carriageReturn();
          if (this.currentHeight + this.line.height() + this.leading > indentTop() - indentBottom())
            newPage(); 
          this.indentation.indentLeft += paragraph.getIndentationLeft();
          this.indentation.indentRight += paragraph.getIndentationRight();
          carriageReturn();
          pageEvent = this.writer.getPageEvent();
          if (pageEvent != null && !this.isSectionTitle)
            pageEvent.onParagraph(this.writer, this, indentTop() - this.currentHeight); 
          if (paragraph.getKeepTogether()) {
            carriageReturn();
            PdfPTable table = new PdfPTable(1);
            table.setWidthPercentage(100.0F);
            PdfPCell cell = new PdfPCell();
            cell.addElement((Element)paragraph);
            cell.setBorder(0);
            cell.setPadding(0.0F);
            table.addCell(cell);
            this.indentation.indentLeft -= paragraph.getIndentationLeft();
            this.indentation.indentRight -= paragraph.getIndentationRight();
            add((Element)table);
            this.indentation.indentLeft += paragraph.getIndentationLeft();
            this.indentation.indentRight += paragraph.getIndentationRight();
          } else {
            this.line.setExtraIndent(paragraph.getFirstLineIndent());
            element.process((ElementListener)this);
            carriageReturn();
            addSpacing(paragraph.getSpacingAfter(), paragraph.getTotalLeading(), paragraph.getFont());
          } 
          if (pageEvent != null && !this.isSectionTitle)
            pageEvent.onParagraphEnd(this.writer, this, indentTop() - this.currentHeight); 
          this.alignment = 0;
          this.indentation.indentLeft -= paragraph.getIndentationLeft();
          this.indentation.indentRight -= paragraph.getIndentationRight();
          carriageReturn();
          this.leadingCount--;
          this.lastElementType = element.type();
          return true;
        case 13:
        case 16:
          section = (Section)element;
          pageEvent = this.writer.getPageEvent();
          hasTitle = (section.isNotAddedYet() && section.getTitle() != null);
          if (section.isTriggerNewPage())
            newPage(); 
          if (hasTitle) {
            float fith = indentTop() - this.currentHeight;
            int rotation = this.pageSize.getRotation();
            if (rotation == 90 || rotation == 180)
              fith = this.pageSize.getHeight() - fith; 
            PdfDestination destination = new PdfDestination(2, fith);
            while (this.currentOutline.level() >= section.getDepth())
              this.currentOutline = this.currentOutline.parent(); 
            PdfOutline outline = new PdfOutline(this.currentOutline, destination, section.getBookmarkTitle(), section.isBookmarkOpen());
            this.currentOutline = outline;
          } 
          carriageReturn();
          this.indentation.sectionIndentLeft += section.getIndentationLeft();
          this.indentation.sectionIndentRight += section.getIndentationRight();
          if (section.isNotAddedYet() && pageEvent != null)
            if (element.type() == 16) {
              pageEvent.onChapter(this.writer, this, indentTop() - this.currentHeight, section.getTitle());
            } else {
              pageEvent.onSection(this.writer, this, indentTop() - this.currentHeight, section.getDepth(), section.getTitle());
            }  
          if (hasTitle) {
            this.isSectionTitle = true;
            add((Element)section.getTitle());
            this.isSectionTitle = false;
          } 
          this.indentation.sectionIndentLeft += section.getIndentation();
          element.process((ElementListener)this);
          flushLines();
          this.indentation.sectionIndentLeft -= section.getIndentationLeft() + section.getIndentation();
          this.indentation.sectionIndentRight -= section.getIndentationRight();
          if (section.isComplete() && pageEvent != null)
            if (element.type() == 16) {
              pageEvent.onChapterEnd(this.writer, this, indentTop() - this.currentHeight);
            } else {
              pageEvent.onSectionEnd(this.writer, this, indentTop() - this.currentHeight);
            }  
          this.lastElementType = element.type();
          return true;
        case 14:
          list = (List)element;
          if (list.isAlignindent())
            list.normalizeIndentation(); 
          this.indentation.listIndentLeft += list.getIndentationLeft();
          this.indentation.indentRight += list.getIndentationRight();
          element.process((ElementListener)this);
          this.indentation.listIndentLeft -= list.getIndentationLeft();
          this.indentation.indentRight -= list.getIndentationRight();
          carriageReturn();
          this.lastElementType = element.type();
          return true;
        case 15:
          this.leadingCount++;
          listItem = (ListItem)element;
          addSpacing(listItem.getSpacingBefore(), this.leading, listItem.getFont());
          this.alignment = listItem.getAlignment();
          this.indentation.listIndentLeft += listItem.getIndentationLeft();
          this.indentation.indentRight += listItem.getIndentationRight();
          this.leading = listItem.getTotalLeading();
          carriageReturn();
          this.line.setListItem(listItem);
          element.process((ElementListener)this);
          addSpacing(listItem.getSpacingAfter(), listItem.getTotalLeading(), listItem.getFont());
          if (this.line.hasToBeJustified())
            this.line.resetAlignment(); 
          carriageReturn();
          this.indentation.listIndentLeft -= listItem.getIndentationLeft();
          this.indentation.indentRight -= listItem.getIndentationRight();
          this.leadingCount--;
          this.lastElementType = element.type();
          return true;
        case 30:
          rectangle = (Rectangle)element;
          this.graphics.rectangle(rectangle);
          this.pageEmpty = false;
          this.lastElementType = element.type();
          return true;
        case 23:
          ptable = (PdfPTable)element;
          if (ptable.size() > ptable.getHeaderRows()) {
            ensureNewLine();
            flushLines();
            addPTable(ptable);
            this.pageEmpty = false;
            newLine();
          } 
          this.lastElementType = element.type();
          return true;
        case 40:
          ensureNewLine();
          flushLines();
          multiText = (MultiColumnText)element;
          height = multiText.write(this.writer.getDirectContent(), this, indentTop() - this.currentHeight);
          this.currentHeight += height;
          this.text.moveText(0.0F, -1.0F * height);
          this.pageEmpty = false;
          this.lastElementType = element.type();
          return true;
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
          add((Image)element);
          this.lastElementType = element.type();
          return true;
        case 55:
          zh = (DrawInterface)element;
          zh.draw(this.graphics, indentLeft(), indentBottom(), indentRight(), indentTop(), indentTop() - this.currentHeight - ((this.leadingCount > 0) ? this.leading : 0.0F));
          this.pageEmpty = false;
          this.lastElementType = element.type();
          return true;
        case 50:
          if (element instanceof MarkedSection) {
            MarkedObject markedObject = ((MarkedSection)element).getTitle();
            if (markedObject != null)
              markedObject.process((ElementListener)this); 
          } 
          mo = (MarkedObject)element;
          mo.process((ElementListener)this);
          this.lastElementType = element.type();
          return true;
        case 666:
          if (null != this.writer)
            ((WriterOperation)element).write(this.writer, this); 
          this.lastElementType = element.type();
          return true;
      } 
      return false;
    } catch (Exception e) {
      throw new DocumentException(e);
    } 
  }
  
  public void open() {
    if (!this.open) {
      super.open();
      this.writer.open();
      this.rootOutline = new PdfOutline(this.writer);
      this.currentOutline = this.rootOutline;
    } 
    try {
      initPage();
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public void close() {
    if (this.close)
      return; 
    try {
      boolean wasImage = (this.imageWait != null);
      newPage();
      if (this.imageWait != null || wasImage)
        newPage(); 
      if (this.annotationsImp.hasUnusedAnnotations())
        throw new RuntimeException(MessageLocalization.getComposedMessage("not.all.annotations.could.be.added.to.the.document.the.document.doesn.t.have.enough.pages", new Object[0])); 
      PdfPageEvent pageEvent = this.writer.getPageEvent();
      if (pageEvent != null)
        pageEvent.onCloseDocument(this.writer, this); 
      super.close();
      this.writer.addLocalDestinations(this.localDestinations);
      calculateOutlineCount();
      writeOutlines();
    } catch (Exception e) {
      throw ExceptionConverter.convertException(e);
    } 
    this.writer.close();
  }
  
  public void setXmpMetadata(byte[] xmpMetadata) throws IOException {
    PdfStream xmp = new PdfStream(xmpMetadata);
    xmp.put(PdfName.TYPE, PdfName.METADATA);
    xmp.put(PdfName.SUBTYPE, PdfName.XML);
    PdfEncryption crypto = this.writer.getEncryption();
    if (crypto != null && !crypto.isMetadataEncrypted()) {
      PdfArray ar = new PdfArray();
      ar.add(PdfName.CRYPT);
      xmp.put(PdfName.FILTER, ar);
    } 
    this.writer.addPageDictEntry(PdfName.METADATA, this.writer.addToBody(xmp).getIndirectReference());
  }
  
  public boolean newPage() {
    this.lastElementType = -1;
    if (isPageEmpty()) {
      setNewPageSizeAndMargins();
      return false;
    } 
    if (!this.open || this.close)
      throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open", new Object[0])); 
    PdfPageEvent pageEvent = this.writer.getPageEvent();
    if (pageEvent != null)
      pageEvent.onEndPage(this.writer, this); 
    super.newPage();
    this.indentation.imageIndentLeft = 0.0F;
    this.indentation.imageIndentRight = 0.0F;
    try {
      flushLines();
      int rotation = this.pageSize.getRotation();
      if (this.writer.isPdfX()) {
        if (this.thisBoxSize.containsKey("art") && this.thisBoxSize.containsKey("trim"))
          throw new PdfXConformanceException(MessageLocalization.getComposedMessage("only.one.of.artbox.or.trimbox.can.exist.in.the.page", new Object[0])); 
        if (!this.thisBoxSize.containsKey("art") && !this.thisBoxSize.containsKey("trim"))
          if (this.thisBoxSize.containsKey("crop")) {
            this.thisBoxSize.put("trim", this.thisBoxSize.get("crop"));
          } else {
            this.thisBoxSize.put("trim", new PdfRectangle(this.pageSize, this.pageSize.getRotation()));
          }  
      } 
      this.pageResources.addDefaultColorDiff(this.writer.getDefaultColorspace());
      if (this.writer.isRgbTransparencyBlending()) {
        PdfDictionary dcs = new PdfDictionary();
        dcs.put(PdfName.CS, PdfName.DEVICERGB);
        this.pageResources.addDefaultColorDiff(dcs);
      } 
      PdfDictionary resources = this.pageResources.getResources();
      PdfPage page = new PdfPage(new PdfRectangle(this.pageSize, rotation), this.thisBoxSize, resources, rotation);
      page.put(PdfName.TABS, this.writer.getTabs());
      page.putAll(this.writer.getPageDictEntries());
      this.writer.resetPageDictEntries();
      if (this.pageAA != null) {
        page.put(PdfName.AA, this.writer.addToBody(this.pageAA).getIndirectReference());
        this.pageAA = null;
      } 
      if (this.annotationsImp.hasUnusedAnnotations()) {
        PdfArray array = this.annotationsImp.rotateAnnotations(this.writer, this.pageSize);
        if (array.size() != 0)
          page.put(PdfName.ANNOTS, array); 
      } 
      if (this.writer.isTagged())
        page.put(PdfName.STRUCTPARENTS, new PdfNumber(this.writer.getCurrentPageNumber() - 1)); 
      if (this.text.size() > this.textEmptySize) {
        this.text.endText();
      } else {
        this.text = null;
      } 
      this.writer.add(page, new PdfContents(this.writer.getDirectContentUnder(), this.graphics, this.text, this.writer.getDirectContent(), this.pageSize));
      initPage();
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } catch (IOException ioe) {
      throw new ExceptionConverter(ioe);
    } 
    return true;
  }
  
  public boolean setPageSize(Rectangle pageSize) {
    if (this.writer != null && this.writer.isPaused())
      return false; 
    this.nextPageSize = new Rectangle(pageSize);
    return true;
  }
  
  public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
    if (this.writer != null && this.writer.isPaused())
      return false; 
    this.nextMarginLeft = marginLeft;
    this.nextMarginRight = marginRight;
    this.nextMarginTop = marginTop;
    this.nextMarginBottom = marginBottom;
    return true;
  }
  
  public boolean setMarginMirroring(boolean MarginMirroring) {
    if (this.writer != null && this.writer.isPaused())
      return false; 
    return super.setMarginMirroring(MarginMirroring);
  }
  
  public boolean setMarginMirroringTopBottom(boolean MarginMirroringTopBottom) {
    if (this.writer != null && this.writer.isPaused())
      return false; 
    return super.setMarginMirroringTopBottom(MarginMirroringTopBottom);
  }
  
  public void setPageCount(int pageN) {
    if (this.writer != null && this.writer.isPaused())
      return; 
    super.setPageCount(pageN);
  }
  
  public void resetPageCount() {
    if (this.writer != null && this.writer.isPaused())
      return; 
    super.resetPageCount();
  }
  
  protected void initPage() throws DocumentException {
    this.pageN++;
    this.annotationsImp.resetAnnotations();
    this.pageResources = new PageResources();
    this.writer.resetContent();
    this.graphics = new PdfContentByte(this.writer);
    this.markPoint = 0;
    setNewPageSizeAndMargins();
    this.imageEnd = -1.0F;
    this.indentation.imageIndentRight = 0.0F;
    this.indentation.imageIndentLeft = 0.0F;
    this.indentation.indentBottom = 0.0F;
    this.indentation.indentTop = 0.0F;
    this.currentHeight = 0.0F;
    this.thisBoxSize = new HashMap<String, PdfRectangle>(this.boxSize);
    if (this.pageSize.getBackgroundColor() != null || this.pageSize.hasBorders() || this.pageSize.getBorderColor() != null)
      add((Element)this.pageSize); 
    float oldleading = this.leading;
    int oldAlignment = this.alignment;
    this.pageEmpty = true;
    try {
      if (this.imageWait != null) {
        add(this.imageWait);
        this.imageWait = null;
      } 
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
    this.leading = oldleading;
    this.alignment = oldAlignment;
    carriageReturn();
    PdfPageEvent pageEvent = this.writer.getPageEvent();
    if (pageEvent != null) {
      if (this.firstPageEvent)
        pageEvent.onOpenDocument(this.writer, this); 
      pageEvent.onStartPage(this.writer, this);
    } 
    this.firstPageEvent = false;
  }
  
  protected void newLine() throws DocumentException {
    this.lastElementType = -1;
    carriageReturn();
    if (this.lines != null && !this.lines.isEmpty()) {
      this.lines.add(this.line);
      this.currentHeight += this.line.height();
    } 
    this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
  }
  
  protected void carriageReturn() {
    if (this.lines == null)
      this.lines = new ArrayList<PdfLine>(); 
    if (this.line != null && this.line.size() > 0) {
      if (this.currentHeight + this.line.height() + this.leading > indentTop() - indentBottom()) {
        PdfLine overflowLine = this.line;
        this.line = null;
        newPage();
        this.line = overflowLine;
      } 
      this.currentHeight += this.line.height();
      this.lines.add(this.line);
      this.pageEmpty = false;
    } 
    if (this.imageEnd > -1.0F && this.currentHeight > this.imageEnd) {
      this.imageEnd = -1.0F;
      this.indentation.imageIndentRight = 0.0F;
      this.indentation.imageIndentLeft = 0.0F;
    } 
    this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
  }
  
  public float getVerticalPosition(boolean ensureNewLine) {
    if (ensureNewLine)
      ensureNewLine(); 
    return top() - this.currentHeight - this.indentation.indentTop;
  }
  
  protected void ensureNewLine() {
    try {
      if (this.lastElementType == 11 || this.lastElementType == 10) {
        newLine();
        flushLines();
      } 
    } catch (DocumentException ex) {
      throw new ExceptionConverter(ex);
    } 
  }
  
  protected float flushLines() throws DocumentException {
    if (this.lines == null)
      return 0.0F; 
    if (this.line != null && this.line.size() > 0) {
      this.lines.add(this.line);
      this.line = new PdfLine(indentLeft(), indentRight(), this.alignment, this.leading);
    } 
    if (this.lines.isEmpty())
      return 0.0F; 
    Object[] currentValues = new Object[2];
    PdfFont currentFont = null;
    float displacement = 0.0F;
    Float lastBaseFactor = new Float(0.0F);
    currentValues[1] = lastBaseFactor;
    for (PdfLine l : this.lines) {
      float moveTextX = l.indentLeft() - indentLeft() + this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.sectionIndentLeft;
      this.text.moveText(moveTextX, -l.height());
      if (l.listSymbol() != null)
        ColumnText.showTextAligned(this.graphics, 0, new Phrase(l.listSymbol()), this.text.getXTLM() - l.listIndent(), this.text.getYTLM(), 0.0F); 
      currentValues[0] = currentFont;
      writeLineToContent(l, this.text, this.graphics, currentValues, this.writer.getSpaceCharRatio());
      currentFont = (PdfFont)currentValues[0];
      displacement += l.height();
      this.text.moveText(-moveTextX, 0.0F);
    } 
    this.lines = new ArrayList<PdfLine>();
    return displacement;
  }
  
  float writeLineToContent(PdfLine line, PdfContentByte text, PdfContentByte graphics, Object[] currentValues, float ratio) throws DocumentException {
    PdfFont currentFont = (PdfFont)currentValues[0];
    float lastBaseFactor = ((Float)currentValues[1]).floatValue();
    float hangingCorrection = 0.0F;
    float hScale = 1.0F;
    float lastHScale = Float.NaN;
    float baseWordSpacing = 0.0F;
    float baseCharacterSpacing = 0.0F;
    float glueWidth = 0.0F;
    float lastX = text.getXTLM() + line.getOriginalWidth();
    int numberOfSpaces = line.numberOfSpaces();
    int lineLen = line.getLineLengthUtf32();
    boolean isJustified = (line.hasToBeJustified() && (numberOfSpaces != 0 || lineLen > 1));
    int separatorCount = line.getSeparatorCount();
    if (separatorCount > 0) {
      glueWidth = line.widthLeft() / separatorCount;
    } else if (isJustified && separatorCount == 0) {
      if (line.isNewlineSplit() && line.widthLeft() >= lastBaseFactor * (ratio * numberOfSpaces + lineLen - 1.0F)) {
        if (line.isRTL())
          text.moveText(line.widthLeft() - lastBaseFactor * (ratio * numberOfSpaces + lineLen - 1.0F), 0.0F); 
        baseWordSpacing = ratio * lastBaseFactor;
        baseCharacterSpacing = lastBaseFactor;
      } else {
        float width = line.widthLeft();
        PdfChunk last = line.getChunk(line.size() - 1);
        if (last != null) {
          String s = last.toString();
          char c;
          if (s.length() > 0 && ".,;:'".indexOf(c = s.charAt(s.length() - 1)) >= 0) {
            float oldWidth = width;
            width += last.font().width(c) * 0.4F;
            hangingCorrection = width - oldWidth;
          } 
        } 
        float baseFactor = width / (ratio * numberOfSpaces + lineLen - 1.0F);
        baseWordSpacing = ratio * baseFactor;
        baseCharacterSpacing = baseFactor;
        lastBaseFactor = baseFactor;
      } 
    } else if (line.alignment == 0 || line.alignment == -1) {
      lastX -= line.widthLeft();
    } 
    int lastChunkStroke = line.getLastStrokeChunk();
    int chunkStrokeIdx = 0;
    float xMarker = text.getXTLM();
    float baseXMarker = xMarker;
    float yMarker = text.getYTLM();
    boolean adjustMatrix = false;
    float tabPosition = 0.0F;
    for (Iterator<PdfChunk> j = line.iterator(); j.hasNext(); ) {
      PdfChunk chunk = j.next();
      BaseColor color = chunk.color();
      float fontSize = chunk.font().size();
      float ascender = chunk.font().getFont().getFontDescriptor(1, fontSize);
      float descender = chunk.font().getFont().getFontDescriptor(3, fontSize);
      hScale = 1.0F;
      if (chunkStrokeIdx <= lastChunkStroke) {
        float f;
        if (isJustified) {
          f = chunk.getWidthCorrected(baseCharacterSpacing, baseWordSpacing);
        } else {
          f = chunk.width();
        } 
        if (chunk.isStroked()) {
          PdfChunk nextChunk = line.getChunk(chunkStrokeIdx + 1);
          if (chunk.isSeparator()) {
            f = glueWidth;
            Object[] sep = (Object[])chunk.getAttribute("SEPARATOR");
            DrawInterface di = (DrawInterface)sep[0];
            Boolean vertical = (Boolean)sep[1];
            if (vertical.booleanValue()) {
              di.draw(graphics, baseXMarker, yMarker + descender, baseXMarker + line.getOriginalWidth(), ascender - descender, yMarker);
            } else {
              di.draw(graphics, xMarker, yMarker + descender, xMarker + f, ascender - descender, yMarker);
            } 
          } 
          if (chunk.isTab()) {
            Object[] tab = (Object[])chunk.getAttribute("TAB");
            DrawInterface di = (DrawInterface)tab[0];
            tabPosition = ((Float)tab[1]).floatValue() + ((Float)tab[3]).floatValue();
            if (tabPosition > xMarker)
              di.draw(graphics, xMarker, yMarker + descender, tabPosition, ascender - descender, yMarker); 
            float tmp = xMarker;
            xMarker = tabPosition;
            tabPosition = tmp;
          } 
          if (chunk.isAttribute("BACKGROUND")) {
            float subtract = lastBaseFactor;
            if (nextChunk != null && nextChunk.isAttribute("BACKGROUND"))
              subtract = 0.0F; 
            if (nextChunk == null)
              subtract += hangingCorrection; 
            Object[] bgr = (Object[])chunk.getAttribute("BACKGROUND");
            graphics.setColorFill((BaseColor)bgr[0]);
            float[] extra = (float[])bgr[1];
            graphics.rectangle(xMarker - extra[0], yMarker + descender - extra[1] + chunk.getTextRise(), f - subtract + extra[0] + extra[2], ascender - descender + extra[1] + extra[3]);
            graphics.fill();
            graphics.setGrayFill(0.0F);
          } 
          if (chunk.isAttribute("UNDERLINE")) {
            float subtract = lastBaseFactor;
            if (nextChunk != null && nextChunk.isAttribute("UNDERLINE"))
              subtract = 0.0F; 
            if (nextChunk == null)
              subtract += hangingCorrection; 
            Object[][] unders = (Object[][])chunk.getAttribute("UNDERLINE");
            BaseColor scolor = null;
            for (int k = 0; k < unders.length; k++) {
              Object[] obj = unders[k];
              scolor = (BaseColor)obj[0];
              float[] ps = (float[])obj[1];
              if (scolor == null)
                scolor = color; 
              if (scolor != null)
                graphics.setColorStroke(scolor); 
              graphics.setLineWidth(ps[0] + fontSize * ps[1]);
              float shift = ps[2] + fontSize * ps[3];
              int cap2 = (int)ps[4];
              if (cap2 != 0)
                graphics.setLineCap(cap2); 
              graphics.moveTo(xMarker, yMarker + shift);
              graphics.lineTo(xMarker + f - subtract, yMarker + shift);
              graphics.stroke();
              if (scolor != null)
                graphics.resetGrayStroke(); 
              if (cap2 != 0)
                graphics.setLineCap(0); 
            } 
            graphics.setLineWidth(1.0F);
          } 
          if (chunk.isAttribute("ACTION")) {
            float subtract = lastBaseFactor;
            if (nextChunk != null && nextChunk.isAttribute("ACTION"))
              subtract = 0.0F; 
            if (nextChunk == null)
              subtract += hangingCorrection; 
            text.addAnnotation(new PdfAnnotation(this.writer, xMarker, yMarker + descender + chunk.getTextRise(), xMarker + f - subtract, yMarker + ascender + chunk.getTextRise(), (PdfAction)chunk.getAttribute("ACTION")));
          } 
          if (chunk.isAttribute("REMOTEGOTO")) {
            float subtract = lastBaseFactor;
            if (nextChunk != null && nextChunk.isAttribute("REMOTEGOTO"))
              subtract = 0.0F; 
            if (nextChunk == null)
              subtract += hangingCorrection; 
            Object[] obj = (Object[])chunk.getAttribute("REMOTEGOTO");
            String filename = (String)obj[0];
            if (obj[1] instanceof String) {
              remoteGoto(filename, (String)obj[1], xMarker, yMarker + descender + chunk.getTextRise(), xMarker + f - subtract, yMarker + ascender + chunk.getTextRise());
            } else {
              remoteGoto(filename, ((Integer)obj[1]).intValue(), xMarker, yMarker + descender + chunk.getTextRise(), xMarker + f - subtract, yMarker + ascender + chunk.getTextRise());
            } 
          } 
          if (chunk.isAttribute("LOCALGOTO")) {
            float subtract = lastBaseFactor;
            if (nextChunk != null && nextChunk.isAttribute("LOCALGOTO"))
              subtract = 0.0F; 
            if (nextChunk == null)
              subtract += hangingCorrection; 
            localGoto((String)chunk.getAttribute("LOCALGOTO"), xMarker, yMarker, xMarker + f - subtract, yMarker + fontSize);
          } 
          if (chunk.isAttribute("LOCALDESTINATION")) {
            float subtract = lastBaseFactor;
            if (nextChunk != null && nextChunk.isAttribute("LOCALDESTINATION"))
              subtract = 0.0F; 
            if (nextChunk == null)
              subtract += hangingCorrection; 
            localDestination((String)chunk.getAttribute("LOCALDESTINATION"), new PdfDestination(0, xMarker, yMarker + fontSize, 0.0F));
          } 
          if (chunk.isAttribute("GENERICTAG")) {
            float subtract = lastBaseFactor;
            if (nextChunk != null && nextChunk.isAttribute("GENERICTAG"))
              subtract = 0.0F; 
            if (nextChunk == null)
              subtract += hangingCorrection; 
            Rectangle rect = new Rectangle(xMarker, yMarker, xMarker + f - subtract, yMarker + fontSize);
            PdfPageEvent pev = this.writer.getPageEvent();
            if (pev != null)
              pev.onGenericTag(this.writer, this, rect, (String)chunk.getAttribute("GENERICTAG")); 
          } 
          if (chunk.isAttribute("PDFANNOTATION")) {
            float subtract = lastBaseFactor;
            if (nextChunk != null && nextChunk.isAttribute("PDFANNOTATION"))
              subtract = 0.0F; 
            if (nextChunk == null)
              subtract += hangingCorrection; 
            PdfAnnotation annot = PdfFormField.shallowDuplicate((PdfAnnotation)chunk.getAttribute("PDFANNOTATION"));
            annot.put(PdfName.RECT, new PdfRectangle(xMarker, yMarker + descender, xMarker + f - subtract, yMarker + ascender));
            text.addAnnotation(annot);
          } 
          float[] params = (float[])chunk.getAttribute("SKEW");
          Float hs = (Float)chunk.getAttribute("HSCALE");
          if (params != null || hs != null) {
            float b = 0.0F, c = 0.0F;
            if (params != null) {
              b = params[0];
              c = params[1];
            } 
            if (hs != null)
              hScale = hs.floatValue(); 
            text.setTextMatrix(hScale, b, c, 1.0F, xMarker, yMarker);
          } 
          if (chunk.isAttribute("CHAR_SPACING")) {
            Float cs = (Float)chunk.getAttribute("CHAR_SPACING");
            text.setCharacterSpacing(cs.floatValue());
          } 
          if (chunk.isImage()) {
            Image image = chunk.getImage();
            float[] matrix = image.matrix();
            matrix[4] = xMarker + chunk.getImageOffsetX() - matrix[4];
            matrix[5] = yMarker + chunk.getImageOffsetY() - matrix[5];
            graphics.addImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
            text.moveText(xMarker + lastBaseFactor + image.getScaledWidth() - text.getXTLM(), 0.0F);
          } 
        } 
        xMarker += f;
        chunkStrokeIdx++;
      } 
      if (chunk.font().compareTo(currentFont) != 0) {
        currentFont = chunk.font();
        text.setFontAndSize(currentFont.getFont(), currentFont.size());
      } 
      float rise = 0.0F;
      Object[] textRender = (Object[])chunk.getAttribute("TEXTRENDERMODE");
      int tr = 0;
      float strokeWidth = 1.0F;
      BaseColor strokeColor = null;
      Float fr = (Float)chunk.getAttribute("SUBSUPSCRIPT");
      if (textRender != null) {
        tr = ((Integer)textRender[0]).intValue() & 0x3;
        if (tr != 0)
          text.setTextRenderingMode(tr); 
        if (tr == 1 || tr == 2) {
          strokeWidth = ((Float)textRender[1]).floatValue();
          if (strokeWidth != 1.0F)
            text.setLineWidth(strokeWidth); 
          strokeColor = (BaseColor)textRender[2];
          if (strokeColor == null)
            strokeColor = color; 
          if (strokeColor != null)
            text.setColorStroke(strokeColor); 
        } 
      } 
      if (fr != null)
        rise = fr.floatValue(); 
      if (color != null)
        text.setColorFill(color); 
      if (rise != 0.0F)
        text.setTextRise(rise); 
      if (chunk.isImage()) {
        adjustMatrix = true;
      } else if (chunk.isHorizontalSeparator()) {
        PdfTextArray array = new PdfTextArray();
        array.add(-glueWidth * 1000.0F / chunk.font.size() / hScale);
        text.showText(array);
      } else if (chunk.isTab()) {
        PdfTextArray array = new PdfTextArray();
        array.add((tabPosition - xMarker) * 1000.0F / chunk.font.size() / hScale);
        text.showText(array);
      } else if (isJustified && numberOfSpaces > 0 && chunk.isSpecialEncoding()) {
        if (hScale != lastHScale) {
          lastHScale = hScale;
          text.setWordSpacing(baseWordSpacing / hScale);
          text.setCharacterSpacing(baseCharacterSpacing / hScale + text.getCharacterSpacing());
        } 
        String s = chunk.toString();
        int idx = s.indexOf(' ');
        if (idx < 0) {
          text.showText(s);
        } else {
          float spaceCorrection = -baseWordSpacing * 1000.0F / chunk.font.size() / hScale;
          PdfTextArray textArray = new PdfTextArray(s.substring(0, idx));
          int lastIdx = idx;
          while ((idx = s.indexOf(' ', lastIdx + 1)) >= 0) {
            textArray.add(spaceCorrection);
            textArray.add(s.substring(lastIdx, idx));
            lastIdx = idx;
          } 
          textArray.add(spaceCorrection);
          textArray.add(s.substring(lastIdx));
          text.showText(textArray);
        } 
      } else {
        if (isJustified && hScale != lastHScale) {
          lastHScale = hScale;
          text.setWordSpacing(baseWordSpacing / hScale);
          text.setCharacterSpacing(baseCharacterSpacing / hScale + text.getCharacterSpacing());
        } 
        text.showText(chunk.toString());
      } 
      if (rise != 0.0F)
        text.setTextRise(0.0F); 
      if (color != null)
        text.resetRGBColorFill(); 
      if (tr != 0)
        text.setTextRenderingMode(0); 
      if (strokeColor != null)
        text.resetRGBColorStroke(); 
      if (strokeWidth != 1.0F)
        text.setLineWidth(1.0F); 
      if (chunk.isAttribute("SKEW") || chunk.isAttribute("HSCALE")) {
        adjustMatrix = true;
        text.setTextMatrix(xMarker, yMarker);
      } 
      if (chunk.isAttribute("CHAR_SPACING"))
        text.setCharacterSpacing(baseCharacterSpacing); 
    } 
    if (isJustified) {
      text.setWordSpacing(0.0F);
      text.setCharacterSpacing(0.0F);
      if (line.isNewlineSplit())
        lastBaseFactor = 0.0F; 
    } 
    if (adjustMatrix)
      text.moveText(baseXMarker - text.getXTLM(), 0.0F); 
    currentValues[0] = currentFont;
    currentValues[1] = new Float(lastBaseFactor);
    return lastX;
  }
  
  public static class Indentation {
    float indentLeft = 0.0F;
    
    float sectionIndentLeft = 0.0F;
    
    float listIndentLeft = 0.0F;
    
    float imageIndentLeft = 0.0F;
    
    float indentRight = 0.0F;
    
    float sectionIndentRight = 0.0F;
    
    float imageIndentRight = 0.0F;
    
    float indentTop = 0.0F;
    
    float indentBottom = 0.0F;
  }
  
  protected float indentLeft() {
    return left(this.indentation.indentLeft + this.indentation.listIndentLeft + this.indentation.imageIndentLeft + this.indentation.sectionIndentLeft);
  }
  
  protected float indentRight() {
    return right(this.indentation.indentRight + this.indentation.sectionIndentRight + this.indentation.imageIndentRight);
  }
  
  protected float indentTop() {
    return top(this.indentation.indentTop);
  }
  
  float indentBottom() {
    return bottom(this.indentation.indentBottom);
  }
  
  protected void addSpacing(float extraspace, float oldleading, Font f) {
    if (extraspace == 0.0F)
      return; 
    if (this.pageEmpty)
      return; 
    if (this.currentHeight + this.line.height() + this.leading > indentTop() - indentBottom())
      return; 
    this.leading = extraspace;
    carriageReturn();
    if (f.isUnderlined() || f.isStrikethru()) {
      f = new Font(f);
      int style = f.getStyle();
      style &= 0xFFFFFFFB;
      style &= 0xFFFFFFF7;
      f.setStyle(style);
    } 
    Chunk space = new Chunk(" ", f);
    space.process((ElementListener)this);
    carriageReturn();
    this.leading = oldleading;
  }
  
  PdfInfo getInfo() {
    return this.info;
  }
  
  PdfCatalog getCatalog(PdfIndirectReference pages) {
    PdfCatalog catalog = new PdfCatalog(pages, this.writer);
    if (this.rootOutline.getKids().size() > 0) {
      catalog.put(PdfName.PAGEMODE, PdfName.USEOUTLINES);
      catalog.put(PdfName.OUTLINES, this.rootOutline.indirectReference());
    } 
    this.writer.getPdfVersion().addToCatalog(catalog);
    this.viewerPreferences.addToCatalog(catalog);
    if (this.pageLabels != null)
      catalog.put(PdfName.PAGELABELS, this.pageLabels.getDictionary(this.writer)); 
    catalog.addNames(this.localDestinations, getDocumentLevelJS(), this.documentFileAttachment, this.writer);
    if (this.openActionName != null) {
      PdfAction action = getLocalGotoAction(this.openActionName);
      catalog.setOpenAction(action);
    } else if (this.openActionAction != null) {
      catalog.setOpenAction(this.openActionAction);
    } 
    if (this.additionalActions != null)
      catalog.setAdditionalActions(this.additionalActions); 
    if (this.collection != null)
      catalog.put(PdfName.COLLECTION, (PdfObject)this.collection); 
    if (this.annotationsImp.hasValidAcroForm())
      try {
        catalog.put(PdfName.ACROFORM, this.writer.addToBody(this.annotationsImp.getAcroForm()).getIndirectReference());
      } catch (IOException e) {
        throw new ExceptionConverter(e);
      }  
    return catalog;
  }
  
  void addOutline(PdfOutline outline, String name) {
    localDestination(name, outline.getPdfDestination());
  }
  
  public PdfOutline getRootOutline() {
    return this.rootOutline;
  }
  
  void calculateOutlineCount() {
    if (this.rootOutline.getKids().size() == 0)
      return; 
    traverseOutlineCount(this.rootOutline);
  }
  
  void traverseOutlineCount(PdfOutline outline) {
    ArrayList<PdfOutline> kids = outline.getKids();
    PdfOutline parent = outline.parent();
    if (kids.isEmpty()) {
      if (parent != null)
        parent.setCount(parent.getCount() + 1); 
    } else {
      for (int k = 0; k < kids.size(); k++)
        traverseOutlineCount(kids.get(k)); 
      if (parent != null)
        if (outline.isOpen()) {
          parent.setCount(outline.getCount() + parent.getCount() + 1);
        } else {
          parent.setCount(parent.getCount() + 1);
          outline.setCount(-outline.getCount());
        }  
    } 
  }
  
  void writeOutlines() throws IOException {
    if (this.rootOutline.getKids().size() == 0)
      return; 
    outlineTree(this.rootOutline);
    this.writer.addToBody(this.rootOutline, this.rootOutline.indirectReference());
  }
  
  void outlineTree(PdfOutline outline) throws IOException {
    outline.setIndirectReference(this.writer.getPdfIndirectReference());
    if (outline.parent() != null)
      outline.put(PdfName.PARENT, outline.parent().indirectReference()); 
    ArrayList<PdfOutline> kids = outline.getKids();
    int size = kids.size();
    int k;
    for (k = 0; k < size; k++)
      outlineTree(kids.get(k)); 
    for (k = 0; k < size; k++) {
      if (k > 0)
        ((PdfOutline)kids.get(k)).put(PdfName.PREV, ((PdfOutline)kids.get(k - 1)).indirectReference()); 
      if (k < size - 1)
        ((PdfOutline)kids.get(k)).put(PdfName.NEXT, ((PdfOutline)kids.get(k + 1)).indirectReference()); 
    } 
    if (size > 0) {
      outline.put(PdfName.FIRST, ((PdfOutline)kids.get(0)).indirectReference());
      outline.put(PdfName.LAST, ((PdfOutline)kids.get(size - 1)).indirectReference());
    } 
    for (k = 0; k < size; k++) {
      PdfOutline kid = kids.get(k);
      this.writer.addToBody(kid, kid.indirectReference());
    } 
  }
  
  void setViewerPreferences(int preferences) {
    this.viewerPreferences.setViewerPreferences(preferences);
  }
  
  void addViewerPreference(PdfName key, PdfObject value) {
    this.viewerPreferences.addViewerPreference(key, value);
  }
  
  void setPageLabels(PdfPageLabels pageLabels) {
    this.pageLabels = pageLabels;
  }
  
  void localGoto(String name, float llx, float lly, float urx, float ury) {
    PdfAction action = getLocalGotoAction(name);
    this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action));
  }
  
  void remoteGoto(String filename, String name, float llx, float lly, float urx, float ury) {
    this.annotationsImp.addPlainAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, new PdfAction(filename, name)));
  }
  
  void remoteGoto(String filename, int page, float llx, float lly, float urx, float ury) {
    addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, new PdfAction(filename, page)));
  }
  
  void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
    addAnnotation(new PdfAnnotation(this.writer, llx, lly, urx, ury, action));
  }
  
  PdfAction getLocalGotoAction(String name) {
    PdfAction action;
    Destination dest = this.localDestinations.get(name);
    if (dest == null)
      dest = new Destination(); 
    if (dest.action == null) {
      if (dest.reference == null)
        dest.reference = this.writer.getPdfIndirectReference(); 
      action = new PdfAction(dest.reference);
      dest.action = action;
      this.localDestinations.put(name, dest);
    } else {
      action = dest.action;
    } 
    return action;
  }
  
  boolean localDestination(String name, PdfDestination destination) {
    Destination dest = this.localDestinations.get(name);
    if (dest == null)
      dest = new Destination(); 
    if (dest.destination != null)
      return false; 
    dest.destination = destination;
    this.localDestinations.put(name, dest);
    if (!destination.hasPage())
      destination.addPage(this.writer.getCurrentPage()); 
    return true;
  }
  
  protected static final DecimalFormat SIXTEEN_DIGITS = new DecimalFormat("0000000000000000");
  
  protected HashMap<String, PdfObject> documentFileAttachment;
  
  protected String openActionName;
  
  protected PdfAction openActionAction;
  
  protected PdfDictionary additionalActions;
  
  protected PdfCollection collection;
  
  PdfAnnotationsImp annotationsImp;
  
  protected int markPoint;
  
  protected Rectangle nextPageSize;
  
  protected HashMap<String, PdfRectangle> thisBoxSize;
  
  protected HashMap<String, PdfRectangle> boxSize;
  
  private boolean pageEmpty;
  
  protected PdfDictionary pageAA;
  
  protected PageResources pageResources;
  
  protected boolean strictImageSequence;
  
  protected float imageEnd;
  
  protected Image imageWait;
  
  void addJavaScript(PdfAction js) {
    if (js.get(PdfName.JS) == null)
      throw new RuntimeException(MessageLocalization.getComposedMessage("only.javascript.actions.are.allowed", new Object[0])); 
    try {
      this.documentLevelJS.put(SIXTEEN_DIGITS.format(this.jsCounter++), this.writer.addToBody(js).getIndirectReference());
    } catch (IOException e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  void addJavaScript(String name, PdfAction js) {
    if (js.get(PdfName.JS) == null)
      throw new RuntimeException(MessageLocalization.getComposedMessage("only.javascript.actions.are.allowed", new Object[0])); 
    try {
      this.documentLevelJS.put(name, this.writer.addToBody(js).getIndirectReference());
    } catch (IOException e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  HashMap<String, PdfObject> getDocumentLevelJS() {
    return this.documentLevelJS;
  }
  
  void addFileAttachment(String description, PdfFileSpecification fs) throws IOException {
    if (description == null) {
      PdfString desc = (PdfString)fs.get(PdfName.DESC);
      if (desc == null) {
        description = "";
      } else {
        description = PdfEncodings.convertToString(desc.getBytes(), null);
      } 
    } 
    fs.addDescription(description, true);
    if (description.length() == 0)
      description = "Unnamed"; 
    String fn = PdfEncodings.convertToString((new PdfString(description, "UnicodeBig")).getBytes(), null);
    int k = 0;
    while (this.documentFileAttachment.containsKey(fn)) {
      k++;
      fn = PdfEncodings.convertToString((new PdfString(description + " " + k, "UnicodeBig")).getBytes(), null);
    } 
    this.documentFileAttachment.put(fn, fs.getReference());
  }
  
  HashMap<String, PdfObject> getDocumentFileAttachment() {
    return this.documentFileAttachment;
  }
  
  void setOpenAction(String name) {
    this.openActionName = name;
    this.openActionAction = null;
  }
  
  void setOpenAction(PdfAction action) {
    this.openActionAction = action;
    this.openActionName = null;
  }
  
  void addAdditionalAction(PdfName actionType, PdfAction action) {
    if (this.additionalActions == null)
      this.additionalActions = new PdfDictionary(); 
    if (action == null) {
      this.additionalActions.remove(actionType);
    } else {
      this.additionalActions.put(actionType, action);
    } 
    if (this.additionalActions.size() == 0)
      this.additionalActions = null; 
  }
  
  public void setCollection(PdfCollection collection) {
    this.collection = collection;
  }
  
  PdfAcroForm getAcroForm() {
    return this.annotationsImp.getAcroForm();
  }
  
  void setSigFlags(int f) {
    this.annotationsImp.setSigFlags(f);
  }
  
  void addCalculationOrder(PdfFormField formField) {
    this.annotationsImp.addCalculationOrder(formField);
  }
  
  void addAnnotation(PdfAnnotation annot) {
    this.pageEmpty = false;
    this.annotationsImp.addAnnotation(annot);
  }
  
  int getMarkPoint() {
    return this.markPoint;
  }
  
  void incMarkPoint() {
    this.markPoint++;
  }
  
  void setCropBoxSize(Rectangle crop) {
    setBoxSize("crop", crop);
  }
  
  void setBoxSize(String boxName, Rectangle size) {
    if (size == null) {
      this.boxSize.remove(boxName);
    } else {
      this.boxSize.put(boxName, new PdfRectangle(size));
    } 
  }
  
  protected void setNewPageSizeAndMargins() {
    this.pageSize = this.nextPageSize;
    if (this.marginMirroring && (getPageNumber() & 0x1) == 0) {
      this.marginRight = this.nextMarginLeft;
      this.marginLeft = this.nextMarginRight;
    } else {
      this.marginLeft = this.nextMarginLeft;
      this.marginRight = this.nextMarginRight;
    } 
    if (this.marginMirroringTopBottom && (getPageNumber() & 0x1) == 0) {
      this.marginTop = this.nextMarginBottom;
      this.marginBottom = this.nextMarginTop;
    } else {
      this.marginTop = this.nextMarginTop;
      this.marginBottom = this.nextMarginBottom;
    } 
    this.text = new PdfContentByte(this.writer);
    this.text.reset();
    this.text.beginText();
    this.textEmptySize = this.text.size();
    this.text.moveText(left(), top());
  }
  
  Rectangle getBoxSize(String boxName) {
    PdfRectangle r = this.thisBoxSize.get(boxName);
    if (r != null)
      return r.getRectangle(); 
    return null;
  }
  
  void setPageEmpty(boolean pageEmpty) {
    this.pageEmpty = pageEmpty;
  }
  
  boolean isPageEmpty() {
    return (this.writer == null || (this.writer.getDirectContent().size() == 0 && this.writer.getDirectContentUnder().size() == 0 && (this.pageEmpty || this.writer.isPaused())));
  }
  
  void setDuration(int seconds) {
    if (seconds > 0)
      this.writer.addPageDictEntry(PdfName.DUR, new PdfNumber(seconds)); 
  }
  
  void setTransition(PdfTransition transition) {
    this.writer.addPageDictEntry(PdfName.TRANS, transition.getTransitionDictionary());
  }
  
  void setPageAction(PdfName actionType, PdfAction action) {
    if (this.pageAA == null)
      this.pageAA = new PdfDictionary(); 
    this.pageAA.put(actionType, action);
  }
  
  void setThumbnail(Image image) throws PdfException, DocumentException {
    this.writer.addPageDictEntry(PdfName.THUMB, this.writer.getImageReference(this.writer.addDirectImageSimple(image)));
  }
  
  PageResources getPageResources() {
    return this.pageResources;
  }
  
  boolean isStrictImageSequence() {
    return this.strictImageSequence;
  }
  
  void setStrictImageSequence(boolean strictImageSequence) {
    this.strictImageSequence = strictImageSequence;
  }
  
  public void clearTextWrap() {
    float tmpHeight = this.imageEnd - this.currentHeight;
    if (this.line != null)
      tmpHeight += this.line.height(); 
    if (this.imageEnd > -1.0F && tmpHeight > 0.0F) {
      carriageReturn();
      this.currentHeight += tmpHeight;
    } 
  }
  
  protected void add(Image image) throws PdfException, DocumentException {
    if (image.hasAbsoluteY()) {
      this.graphics.addImage(image);
      this.pageEmpty = false;
      return;
    } 
    if (this.currentHeight != 0.0F && indentTop() - this.currentHeight - image.getScaledHeight() < indentBottom()) {
      if (!this.strictImageSequence && this.imageWait == null) {
        this.imageWait = image;
        return;
      } 
      newPage();
      if (this.currentHeight != 0.0F && indentTop() - this.currentHeight - image.getScaledHeight() < indentBottom()) {
        this.imageWait = image;
        return;
      } 
    } 
    this.pageEmpty = false;
    if (image == this.imageWait)
      this.imageWait = null; 
    boolean textwrap = ((image.getAlignment() & 0x4) == 4 && (image.getAlignment() & 0x1) != 1);
    boolean underlying = ((image.getAlignment() & 0x8) == 8);
    float diff = this.leading / 2.0F;
    if (textwrap)
      diff += this.leading; 
    float lowerleft = indentTop() - this.currentHeight - image.getScaledHeight() - diff;
    float[] mt = image.matrix();
    float startPosition = indentLeft() - mt[4];
    if ((image.getAlignment() & 0x2) == 2)
      startPosition = indentRight() - image.getScaledWidth() - mt[4]; 
    if ((image.getAlignment() & 0x1) == 1)
      startPosition = indentLeft() + (indentRight() - indentLeft() - image.getScaledWidth()) / 2.0F - mt[4]; 
    if (image.hasAbsoluteX())
      startPosition = image.getAbsoluteX(); 
    if (textwrap) {
      if (this.imageEnd < 0.0F || this.imageEnd < this.currentHeight + image.getScaledHeight() + diff)
        this.imageEnd = this.currentHeight + image.getScaledHeight() + diff; 
      if ((image.getAlignment() & 0x2) == 2) {
        this.indentation.imageIndentRight += image.getScaledWidth() + image.getIndentationLeft();
      } else {
        this.indentation.imageIndentLeft += image.getScaledWidth() + image.getIndentationRight();
      } 
    } else if ((image.getAlignment() & 0x2) == 2) {
      startPosition -= image.getIndentationRight();
    } else if ((image.getAlignment() & 0x1) == 1) {
      startPosition += image.getIndentationLeft() - image.getIndentationRight();
    } else {
      startPosition += image.getIndentationLeft();
    } 
    this.graphics.addImage(image, mt[0], mt[1], mt[2], mt[3], startPosition, lowerleft - mt[5]);
    if (!textwrap && !underlying) {
      this.currentHeight += image.getScaledHeight() + diff;
      flushLines();
      this.text.moveText(0.0F, -(image.getScaledHeight() + diff));
      newLine();
    } 
  }
  
  void addPTable(PdfPTable ptable) throws DocumentException {
    ColumnText ct = new ColumnText(this.writer.getDirectContent());
    if (ptable.getKeepTogether() && !fitsPage(ptable, 0.0F) && this.currentHeight > 0.0F)
      newPage(); 
    if (this.currentHeight == 0.0F)
      ct.setAdjustFirstLine(false); 
    ct.addElement((Element)ptable);
    boolean he = ptable.isHeadersInEvent();
    ptable.setHeadersInEvent(true);
    int loop = 0;
    while (true) {
      ct.setSimpleColumn(indentLeft(), indentBottom(), indentRight(), indentTop() - this.currentHeight);
      int status = ct.go();
      if ((status & 0x1) != 0) {
        this.text.moveText(0.0F, ct.getYLine() - indentTop() + this.currentHeight);
        this.currentHeight = indentTop() - ct.getYLine();
        break;
      } 
      if (indentTop() - this.currentHeight == ct.getYLine()) {
        loop++;
      } else {
        loop = 0;
      } 
      if (loop == 3)
        throw new DocumentException(MessageLocalization.getComposedMessage("infinite.table.loop", new Object[0])); 
      newPage();
    } 
    ptable.setHeadersInEvent(he);
  }
  
  boolean fitsPage(PdfPTable table, float margin) {
    if (!table.isLockedWidth()) {
      float totalWidth = (indentRight() - indentLeft()) * table.getWidthPercentage() / 100.0F;
      table.setTotalWidth(totalWidth);
    } 
    ensureNewLine();
    return (table.getTotalHeight() + ((this.currentHeight > 0.0F) ? table.spacingBefore() : 0.0F) <= indentTop() - this.currentHeight - indentBottom() - margin);
  }
  
  public class Destination {
    public PdfAction action;
    
    public PdfIndirectReference reference;
    
    public PdfDestination destination;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfDocument.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */