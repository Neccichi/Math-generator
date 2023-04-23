package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import java.io.IOException;

public class PdfTemplate extends PdfContentByte {
  public static final int TYPE_TEMPLATE = 1;
  
  public static final int TYPE_IMPORTED = 2;
  
  public static final int TYPE_PATTERN = 3;
  
  protected int type;
  
  protected PdfIndirectReference thisReference;
  
  protected PageResources pageResources;
  
  protected Rectangle bBox = new Rectangle(0.0F, 0.0F);
  
  protected PdfArray matrix;
  
  protected PdfTransparencyGroup group;
  
  protected PdfOCG layer;
  
  private PdfDictionary additional = null;
  
  protected PdfTemplate() {
    super(null);
    this.type = 1;
  }
  
  PdfTemplate(PdfWriter wr) {
    super(wr);
    this.type = 1;
    this.pageResources = new PageResources();
    this.pageResources.addDefaultColor(wr.getDefaultColorspace());
    this.thisReference = this.writer.getPdfIndirectReference();
  }
  
  public static PdfTemplate createTemplate(PdfWriter writer, float width, float height) {
    return createTemplate(writer, width, height, (PdfName)null);
  }
  
  static PdfTemplate createTemplate(PdfWriter writer, float width, float height, PdfName forcedName) {
    PdfTemplate template = new PdfTemplate(writer);
    template.setWidth(width);
    template.setHeight(height);
    writer.addDirectTemplateSimple(template, forcedName);
    return template;
  }
  
  public void setWidth(float width) {
    this.bBox.setLeft(0.0F);
    this.bBox.setRight(width);
  }
  
  public void setHeight(float height) {
    this.bBox.setBottom(0.0F);
    this.bBox.setTop(height);
  }
  
  public float getWidth() {
    return this.bBox.getWidth();
  }
  
  public float getHeight() {
    return this.bBox.getHeight();
  }
  
  public Rectangle getBoundingBox() {
    return this.bBox;
  }
  
  public void setBoundingBox(Rectangle bBox) {
    this.bBox = bBox;
  }
  
  public void setLayer(PdfOCG layer) {
    this.layer = layer;
  }
  
  public PdfOCG getLayer() {
    return this.layer;
  }
  
  public void setMatrix(float a, float b, float c, float d, float e, float f) {
    this.matrix = new PdfArray();
    this.matrix.add(new PdfNumber(a));
    this.matrix.add(new PdfNumber(b));
    this.matrix.add(new PdfNumber(c));
    this.matrix.add(new PdfNumber(d));
    this.matrix.add(new PdfNumber(e));
    this.matrix.add(new PdfNumber(f));
  }
  
  PdfArray getMatrix() {
    return this.matrix;
  }
  
  public PdfIndirectReference getIndirectReference() {
    if (this.thisReference == null)
      this.thisReference = this.writer.getPdfIndirectReference(); 
    return this.thisReference;
  }
  
  public void beginVariableText() {
    this.content.append("/Tx BMC ");
  }
  
  public void endVariableText() {
    this.content.append("EMC ");
  }
  
  PdfObject getResources() {
    return getPageResources().getResources();
  }
  
  PdfStream getFormXObject(int compressionLevel) throws IOException {
    return new PdfFormXObject(this, compressionLevel);
  }
  
  public PdfContentByte getDuplicate() {
    PdfTemplate tpl = new PdfTemplate();
    tpl.writer = this.writer;
    tpl.pdf = this.pdf;
    tpl.thisReference = this.thisReference;
    tpl.pageResources = this.pageResources;
    tpl.bBox = new Rectangle(this.bBox);
    tpl.group = this.group;
    tpl.layer = this.layer;
    if (this.matrix != null)
      tpl.matrix = new PdfArray(this.matrix); 
    tpl.separator = this.separator;
    tpl.additional = this.additional;
    return tpl;
  }
  
  public int getType() {
    return this.type;
  }
  
  PageResources getPageResources() {
    return this.pageResources;
  }
  
  public PdfTransparencyGroup getGroup() {
    return this.group;
  }
  
  public void setGroup(PdfTransparencyGroup group) {
    this.group = group;
  }
  
  public PdfDictionary getAdditional() {
    return this.additional;
  }
  
  public void setAdditional(PdfDictionary additional) {
    this.additional = additional;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfTemplate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */