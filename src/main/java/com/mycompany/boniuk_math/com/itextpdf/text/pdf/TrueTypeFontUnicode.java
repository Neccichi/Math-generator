package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.Utilities;
import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

class TrueTypeFontUnicode extends TrueTypeFont implements Comparator<int[]> {
  boolean vertical = false;
  
  TrueTypeFontUnicode(String ttFile, String enc, boolean emb, byte[] ttfAfm, boolean forceRead) throws DocumentException, IOException {
    String nameBase = getBaseName(ttFile);
    String ttcName = getTTCName(nameBase);
    if (nameBase.length() < ttFile.length())
      this.style = ttFile.substring(nameBase.length()); 
    this.encoding = enc;
    this.embedded = emb;
    this.fileName = ttcName;
    this.ttcIndex = "";
    if (ttcName.length() < nameBase.length())
      this.ttcIndex = nameBase.substring(ttcName.length() + 1); 
    this.fontType = 3;
    if ((this.fileName.toLowerCase().endsWith(".ttf") || this.fileName.toLowerCase().endsWith(".otf") || this.fileName.toLowerCase().endsWith(".ttc")) && (enc.equals("Identity-H") || enc.equals("Identity-V")) && emb) {
      process(ttfAfm, forceRead);
      if (this.os_2.fsType == 2)
        throw new DocumentException(MessageLocalization.getComposedMessage("1.cannot.be.embedded.due.to.licensing.restrictions", new Object[] { this.fileName + this.style })); 
      if ((this.cmap31 == null && !this.fontSpecific) || (this.cmap10 == null && this.fontSpecific))
        this.directTextToByte = true; 
      if (this.fontSpecific) {
        this.fontSpecific = false;
        String tempEncoding = this.encoding;
        this.encoding = "";
        createEncoding();
        this.encoding = tempEncoding;
        this.fontSpecific = true;
      } 
    } else {
      throw new DocumentException(MessageLocalization.getComposedMessage("1.2.is.not.a.ttf.font.file", new Object[] { this.fileName, this.style }));
    } 
    this.vertical = enc.endsWith("V");
  }
  
  public int getWidth(int char1) {
    if (this.vertical)
      return 1000; 
    if (this.fontSpecific) {
      if ((char1 & 0xFF00) == 0 || (char1 & 0xFF00) == 61440)
        return getRawWidth(char1 & 0xFF, (String)null); 
      return 0;
    } 
    return getRawWidth(char1, this.encoding);
  }
  
  public int getWidth(String text) {
    if (this.vertical)
      return text.length() * 1000; 
    int total = 0;
    if (this.fontSpecific) {
      char[] cc = text.toCharArray();
      int len = cc.length;
      for (int k = 0; k < len; k++) {
        char c = cc[k];
        if ((c & 0xFF00) == 0 || (c & 0xFF00) == 61440)
          total += getRawWidth(c & 0xFF, (String)null); 
      } 
    } else {
      int len = text.length();
      for (int k = 0; k < len; k++) {
        if (Utilities.isSurrogatePair(text, k)) {
          total += getRawWidth(Utilities.convertToUtf32(text, k), this.encoding);
          k++;
        } else {
          total += getRawWidth(text.charAt(k), this.encoding);
        } 
      } 
    } 
    return total;
  }
  
  private PdfStream getToUnicode(Object[] metrics) {
    if (metrics.length == 0)
      return null; 
    StringBuffer buf = new StringBuffer("/CIDInit /ProcSet findresource begin\n12 dict begin\nbegincmap\n/CIDSystemInfo\n<< /Registry (TTX+0)\n/Ordering (T42UV)\n/Supplement 0\n>> def\n/CMapName /TTX+0 def\n/CMapType 2 def\n1 begincodespacerange\n<0000><FFFF>\nendcodespacerange\n");
    int size = 0;
    for (int k = 0; k < metrics.length; k++) {
      if (size == 0) {
        if (k != 0)
          buf.append("endbfrange\n"); 
        size = Math.min(100, metrics.length - k);
        buf.append(size).append(" beginbfrange\n");
      } 
      size--;
      int[] metric = (int[])metrics[k];
      String fromTo = toHex(metric[0]);
      buf.append(fromTo).append(fromTo).append(toHex(metric[2])).append('\n');
    } 
    buf.append("endbfrange\nendcmap\nCMapName currentdict /CMap defineresource pop\nend end\n");
    String s = buf.toString();
    PdfStream stream = new PdfStream(PdfEncodings.convertToBytes(s, (String)null));
    stream.flateCompress(this.compressionLevel);
    return stream;
  }
  
  private static String toHex4(int n) {
    String s = "0000" + Integer.toHexString(n);
    return s.substring(s.length() - 4);
  }
  
  static String toHex(int n) {
    if (n < 65536)
      return "<" + toHex4(n) + ">"; 
    n -= 65536;
    int high = n / 1024 + 55296;
    int low = n % 1024 + 56320;
    return "[<" + toHex4(high) + toHex4(low) + ">]";
  }
  
  private PdfDictionary getCIDFontType2(PdfIndirectReference fontDescriptor, String subsetPrefix, Object[] metrics) {
    PdfDictionary dic = new PdfDictionary(PdfName.FONT);
    if (this.cff) {
      dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE0);
      dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
    } else {
      dic.put(PdfName.SUBTYPE, PdfName.CIDFONTTYPE2);
      dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName));
    } 
    dic.put(PdfName.FONTDESCRIPTOR, fontDescriptor);
    if (!this.cff)
      dic.put(PdfName.CIDTOGIDMAP, PdfName.IDENTITY); 
    PdfDictionary cdic = new PdfDictionary();
    cdic.put(PdfName.REGISTRY, new PdfString("Adobe"));
    cdic.put(PdfName.ORDERING, new PdfString("Identity"));
    cdic.put(PdfName.SUPPLEMENT, new PdfNumber(0));
    dic.put(PdfName.CIDSYSTEMINFO, cdic);
    if (!this.vertical) {
      dic.put(PdfName.DW, new PdfNumber(1000));
      StringBuffer buf = new StringBuffer("[");
      int lastNumber = -10;
      boolean firstTime = true;
      for (int k = 0; k < metrics.length; k++) {
        int[] metric = (int[])metrics[k];
        if (metric[1] != 1000) {
          int m = metric[0];
          if (m == lastNumber + 1) {
            buf.append(' ').append(metric[1]);
          } else {
            if (!firstTime)
              buf.append(']'); 
            firstTime = false;
            buf.append(m).append('[').append(metric[1]);
          } 
          lastNumber = m;
        } 
      } 
      if (buf.length() > 1) {
        buf.append("]]");
        dic.put(PdfName.W, new PdfLiteral(buf.toString()));
      } 
    } 
    return dic;
  }
  
  private PdfDictionary getFontBaseType(PdfIndirectReference descendant, String subsetPrefix, PdfIndirectReference toUnicode) {
    PdfDictionary dic = new PdfDictionary(PdfName.FONT);
    dic.put(PdfName.SUBTYPE, PdfName.TYPE0);
    if (this.cff) {
      dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName + "-" + this.encoding));
    } else {
      dic.put(PdfName.BASEFONT, new PdfName(subsetPrefix + this.fontName));
    } 
    dic.put(PdfName.ENCODING, new PdfName(this.encoding));
    dic.put(PdfName.DESCENDANTFONTS, new PdfArray(descendant));
    if (toUnicode != null)
      dic.put(PdfName.TOUNICODE, toUnicode); 
    return dic;
  }
  
  public int compare(int[] o1, int[] o2) {
    int m1 = o1[0];
    int m2 = o2[0];
    if (m1 < m2)
      return -1; 
    if (m1 == m2)
      return 0; 
    return 1;
  }
  
  private static final byte[] rotbits = new byte[] { Byte.MIN_VALUE, 64, 32, 16, 8, 4, 2, 1 };
  
  void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws DocumentException, IOException {
    HashMap<Integer, int[]> longTag = (HashMap<Integer, int[]>)params[0];
    addRangeUni(longTag, true, this.subset);
    int[][] metrics = (int[][])longTag.values().toArray((Object[])new int[0][]);
    Arrays.sort(metrics, this);
    PdfIndirectReference ind_font = null;
    PdfObject pobj = null;
    PdfIndirectObject obj = null;
    PdfIndirectReference cidset = null;
    if (writer.getPDFXConformance() == 3 || writer.getPDFXConformance() == 4) {
      PdfStream stream;
      if (metrics.length == 0) {
        stream = new PdfStream(new byte[] { Byte.MIN_VALUE });
      } else {
        int top = metrics[metrics.length - 1][0];
        byte[] bt = new byte[top / 8 + 1];
        for (int k = 0; k < metrics.length; k++) {
          int v = metrics[k][0];
          bt[v / 8] = (byte)(bt[v / 8] | rotbits[v % 8]);
        } 
        stream = new PdfStream(bt);
        stream.flateCompress(this.compressionLevel);
      } 
      cidset = writer.addToBody(stream).getIndirectReference();
    } 
    if (this.cff) {
      byte[] b = readCffFont();
      if (this.subset || this.subsetRanges != null) {
        CFFFontSubset cff = new CFFFontSubset(new RandomAccessFileOrArray(b), longTag);
        b = cff.Process(cff.getNames()[0]);
      } 
      pobj = new BaseFont.StreamFont(b, "CIDFontType0C", this.compressionLevel);
      obj = writer.addToBody(pobj);
      ind_font = obj.getIndirectReference();
    } else {
      byte[] b;
      if (this.subset || this.directoryOffset != 0) {
        TrueTypeFontSubSet sb = new TrueTypeFontSubSet(this.fileName, new RandomAccessFileOrArray(this.rf), new HashSet<Integer>(longTag.keySet()), this.directoryOffset, false, false);
        b = sb.process();
      } else {
        b = getFullFont();
      } 
      int[] lengths = { b.length };
      pobj = new BaseFont.StreamFont(b, lengths, this.compressionLevel);
      obj = writer.addToBody(pobj);
      ind_font = obj.getIndirectReference();
    } 
    String subsetPrefix = "";
    if (this.subset)
      subsetPrefix = createSubsetPrefix(); 
    PdfDictionary dic = getFontDescriptor(ind_font, subsetPrefix, cidset);
    obj = writer.addToBody(dic);
    ind_font = obj.getIndirectReference();
    pobj = getCIDFontType2(ind_font, subsetPrefix, (Object[])metrics);
    obj = writer.addToBody(pobj);
    ind_font = obj.getIndirectReference();
    pobj = getToUnicode((Object[])metrics);
    PdfIndirectReference toUnicodeRef = null;
    if (pobj != null) {
      obj = writer.addToBody(pobj);
      toUnicodeRef = obj.getIndirectReference();
    } 
    pobj = getFontBaseType(ind_font, subsetPrefix, toUnicodeRef);
    writer.addToBody(pobj, ref);
  }
  
  public PdfStream getFullFontStream() throws IOException, DocumentException {
    if (this.cff)
      return new BaseFont.StreamFont(readCffFont(), "CIDFontType0C", this.compressionLevel); 
    return super.getFullFontStream();
  }
  
  byte[] convertToBytes(String text) {
    return null;
  }
  
  byte[] convertToBytes(int char1) {
    return null;
  }
  
  public int[] getMetricsTT(int c) {
    if (this.cmapExt != null)
      return this.cmapExt.get(Integer.valueOf(c)); 
    HashMap<Integer, int[]> map = null;
    if (this.fontSpecific) {
      map = this.cmap10;
    } else {
      map = this.cmap31;
    } 
    if (map == null)
      return null; 
    if (this.fontSpecific) {
      if ((c & 0xFFFFFF00) == 0 || (c & 0xFFFFFF00) == 61440)
        return map.get(Integer.valueOf(c & 0xFF)); 
      return null;
    } 
    return map.get(Integer.valueOf(c));
  }
  
  public boolean charExists(int c) {
    return (getMetricsTT(c) != null);
  }
  
  public boolean setCharAdvance(int c, int advance) {
    int[] m = getMetricsTT(c);
    if (m == null)
      return false; 
    m[1] = advance;
    return true;
  }
  
  public int[] getCharBBox(int c) {
    if (this.bboxes == null)
      return null; 
    int[] m = getMetricsTT(c);
    if (m == null)
      return null; 
    return this.bboxes[m[0]];
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\TrueTypeFontUnicode.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */