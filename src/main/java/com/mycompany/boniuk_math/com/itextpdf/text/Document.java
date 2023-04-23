package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Document implements DocListener {
  private static final String ITEXT = "iText®";
  
  private static final String RELEASE = "5.1.1";
  
  private static final String ITEXT_VERSION = "iText® 5.1.1 ©2000-2011 1T3XT BVBA";
  
  public static boolean compress = true;
  
  public static boolean plainRandomAccess = false;
  
  public static float wmfFontCorrection = 0.86F;
  
  protected ArrayList<DocListener> listeners = new ArrayList<DocListener>();
  
  protected boolean open;
  
  protected boolean close;
  
  protected Rectangle pageSize;
  
  protected float marginLeft = 0.0F;
  
  protected float marginRight = 0.0F;
  
  protected float marginTop = 0.0F;
  
  protected float marginBottom = 0.0F;
  
  protected boolean marginMirroring = false;
  
  protected boolean marginMirroringTopBottom = false;
  
  protected String javaScript_onLoad = null;
  
  protected String javaScript_onUnLoad = null;
  
  protected String htmlStyleClass = null;
  
  protected int pageN = 0;
  
  protected int chapternumber = 0;
  
  public Document() {
    this(PageSize.A4);
  }
  
  public Document(Rectangle pageSize) {
    this(pageSize, 36.0F, 36.0F, 36.0F, 36.0F);
  }
  
  public Document(Rectangle pageSize, float marginLeft, float marginRight, float marginTop, float marginBottom) {
    this.pageSize = pageSize;
    this.marginLeft = marginLeft;
    this.marginRight = marginRight;
    this.marginTop = marginTop;
    this.marginBottom = marginBottom;
  }
  
  public void addDocListener(DocListener listener) {
    this.listeners.add(listener);
  }
  
  public void removeDocListener(DocListener listener) {
    this.listeners.remove(listener);
  }
  
  public boolean add(Element element) throws DocumentException {
    if (this.close)
      throw new DocumentException(MessageLocalization.getComposedMessage("the.document.has.been.closed.you.can.t.add.any.elements", new Object[0])); 
    if (!this.open && element.isContent())
      throw new DocumentException(MessageLocalization.getComposedMessage("the.document.is.not.open.yet.you.can.only.add.meta.information", new Object[0])); 
    boolean success = false;
    if (element instanceof ChapterAutoNumber)
      this.chapternumber = ((ChapterAutoNumber)element).setAutomaticNumber(this.chapternumber); 
    for (DocListener listener : this.listeners)
      success |= listener.add(element); 
    if (element instanceof LargeElement) {
      LargeElement e = (LargeElement)element;
      if (!e.isComplete())
        e.flushContent(); 
    } 
    return success;
  }
  
  public void open() {
    if (!this.close)
      this.open = true; 
    for (DocListener listener : this.listeners) {
      listener.setPageSize(this.pageSize);
      listener.setMargins(this.marginLeft, this.marginRight, this.marginTop, this.marginBottom);
      listener.open();
    } 
  }
  
  public boolean setPageSize(Rectangle pageSize) {
    this.pageSize = pageSize;
    for (DocListener listener : this.listeners)
      listener.setPageSize(pageSize); 
    return true;
  }
  
  public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
    this.marginLeft = marginLeft;
    this.marginRight = marginRight;
    this.marginTop = marginTop;
    this.marginBottom = marginBottom;
    for (DocListener listener : this.listeners)
      listener.setMargins(marginLeft, marginRight, marginTop, marginBottom); 
    return true;
  }
  
  public boolean newPage() {
    if (!this.open || this.close)
      return false; 
    for (DocListener listener : this.listeners)
      listener.newPage(); 
    return true;
  }
  
  public void resetPageCount() {
    this.pageN = 0;
    for (DocListener listener : this.listeners)
      listener.resetPageCount(); 
  }
  
  public void setPageCount(int pageN) {
    this.pageN = pageN;
    for (DocListener listener : this.listeners)
      listener.setPageCount(pageN); 
  }
  
  public int getPageNumber() {
    return this.pageN;
  }
  
  public void close() {
    if (!this.close) {
      this.open = false;
      this.close = true;
    } 
    for (DocListener listener : this.listeners)
      listener.close(); 
  }
  
  public boolean addHeader(String name, String content) {
    try {
      return add(new Header(name, content));
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public boolean addTitle(String title) {
    try {
      return add(new Meta(1, title));
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public boolean addSubject(String subject) {
    try {
      return add(new Meta(2, subject));
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public boolean addKeywords(String keywords) {
    try {
      return add(new Meta(3, keywords));
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public boolean addAuthor(String author) {
    try {
      return add(new Meta(4, author));
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public boolean addCreator(String creator) {
    try {
      return add(new Meta(7, creator));
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public boolean addProducer() {
    try {
      return add(new Meta(5, getVersion()));
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public boolean addCreationDate() {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
      return add(new Meta(6, sdf.format(new Date())));
    } catch (DocumentException de) {
      throw new ExceptionConverter(de);
    } 
  }
  
  public float leftMargin() {
    return this.marginLeft;
  }
  
  public float rightMargin() {
    return this.marginRight;
  }
  
  public float topMargin() {
    return this.marginTop;
  }
  
  public float bottomMargin() {
    return this.marginBottom;
  }
  
  public float left() {
    return this.pageSize.getLeft(this.marginLeft);
  }
  
  public float right() {
    return this.pageSize.getRight(this.marginRight);
  }
  
  public float top() {
    return this.pageSize.getTop(this.marginTop);
  }
  
  public float bottom() {
    return this.pageSize.getBottom(this.marginBottom);
  }
  
  public float left(float margin) {
    return this.pageSize.getLeft(this.marginLeft + margin);
  }
  
  public float right(float margin) {
    return this.pageSize.getRight(this.marginRight + margin);
  }
  
  public float top(float margin) {
    return this.pageSize.getTop(this.marginTop + margin);
  }
  
  public float bottom(float margin) {
    return this.pageSize.getBottom(this.marginBottom + margin);
  }
  
  public Rectangle getPageSize() {
    return this.pageSize;
  }
  
  public boolean isOpen() {
    return this.open;
  }
  
  public static final String getProduct() {
    return "iText®";
  }
  
  public static final String getRelease() {
    return "5.1.1";
  }
  
  public static final String getVersion() {
    return "iText® 5.1.1 ©2000-2011 1T3XT BVBA";
  }
  
  public void setJavaScript_onLoad(String code) {
    this.javaScript_onLoad = code;
  }
  
  public String getJavaScript_onLoad() {
    return this.javaScript_onLoad;
  }
  
  public void setJavaScript_onUnLoad(String code) {
    this.javaScript_onUnLoad = code;
  }
  
  public String getJavaScript_onUnLoad() {
    return this.javaScript_onUnLoad;
  }
  
  public void setHtmlStyleClass(String htmlStyleClass) {
    this.htmlStyleClass = htmlStyleClass;
  }
  
  public String getHtmlStyleClass() {
    return this.htmlStyleClass;
  }
  
  public boolean setMarginMirroring(boolean marginMirroring) {
    this.marginMirroring = marginMirroring;
    for (DocListener element : this.listeners) {
      DocListener listener = element;
      listener.setMarginMirroring(marginMirroring);
    } 
    return true;
  }
  
  public boolean setMarginMirroringTopBottom(boolean marginMirroringTopBottom) {
    this.marginMirroringTopBottom = marginMirroringTopBottom;
    for (DocListener element : this.listeners) {
      DocListener listener = element;
      listener.setMarginMirroringTopBottom(marginMirroringTopBottom);
    } 
    return true;
  }
  
  public boolean isMarginMirroring() {
    return this.marginMirroring;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\Document.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */