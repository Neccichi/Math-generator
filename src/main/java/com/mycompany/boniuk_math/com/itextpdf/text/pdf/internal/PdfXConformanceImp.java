package com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.BaseFont;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.ExtendedColor;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PatternColor;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfArray;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfGState;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfImage;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfNumber;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfString;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfWriter;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfXConformanceException;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.ShadingColor;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.SpotColor;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces.PdfXConformance;

public class PdfXConformanceImp implements PdfXConformance {
  public static final int PDFXKEY_COLOR = 1;
  
  public static final int PDFXKEY_CMYK = 2;
  
  public static final int PDFXKEY_RGB = 3;
  
  public static final int PDFXKEY_FONT = 4;
  
  public static final int PDFXKEY_IMAGE = 5;
  
  public static final int PDFXKEY_GSTATE = 6;
  
  public static final int PDFXKEY_LAYER = 7;
  
  protected int pdfxConformance = 0;
  
  public void setPDFXConformance(int pdfxConformance) {
    this.pdfxConformance = pdfxConformance;
  }
  
  public int getPDFXConformance() {
    return this.pdfxConformance;
  }
  
  public boolean isPdfX() {
    return (this.pdfxConformance != 0);
  }
  
  public boolean isPdfX1A2001() {
    return (this.pdfxConformance == 1);
  }
  
  public boolean isPdfX32002() {
    return (this.pdfxConformance == 2);
  }
  
  public boolean isPdfA1() {
    return (this.pdfxConformance == 3 || this.pdfxConformance == 4);
  }
  
  public boolean isPdfA1A() {
    return (this.pdfxConformance == 3);
  }
  
  public void completeInfoDictionary(PdfDictionary info) {
    if (isPdfX() && !isPdfA1()) {
      if (info.get(PdfName.GTS_PDFXVERSION) == null)
        if (isPdfX1A2001()) {
          info.put(PdfName.GTS_PDFXVERSION, (PdfObject)new PdfString("PDF/X-1:2001"));
          info.put(new PdfName("GTS_PDFXConformance"), (PdfObject)new PdfString("PDF/X-1a:2001"));
        } else if (isPdfX32002()) {
          info.put(PdfName.GTS_PDFXVERSION, (PdfObject)new PdfString("PDF/X-3:2002"));
        }  
      if (info.get(PdfName.TITLE) == null)
        info.put(PdfName.TITLE, (PdfObject)new PdfString("Pdf document")); 
      if (info.get(PdfName.CREATOR) == null)
        info.put(PdfName.CREATOR, (PdfObject)new PdfString("Unknown")); 
      if (info.get(PdfName.TRAPPED) == null)
        info.put(PdfName.TRAPPED, (PdfObject)new PdfName("False")); 
    } 
  }
  
  public void completeExtraCatalog(PdfDictionary extraCatalog) {
    if (isPdfX() && !isPdfA1() && 
      extraCatalog.get(PdfName.OUTPUTINTENTS) == null) {
      PdfDictionary out = new PdfDictionary(PdfName.OUTPUTINTENT);
      out.put(PdfName.OUTPUTCONDITION, (PdfObject)new PdfString("SWOP CGATS TR 001-1995"));
      out.put(PdfName.OUTPUTCONDITIONIDENTIFIER, (PdfObject)new PdfString("CGATS TR 001"));
      out.put(PdfName.REGISTRYNAME, (PdfObject)new PdfString("http://www.color.org"));
      out.put(PdfName.INFO, (PdfObject)new PdfString(""));
      out.put(PdfName.S, (PdfObject)PdfName.GTS_PDFX);
      extraCatalog.put(PdfName.OUTPUTINTENTS, (PdfObject)new PdfArray((PdfObject)out));
    } 
  }
  
  public static void checkPDFXConformance(PdfWriter writer, int key, Object obj1) {
    PdfImage image;
    PdfObject cs;
    PdfDictionary gs;
    PdfObject obj;
    double v;
    if (writer == null || !writer.isPdfX())
      return; 
    int conf = writer.getPDFXConformance();
    switch (key) {
      case 1:
        switch (conf) {
          case 1:
            if (obj1 instanceof ExtendedColor) {
              SpotColor sc;
              ShadingColor xc;
              PatternColor pc;
              ExtendedColor ec = (ExtendedColor)obj1;
              switch (ec.getType()) {
                case 1:
                case 2:
                  return;
                case 0:
                  throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.rgb.is.not.allowed", new Object[0]));
                case 3:
                  sc = (SpotColor)ec;
                  checkPDFXConformance(writer, 1, sc.getPdfSpotColor().getAlternativeCS());
                  break;
                case 5:
                  xc = (ShadingColor)ec;
                  checkPDFXConformance(writer, 1, xc.getPdfShadingPattern().getShading().getColorSpace());
                  break;
                case 4:
                  pc = (PatternColor)ec;
                  checkPDFXConformance(writer, 1, pc.getPainter().getDefaultColor());
                  break;
              } 
              break;
            } 
            if (obj1 instanceof com.itextpdf.text.BaseColor)
              throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.rgb.is.not.allowed", new Object[0])); 
            break;
        } 
        break;
      case 3:
        if (conf == 1)
          throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.rgb.is.not.allowed", new Object[0])); 
        break;
      case 4:
        if (!((BaseFont)obj1).isEmbedded())
          throw new PdfXConformanceException(MessageLocalization.getComposedMessage("all.the.fonts.must.be.embedded.this.one.isn.t.1", new Object[] { ((BaseFont)obj1).getPostscriptFontName() })); 
        break;
      case 5:
        image = (PdfImage)obj1;
        if (image.get(PdfName.SMASK) != null)
          throw new PdfXConformanceException(MessageLocalization.getComposedMessage("the.smask.key.is.not.allowed.in.images", new Object[0])); 
        switch (conf) {
          case 1:
            cs = image.get(PdfName.COLORSPACE);
            if (cs == null)
              return; 
            if (cs.isName()) {
              if (PdfName.DEVICERGB.equals(cs))
                throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.rgb.is.not.allowed", new Object[0])); 
              break;
            } 
            if (cs.isArray() && 
              PdfName.CALRGB.equals(((PdfArray)cs).getPdfObject(0)))
              throw new PdfXConformanceException(MessageLocalization.getComposedMessage("colorspace.calrgb.is.not.allowed", new Object[0])); 
            break;
        } 
        break;
      case 6:
        gs = (PdfDictionary)obj1;
        obj = gs.get(PdfName.BM);
        if (obj != null && !PdfGState.BM_NORMAL.equals(obj) && !PdfGState.BM_COMPATIBLE.equals(obj))
          throw new PdfXConformanceException(MessageLocalization.getComposedMessage("blend.mode.1.not.allowed", new Object[] { obj.toString() })); 
        obj = gs.get(PdfName.CA);
        v = 0.0D;
        if (obj != null && (v = ((PdfNumber)obj).doubleValue()) != 1.0D)
          throw new PdfXConformanceException(MessageLocalization.getComposedMessage("transparency.is.not.allowed.ca.eq.1", new Object[] { String.valueOf(v) })); 
        obj = gs.get(PdfName.ca);
        v = 0.0D;
        if (obj != null && (v = ((PdfNumber)obj).doubleValue()) != 1.0D)
          throw new PdfXConformanceException(MessageLocalization.getComposedMessage("transparency.is.not.allowed.ca.eq.1", new Object[] { String.valueOf(v) })); 
        break;
      case 7:
        throw new PdfXConformanceException(MessageLocalization.getComposedMessage("layers.are.not.allowed", new Object[0]));
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\internal\PdfXConformanceImp.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */