package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.BaseColor;
import com.mycompany.boniuk_math.com.itextpdf.text.Chunk;
import com.mycompany.boniuk_math.com.itextpdf.text.Font;
import com.mycompany.boniuk_math.com.itextpdf.text.Image;
import com.mycompany.boniuk_math.com.itextpdf.text.SplitCharacter;
import com.mycompany.boniuk_math.com.itextpdf.text.Utilities;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PdfChunk {
  private static final char[] singleSpace = new char[] { ' ' };
  
  private static final PdfChunk[] thisChunk = new PdfChunk[1];
  
  private static final float ITALIC_ANGLE = 0.21256F;
  
  private static final HashSet<String> keysAttributes = new HashSet<String>();
  
  private static final HashSet<String> keysNoStroke = new HashSet<String>();
  
  static {
    keysAttributes.add("ACTION");
    keysAttributes.add("UNDERLINE");
    keysAttributes.add("REMOTEGOTO");
    keysAttributes.add("LOCALGOTO");
    keysAttributes.add("LOCALDESTINATION");
    keysAttributes.add("GENERICTAG");
    keysAttributes.add("NEWPAGE");
    keysAttributes.add("IMAGE");
    keysAttributes.add("BACKGROUND");
    keysAttributes.add("PDFANNOTATION");
    keysAttributes.add("SKEW");
    keysAttributes.add("HSCALE");
    keysAttributes.add("SEPARATOR");
    keysAttributes.add("TAB");
    keysAttributes.add("CHAR_SPACING");
    keysNoStroke.add("SUBSUPSCRIPT");
    keysNoStroke.add("SPLITCHARACTER");
    keysNoStroke.add("HYPHENATION");
    keysNoStroke.add("TEXTRENDERMODE");
  }
  
  protected String value = "";
  
  protected String encoding = "Cp1252";
  
  protected PdfFont font;
  
  protected BaseFont baseFont;
  
  protected SplitCharacter splitCharacter;
  
  protected HashMap<String, Object> attributes = new HashMap<String, Object>();
  
  protected HashMap<String, Object> noStroke = new HashMap<String, Object>();
  
  protected boolean newlineSplit;
  
  protected Image image;
  
  protected float offsetX;
  
  protected float offsetY;
  
  protected boolean changeLeading = false;
  
  PdfChunk(String string, PdfChunk other) {
    thisChunk[0] = this;
    this.value = string;
    this.font = other.font;
    this.attributes = other.attributes;
    this.noStroke = other.noStroke;
    this.baseFont = other.baseFont;
    Object[] obj = (Object[])this.attributes.get("IMAGE");
    if (obj == null) {
      this.image = null;
    } else {
      this.image = (Image)obj[0];
      this.offsetX = ((Float)obj[1]).floatValue();
      this.offsetY = ((Float)obj[2]).floatValue();
      this.changeLeading = ((Boolean)obj[3]).booleanValue();
    } 
    this.encoding = this.font.getFont().getEncoding();
    this.splitCharacter = (SplitCharacter)this.noStroke.get("SPLITCHARACTER");
    if (this.splitCharacter == null)
      this.splitCharacter = DefaultSplitCharacter.DEFAULT; 
  }
  
  PdfChunk(Chunk chunk, PdfAction action) {
    thisChunk[0] = this;
    this.value = chunk.getContent();
    Font f = chunk.getFont();
    float size = f.getSize();
    if (size == -1.0F)
      size = 12.0F; 
    this.baseFont = f.getBaseFont();
    int style = f.getStyle();
    if (style == -1)
      style = 0; 
    if (this.baseFont == null) {
      this.baseFont = f.getCalculatedBaseFont(false);
    } else {
      if ((style & 0x1) != 0)
        this.attributes.put("TEXTRENDERMODE", new Object[] { Integer.valueOf(2), new Float(size / 30.0F), null }); 
      if ((style & 0x2) != 0)
        this.attributes.put("SKEW", new float[] { 0.0F, 0.21256F }); 
    } 
    this.font = new PdfFont(this.baseFont, size);
    HashMap<String, Object> attr = chunk.getAttributes();
    if (attr != null) {
      for (Map.Entry<String, Object> entry : attr.entrySet()) {
        String name = entry.getKey();
        if (keysAttributes.contains(name)) {
          this.attributes.put(name, entry.getValue());
          continue;
        } 
        if (keysNoStroke.contains(name))
          this.noStroke.put(name, entry.getValue()); 
      } 
      if ("".equals(attr.get("GENERICTAG")))
        this.attributes.put("GENERICTAG", chunk.getContent()); 
    } 
    if (f.isUnderlined()) {
      Object[] arrayOfObject = { null, { 0.0F, 0.06666667F, 0.0F, -0.33333334F, 0.0F } };
      Object[][] unders = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), arrayOfObject);
      this.attributes.put("UNDERLINE", unders);
    } 
    if (f.isStrikethru()) {
      Object[] arrayOfObject = { null, { 0.0F, 0.06666667F, 0.0F, 0.33333334F, 0.0F } };
      Object[][] unders = Utilities.addToArray((Object[][])this.attributes.get("UNDERLINE"), arrayOfObject);
      this.attributes.put("UNDERLINE", unders);
    } 
    if (action != null)
      this.attributes.put("ACTION", action); 
    this.noStroke.put("COLOR", f.getColor());
    this.noStroke.put("ENCODING", this.font.getFont().getEncoding());
    Object[] obj = (Object[])this.attributes.get("IMAGE");
    if (obj == null) {
      this.image = null;
    } else {
      this.attributes.remove("HSCALE");
      this.image = (Image)obj[0];
      this.offsetX = ((Float)obj[1]).floatValue();
      this.offsetY = ((Float)obj[2]).floatValue();
      this.changeLeading = ((Boolean)obj[3]).booleanValue();
    } 
    this.font.setImage(this.image);
    Float hs = (Float)this.attributes.get("HSCALE");
    if (hs != null)
      this.font.setHorizontalScaling(hs.floatValue()); 
    this.encoding = this.font.getFont().getEncoding();
    this.splitCharacter = (SplitCharacter)this.noStroke.get("SPLITCHARACTER");
    if (this.splitCharacter == null)
      this.splitCharacter = DefaultSplitCharacter.DEFAULT; 
  }
  
  public int getUnicodeEquivalent(int c) {
    return this.baseFont.getUnicodeEquivalent(c);
  }
  
  protected int getWord(String text, int start) {
    int len = text.length();
    while (start < len && 
      Character.isLetter(text.charAt(start)))
      start++; 
    return start;
  }
  
  PdfChunk split(float width) {
    this.newlineSplit = false;
    if (this.image != null) {
      if (this.image.getScaledWidth() > width) {
        PdfChunk pdfChunk = new PdfChunk("￼", this);
        this.value = "";
        this.attributes = new HashMap<String, Object>();
        this.image = null;
        this.font = PdfFont.getDefaultFont();
        return pdfChunk;
      } 
      return null;
    } 
    HyphenationEvent hyphenationEvent = (HyphenationEvent)this.noStroke.get("HYPHENATION");
    int currentPosition = 0;
    int splitPosition = -1;
    float currentWidth = 0.0F;
    int lastSpace = -1;
    float lastSpaceWidth = 0.0F;
    int length = this.value.length();
    char[] valueArray = this.value.toCharArray();
    char character = Character.MIN_VALUE;
    BaseFont ft = this.font.getFont();
    boolean surrogate = false;
    if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
      while (currentPosition < length) {
        char cidChar = valueArray[currentPosition];
        character = (char)ft.getUnicodeEquivalent(cidChar);
        if (character == '\n') {
          this.newlineSplit = true;
          String str = this.value.substring(currentPosition + 1);
          this.value = this.value.substring(0, currentPosition);
          if (this.value.length() < 1)
            this.value = "\001"; 
          PdfChunk pdfChunk = new PdfChunk(str, this);
          return pdfChunk;
        } 
        currentWidth += getCharWidth(cidChar);
        if (character == ' ') {
          lastSpace = currentPosition + 1;
          lastSpaceWidth = currentWidth;
        } 
        if (currentWidth > width)
          break; 
        if (this.splitCharacter.isSplitCharacter(0, currentPosition, length, valueArray, thisChunk))
          splitPosition = currentPosition + 1; 
        currentPosition++;
      } 
    } else {
      while (currentPosition < length) {
        character = valueArray[currentPosition];
        if (character == '\r' || character == '\n') {
          this.newlineSplit = true;
          int inc = 1;
          if (character == '\r' && currentPosition + 1 < length && valueArray[currentPosition + 1] == '\n')
            inc = 2; 
          String str = this.value.substring(currentPosition + inc);
          this.value = this.value.substring(0, currentPosition);
          if (this.value.length() < 1)
            this.value = " "; 
          PdfChunk pdfChunk = new PdfChunk(str, this);
          return pdfChunk;
        } 
        surrogate = Utilities.isSurrogatePair(valueArray, currentPosition);
        if (surrogate) {
          currentWidth += getCharWidth(Utilities.convertToUtf32(valueArray[currentPosition], valueArray[currentPosition + 1]));
        } else {
          currentWidth += getCharWidth(character);
        } 
        if (character == ' ') {
          lastSpace = currentPosition + 1;
          lastSpaceWidth = currentWidth;
        } 
        if (surrogate)
          currentPosition++; 
        if (currentWidth > width)
          break; 
        if (this.splitCharacter.isSplitCharacter(0, currentPosition, length, valueArray, null))
          splitPosition = currentPosition + 1; 
        currentPosition++;
      } 
    } 
    if (currentPosition == length)
      return null; 
    if (splitPosition < 0) {
      String str = this.value;
      this.value = "";
      PdfChunk pdfChunk = new PdfChunk(str, this);
      return pdfChunk;
    } 
    if (lastSpace > splitPosition && this.splitCharacter.isSplitCharacter(0, 0, 1, singleSpace, null))
      splitPosition = lastSpace; 
    if (hyphenationEvent != null && lastSpace >= 0 && lastSpace < currentPosition) {
      int wordIdx = getWord(this.value, lastSpace);
      if (wordIdx > lastSpace) {
        String pre = hyphenationEvent.getHyphenatedWordPre(this.value.substring(lastSpace, wordIdx), this.font.getFont(), this.font.size(), width - lastSpaceWidth);
        String post = hyphenationEvent.getHyphenatedWordPost();
        if (pre.length() > 0) {
          String str = post + this.value.substring(wordIdx);
          this.value = trim(this.value.substring(0, lastSpace) + pre);
          PdfChunk pdfChunk = new PdfChunk(str, this);
          return pdfChunk;
        } 
      } 
    } 
    String returnValue = this.value.substring(splitPosition);
    this.value = trim(this.value.substring(0, splitPosition));
    PdfChunk pc = new PdfChunk(returnValue, this);
    return pc;
  }
  
  PdfChunk truncate(float width) {
    if (this.image != null) {
      if (this.image.getScaledWidth() > width) {
        if (this.image.isScaleToFitLineWhenOverflow()) {
          float scalePercent = width / this.image.getWidth() * 100.0F;
          this.image.scalePercent(scalePercent);
          return null;
        } 
        PdfChunk pdfChunk = new PdfChunk("", this);
        this.value = "";
        this.attributes.remove("IMAGE");
        this.image = null;
        this.font = PdfFont.getDefaultFont();
        return pdfChunk;
      } 
      return null;
    } 
    int currentPosition = 0;
    float currentWidth = 0.0F;
    if (width < this.font.width()) {
      String str = this.value.substring(1);
      this.value = this.value.substring(0, 1);
      PdfChunk pdfChunk = new PdfChunk(str, this);
      return pdfChunk;
    } 
    int length = this.value.length();
    boolean surrogate = false;
    while (currentPosition < length) {
      surrogate = Utilities.isSurrogatePair(this.value, currentPosition);
      if (surrogate) {
        currentWidth += getCharWidth(Utilities.convertToUtf32(this.value, currentPosition));
      } else {
        currentWidth += getCharWidth(this.value.charAt(currentPosition));
      } 
      if (currentWidth > width)
        break; 
      if (surrogate)
        currentPosition++; 
      currentPosition++;
    } 
    if (currentPosition == length)
      return null; 
    if (currentPosition == 0) {
      currentPosition = 1;
      if (surrogate)
        currentPosition++; 
    } 
    String returnValue = this.value.substring(currentPosition);
    this.value = this.value.substring(0, currentPosition);
    PdfChunk pc = new PdfChunk(returnValue, this);
    return pc;
  }
  
  PdfFont font() {
    return this.font;
  }
  
  BaseColor color() {
    return (BaseColor)this.noStroke.get("COLOR");
  }
  
  float width() {
    if (isAttribute("CHAR_SPACING")) {
      Float cs = (Float)getAttribute("CHAR_SPACING");
      return this.font.width(this.value) + this.value.length() * cs.floatValue();
    } 
    if (isAttribute("SEPARATOR"))
      return 0.0F; 
    return this.font.width(this.value);
  }
  
  public boolean isNewlineSplit() {
    return this.newlineSplit;
  }
  
  public float getWidthCorrected(float charSpacing, float wordSpacing) {
    if (this.image != null)
      return this.image.getScaledWidth() + charSpacing; 
    int numberOfSpaces = 0;
    int idx = -1;
    while ((idx = this.value.indexOf(' ', idx + 1)) >= 0)
      numberOfSpaces++; 
    return width() + this.value.length() * charSpacing + numberOfSpaces * wordSpacing;
  }
  
  public float getTextRise() {
    Float f = (Float)getAttribute("SUBSUPSCRIPT");
    if (f != null)
      return f.floatValue(); 
    return 0.0F;
  }
  
  public float trimLastSpace() {
    BaseFont ft = this.font.getFont();
    if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
      if (this.value.length() > 1 && this.value.endsWith("\001")) {
        this.value = this.value.substring(0, this.value.length() - 1);
        return this.font.width(1);
      } 
    } else if (this.value.length() > 1 && this.value.endsWith(" ")) {
      this.value = this.value.substring(0, this.value.length() - 1);
      return this.font.width(32);
    } 
    return 0.0F;
  }
  
  public float trimFirstSpace() {
    BaseFont ft = this.font.getFont();
    if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
      if (this.value.length() > 1 && this.value.startsWith("\001")) {
        this.value = this.value.substring(1);
        return this.font.width(1);
      } 
    } else if (this.value.length() > 1 && this.value.startsWith(" ")) {
      this.value = this.value.substring(1);
      return this.font.width(32);
    } 
    return 0.0F;
  }
  
  Object getAttribute(String name) {
    if (this.attributes.containsKey(name))
      return this.attributes.get(name); 
    return this.noStroke.get(name);
  }
  
  boolean isAttribute(String name) {
    if (this.attributes.containsKey(name))
      return true; 
    return this.noStroke.containsKey(name);
  }
  
  boolean isStroked() {
    return !this.attributes.isEmpty();
  }
  
  boolean isSeparator() {
    return isAttribute("SEPARATOR");
  }
  
  boolean isHorizontalSeparator() {
    if (isAttribute("SEPARATOR")) {
      Object[] o = (Object[])getAttribute("SEPARATOR");
      return !((Boolean)o[1]).booleanValue();
    } 
    return false;
  }
  
  boolean isTab() {
    return isAttribute("TAB");
  }
  
  void adjustLeft(float newValue) {
    Object[] o = (Object[])this.attributes.get("TAB");
    if (o != null)
      this.attributes.put("TAB", new Object[] { o[0], o[1], o[2], new Float(newValue) }); 
  }
  
  boolean isImage() {
    return (this.image != null);
  }
  
  Image getImage() {
    return this.image;
  }
  
  void setImageOffsetX(float offsetX) {
    this.offsetX = offsetX;
  }
  
  float getImageOffsetX() {
    return this.offsetX;
  }
  
  void setImageOffsetY(float offsetY) {
    this.offsetY = offsetY;
  }
  
  float getImageOffsetY() {
    return this.offsetY;
  }
  
  void setValue(String value) {
    this.value = value;
  }
  
  public String toString() {
    return this.value;
  }
  
  boolean isSpecialEncoding() {
    return (this.encoding.equals("UnicodeBigUnmarked") || this.encoding.equals("Identity-H"));
  }
  
  String getEncoding() {
    return this.encoding;
  }
  
  int length() {
    return this.value.length();
  }
  
  int lengthUtf32() {
    if (!"Identity-H".equals(this.encoding))
      return this.value.length(); 
    int total = 0;
    int len = this.value.length();
    for (int k = 0; k < len; k++) {
      if (Utilities.isSurrogateHigh(this.value.charAt(k)))
        k++; 
      total++;
    } 
    return total;
  }
  
  boolean isExtSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck) {
    return this.splitCharacter.isSplitCharacter(start, current, end, cc, ck);
  }
  
  String trim(String string) {
    BaseFont ft = this.font.getFont();
    if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
      while (string.endsWith("\001"))
        string = string.substring(0, string.length() - 1); 
    } else {
      while (string.endsWith(" ") || string.endsWith("\t"))
        string = string.substring(0, string.length() - 1); 
    } 
    return string;
  }
  
  public boolean changeLeading() {
    return this.changeLeading;
  }
  
  float getCharWidth(int c) {
    if (noPrint(c))
      return 0.0F; 
    if (isAttribute("CHAR_SPACING")) {
      Float cs = (Float)getAttribute("CHAR_SPACING");
      return this.font.width(c) + cs.floatValue() * this.font.getHorizontalScaling();
    } 
    return this.font.width(c);
  }
  
  public static boolean noPrint(int c) {
    return ((c >= 8203 && c <= 8207) || (c >= 8234 && c <= 8238));
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfChunk.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */