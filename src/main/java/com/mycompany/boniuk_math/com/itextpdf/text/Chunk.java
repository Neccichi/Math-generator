package com.mycompany.boniuk_math.com.itextpdf.text;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.HyphenationEvent;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAction;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAnnotation;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.draw.DrawInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Chunk implements Element {
  public static final String OBJECT_REPLACEMENT_CHARACTER = "￼";
  
  public static final Chunk NEWLINE = new Chunk("\n");
  
  public static final Chunk NEXTPAGE = new Chunk("");
  
  static {
    NEXTPAGE.setNewPage();
  }
  
  protected StringBuffer content = null;
  
  protected Font font = null;
  
  protected HashMap<String, Object> attributes = null;
  
  public static final String SEPARATOR = "SEPARATOR";
  
  public static final String TAB = "TAB";
  
  public static final String HSCALE = "HSCALE";
  
  public static final String UNDERLINE = "UNDERLINE";
  
  public static final String SUBSUPSCRIPT = "SUBSUPSCRIPT";
  
  public static final String SKEW = "SKEW";
  
  public static final String BACKGROUND = "BACKGROUND";
  
  public static final String TEXTRENDERMODE = "TEXTRENDERMODE";
  
  public static final String SPLITCHARACTER = "SPLITCHARACTER";
  
  public static final String HYPHENATION = "HYPHENATION";
  
  public static final String REMOTEGOTO = "REMOTEGOTO";
  
  public static final String LOCALGOTO = "LOCALGOTO";
  
  public static final String LOCALDESTINATION = "LOCALDESTINATION";
  
  public static final String GENERICTAG = "GENERICTAG";
  
  public static final String IMAGE = "IMAGE";
  
  public static final String ACTION = "ACTION";
  
  public static final String NEWPAGE = "NEWPAGE";
  
  public static final String PDFANNOTATION = "PDFANNOTATION";
  
  public static final String COLOR = "COLOR";
  
  public static final String ENCODING = "ENCODING";
  
  public static final String CHAR_SPACING = "CHAR_SPACING";
  
  public Chunk() {
    this.content = new StringBuffer();
    this.font = new Font();
  }
  
  public Chunk(Chunk ck) {
    if (ck.content != null)
      this.content = new StringBuffer(ck.content.toString()); 
    if (ck.font != null)
      this.font = new Font(ck.font); 
    if (ck.attributes != null)
      this.attributes = new HashMap<String, Object>(ck.attributes); 
  }
  
  public Chunk(String content, Font font) {
    this.content = new StringBuffer(content);
    this.font = font;
  }
  
  public Chunk(String content) {
    this(content, new Font());
  }
  
  public Chunk(char c, Font font) {
    this.content = new StringBuffer();
    this.content.append(c);
    this.font = font;
  }
  
  public Chunk(char c) {
    this(c, new Font());
  }
  
  public Chunk(Image image, float offsetX, float offsetY) {
    this("￼", new Font());
    Image copyImage = Image.getInstance(image);
    copyImage.setAbsolutePosition(Float.NaN, Float.NaN);
    setAttribute("IMAGE", new Object[] { copyImage, new Float(offsetX), new Float(offsetY), Boolean.FALSE });
  }
  
  public Chunk(DrawInterface separator) {
    this(separator, false);
  }
  
  public Chunk(DrawInterface separator, boolean vertical) {
    this("￼", new Font());
    setAttribute("SEPARATOR", new Object[] { separator, Boolean.valueOf(vertical) });
  }
  
  public Chunk(DrawInterface separator, float tabPosition) {
    this(separator, tabPosition, false);
  }
  
  public Chunk(DrawInterface separator, float tabPosition, boolean newline) {
    this("￼", new Font());
    if (tabPosition < 0.0F)
      throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.tab.position.may.not.be.lower.than.0.yours.is.1", new Object[] { String.valueOf(tabPosition) })); 
    setAttribute("TAB", new Object[] { separator, new Float(tabPosition), Boolean.valueOf(newline), new Float(0.0F) });
  }
  
  public Chunk(Image image, float offsetX, float offsetY, boolean changeLeading) {
    this("￼", new Font());
    setAttribute("IMAGE", new Object[] { image, new Float(offsetX), new Float(offsetY), Boolean.valueOf(changeLeading) });
  }
  
  public boolean process(ElementListener listener) {
    try {
      return listener.add(this);
    } catch (DocumentException de) {
      return false;
    } 
  }
  
  public int type() {
    return 10;
  }
  
  public List<Chunk> getChunks() {
    List<Chunk> tmp = new ArrayList<Chunk>();
    tmp.add(this);
    return tmp;
  }
  
  public StringBuffer append(String string) {
    return this.content.append(string);
  }
  
  public void setFont(Font font) {
    this.font = font;
  }
  
  public Font getFont() {
    return this.font;
  }
  
  public String getContent() {
    return this.content.toString();
  }
  
  public String toString() {
    return getContent();
  }
  
  public boolean isEmpty() {
    return (this.content.toString().trim().length() == 0 && this.content.toString().indexOf("\n") == -1 && this.attributes == null);
  }
  
  public float getWidthPoint() {
    if (getImage() != null)
      return getImage().getScaledWidth(); 
    return this.font.getCalculatedBaseFont(true).getWidthPoint(getContent(), this.font.getCalculatedSize()) * getHorizontalScaling();
  }
  
  public boolean hasAttributes() {
    return (this.attributes != null);
  }
  
  public HashMap<String, Object> getAttributes() {
    return this.attributes;
  }
  
  public void setAttributes(HashMap<String, Object> attributes) {
    this.attributes = attributes;
  }
  
  private Chunk setAttribute(String name, Object obj) {
    if (this.attributes == null)
      this.attributes = new HashMap<String, Object>(); 
    this.attributes.put(name, obj);
    return this;
  }
  
  public Chunk setHorizontalScaling(float scale) {
    return setAttribute("HSCALE", new Float(scale));
  }
  
  public float getHorizontalScaling() {
    if (this.attributes == null)
      return 1.0F; 
    Float f = (Float)this.attributes.get("HSCALE");
    if (f == null)
      return 1.0F; 
    return f.floatValue();
  }
  
  public Chunk setUnderline(float thickness, float yPosition) {
    return setUnderline(null, thickness, 0.0F, yPosition, 0.0F, 0);
  }
  
  public Chunk setUnderline(BaseColor color, float thickness, float thicknessMul, float yPosition, float yPositionMul, int cap) {
    if (this.attributes == null)
      this.attributes = new HashMap<String, Object>(); 
    Object[] obj = { color, { thickness, thicknessMul, yPosition, yPositionMul, cap } };
    Object[][] unders = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), obj);
    return setAttribute("UNDERLINE", unders);
  }
  
  public Chunk setTextRise(float rise) {
    return setAttribute("SUBSUPSCRIPT", new Float(rise));
  }
  
  public float getTextRise() {
    if (this.attributes != null && this.attributes.containsKey("SUBSUPSCRIPT")) {
      Float f = (Float)this.attributes.get("SUBSUPSCRIPT");
      return f.floatValue();
    } 
    return 0.0F;
  }
  
  public Chunk setSkew(float alpha, float beta) {
    alpha = (float)Math.tan(alpha * Math.PI / 180.0D);
    beta = (float)Math.tan(beta * Math.PI / 180.0D);
    return setAttribute("SKEW", new float[] { alpha, beta });
  }
  
  public Chunk setBackground(BaseColor color) {
    return setBackground(color, 0.0F, 0.0F, 0.0F, 0.0F);
  }
  
  public Chunk setBackground(BaseColor color, float extraLeft, float extraBottom, float extraRight, float extraTop) {
    return setAttribute("BACKGROUND", new Object[] { color, { extraLeft, extraBottom, extraRight, extraTop } });
  }
  
  public Chunk setTextRenderMode(int mode, float strokeWidth, BaseColor strokeColor) {
    return setAttribute("TEXTRENDERMODE", new Object[] { Integer.valueOf(mode), new Float(strokeWidth), strokeColor });
  }
  
  public Chunk setSplitCharacter(SplitCharacter splitCharacter) {
    return setAttribute("SPLITCHARACTER", splitCharacter);
  }
  
  public Chunk setHyphenation(HyphenationEvent hyphenation) {
    return setAttribute("HYPHENATION", hyphenation);
  }
  
  public Chunk setRemoteGoto(String filename, String name) {
    return setAttribute("REMOTEGOTO", new Object[] { filename, name });
  }
  
  public Chunk setRemoteGoto(String filename, int page) {
    return setAttribute("REMOTEGOTO", new Object[] { filename, Integer.valueOf(page) });
  }
  
  public Chunk setLocalGoto(String name) {
    return setAttribute("LOCALGOTO", name);
  }
  
  public Chunk setLocalDestination(String name) {
    return setAttribute("LOCALDESTINATION", name);
  }
  
  public Chunk setGenericTag(String text) {
    return setAttribute("GENERICTAG", text);
  }
  
  public Image getImage() {
    if (this.attributes == null)
      return null; 
    Object[] obj = (Object[])this.attributes.get("IMAGE");
    if (obj == null)
      return null; 
    return (Image)obj[0];
  }
  
  public Chunk setAction(PdfAction action) {
    return setAttribute("ACTION", action);
  }
  
  public Chunk setAnchor(URL url) {
    return setAttribute("ACTION", new PdfAction(url.toExternalForm()));
  }
  
  public Chunk setAnchor(String url) {
    return setAttribute("ACTION", new PdfAction(url));
  }
  
  public Chunk setNewPage() {
    return setAttribute("NEWPAGE", null);
  }
  
  public Chunk setAnnotation(PdfAnnotation annotation) {
    return setAttribute("PDFANNOTATION", annotation);
  }
  
  public boolean isContent() {
    return true;
  }
  
  public boolean isNestable() {
    return true;
  }
  
  public HyphenationEvent getHyphenation() {
    if (this.attributes == null)
      return null; 
    return (HyphenationEvent)this.attributes.get("HYPHENATION");
  }
  
  public Chunk setCharacterSpacing(float charSpace) {
    return setAttribute("CHAR_SPACING", new Float(charSpace));
  }
  
  public float getCharacterSpacing() {
    if (this.attributes != null && this.attributes.containsKey("CHAR_SPACING")) {
      Float f = (Float)this.attributes.get("CHAR_SPACING");
      return f.floatValue();
    } 
    return 0.0F;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\Chunk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */