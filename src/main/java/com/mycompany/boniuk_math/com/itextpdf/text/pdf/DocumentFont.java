package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.ExceptionConverter;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.fonts.cmaps.CMap;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.fonts.cmaps.CMapParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

public class DocumentFont extends BaseFont {
  private HashMap<Integer, int[]> metrics = (HashMap)new HashMap<Integer, int>();
  
  private String fontName;
  
  private PRIndirectReference refFont;
  
  private PdfDictionary font;
  
  private IntHashtable uni2byte = new IntHashtable();
  
  private IntHashtable diffmap;
  
  private float Ascender = 800.0F;
  
  private float CapHeight = 700.0F;
  
  private float Descender = -200.0F;
  
  private float ItalicAngle = 0.0F;
  
  private float llx = -50.0F;
  
  private float lly = -200.0F;
  
  private float urx = 100.0F;
  
  private float ury = 900.0F;
  
  private boolean isType0 = false;
  
  private BaseFont cjkMirror;
  
  private static String[] cjkNames = new String[] { 
      "HeiseiMin-W3", "HeiseiKakuGo-W5", "STSong-Light", "MHei-Medium", "MSung-Light", "HYGoThic-Medium", "HYSMyeongJo-Medium", "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", 
      "KozMinPro-Regular" };
  
  private static String[] cjkEncs = new String[] { 
      "UniJIS-UCS2-H", "UniJIS-UCS2-H", "UniGB-UCS2-H", "UniCNS-UCS2-H", "UniCNS-UCS2-H", "UniKS-UCS2-H", "UniKS-UCS2-H", "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", 
      "UniJIS-UCS2-H" };
  
  private static String[] cjkNames2 = new String[] { "MSungStd-Light", "STSongStd-Light", "HYSMyeongJoStd-Medium", "KozMinPro-Regular" };
  
  private static String[] cjkEncs2 = new String[] { "UniCNS-UCS2-H", "UniGB-UCS2-H", "UniKS-UCS2-H", "UniJIS-UCS2-H", "UniCNS-UTF16-H", "UniGB-UTF16-H", "UniKS-UTF16-H", "UniJIS-UTF16-H" };
  
  private static final int[] stdEnc = new int[] { 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 32, 33, 34, 35, 36, 37, 38, 8217, 
      40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 
      50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 
      60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 
      70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 
      80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 
      90, 91, 92, 93, 94, 95, 8216, 97, 98, 99, 
      100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 
      110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 
      120, 121, 122, 123, 124, 125, 126, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 161, 162, 163, 8260, 165, 402, 167, 164, 39, 
      8220, 171, 8249, 8250, 64257, 64258, 0, 8211, 8224, 8225, 
      183, 0, 182, 8226, 8218, 8222, 8221, 187, 8230, 8240, 
      0, 191, 0, 96, 180, 710, 732, 175, 728, 729, 
      168, 0, 730, 184, 0, 733, 731, 711, 8212, 0, 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 0, 198, 0, 170, 0, 0, 
      0, 0, 321, 216, 338, 186, 0, 0, 0, 0, 
      0, 230, 0, 0, 0, 305, 0, 0, 322, 248, 
      339, 223, 0, 0, 0, 0 };
  
  DocumentFont(PRIndirectReference refFont) {
    this.encoding = "";
    this.fontSpecific = false;
    this.refFont = refFont;
    this.fontType = 4;
    this.font = (PdfDictionary)PdfReader.getPdfObject(refFont);
    PdfName baseFont = this.font.getAsName(PdfName.BASEFONT);
    this.fontName = (baseFont != null) ? PdfName.decodeName(baseFont.toString()) : "Unspecified Font Name";
    PdfName subType = this.font.getAsName(PdfName.SUBTYPE);
    if (PdfName.TYPE1.equals(subType) || PdfName.TRUETYPE.equals(subType)) {
      doType1TT();
    } else {
      for (int k = 0; k < cjkNames.length; k++) {
        if (this.fontName.startsWith(cjkNames[k])) {
          this.fontName = cjkNames[k];
          try {
            this.cjkMirror = BaseFont.createFont(this.fontName, cjkEncs[k], false);
          } catch (Exception e) {
            throw new ExceptionConverter(e);
          } 
          return;
        } 
      } 
      PdfName encodingName = this.font.getAsName(PdfName.ENCODING);
      if (encodingName != null) {
        String enc = PdfName.decodeName(encodingName.toString());
        for (int i = 0; i < cjkEncs2.length; i++) {
          if (enc.startsWith(cjkEncs2[i])) {
            try {
              if (i > 3)
                i -= 4; 
              this.cjkMirror = BaseFont.createFont(cjkNames2[i], cjkEncs2[i], false);
            } catch (Exception e) {
              throw new ExceptionConverter(e);
            } 
            return;
          } 
        } 
        if (PdfName.TYPE0.equals(subType) && enc.equals("Identity-H")) {
          processType0(this.font);
          this.isType0 = true;
        } 
      } 
    } 
  }
  
  private void processType0(PdfDictionary font) {
    try {
      PdfObject toUniObject = PdfReader.getPdfObjectRelease(font.get(PdfName.TOUNICODE));
      PdfArray df = (PdfArray)PdfReader.getPdfObjectRelease(font.get(PdfName.DESCENDANTFONTS));
      PdfDictionary cidft = (PdfDictionary)PdfReader.getPdfObjectRelease(df.getPdfObject(0));
      PdfNumber dwo = (PdfNumber)PdfReader.getPdfObjectRelease(cidft.get(PdfName.DW));
      int dw = 1000;
      if (dwo != null)
        dw = dwo.intValue(); 
      IntHashtable widths = readWidths((PdfArray)PdfReader.getPdfObjectRelease(cidft.get(PdfName.W)));
      PdfDictionary fontDesc = (PdfDictionary)PdfReader.getPdfObjectRelease(cidft.get(PdfName.FONTDESCRIPTOR));
      fillFontDesc(fontDesc);
      if (toUniObject instanceof PRStream)
        fillMetrics(PdfReader.getStreamBytes((PRStream)toUniObject), widths, dw); 
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  private IntHashtable readWidths(PdfArray ws) {
    IntHashtable hh = new IntHashtable();
    if (ws == null)
      return hh; 
    for (int k = 0; k < ws.size(); k++) {
      int c1 = ((PdfNumber)PdfReader.getPdfObjectRelease(ws.getPdfObject(k))).intValue();
      PdfObject obj = PdfReader.getPdfObjectRelease(ws.getPdfObject(++k));
      if (obj.isArray()) {
        PdfArray a2 = (PdfArray)obj;
        for (int j = 0; j < a2.size(); j++) {
          int c2 = ((PdfNumber)PdfReader.getPdfObjectRelease(a2.getPdfObject(j))).intValue();
          hh.put(c1++, c2);
        } 
      } else {
        int c2 = ((PdfNumber)obj).intValue();
        int w = ((PdfNumber)PdfReader.getPdfObjectRelease(ws.getPdfObject(++k))).intValue();
        for (; c1 <= c2; c1++)
          hh.put(c1, w); 
      } 
    } 
    return hh;
  }
  
  private String decodeString(PdfString ps) {
    if (ps.isHexWriting())
      return PdfEncodings.convertToString(ps.getBytes(), "UnicodeBigUnmarked"); 
    return ps.toUnicodeString();
  }
  
  private void fillMetrics(byte[] touni, IntHashtable widths, int dw) {
    try {
      PdfContentParser ps = new PdfContentParser(new PRTokeniser(touni));
      PdfObject ob = null;
      boolean notFound = true;
      int nestLevel = 0;
      label62: while ((notFound || nestLevel > 0) && (ob = ps.readPRObject()) != null) {
        if (ob.type() == 200) {
          if (ob.toString().equals("begin")) {
            notFound = false;
            nestLevel++;
            continue;
          } 
          if (ob.toString().equals("end")) {
            nestLevel--;
            continue;
          } 
          if (ob.toString().equals("beginbfchar"))
            while (true) {
              PdfObject nx = ps.readPRObject();
              if (nx.toString().equals("endbfchar"))
                continue label62; 
              String cid = decodeString((PdfString)nx);
              String uni = decodeString((PdfString)ps.readPRObject());
              if (uni.length() == 1) {
                int cidc = cid.charAt(0);
                int unic = uni.charAt(uni.length() - 1);
                int w = dw;
                if (widths.containsKey(cidc))
                  w = widths.get(cidc); 
                this.metrics.put(Integer.valueOf(unic), new int[] { cidc, w });
              } 
            }  
          if (ob.toString().equals("beginbfrange"))
            while (true) {
              PdfObject nx = ps.readPRObject();
              if (nx.toString().equals("endbfrange"))
                continue label62; 
              String cid1 = decodeString((PdfString)nx);
              String cid2 = decodeString((PdfString)ps.readPRObject());
              int cid1c = cid1.charAt(0);
              int cid2c = cid2.charAt(0);
              PdfObject ob2 = ps.readPRObject();
              if (ob2.isString()) {
                String uni = decodeString((PdfString)ob2);
                if (uni.length() == 1) {
                  int unic = uni.charAt(uni.length() - 1);
                  for (; cid1c <= cid2c; cid1c++, unic++) {
                    int w = dw;
                    if (widths.containsKey(cid1c))
                      w = widths.get(cid1c); 
                    this.metrics.put(Integer.valueOf(unic), new int[] { cid1c, w });
                  } 
                } 
                continue;
              } 
              PdfArray a = (PdfArray)ob2;
              for (int j = 0; j < a.size(); j++, cid1c++) {
                String uni = decodeString(a.getAsString(j));
                if (uni.length() == 1) {
                  int unic = uni.charAt(uni.length() - 1);
                  int w = dw;
                  if (widths.containsKey(cid1c))
                    w = widths.get(cid1c); 
                  this.metrics.put(Integer.valueOf(unic), new int[] { cid1c, w });
                } 
              } 
            }  
        } 
      } 
    } catch (Exception e) {
      throw new ExceptionConverter(e);
    } 
  }
  
  private void doType1TT() {
    PdfObject enc = PdfReader.getPdfObject(this.font.get(PdfName.ENCODING));
    if (enc == null) {
      fillEncoding((PdfName)null);
    } else if (enc.isName()) {
      fillEncoding((PdfName)enc);
    } else {
      PdfDictionary encDic = (PdfDictionary)enc;
      enc = PdfReader.getPdfObject(encDic.get(PdfName.BASEENCODING));
      if (enc == null) {
        fillEncoding((PdfName)null);
      } else {
        fillEncoding((PdfName)enc);
      } 
      PdfArray diffs = encDic.getAsArray(PdfName.DIFFERENCES);
      if (diffs != null) {
        CMap toUnicode = null;
        this.diffmap = new IntHashtable();
        int currentNumber = 0;
        for (int k = 0; k < diffs.size(); k++) {
          PdfObject obj = diffs.getPdfObject(k);
          if (obj.isNumber()) {
            currentNumber = ((PdfNumber)obj).intValue();
          } else {
            int[] c = GlyphList.nameToUnicode(PdfName.decodeName(((PdfName)obj).toString()));
            if (c != null && c.length > 0) {
              this.uni2byte.put(c[0], currentNumber);
              this.diffmap.put(c[0], currentNumber);
            } else {
              if (toUnicode == null) {
                toUnicode = processToUnicode();
                if (toUnicode == null)
                  toUnicode = new CMap(); 
              } 
              String unicode = toUnicode.lookup(new byte[] { (byte)currentNumber }, 0, 1);
              if (unicode != null && unicode.length() == 1) {
                this.uni2byte.put(unicode.charAt(0), currentNumber);
                this.diffmap.put(unicode.charAt(0), currentNumber);
              } 
            } 
            currentNumber++;
          } 
        } 
      } 
    } 
    PdfArray newWidths = this.font.getAsArray(PdfName.WIDTHS);
    PdfNumber first = this.font.getAsNumber(PdfName.FIRSTCHAR);
    PdfNumber last = this.font.getAsNumber(PdfName.LASTCHAR);
    if (BuiltinFonts14.containsKey(this.fontName)) {
      BaseFont bf;
      try {
        bf = BaseFont.createFont(this.fontName, "Cp1252", false);
      } catch (Exception exception) {
        throw new ExceptionConverter(exception);
      } 
      int[] e = this.uni2byte.toOrderedKeys();
      int k;
      for (k = 0; k < e.length; k++) {
        int n = this.uni2byte.get(e[k]);
        this.widths[n] = bf.getRawWidth(n, GlyphList.unicodeToName(e[k]));
      } 
      if (this.diffmap != null) {
        e = this.diffmap.toOrderedKeys();
        for (k = 0; k < e.length; k++) {
          int n = this.diffmap.get(e[k]);
          this.widths[n] = bf.getRawWidth(n, GlyphList.unicodeToName(e[k]));
        } 
        this.diffmap = null;
      } 
      this.Ascender = bf.getFontDescriptor(1, 1000.0F);
      this.CapHeight = bf.getFontDescriptor(2, 1000.0F);
      this.Descender = bf.getFontDescriptor(3, 1000.0F);
      this.ItalicAngle = bf.getFontDescriptor(4, 1000.0F);
      this.llx = bf.getFontDescriptor(5, 1000.0F);
      this.lly = bf.getFontDescriptor(6, 1000.0F);
      this.urx = bf.getFontDescriptor(7, 1000.0F);
      this.ury = bf.getFontDescriptor(8, 1000.0F);
    } 
    if (first != null && last != null && newWidths != null) {
      int f = first.intValue();
      for (int k = 0; k < newWidths.size(); k++)
        this.widths[f + k] = newWidths.getAsNumber(k).intValue(); 
    } 
    fillFontDesc(this.font.getAsDict(PdfName.FONTDESCRIPTOR));
  }
  
  private CMap processToUnicode() {
    CMap cmapRet = null;
    PdfObject toUni = PdfReader.getPdfObjectRelease(this.font.get(PdfName.TOUNICODE));
    if (toUni instanceof PRStream)
      try {
        byte[] touni = PdfReader.getStreamBytes((PRStream)toUni);
        CMapParser cmapParser = new CMapParser();
        cmapRet = cmapParser.parse(new ByteArrayInputStream(touni));
      } catch (Exception e) {} 
    return cmapRet;
  }
  
  private void fillFontDesc(PdfDictionary fontDesc) {
    if (fontDesc == null)
      return; 
    PdfNumber v = fontDesc.getAsNumber(PdfName.ASCENT);
    if (v != null)
      this.Ascender = v.floatValue(); 
    v = fontDesc.getAsNumber(PdfName.CAPHEIGHT);
    if (v != null)
      this.CapHeight = v.floatValue(); 
    v = fontDesc.getAsNumber(PdfName.DESCENT);
    if (v != null)
      this.Descender = v.floatValue(); 
    v = fontDesc.getAsNumber(PdfName.ITALICANGLE);
    if (v != null)
      this.ItalicAngle = v.floatValue(); 
    PdfArray bbox = fontDesc.getAsArray(PdfName.FONTBBOX);
    if (bbox != null) {
      this.llx = bbox.getAsNumber(0).floatValue();
      this.lly = bbox.getAsNumber(1).floatValue();
      this.urx = bbox.getAsNumber(2).floatValue();
      this.ury = bbox.getAsNumber(3).floatValue();
      if (this.llx > this.urx) {
        float t = this.llx;
        this.llx = this.urx;
        this.urx = t;
      } 
      if (this.lly > this.ury) {
        float t = this.lly;
        this.lly = this.ury;
        this.ury = t;
      } 
    } 
  }
  
  private void fillEncoding(PdfName encoding) {
    if (PdfName.MAC_ROMAN_ENCODING.equals(encoding) || PdfName.WIN_ANSI_ENCODING.equals(encoding)) {
      byte[] b = new byte[256];
      for (int k = 0; k < 256; k++)
        b[k] = (byte)k; 
      String enc = "Cp1252";
      if (PdfName.MAC_ROMAN_ENCODING.equals(encoding))
        enc = "MacRoman"; 
      String cv = PdfEncodings.convertToString(b, enc);
      char[] arr = cv.toCharArray();
      for (int i = 0; i < 256; i++)
        this.uni2byte.put(arr[i], i); 
    } else {
      for (int k = 0; k < 256; k++)
        this.uni2byte.put(stdEnc[k], k); 
    } 
  }
  
  public String[][] getFamilyFontName() {
    return getFullFontName();
  }
  
  public float getFontDescriptor(int key, float fontSize) {
    if (this.cjkMirror != null)
      return this.cjkMirror.getFontDescriptor(key, fontSize); 
    switch (key) {
      case 1:
      case 9:
        return this.Ascender * fontSize / 1000.0F;
      case 2:
        return this.CapHeight * fontSize / 1000.0F;
      case 3:
      case 10:
        return this.Descender * fontSize / 1000.0F;
      case 4:
        return this.ItalicAngle;
      case 5:
        return this.llx * fontSize / 1000.0F;
      case 6:
        return this.lly * fontSize / 1000.0F;
      case 7:
        return this.urx * fontSize / 1000.0F;
      case 8:
        return this.ury * fontSize / 1000.0F;
      case 11:
        return 0.0F;
      case 12:
        return (this.urx - this.llx) * fontSize / 1000.0F;
    } 
    return 0.0F;
  }
  
  public String[][] getFullFontName() {
    return new String[][] { { "", "", "", this.fontName } };
  }
  
  public String[][] getAllNameEntries() {
    return new String[][] { { "4", "", "", "", this.fontName } };
  }
  
  public int getKerning(int char1, int char2) {
    return 0;
  }
  
  public String getPostscriptFontName() {
    return this.fontName;
  }
  
  int getRawWidth(int c, String name) {
    return 0;
  }
  
  public boolean hasKernPairs() {
    return false;
  }
  
  void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException, IOException {}
  
  public PdfStream getFullFontStream() {
    return null;
  }
  
  public int getWidth(int char1) {
    if (this.cjkMirror != null)
      return this.cjkMirror.getWidth(char1); 
    if (this.isType0) {
      int[] ws = this.metrics.get(Integer.valueOf(char1));
      if (ws != null)
        return ws[1]; 
      return 0;
    } 
    return super.getWidth(char1);
  }
  
  public int getWidth(String text) {
    if (this.cjkMirror != null)
      return this.cjkMirror.getWidth(text); 
    if (this.isType0) {
      char[] chars = text.toCharArray();
      int len = chars.length;
      int total = 0;
      for (int k = 0; k < len; k++) {
        int[] ws = this.metrics.get(Integer.valueOf(chars[k]));
        if (ws != null)
          total += ws[1]; 
      } 
      return total;
    } 
    return super.getWidth(text);
  }
  
  byte[] convertToBytes(String text) {
    if (this.cjkMirror != null)
      return PdfEncodings.convertToBytes(text, "UnicodeBigUnmarked"); 
    if (this.isType0) {
      char[] chars = text.toCharArray();
      int len = chars.length;
      byte[] arrayOfByte1 = new byte[len * 2];
      int bptr = 0;
      for (int i = 0; i < len; i++) {
        int[] ws = this.metrics.get(Integer.valueOf(chars[i]));
        if (ws != null) {
          int g = ws[0];
          arrayOfByte1[bptr++] = (byte)(g / 256);
          arrayOfByte1[bptr++] = (byte)g;
        } 
      } 
      if (bptr == arrayOfByte1.length)
        return arrayOfByte1; 
      byte[] nb = new byte[bptr];
      System.arraycopy(arrayOfByte1, 0, nb, 0, bptr);
      return nb;
    } 
    char[] cc = text.toCharArray();
    byte[] b = new byte[cc.length];
    int ptr = 0;
    for (int k = 0; k < cc.length; k++) {
      if (this.uni2byte.containsKey(cc[k]))
        b[ptr++] = (byte)this.uni2byte.get(cc[k]); 
    } 
    if (ptr == b.length)
      return b; 
    byte[] b2 = new byte[ptr];
    System.arraycopy(b, 0, b2, 0, ptr);
    return b2;
  }
  
  byte[] convertToBytes(int char1) {
    if (this.cjkMirror != null)
      return PdfEncodings.convertToBytes((char)char1, "UnicodeBigUnmarked"); 
    if (this.isType0) {
      int[] ws = this.metrics.get(Integer.valueOf(char1));
      if (ws != null) {
        int g = ws[0];
        return new byte[] { (byte)(g / 256), (byte)g };
      } 
      return new byte[0];
    } 
    if (this.uni2byte.containsKey(char1))
      return new byte[] { (byte)this.uni2byte.get(char1) }; 
    return new byte[0];
  }
  
  PdfIndirectReference getIndirectReference() {
    return this.refFont;
  }
  
  public boolean charExists(int c) {
    if (this.cjkMirror != null)
      return this.cjkMirror.charExists(c); 
    if (this.isType0)
      return this.metrics.containsKey(Integer.valueOf(c)); 
    return super.charExists(c);
  }
  
  public void setPostscriptFontName(String name) {}
  
  public boolean setKerning(int char1, int char2, int kern) {
    return false;
  }
  
  public int[] getCharBBox(int c) {
    return null;
  }
  
  protected int[] getRawCharBBox(int c, String name) {
    return null;
  }
  
  IntHashtable getUni2Byte() {
    return this.uni2byte;
  }
  
  IntHashtable getDiffmap() {
    return this.diffmap;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\DocumentFont.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */