package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.Annotation;
import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.ImgJBIG2;
import com.mycompany.boniuk_math.com.itextpdf.text.Rectangle;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.exceptions.IllegalPdfSyntaxException;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal.PdfAnnotationsImp;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.internal.PdfXConformanceImp;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.HashMap;

public class PdfContentByte {
  public static final int ALIGN_CENTER = 1;
  
  public static final int ALIGN_LEFT = 0;
  
  public static final int ALIGN_RIGHT = 2;
  
  public static final int LINE_CAP_BUTT = 0;
  
  public static final int LINE_CAP_ROUND = 1;
  
  public static final int LINE_CAP_PROJECTING_SQUARE = 2;
  
  public static final int LINE_JOIN_MITER = 0;
  
  public static final int LINE_JOIN_ROUND = 1;
  
  public static final int LINE_JOIN_BEVEL = 2;
  
  public static final int TEXT_RENDER_MODE_FILL = 0;
  
  public static final int TEXT_RENDER_MODE_STROKE = 1;
  
  public static final int TEXT_RENDER_MODE_FILL_STROKE = 2;
  
  public static final int TEXT_RENDER_MODE_INVISIBLE = 3;
  
  public static final int TEXT_RENDER_MODE_FILL_CLIP = 4;
  
  public static final int TEXT_RENDER_MODE_STROKE_CLIP = 5;
  
  public static final int TEXT_RENDER_MODE_FILL_STROKE_CLIP = 6;
  
  public static final int TEXT_RENDER_MODE_CLIP = 7;
  
  static class GraphicState {
    FontDetails fontDetails;
    
    ColorDetails colorDetails;
    
    float size;
    
    protected float xTLM = 0.0F;
    
    protected float yTLM = 0.0F;
    
    protected float leading = 0.0F;
    
    protected float scale = 100.0F;
    
    protected float charSpace = 0.0F;
    
    protected float wordSpace = 0.0F;
    
    GraphicState() {}
    
    GraphicState(GraphicState cp) {
      this.fontDetails = cp.fontDetails;
      this.colorDetails = cp.colorDetails;
      this.size = cp.size;
      this.xTLM = cp.xTLM;
      this.yTLM = cp.yTLM;
      this.leading = cp.leading;
      this.scale = cp.scale;
      this.charSpace = cp.charSpace;
      this.wordSpace = cp.wordSpace;
    }
  }
  
  private static final float[] unitRect = new float[] { 0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F };
  
  protected ByteBuffer content = new ByteBuffer();
  
  protected PdfWriter writer;
  
  protected PdfDocument pdf;
  
  protected GraphicState state = new GraphicState();
  
  protected ArrayList<GraphicState> stateList = new ArrayList<GraphicState>();
  
  protected ArrayList<Integer> layerDepth;
  
  protected int separator = 10;
  
  private int mcDepth = 0;
  
  private boolean inText = false;
  
  private static HashMap<PdfName, String> abrev = new HashMap<PdfName, String>();
  
  static {
    abrev.put(PdfName.BITSPERCOMPONENT, "/BPC ");
    abrev.put(PdfName.COLORSPACE, "/CS ");
    abrev.put(PdfName.DECODE, "/D ");
    abrev.put(PdfName.DECODEPARMS, "/DP ");
    abrev.put(PdfName.FILTER, "/F ");
    abrev.put(PdfName.HEIGHT, "/H ");
    abrev.put(PdfName.IMAGEMASK, "/IM ");
    abrev.put(PdfName.INTENT, "/Intent ");
    abrev.put(PdfName.INTERPOLATE, "/I ");
    abrev.put(PdfName.WIDTH, "/W ");
  }
  
  public PdfContentByte(PdfWriter wr) {
    if (wr != null) {
      this.writer = wr;
      this.pdf = this.writer.getPdfDocument();
    } 
  }
  
  public String toString() {
    return this.content.toString();
  }
  
  public ByteBuffer getInternalBuffer() {
    return this.content;
  }
  
  public byte[] toPdf(PdfWriter writer) {
    sanityCheck();
    return this.content.toByteArray();
  }
  
  public void add(PdfContentByte other) {
    if (other.writer != null && this.writer != other.writer)
      throw new RuntimeException(MessageLocalization.getComposedMessage("inconsistent.writers.are.you.mixing.two.documents", new Object[0])); 
    this.content.append(other.content);
  }
  
  public float getXTLM() {
    return this.state.xTLM;
  }
  
  public float getYTLM() {
    return this.state.yTLM;
  }
  
  public float getLeading() {
    return this.state.leading;
  }
  
  public float getCharacterSpacing() {
    return this.state.charSpace;
  }
  
  public float getWordSpacing() {
    return this.state.wordSpace;
  }
  
  public float getHorizontalScaling() {
    return this.state.scale;
  }
  
  public void setFlatness(float flatness) {
    if (flatness >= 0.0F && flatness <= 100.0F)
      this.content.append(flatness).append(" i").append_i(this.separator); 
  }
  
  public void setLineCap(int style) {
    if (style >= 0 && style <= 2)
      this.content.append(style).append(" J").append_i(this.separator); 
  }
  
  public void setLineDash(float phase) {
    this.content.append("[] ").append(phase).append(" d").append_i(this.separator);
  }
  
  public void setLineDash(float unitsOn, float phase) {
    this.content.append("[").append(unitsOn).append("] ").append(phase).append(" d").append_i(this.separator);
  }
  
  public void setLineDash(float unitsOn, float unitsOff, float phase) {
    this.content.append("[").append(unitsOn).append(' ').append(unitsOff).append("] ").append(phase).append(" d").append_i(this.separator);
  }
  
  public final void setLineDash(float[] array, float phase) {
    this.content.append("[");
    for (int i = 0; i < array.length; i++) {
      this.content.append(array[i]);
      if (i < array.length - 1)
        this.content.append(' '); 
    } 
    this.content.append("] ").append(phase).append(" d").append_i(this.separator);
  }
  
  public void setLineJoin(int style) {
    if (style >= 0 && style <= 2)
      this.content.append(style).append(" j").append_i(this.separator); 
  }
  
  public void setLineWidth(float w) {
    this.content.append(w).append(" w").append_i(this.separator);
  }
  
  public void setMiterLimit(float miterLimit) {
    if (miterLimit > 1.0F)
      this.content.append(miterLimit).append(" M").append_i(this.separator); 
  }
  
  public void clip() {
    this.content.append("W").append_i(this.separator);
  }
  
  public void eoClip() {
    this.content.append("W*").append_i(this.separator);
  }
  
  public void setGrayFill(float gray) {
    this.content.append(gray).append(" g").append_i(this.separator);
  }
  
  public void resetGrayFill() {
    this.content.append("0 g").append_i(this.separator);
  }
  
  public void setGrayStroke(float gray) {
    this.content.append(gray).append(" G").append_i(this.separator);
  }
  
  public void resetGrayStroke() {
    this.content.append("0 G").append_i(this.separator);
  }
  
  private void HelperRGB(float red, float green, float blue) {
    PdfXConformanceImp.checkPDFXConformance(this.writer, 3, null);
    if (red < 0.0F) {
      red = 0.0F;
    } else if (red > 1.0F) {
      red = 1.0F;
    } 
    if (green < 0.0F) {
      green = 0.0F;
    } else if (green > 1.0F) {
      green = 1.0F;
    } 
    if (blue < 0.0F) {
      blue = 0.0F;
    } else if (blue > 1.0F) {
      blue = 1.0F;
    } 
    this.content.append(red).append(' ').append(green).append(' ').append(blue);
  }
  
  public void setRGBColorFillF(float red, float green, float blue) {
    HelperRGB(red, green, blue);
    this.content.append(" rg").append_i(this.separator);
  }
  
  public void resetRGBColorFill() {
    this.content.append("0 g").append_i(this.separator);
  }
  
  public void setRGBColorStrokeF(float red, float green, float blue) {
    HelperRGB(red, green, blue);
    this.content.append(" RG").append_i(this.separator);
  }
  
  public void resetRGBColorStroke() {
    this.content.append("0 G").append_i(this.separator);
  }
  
  private void HelperCMYK(float cyan, float magenta, float yellow, float black) {
    if (cyan < 0.0F) {
      cyan = 0.0F;
    } else if (cyan > 1.0F) {
      cyan = 1.0F;
    } 
    if (magenta < 0.0F) {
      magenta = 0.0F;
    } else if (magenta > 1.0F) {
      magenta = 1.0F;
    } 
    if (yellow < 0.0F) {
      yellow = 0.0F;
    } else if (yellow > 1.0F) {
      yellow = 1.0F;
    } 
    if (black < 0.0F) {
      black = 0.0F;
    } else if (black > 1.0F) {
      black = 1.0F;
    } 
    this.content.append(cyan).append(' ').append(magenta).append(' ').append(yellow).append(' ').append(black);
  }
  
  public void setCMYKColorFillF(float cyan, float magenta, float yellow, float black) {
    HelperCMYK(cyan, magenta, yellow, black);
    this.content.append(" k").append_i(this.separator);
  }
  
  public void resetCMYKColorFill() {
    this.content.append("0 0 0 1 k").append_i(this.separator);
  }
  
  public void setCMYKColorStrokeF(float cyan, float magenta, float yellow, float black) {
    HelperCMYK(cyan, magenta, yellow, black);
    this.content.append(" K").append_i(this.separator);
  }
  
  public void resetCMYKColorStroke() {
    this.content.append("0 0 0 1 K").append_i(this.separator);
  }
  
  public void moveTo(float x, float y) {
    this.content.append(x).append(' ').append(y).append(" m").append_i(this.separator);
  }
  
  public void lineTo(float x, float y) {
    this.content.append(x).append(' ').append(y).append(" l").append_i(this.separator);
  }
  
  public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
    this.content.append(x1).append(' ').append(y1).append(' ').append(x2).append(' ').append(y2).append(' ').append(x3).append(' ').append(y3).append(" c").append_i(this.separator);
  }
  
  public void curveTo(float x2, float y2, float x3, float y3) {
    this.content.append(x2).append(' ').append(y2).append(' ').append(x3).append(' ').append(y3).append(" v").append_i(this.separator);
  }
  
  public void curveFromTo(float x1, float y1, float x3, float y3) {
    this.content.append(x1).append(' ').append(y1).append(' ').append(x3).append(' ').append(y3).append(" y").append_i(this.separator);
  }
  
  public void circle(float x, float y, float r) {
    float b = 0.5523F;
    moveTo(x + r, y);
    curveTo(x + r, y + r * b, x + r * b, y + r, x, y + r);
    curveTo(x - r * b, y + r, x - r, y + r * b, x - r, y);
    curveTo(x - r, y - r * b, x - r * b, y - r, x, y - r);
    curveTo(x + r * b, y - r, x + r, y - r * b, x + r, y);
  }
  
  public void rectangle(float x, float y, float w, float h) {
    this.content.append(x).append(' ').append(y).append(' ').append(w).append(' ').append(h).append(" re").append_i(this.separator);
  }
  
  private boolean compareColors(BaseColor c1, BaseColor c2) {
    if (c1 == null && c2 == null)
      return true; 
    if (c1 == null || c2 == null)
      return false; 
    if (c1 instanceof ExtendedColor)
      return c1.equals(c2); 
    return c2.equals(c1);
  }
  
  public void variableRectangle(Rectangle rect) {
    float t = rect.getTop();
    float b = rect.getBottom();
    float r = rect.getRight();
    float l = rect.getLeft();
    float wt = rect.getBorderWidthTop();
    float wb = rect.getBorderWidthBottom();
    float wr = rect.getBorderWidthRight();
    float wl = rect.getBorderWidthLeft();
    BaseColor ct = rect.getBorderColorTop();
    BaseColor cb = rect.getBorderColorBottom();
    BaseColor cr = rect.getBorderColorRight();
    BaseColor cl = rect.getBorderColorLeft();
    saveState();
    setLineCap(0);
    setLineJoin(0);
    float clw = 0.0F;
    boolean cdef = false;
    BaseColor ccol = null;
    boolean cdefi = false;
    BaseColor cfil = null;
    if (wt > 0.0F) {
      setLineWidth(clw = wt);
      cdef = true;
      if (ct == null) {
        resetRGBColorStroke();
      } else {
        setColorStroke(ct);
      } 
      ccol = ct;
      moveTo(l, t - wt / 2.0F);
      lineTo(r, t - wt / 2.0F);
      stroke();
    } 
    if (wb > 0.0F) {
      if (wb != clw)
        setLineWidth(clw = wb); 
      if (!cdef || !compareColors(ccol, cb)) {
        cdef = true;
        if (cb == null) {
          resetRGBColorStroke();
        } else {
          setColorStroke(cb);
        } 
        ccol = cb;
      } 
      moveTo(r, b + wb / 2.0F);
      lineTo(l, b + wb / 2.0F);
      stroke();
    } 
    if (wr > 0.0F) {
      if (wr != clw)
        setLineWidth(clw = wr); 
      if (!cdef || !compareColors(ccol, cr)) {
        cdef = true;
        if (cr == null) {
          resetRGBColorStroke();
        } else {
          setColorStroke(cr);
        } 
        ccol = cr;
      } 
      boolean bt = compareColors(ct, cr);
      boolean bb = compareColors(cb, cr);
      moveTo(r - wr / 2.0F, bt ? t : (t - wt));
      lineTo(r - wr / 2.0F, bb ? b : (b + wb));
      stroke();
      if (!bt || !bb) {
        cdefi = true;
        if (cr == null) {
          resetRGBColorFill();
        } else {
          setColorFill(cr);
        } 
        cfil = cr;
        if (!bt) {
          moveTo(r, t);
          lineTo(r, t - wt);
          lineTo(r - wr, t - wt);
          fill();
        } 
        if (!bb) {
          moveTo(r, b);
          lineTo(r, b + wb);
          lineTo(r - wr, b + wb);
          fill();
        } 
      } 
    } 
    if (wl > 0.0F) {
      if (wl != clw)
        setLineWidth(wl); 
      if (!cdef || !compareColors(ccol, cl))
        if (cl == null) {
          resetRGBColorStroke();
        } else {
          setColorStroke(cl);
        }  
      boolean bt = compareColors(ct, cl);
      boolean bb = compareColors(cb, cl);
      moveTo(l + wl / 2.0F, bt ? t : (t - wt));
      lineTo(l + wl / 2.0F, bb ? b : (b + wb));
      stroke();
      if (!bt || !bb) {
        if (!cdefi || !compareColors(cfil, cl))
          if (cl == null) {
            resetRGBColorFill();
          } else {
            setColorFill(cl);
          }  
        if (!bt) {
          moveTo(l, t);
          lineTo(l, t - wt);
          lineTo(l + wl, t - wt);
          fill();
        } 
        if (!bb) {
          moveTo(l, b);
          lineTo(l, b + wb);
          lineTo(l + wl, b + wb);
          fill();
        } 
      } 
    } 
    restoreState();
  }
  
  public void rectangle(Rectangle rectangle) {
    float x1 = rectangle.getLeft();
    float y1 = rectangle.getBottom();
    float x2 = rectangle.getRight();
    float y2 = rectangle.getTop();
    BaseColor background = rectangle.getBackgroundColor();
    if (background != null) {
      saveState();
      setColorFill(background);
      rectangle(x1, y1, x2 - x1, y2 - y1);
      fill();
      restoreState();
    } 
    if (!rectangle.hasBorders())
      return; 
    if (rectangle.isUseVariableBorders()) {
      variableRectangle(rectangle);
    } else {
      if (rectangle.getBorderWidth() != -1.0F)
        setLineWidth(rectangle.getBorderWidth()); 
      BaseColor color = rectangle.getBorderColor();
      if (color != null)
        setColorStroke(color); 
      if (rectangle.hasBorder(15)) {
        rectangle(x1, y1, x2 - x1, y2 - y1);
      } else {
        if (rectangle.hasBorder(8)) {
          moveTo(x2, y1);
          lineTo(x2, y2);
        } 
        if (rectangle.hasBorder(4)) {
          moveTo(x1, y1);
          lineTo(x1, y2);
        } 
        if (rectangle.hasBorder(2)) {
          moveTo(x1, y1);
          lineTo(x2, y1);
        } 
        if (rectangle.hasBorder(1)) {
          moveTo(x1, y2);
          lineTo(x2, y2);
        } 
      } 
      stroke();
      if (color != null)
        resetRGBColorStroke(); 
    } 
  }
  
  public void closePath() {
    this.content.append("h").append_i(this.separator);
  }
  
  public void newPath() {
    this.content.append("n").append_i(this.separator);
  }
  
  public void stroke() {
    this.content.append("S").append_i(this.separator);
  }
  
  public void closePathStroke() {
    this.content.append("s").append_i(this.separator);
  }
  
  public void fill() {
    this.content.append("f").append_i(this.separator);
  }
  
  public void eoFill() {
    this.content.append("f*").append_i(this.separator);
  }
  
  public void fillStroke() {
    this.content.append("B").append_i(this.separator);
  }
  
  public void closePathFillStroke() {
    this.content.append("b").append_i(this.separator);
  }
  
  public void eoFillStroke() {
    this.content.append("B*").append_i(this.separator);
  }
  
  public void closePathEoFillStroke() {
    this.content.append("b*").append_i(this.separator);
  }
  
  public void addImage(Image image) throws DocumentException {
    addImage(image, false);
  }
  
  public void addImage(Image image, boolean inlineImage) throws DocumentException {
    if (!image.hasAbsoluteY())
      throw new DocumentException(MessageLocalization.getComposedMessage("the.image.must.have.absolute.positioning", new Object[0])); 
    float[] matrix = image.matrix();
    matrix[4] = image.getAbsoluteX() - matrix[4];
    matrix[5] = image.getAbsoluteY() - matrix[5];
    addImage(image, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5], inlineImage);
  }
  
  public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
    addImage(image, a, b, c, d, e, f, false);
  }
  
  public void addImage(Image image, AffineTransform transform) throws DocumentException {
    double[] matrix = new double[6];
    transform.getMatrix(matrix);
    addImage(image, (float)matrix[0], (float)matrix[1], (float)matrix[2], (float)matrix[3], (float)matrix[4], (float)matrix[5], false);
  }
  
  public void addImage(Image image, float a, float b, float c, float d, float e, float f, boolean inlineImage) throws DocumentException {
    try {
      if (image.getLayer() != null)
        beginLayer(image.getLayer()); 
      if (image.isImgTemplate()) {
        this.writer.addDirectImageSimple(image);
        PdfTemplate template = image.getTemplateData();
        float w = template.getWidth();
        float h = template.getHeight();
        addTemplate(template, a / w, b / w, c / h, d / h, e, f);
      } else {
        this.content.append("q ");
        this.content.append(a).append(' ');
        this.content.append(b).append(' ');
        this.content.append(c).append(' ');
        this.content.append(d).append(' ');
        this.content.append(e).append(' ');
        this.content.append(f).append(" cm");
        if (inlineImage) {
          this.content.append("\nBI\n");
          PdfImage pimage = new PdfImage(image, "", null);
          if (image instanceof ImgJBIG2) {
            byte[] globals = ((ImgJBIG2)image).getGlobalBytes();
            if (globals != null) {
              PdfDictionary decodeparms = new PdfDictionary();
              decodeparms.put(PdfName.JBIG2GLOBALS, this.writer.getReferenceJBIG2Globals(globals));
              pimage.put(PdfName.DECODEPARMS, decodeparms);
            } 
          } 
          for (PdfName element : pimage.getKeys()) {
            PdfName key = element;
            PdfObject value = pimage.get(key);
            String s = abrev.get(key);
            if (s == null)
              continue; 
            this.content.append(s);
            boolean check = true;
            if (key.equals(PdfName.COLORSPACE) && value.isArray()) {
              PdfArray ar = (PdfArray)value;
              if (ar.size() == 4 && PdfName.INDEXED.equals(ar.getAsName(0)) && ar.getPdfObject(1).isName() && ar.getPdfObject(2).isNumber() && ar.getPdfObject(3).isString())
                check = false; 
            } 
            if (check && key.equals(PdfName.COLORSPACE) && !value.isName()) {
              PdfName cs = this.writer.getColorspaceName();
              PageResources prs = getPageResources();
              prs.addColor(cs, this.writer.addToBody(value).getIndirectReference());
              value = cs;
            } 
            value.toPdf(null, this.content);
            this.content.append('\n');
          } 
          this.content.append("ID\n");
          pimage.writeContent(this.content);
          this.content.append("\nEI\nQ").append_i(this.separator);
        } else {
          PageResources prs = getPageResources();
          Image maskImage = image.getImageMask();
          if (maskImage != null) {
            PdfName pdfName = this.writer.addDirectImageSimple(maskImage);
            prs.addXObject(pdfName, this.writer.getImageReference(pdfName));
          } 
          PdfName name = this.writer.addDirectImageSimple(image);
          name = prs.addXObject(name, this.writer.getImageReference(name));
          this.content.append(' ').append(name.getBytes()).append(" Do Q").append_i(this.separator);
        } 
      } 
      if (image.hasBorders()) {
        saveState();
        float w = image.getWidth();
        float h = image.getHeight();
        concatCTM(a / w, b / w, c / h, d / h, e, f);
        rectangle((Rectangle)image);
        restoreState();
      } 
      if (image.getLayer() != null)
        endLayer(); 
      Annotation annot = image.getAnnotation();
      if (annot == null)
        return; 
      float[] r = new float[unitRect.length];
      for (int k = 0; k < unitRect.length; k += 2) {
        r[k] = a * unitRect[k] + c * unitRect[k + 1] + e;
        r[k + 1] = b * unitRect[k] + d * unitRect[k + 1] + f;
      } 
      float llx = r[0];
      float lly = r[1];
      float urx = llx;
      float ury = lly;
      for (int i = 2; i < r.length; i += 2) {
        llx = Math.min(llx, r[i]);
        lly = Math.min(lly, r[i + 1]);
        urx = Math.max(urx, r[i]);
        ury = Math.max(ury, r[i + 1]);
      } 
      annot = new Annotation(annot);
      annot.setDimensions(llx, lly, urx, ury);
      PdfAnnotation an = PdfAnnotationsImp.convertAnnotation(this.writer, annot, new Rectangle(llx, lly, urx, ury));
      if (an == null)
        return; 
      addAnnotation(an);
    } catch (Exception ee) {
      throw new DocumentException(ee);
    } 
  }
  
  public void reset() {
    reset(true);
  }
  
  public void reset(boolean validateContent) {
    this.content.reset();
    if (validateContent)
      sanityCheck(); 
    this.state = new GraphicState();
  }
  
  public void beginText() {
    if (this.inText)
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators", new Object[0])); 
    this.inText = true;
    this.state.xTLM = 0.0F;
    this.state.yTLM = 0.0F;
    this.content.append("BT").append_i(this.separator);
  }
  
  public void endText() {
    if (!this.inText)
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators", new Object[0])); 
    this.inText = false;
    this.content.append("ET").append_i(this.separator);
  }
  
  public void saveState() {
    this.content.append("q").append_i(this.separator);
    this.stateList.add(new GraphicState(this.state));
  }
  
  public void restoreState() {
    this.content.append("Q").append_i(this.separator);
    int idx = this.stateList.size() - 1;
    if (idx < 0)
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.save.restore.state.operators", new Object[0])); 
    this.state = this.stateList.get(idx);
    this.stateList.remove(idx);
  }
  
  public void setCharacterSpacing(float charSpace) {
    this.state.charSpace = charSpace;
    this.content.append(charSpace).append(" Tc").append_i(this.separator);
  }
  
  public void setWordSpacing(float wordSpace) {
    this.state.wordSpace = wordSpace;
    this.content.append(wordSpace).append(" Tw").append_i(this.separator);
  }
  
  public void setHorizontalScaling(float scale) {
    this.state.scale = scale;
    this.content.append(scale).append(" Tz").append_i(this.separator);
  }
  
  public void setLeading(float leading) {
    this.state.leading = leading;
    this.content.append(leading).append(" TL").append_i(this.separator);
  }
  
  public void setFontAndSize(BaseFont bf, float size) {
    checkWriter();
    if (size < 1.0E-4F && size > -1.0E-4F)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("font.size.too.small.1", new Object[] { String.valueOf(size) })); 
    this.state.size = size;
    this.state.fontDetails = this.writer.addSimple(bf);
    PageResources prs = getPageResources();
    PdfName name = this.state.fontDetails.getFontName();
    name = prs.addFont(name, this.state.fontDetails.getIndirectReference());
    this.content.append(name.getBytes()).append(' ').append(size).append(" Tf").append_i(this.separator);
  }
  
  public void setTextRenderingMode(int rendering) {
    this.content.append(rendering).append(" Tr").append_i(this.separator);
  }
  
  public void setTextRise(float rise) {
    this.content.append(rise).append(" Ts").append_i(this.separator);
  }
  
  private void showText2(String text) {
    if (this.state.fontDetails == null)
      throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text", new Object[0])); 
    byte[] b = this.state.fontDetails.convertToBytes(text);
    escapeString(b, this.content);
  }
  
  public void showText(String text) {
    showText2(text);
    this.content.append("Tj").append_i(this.separator);
  }
  
  public static PdfTextArray getKernArray(String text, BaseFont font) {
    PdfTextArray pa = new PdfTextArray();
    StringBuffer acc = new StringBuffer();
    int len = text.length() - 1;
    char[] c = text.toCharArray();
    if (len >= 0)
      acc.append(c, 0, 1); 
    for (int k = 0; k < len; k++) {
      char c2 = c[k + 1];
      int kern = font.getKerning(c[k], c2);
      if (kern == 0) {
        acc.append(c2);
      } else {
        pa.add(acc.toString());
        acc.setLength(0);
        acc.append(c, k + 1, 1);
        pa.add(-kern);
      } 
    } 
    pa.add(acc.toString());
    return pa;
  }
  
  public void showTextKerned(String text) {
    if (this.state.fontDetails == null)
      throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text", new Object[0])); 
    BaseFont bf = this.state.fontDetails.getBaseFont();
    if (bf.hasKernPairs()) {
      showText(getKernArray(text, bf));
    } else {
      showText(text);
    } 
  }
  
  public void newlineShowText(String text) {
    this.state.yTLM -= this.state.leading;
    showText2(text);
    this.content.append("'").append_i(this.separator);
  }
  
  public void newlineShowText(float wordSpacing, float charSpacing, String text) {
    this.state.yTLM -= this.state.leading;
    this.content.append(wordSpacing).append(' ').append(charSpacing);
    showText2(text);
    this.content.append("\"").append_i(this.separator);
    this.state.charSpace = charSpacing;
    this.state.wordSpace = wordSpacing;
  }
  
  public void setTextMatrix(float a, float b, float c, float d, float x, float y) {
    this.state.xTLM = x;
    this.state.yTLM = y;
    this.content.append(a).append(' ').append(b).append_i(32).append(c).append_i(32).append(d).append_i(32).append(x).append_i(32).append(y).append(" Tm").append_i(this.separator);
  }
  
  public void setTextMatrix(AffineTransform transform) {
    double[] matrix = new double[6];
    transform.getMatrix(matrix);
    setTextMatrix((float)matrix[0], (float)matrix[1], (float)matrix[2], (float)matrix[3], (float)matrix[4], (float)matrix[5]);
  }
  
  public void setTextMatrix(float x, float y) {
    setTextMatrix(1.0F, 0.0F, 0.0F, 1.0F, x, y);
  }
  
  public void moveText(float x, float y) {
    this.state.xTLM += x;
    this.state.yTLM += y;
    this.content.append(x).append(' ').append(y).append(" Td").append_i(this.separator);
  }
  
  public void moveTextWithLeading(float x, float y) {
    this.state.xTLM += x;
    this.state.yTLM += y;
    this.state.leading = -y;
    this.content.append(x).append(' ').append(y).append(" TD").append_i(this.separator);
  }
  
  public void newlineText() {
    this.state.yTLM -= this.state.leading;
    this.content.append("T*").append_i(this.separator);
  }
  
  int size() {
    return this.content.size();
  }
  
  static byte[] escapeString(byte[] b) {
    ByteBuffer content = new ByteBuffer();
    escapeString(b, content);
    return content.toByteArray();
  }
  
  static void escapeString(byte[] b, ByteBuffer content) {
    content.append_i(40);
    for (int k = 0; k < b.length; k++) {
      byte c = b[k];
      switch (c) {
        case 13:
          content.append("\\r");
          break;
        case 10:
          content.append("\\n");
          break;
        case 9:
          content.append("\\t");
          break;
        case 8:
          content.append("\\b");
          break;
        case 12:
          content.append("\\f");
          break;
        case 40:
        case 41:
        case 92:
          content.append_i(92).append_i(c);
          break;
        default:
          content.append_i(c);
          break;
      } 
    } 
    content.append(")");
  }
  
  public void addOutline(PdfOutline outline, String name) {
    checkWriter();
    this.pdf.addOutline(outline, name);
  }
  
  public PdfOutline getRootOutline() {
    checkWriter();
    return this.pdf.getRootOutline();
  }
  
  public float getEffectiveStringWidth(String text, boolean kerned) {
    float w;
    BaseFont bf = this.state.fontDetails.getBaseFont();
    if (kerned) {
      w = bf.getWidthPointKerned(text, this.state.size);
    } else {
      w = bf.getWidthPoint(text, this.state.size);
    } 
    if (this.state.charSpace != 0.0F && text.length() > 1)
      w += this.state.charSpace * (text.length() - 1); 
    int ft = bf.getFontType();
    if (this.state.wordSpace != 0.0F && (ft == 0 || ft == 1 || ft == 5))
      for (int i = 0; i < text.length() - 1; i++) {
        if (text.charAt(i) == ' ')
          w += this.state.wordSpace; 
      }  
    if (this.state.scale != 100.0D)
      w = w * this.state.scale / 100.0F; 
    return w;
  }
  
  public void showTextAligned(int alignment, String text, float x, float y, float rotation) {
    showTextAligned(alignment, text, x, y, rotation, false);
  }
  
  private void showTextAligned(int alignment, String text, float x, float y, float rotation, boolean kerned) {
    if (this.state.fontDetails == null)
      throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text", new Object[0])); 
    if (rotation == 0.0F) {
      switch (alignment) {
        case 1:
          x -= getEffectiveStringWidth(text, kerned) / 2.0F;
          break;
        case 2:
          x -= getEffectiveStringWidth(text, kerned);
          break;
      } 
      setTextMatrix(x, y);
      if (kerned) {
        showTextKerned(text);
      } else {
        showText(text);
      } 
    } else {
      float len;
      double alpha = rotation * Math.PI / 180.0D;
      float cos = (float)Math.cos(alpha);
      float sin = (float)Math.sin(alpha);
      switch (alignment) {
        case 1:
          len = getEffectiveStringWidth(text, kerned) / 2.0F;
          x -= len * cos;
          y -= len * sin;
          break;
        case 2:
          len = getEffectiveStringWidth(text, kerned);
          x -= len * cos;
          y -= len * sin;
          break;
      } 
      setTextMatrix(cos, sin, -sin, cos, x, y);
      if (kerned) {
        showTextKerned(text);
      } else {
        showText(text);
      } 
      setTextMatrix(0.0F, 0.0F);
    } 
  }
  
  public void showTextAlignedKerned(int alignment, String text, float x, float y, float rotation) {
    showTextAligned(alignment, text, x, y, rotation, true);
  }
  
  public void concatCTM(float a, float b, float c, float d, float e, float f) {
    this.content.append(a).append(' ').append(b).append(' ').append(c).append(' ');
    this.content.append(d).append(' ').append(e).append(' ').append(f).append(" cm").append_i(this.separator);
  }
  
  public void concatCTM(AffineTransform transform) {
    double[] matrix = new double[6];
    transform.getMatrix(matrix);
    concatCTM((float)matrix[0], (float)matrix[1], (float)matrix[2], (float)matrix[3], (float)matrix[4], (float)matrix[5]);
  }
  
  public static ArrayList<float[]> bezierArc(float x1, float y1, float x2, float y2, float startAng, float extent) {
    float fragAngle;
    int Nfrag;
    if (x1 > x2) {
      float tmp = x1;
      x1 = x2;
      x2 = tmp;
    } 
    if (y2 > y1) {
      float tmp = y1;
      y1 = y2;
      y2 = tmp;
    } 
    if (Math.abs(extent) <= 90.0F) {
      fragAngle = extent;
      Nfrag = 1;
    } else {
      Nfrag = (int)Math.ceil((Math.abs(extent) / 90.0F));
      fragAngle = extent / Nfrag;
    } 
    float x_cen = (x1 + x2) / 2.0F;
    float y_cen = (y1 + y2) / 2.0F;
    float rx = (x2 - x1) / 2.0F;
    float ry = (y2 - y1) / 2.0F;
    float halfAng = (float)(fragAngle * Math.PI / 360.0D);
    float kappa = (float)Math.abs(1.3333333333333333D * (1.0D - Math.cos(halfAng)) / Math.sin(halfAng));
    ArrayList<float[]> pointList = (ArrayList)new ArrayList<float>();
    for (int i = 0; i < Nfrag; i++) {
      float theta0 = (float)((startAng + i * fragAngle) * Math.PI / 180.0D);
      float theta1 = (float)((startAng + (i + 1) * fragAngle) * Math.PI / 180.0D);
      float cos0 = (float)Math.cos(theta0);
      float cos1 = (float)Math.cos(theta1);
      float sin0 = (float)Math.sin(theta0);
      float sin1 = (float)Math.sin(theta1);
      if (fragAngle > 0.0F) {
        pointList.add(new float[] { x_cen + rx * cos0, y_cen - ry * sin0, x_cen + rx * (cos0 - kappa * sin0), y_cen - ry * (sin0 + kappa * cos0), x_cen + rx * (cos1 + kappa * sin1), y_cen - ry * (sin1 - kappa * cos1), x_cen + rx * cos1, y_cen - ry * sin1 });
      } else {
        pointList.add(new float[] { x_cen + rx * cos0, y_cen - ry * sin0, x_cen + rx * (cos0 + kappa * sin0), y_cen - ry * (sin0 - kappa * cos0), x_cen + rx * (cos1 - kappa * sin1), y_cen - ry * (sin1 + kappa * cos1), x_cen + rx * cos1, y_cen - ry * sin1 });
      } 
    } 
    return pointList;
  }
  
  public void arc(float x1, float y1, float x2, float y2, float startAng, float extent) {
    ArrayList<float[]> ar = bezierArc(x1, y1, x2, y2, startAng, extent);
    if (ar.isEmpty())
      return; 
    float[] pt = ar.get(0);
    moveTo(pt[0], pt[1]);
    for (int k = 0; k < ar.size(); k++) {
      pt = ar.get(k);
      curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
    } 
  }
  
  public void ellipse(float x1, float y1, float x2, float y2) {
    arc(x1, y1, x2, y2, 0.0F, 360.0F);
  }
  
  public PdfPatternPainter createPattern(float width, float height, float xstep, float ystep) {
    checkWriter();
    if (xstep == 0.0F || ystep == 0.0F)
      throw new RuntimeException(MessageLocalization.getComposedMessage("xstep.or.ystep.can.not.be.zero", new Object[0])); 
    PdfPatternPainter painter = new PdfPatternPainter(this.writer);
    painter.setWidth(width);
    painter.setHeight(height);
    painter.setXStep(xstep);
    painter.setYStep(ystep);
    this.writer.addSimplePattern(painter);
    return painter;
  }
  
  public PdfPatternPainter createPattern(float width, float height) {
    return createPattern(width, height, width, height);
  }
  
  public PdfPatternPainter createPattern(float width, float height, float xstep, float ystep, BaseColor color) {
    checkWriter();
    if (xstep == 0.0F || ystep == 0.0F)
      throw new RuntimeException(MessageLocalization.getComposedMessage("xstep.or.ystep.can.not.be.zero", new Object[0])); 
    PdfPatternPainter painter = new PdfPatternPainter(this.writer, color);
    painter.setWidth(width);
    painter.setHeight(height);
    painter.setXStep(xstep);
    painter.setYStep(ystep);
    this.writer.addSimplePattern(painter);
    return painter;
  }
  
  public PdfPatternPainter createPattern(float width, float height, BaseColor color) {
    return createPattern(width, height, width, height, color);
  }
  
  public PdfTemplate createTemplate(float width, float height) {
    return createTemplate(width, height, null);
  }
  
  PdfTemplate createTemplate(float width, float height, PdfName forcedName) {
    checkWriter();
    PdfTemplate template = new PdfTemplate(this.writer);
    template.setWidth(width);
    template.setHeight(height);
    this.writer.addDirectTemplateSimple(template, forcedName);
    return template;
  }
  
  public PdfAppearance createAppearance(float width, float height) {
    return createAppearance(width, height, null);
  }
  
  PdfAppearance createAppearance(float width, float height, PdfName forcedName) {
    checkWriter();
    PdfAppearance template = new PdfAppearance(this.writer);
    template.setWidth(width);
    template.setHeight(height);
    this.writer.addDirectTemplateSimple(template, forcedName);
    return template;
  }
  
  public void addPSXObject(PdfPSXObject psobject) {
    checkWriter();
    PdfName name = this.writer.addDirectTemplateSimple(psobject, null);
    PageResources prs = getPageResources();
    name = prs.addXObject(name, psobject.getIndirectReference());
    this.content.append(name.getBytes()).append(" Do").append_i(this.separator);
  }
  
  public void addTemplate(PdfTemplate template, float a, float b, float c, float d, float e, float f) {
    checkWriter();
    checkNoPattern(template);
    PdfName name = this.writer.addDirectTemplateSimple(template, null);
    PageResources prs = getPageResources();
    name = prs.addXObject(name, template.getIndirectReference());
    this.content.append("q ");
    this.content.append(a).append(' ');
    this.content.append(b).append(' ');
    this.content.append(c).append(' ');
    this.content.append(d).append(' ');
    this.content.append(e).append(' ');
    this.content.append(f).append(" cm ");
    this.content.append(name.getBytes()).append(" Do Q").append_i(this.separator);
  }
  
  public void addTemplate(PdfTemplate template, AffineTransform transform) {
    double[] matrix = new double[6];
    transform.getMatrix(matrix);
    addTemplate(template, (float)matrix[0], (float)matrix[1], (float)matrix[2], (float)matrix[3], (float)matrix[4], (float)matrix[5]);
  }
  
  void addTemplateReference(PdfIndirectReference template, PdfName name, float a, float b, float c, float d, float e, float f) {
    checkWriter();
    PageResources prs = getPageResources();
    name = prs.addXObject(name, template);
    this.content.append("q ");
    this.content.append(a).append(' ');
    this.content.append(b).append(' ');
    this.content.append(c).append(' ');
    this.content.append(d).append(' ');
    this.content.append(e).append(' ');
    this.content.append(f).append(" cm ");
    this.content.append(name.getBytes()).append(" Do Q").append_i(this.separator);
  }
  
  public void addTemplate(PdfTemplate template, float x, float y) {
    addTemplate(template, 1.0F, 0.0F, 0.0F, 1.0F, x, y);
  }
  
  public void setCMYKColorFill(int cyan, int magenta, int yellow, int black) {
    this.content.append((cyan & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((magenta & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((yellow & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((black & 0xFF) / 255.0F);
    this.content.append(" k").append_i(this.separator);
  }
  
  public void setCMYKColorStroke(int cyan, int magenta, int yellow, int black) {
    this.content.append((cyan & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((magenta & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((yellow & 0xFF) / 255.0F);
    this.content.append(' ');
    this.content.append((black & 0xFF) / 255.0F);
    this.content.append(" K").append_i(this.separator);
  }
  
  public void setRGBColorFill(int red, int green, int blue) {
    HelperRGB((red & 0xFF) / 255.0F, (green & 0xFF) / 255.0F, (blue & 0xFF) / 255.0F);
    this.content.append(" rg").append_i(this.separator);
  }
  
  public void setRGBColorStroke(int red, int green, int blue) {
    HelperRGB((red & 0xFF) / 255.0F, (green & 0xFF) / 255.0F, (blue & 0xFF) / 255.0F);
    this.content.append(" RG").append_i(this.separator);
  }
  
  public void setColorStroke(BaseColor color) {
    CMYKColor cmyk;
    SpotColor spot;
    PatternColor pat;
    ShadingColor shading;
    PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
    int type = ExtendedColor.getType(color);
    switch (type) {
      case 1:
        setGrayStroke(((GrayColor)color).getGray());
        return;
      case 2:
        cmyk = (CMYKColor)color;
        setCMYKColorStrokeF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
        return;
      case 3:
        spot = (SpotColor)color;
        setColorStroke(spot.getPdfSpotColor(), spot.getTint());
        return;
      case 4:
        pat = (PatternColor)color;
        setPatternStroke(pat.getPainter());
        return;
      case 5:
        shading = (ShadingColor)color;
        setShadingStroke(shading.getPdfShadingPattern());
        return;
    } 
    setRGBColorStroke(color.getRed(), color.getGreen(), color.getBlue());
  }
  
  public void setColorFill(BaseColor color) {
    CMYKColor cmyk;
    SpotColor spot;
    PatternColor pat;
    ShadingColor shading;
    PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
    int type = ExtendedColor.getType(color);
    switch (type) {
      case 1:
        setGrayFill(((GrayColor)color).getGray());
        return;
      case 2:
        cmyk = (CMYKColor)color;
        setCMYKColorFillF(cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack());
        return;
      case 3:
        spot = (SpotColor)color;
        setColorFill(spot.getPdfSpotColor(), spot.getTint());
        return;
      case 4:
        pat = (PatternColor)color;
        setPatternFill(pat.getPainter());
        return;
      case 5:
        shading = (ShadingColor)color;
        setShadingFill(shading.getPdfShadingPattern());
        return;
    } 
    setRGBColorFill(color.getRed(), color.getGreen(), color.getBlue());
  }
  
  public void setColorFill(PdfSpotColor sp, float tint) {
    checkWriter();
    this.state.colorDetails = this.writer.addSimple(sp);
    PageResources prs = getPageResources();
    PdfName name = this.state.colorDetails.getColorName();
    name = prs.addColor(name, this.state.colorDetails.getIndirectReference());
    this.content.append(name.getBytes()).append(" cs ").append(tint).append(" scn").append_i(this.separator);
  }
  
  public void setColorStroke(PdfSpotColor sp, float tint) {
    checkWriter();
    this.state.colorDetails = this.writer.addSimple(sp);
    PageResources prs = getPageResources();
    PdfName name = this.state.colorDetails.getColorName();
    name = prs.addColor(name, this.state.colorDetails.getIndirectReference());
    this.content.append(name.getBytes()).append(" CS ").append(tint).append(" SCN").append_i(this.separator);
  }
  
  public void setPatternFill(PdfPatternPainter p) {
    if (p.isStencil()) {
      setPatternFill(p, p.getDefaultColor());
      return;
    } 
    checkWriter();
    PageResources prs = getPageResources();
    PdfName name = this.writer.addSimplePattern(p);
    name = prs.addPattern(name, p.getIndirectReference());
    this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(this.separator);
  }
  
  void outputColorNumbers(BaseColor color, float tint) {
    CMYKColor cmyk;
    PdfXConformanceImp.checkPDFXConformance(this.writer, 1, color);
    int type = ExtendedColor.getType(color);
    switch (type) {
      case 0:
        this.content.append(color.getRed() / 255.0F);
        this.content.append(' ');
        this.content.append(color.getGreen() / 255.0F);
        this.content.append(' ');
        this.content.append(color.getBlue() / 255.0F);
        return;
      case 1:
        this.content.append(((GrayColor)color).getGray());
        return;
      case 2:
        cmyk = (CMYKColor)color;
        this.content.append(cmyk.getCyan()).append(' ').append(cmyk.getMagenta());
        this.content.append(' ').append(cmyk.getYellow()).append(' ').append(cmyk.getBlack());
        return;
      case 3:
        this.content.append(tint);
        return;
    } 
    throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.color.type", new Object[0]));
  }
  
  public void setPatternFill(PdfPatternPainter p, BaseColor color) {
    if (ExtendedColor.getType(color) == 3) {
      setPatternFill(p, color, ((SpotColor)color).getTint());
    } else {
      setPatternFill(p, color, 0.0F);
    } 
  }
  
  public void setPatternFill(PdfPatternPainter p, BaseColor color, float tint) {
    checkWriter();
    if (!p.isStencil())
      throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.pattern.was.expected", new Object[0])); 
    PageResources prs = getPageResources();
    PdfName name = this.writer.addSimplePattern(p);
    name = prs.addPattern(name, p.getIndirectReference());
    ColorDetails csDetail = this.writer.addSimplePatternColorspace(color);
    PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
    this.content.append(cName.getBytes()).append(" cs").append_i(this.separator);
    outputColorNumbers(color, tint);
    this.content.append(' ').append(name.getBytes()).append(" scn").append_i(this.separator);
  }
  
  public void setPatternStroke(PdfPatternPainter p, BaseColor color) {
    if (ExtendedColor.getType(color) == 3) {
      setPatternStroke(p, color, ((SpotColor)color).getTint());
    } else {
      setPatternStroke(p, color, 0.0F);
    } 
  }
  
  public void setPatternStroke(PdfPatternPainter p, BaseColor color, float tint) {
    checkWriter();
    if (!p.isStencil())
      throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.pattern.was.expected", new Object[0])); 
    PageResources prs = getPageResources();
    PdfName name = this.writer.addSimplePattern(p);
    name = prs.addPattern(name, p.getIndirectReference());
    ColorDetails csDetail = this.writer.addSimplePatternColorspace(color);
    PdfName cName = prs.addColor(csDetail.getColorName(), csDetail.getIndirectReference());
    this.content.append(cName.getBytes()).append(" CS").append_i(this.separator);
    outputColorNumbers(color, tint);
    this.content.append(' ').append(name.getBytes()).append(" SCN").append_i(this.separator);
  }
  
  public void setPatternStroke(PdfPatternPainter p) {
    if (p.isStencil()) {
      setPatternStroke(p, p.getDefaultColor());
      return;
    } 
    checkWriter();
    PageResources prs = getPageResources();
    PdfName name = this.writer.addSimplePattern(p);
    name = prs.addPattern(name, p.getIndirectReference());
    this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(this.separator);
  }
  
  public void paintShading(PdfShading shading) {
    this.writer.addSimpleShading(shading);
    PageResources prs = getPageResources();
    PdfName name = prs.addShading(shading.getShadingName(), shading.getShadingReference());
    this.content.append(name.getBytes()).append(" sh").append_i(this.separator);
    ColorDetails details = shading.getColorDetails();
    if (details != null)
      prs.addColor(details.getColorName(), details.getIndirectReference()); 
  }
  
  public void paintShading(PdfShadingPattern shading) {
    paintShading(shading.getShading());
  }
  
  public void setShadingFill(PdfShadingPattern shading) {
    this.writer.addSimpleShadingPattern(shading);
    PageResources prs = getPageResources();
    PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
    this.content.append(PdfName.PATTERN.getBytes()).append(" cs ").append(name.getBytes()).append(" scn").append_i(this.separator);
    ColorDetails details = shading.getColorDetails();
    if (details != null)
      prs.addColor(details.getColorName(), details.getIndirectReference()); 
  }
  
  public void setShadingStroke(PdfShadingPattern shading) {
    this.writer.addSimpleShadingPattern(shading);
    PageResources prs = getPageResources();
    PdfName name = prs.addPattern(shading.getPatternName(), shading.getPatternReference());
    this.content.append(PdfName.PATTERN.getBytes()).append(" CS ").append(name.getBytes()).append(" SCN").append_i(this.separator);
    ColorDetails details = shading.getColorDetails();
    if (details != null)
      prs.addColor(details.getColorName(), details.getIndirectReference()); 
  }
  
  protected void checkWriter() {
    if (this.writer == null)
      throw new NullPointerException(MessageLocalization.getComposedMessage("the.writer.in.pdfcontentbyte.is.null", new Object[0])); 
  }
  
  public void showText(PdfTextArray text) {
    if (this.state.fontDetails == null)
      throw new NullPointerException(MessageLocalization.getComposedMessage("font.and.size.must.be.set.before.writing.any.text", new Object[0])); 
    this.content.append("[");
    ArrayList<Object> arrayList = text.getArrayList();
    boolean lastWasNumber = false;
    for (Object obj : arrayList) {
      if (obj instanceof String) {
        showText2((String)obj);
        lastWasNumber = false;
        continue;
      } 
      if (lastWasNumber) {
        this.content.append(' ');
      } else {
        lastWasNumber = true;
      } 
      this.content.append(((Float)obj).floatValue());
    } 
    this.content.append("]TJ").append_i(this.separator);
  }
  
  public PdfWriter getPdfWriter() {
    return this.writer;
  }
  
  public PdfDocument getPdfDocument() {
    return this.pdf;
  }
  
  public void localGoto(String name, float llx, float lly, float urx, float ury) {
    this.pdf.localGoto(name, llx, lly, urx, ury);
  }
  
  public boolean localDestination(String name, PdfDestination destination) {
    return this.pdf.localDestination(name, destination);
  }
  
  public PdfContentByte getDuplicate() {
    return new PdfContentByte(this.writer);
  }
  
  public void remoteGoto(String filename, String name, float llx, float lly, float urx, float ury) {
    this.pdf.remoteGoto(filename, name, llx, lly, urx, ury);
  }
  
  public void remoteGoto(String filename, int page, float llx, float lly, float urx, float ury) {
    this.pdf.remoteGoto(filename, page, llx, lly, urx, ury);
  }
  
  public void roundRectangle(float x, float y, float w, float h, float r) {
    if (w < 0.0F) {
      x += w;
      w = -w;
    } 
    if (h < 0.0F) {
      y += h;
      h = -h;
    } 
    if (r < 0.0F)
      r = -r; 
    float b = 0.4477F;
    moveTo(x + r, y);
    lineTo(x + w - r, y);
    curveTo(x + w - r * b, y, x + w, y + r * b, x + w, y + r);
    lineTo(x + w, y + h - r);
    curveTo(x + w, y + h - r * b, x + w - r * b, y + h, x + w - r, y + h);
    lineTo(x + r, y + h);
    curveTo(x + r * b, y + h, x, y + h - r * b, x, y + h - r);
    lineTo(x, y + r);
    curveTo(x, y + r * b, x + r * b, y, x + r, y);
  }
  
  public void setAction(PdfAction action, float llx, float lly, float urx, float ury) {
    this.pdf.setAction(action, llx, lly, urx, ury);
  }
  
  public void setLiteral(String s) {
    this.content.append(s);
  }
  
  public void setLiteral(char c) {
    this.content.append(c);
  }
  
  public void setLiteral(float n) {
    this.content.append(n);
  }
  
  void checkNoPattern(PdfTemplate t) {
    if (t.getType() == 3)
      throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.use.of.a.pattern.a.template.was.expected", new Object[0])); 
  }
  
  public void drawRadioField(float llx, float lly, float urx, float ury, boolean on) {
    if (llx > urx) {
      float x = llx;
      llx = urx;
      urx = x;
    } 
    if (lly > ury) {
      float y = lly;
      lly = ury;
      ury = y;
    } 
    setLineWidth(1.0F);
    setLineCap(1);
    setColorStroke(new BaseColor(192, 192, 192));
    arc(llx + 1.0F, lly + 1.0F, urx - 1.0F, ury - 1.0F, 0.0F, 360.0F);
    stroke();
    setLineWidth(1.0F);
    setLineCap(1);
    setColorStroke(new BaseColor(160, 160, 160));
    arc(llx + 0.5F, lly + 0.5F, urx - 0.5F, ury - 0.5F, 45.0F, 180.0F);
    stroke();
    setLineWidth(1.0F);
    setLineCap(1);
    setColorStroke(new BaseColor(0, 0, 0));
    arc(llx + 1.5F, lly + 1.5F, urx - 1.5F, ury - 1.5F, 45.0F, 180.0F);
    stroke();
    if (on) {
      setLineWidth(1.0F);
      setLineCap(1);
      setColorFill(new BaseColor(0, 0, 0));
      arc(llx + 4.0F, lly + 4.0F, urx - 4.0F, ury - 4.0F, 0.0F, 360.0F);
      fill();
    } 
  }
  
  public void drawTextField(float llx, float lly, float urx, float ury) {
    if (llx > urx) {
      float x = llx;
      llx = urx;
      urx = x;
    } 
    if (lly > ury) {
      float y = lly;
      lly = ury;
      ury = y;
    } 
    setColorStroke(new BaseColor(192, 192, 192));
    setLineWidth(1.0F);
    setLineCap(0);
    rectangle(llx, lly, urx - llx, ury - lly);
    stroke();
    setLineWidth(1.0F);
    setLineCap(0);
    setColorFill(new BaseColor(255, 255, 255));
    rectangle(llx + 0.5F, lly + 0.5F, urx - llx - 1.0F, ury - lly - 1.0F);
    fill();
    setColorStroke(new BaseColor(192, 192, 192));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(llx + 1.0F, lly + 1.5F);
    lineTo(urx - 1.5F, lly + 1.5F);
    lineTo(urx - 1.5F, ury - 1.0F);
    stroke();
    setColorStroke(new BaseColor(160, 160, 160));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(llx + 1.0F, lly + 1.0F);
    lineTo(llx + 1.0F, ury - 1.0F);
    lineTo(urx - 1.0F, ury - 1.0F);
    stroke();
    setColorStroke(new BaseColor(0, 0, 0));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(llx + 2.0F, lly + 2.0F);
    lineTo(llx + 2.0F, ury - 2.0F);
    lineTo(urx - 2.0F, ury - 2.0F);
    stroke();
  }
  
  public void drawButton(float llx, float lly, float urx, float ury, String text, BaseFont bf, float size) {
    if (llx > urx) {
      float x = llx;
      llx = urx;
      urx = x;
    } 
    if (lly > ury) {
      float y = lly;
      lly = ury;
      ury = y;
    } 
    setColorStroke(new BaseColor(0, 0, 0));
    setLineWidth(1.0F);
    setLineCap(0);
    rectangle(llx, lly, urx - llx, ury - lly);
    stroke();
    setLineWidth(1.0F);
    setLineCap(0);
    setColorFill(new BaseColor(192, 192, 192));
    rectangle(llx + 0.5F, lly + 0.5F, urx - llx - 1.0F, ury - lly - 1.0F);
    fill();
    setColorStroke(new BaseColor(255, 255, 255));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(llx + 1.0F, lly + 1.0F);
    lineTo(llx + 1.0F, ury - 1.0F);
    lineTo(urx - 1.0F, ury - 1.0F);
    stroke();
    setColorStroke(new BaseColor(160, 160, 160));
    setLineWidth(1.0F);
    setLineCap(0);
    moveTo(llx + 1.0F, lly + 1.0F);
    lineTo(urx - 1.0F, lly + 1.0F);
    lineTo(urx - 1.0F, ury - 1.0F);
    stroke();
    resetRGBColorFill();
    beginText();
    setFontAndSize(bf, size);
    showTextAligned(1, text, llx + (urx - llx) / 2.0F, lly + (ury - lly - size) / 2.0F, 0.0F);
    endText();
  }
  
  public Graphics2D createGraphicsShapes(float width, float height) {
    return new PdfGraphics2D(this, width, height, null, true, false, 0.0F);
  }
  
  public Graphics2D createPrinterGraphicsShapes(float width, float height, PrinterJob printerJob) {
    return new PdfPrinterGraphics2D(this, width, height, null, true, false, 0.0F, printerJob);
  }
  
  public Graphics2D createGraphics(float width, float height) {
    return new PdfGraphics2D(this, width, height, null, false, false, 0.0F);
  }
  
  public Graphics2D createPrinterGraphics(float width, float height, PrinterJob printerJob) {
    return new PdfPrinterGraphics2D(this, width, height, null, false, false, 0.0F, printerJob);
  }
  
  public Graphics2D createGraphics(float width, float height, boolean convertImagesToJPEG, float quality) {
    return new PdfGraphics2D(this, width, height, null, false, convertImagesToJPEG, quality);
  }
  
  public Graphics2D createPrinterGraphics(float width, float height, boolean convertImagesToJPEG, float quality, PrinterJob printerJob) {
    return new PdfPrinterGraphics2D(this, width, height, null, false, convertImagesToJPEG, quality, printerJob);
  }
  
  public Graphics2D createGraphicsShapes(float width, float height, boolean convertImagesToJPEG, float quality) {
    return new PdfGraphics2D(this, width, height, null, true, convertImagesToJPEG, quality);
  }
  
  public Graphics2D createPrinterGraphicsShapes(float width, float height, boolean convertImagesToJPEG, float quality, PrinterJob printerJob) {
    return new PdfPrinterGraphics2D(this, width, height, null, true, convertImagesToJPEG, quality, printerJob);
  }
  
  public Graphics2D createGraphics(float width, float height, FontMapper fontMapper) {
    return new PdfGraphics2D(this, width, height, fontMapper, false, false, 0.0F);
  }
  
  public Graphics2D createPrinterGraphics(float width, float height, FontMapper fontMapper, PrinterJob printerJob) {
    return new PdfPrinterGraphics2D(this, width, height, fontMapper, false, false, 0.0F, printerJob);
  }
  
  public Graphics2D createGraphics(float width, float height, FontMapper fontMapper, boolean convertImagesToJPEG, float quality) {
    return new PdfGraphics2D(this, width, height, fontMapper, false, convertImagesToJPEG, quality);
  }
  
  public Graphics2D createPrinterGraphics(float width, float height, FontMapper fontMapper, boolean convertImagesToJPEG, float quality, PrinterJob printerJob) {
    return new PdfPrinterGraphics2D(this, width, height, fontMapper, false, convertImagesToJPEG, quality, printerJob);
  }
  
  PageResources getPageResources() {
    return this.pdf.getPageResources();
  }
  
  public void setGState(PdfGState gstate) {
    PdfObject[] obj = this.writer.addSimpleExtGState(gstate);
    PageResources prs = getPageResources();
    PdfName name = prs.addExtGState((PdfName)obj[0], (PdfIndirectReference)obj[1]);
    this.content.append(name.getBytes()).append(" gs").append_i(this.separator);
  }
  
  public void beginLayer(PdfOCG layer) {
    if (layer instanceof PdfLayer && ((PdfLayer)layer).getTitle() != null)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.title.is.not.a.layer", new Object[0])); 
    if (this.layerDepth == null)
      this.layerDepth = new ArrayList<Integer>(); 
    if (layer instanceof PdfLayerMembership) {
      this.layerDepth.add(Integer.valueOf(1));
      beginLayer2(layer);
      return;
    } 
    int n = 0;
    PdfLayer la = (PdfLayer)layer;
    while (la != null) {
      if (la.getTitle() == null) {
        beginLayer2(la);
        n++;
      } 
      la = la.getParent();
    } 
    this.layerDepth.add(Integer.valueOf(n));
  }
  
  private void beginLayer2(PdfOCG layer) {
    PdfName name = (PdfName)this.writer.addSimpleProperty(layer, layer.getRef())[0];
    PageResources prs = getPageResources();
    name = prs.addProperty(name, layer.getRef());
    this.content.append("/OC ").append(name.getBytes()).append(" BDC").append_i(this.separator);
  }
  
  public void endLayer() {
    int n = 1;
    if (this.layerDepth != null && !this.layerDepth.isEmpty()) {
      n = ((Integer)this.layerDepth.get(this.layerDepth.size() - 1)).intValue();
      this.layerDepth.remove(this.layerDepth.size() - 1);
    } else {
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.layer.operators", new Object[0]));
    } 
    while (n-- > 0)
      this.content.append("EMC").append_i(this.separator); 
  }
  
  public void transform(AffineTransform af) {
    double[] arr = new double[6];
    af.getMatrix(arr);
    this.content.append(arr[0]).append(' ').append(arr[1]).append(' ').append(arr[2]).append(' ');
    this.content.append(arr[3]).append(' ').append(arr[4]).append(' ').append(arr[5]).append(" cm").append_i(this.separator);
  }
  
  void addAnnotation(PdfAnnotation annot) {
    this.writer.addAnnotation(annot);
  }
  
  public void setDefaultColorspace(PdfName name, PdfObject obj) {
    PageResources prs = getPageResources();
    prs.addDefaultColor(name, obj);
  }
  
  public void beginMarkedContentSequence(PdfStructureElement struc) {
    PdfObject obj = struc.get(PdfName.K);
    int mark = this.pdf.getMarkPoint();
    if (obj != null) {
      PdfArray ar = null;
      if (obj.isNumber()) {
        ar = new PdfArray();
        ar.add(obj);
        struc.put(PdfName.K, ar);
      } else if (obj.isArray()) {
        ar = (PdfArray)obj;
        if (!ar.getPdfObject(0).isNumber())
          throw new IllegalArgumentException(MessageLocalization.getComposedMessage("the.structure.has.kids", new Object[0])); 
      } else {
        throw new IllegalArgumentException(MessageLocalization.getComposedMessage("unknown.object.at.k.1", new Object[] { obj.getClass().toString() }));
      } 
      PdfDictionary dic = new PdfDictionary(PdfName.MCR);
      dic.put(PdfName.PG, this.writer.getCurrentPage());
      dic.put(PdfName.MCID, new PdfNumber(mark));
      ar.add(dic);
      struc.setPageMark(this.writer.getPageNumber() - 1, -1);
    } else {
      struc.setPageMark(this.writer.getPageNumber() - 1, mark);
      struc.put(PdfName.PG, this.writer.getCurrentPage());
    } 
    this.pdf.incMarkPoint();
    this.mcDepth++;
    this.content.append(struc.get(PdfName.S).getBytes()).append(" <</MCID ").append(mark).append(">> BDC").append_i(this.separator);
  }
  
  public void endMarkedContentSequence() {
    if (this.mcDepth == 0)
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.marked.content.operators", new Object[0])); 
    this.mcDepth--;
    this.content.append("EMC").append_i(this.separator);
  }
  
  public void beginMarkedContentSequence(PdfName tag, PdfDictionary property, boolean inline) {
    if (property == null) {
      this.content.append(tag.getBytes()).append(" BMC").append_i(this.separator);
      return;
    } 
    this.content.append(tag.getBytes()).append(' ');
    if (inline) {
      try {
        property.toPdf(this.writer, this.content);
      } catch (Exception e) {
        throw new ExceptionConverter(e);
      } 
    } else {
      PdfObject[] objs;
      if (this.writer.propertyExists(property)) {
        objs = this.writer.addSimpleProperty(property, null);
      } else {
        objs = this.writer.addSimpleProperty(property, this.writer.getPdfIndirectReference());
      } 
      PdfName name = (PdfName)objs[0];
      PageResources prs = getPageResources();
      name = prs.addProperty(name, (PdfIndirectReference)objs[1]);
      this.content.append(name.getBytes());
    } 
    this.content.append(" BDC").append_i(this.separator);
    this.mcDepth++;
  }
  
  public void beginMarkedContentSequence(PdfName tag) {
    beginMarkedContentSequence(tag, null, false);
  }
  
  public void sanityCheck() {
    if (this.mcDepth != 0)
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.marked.content.operators", new Object[0])); 
    if (this.inText)
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.begin.end.text.operators", new Object[0])); 
    if (this.layerDepth != null && !this.layerDepth.isEmpty())
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.layer.operators", new Object[0])); 
    if (!this.stateList.isEmpty())
      throw new IllegalPdfSyntaxException(MessageLocalization.getComposedMessage("unbalanced.save.restore.state.operators", new Object[0])); 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfContentByte.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */