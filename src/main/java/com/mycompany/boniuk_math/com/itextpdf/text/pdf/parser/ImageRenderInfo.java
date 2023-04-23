package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PRStream;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfIndirectReference;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfReader;
import java.io.IOException;

public class ImageRenderInfo {
  private final Matrix ctm;
  
  private final PdfIndirectReference ref;
  
  private PdfImageObject imageObject = null;
  
  private ImageRenderInfo(Matrix ctm, PdfIndirectReference ref) {
    this.ctm = ctm;
    this.ref = ref;
  }
  
  public static ImageRenderInfo createForXObject(Matrix ctm, PdfIndirectReference ref) {
    return new ImageRenderInfo(ctm, ref);
  }
  
  protected static ImageRenderInfo createdForEmbeddedImage(Matrix ctm, PdfImageObject imageObject) {
    ImageRenderInfo renderInfo = new ImageRenderInfo(ctm, null);
    renderInfo.imageObject = imageObject;
    return renderInfo;
  }
  
  public PdfImageObject getImage() {
    try {
      prepareImageObject();
      return this.imageObject;
    } catch (IOException e) {
      return null;
    } 
  }
  
  private void prepareImageObject() throws IOException {
    if (this.imageObject != null)
      return; 
    PRStream stream = (PRStream)PdfReader.getPdfObject((PdfObject)this.ref);
    this.imageObject = new PdfImageObject(stream);
  }
  
  public Vector getStartPoint() {
    return (new Vector(0.0F, 0.0F, 1.0F)).cross(this.ctm);
  }
  
  public Matrix getImageCTM() {
    return this.ctm;
  }
  
  public float getArea() {
    return this.ctm.getDeterminant();
  }
  
  public PdfIndirectReference getRef() {
    return this.ref;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\ImageRenderInfo.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */