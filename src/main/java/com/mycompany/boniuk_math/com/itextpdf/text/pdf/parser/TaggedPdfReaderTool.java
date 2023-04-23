package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.error_messages.MessageLocalization;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfArray;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfNumber;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfReader;
import com.mycompany.boniuk_math.com.itextpdf.text.xml.XMLUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class TaggedPdfReaderTool {
  PdfReader reader;
  
  PrintWriter out;
  
  public void convertToXml(PdfReader reader, OutputStream os, String charset) throws IOException {
    this.reader = reader;
    OutputStreamWriter outs = new OutputStreamWriter(os, charset);
    this.out = new PrintWriter(outs);
    PdfDictionary catalog = reader.getCatalog();
    PdfDictionary struct = catalog.getAsDict(PdfName.STRUCTTREEROOT);
    if (struct == null)
      throw new IOException(MessageLocalization.getComposedMessage("no.structtreeroot.found", new Object[0])); 
    inspectChild(struct.getDirectObject(PdfName.K));
    this.out.flush();
    this.out.close();
  }
  
  public void convertToXml(PdfReader reader, OutputStream os) throws IOException {
    convertToXml(reader, os, Charset.defaultCharset().name());
  }
  
  public void inspectChild(PdfObject k) throws IOException {
    if (k == null)
      return; 
    if (k instanceof PdfArray) {
      inspectChildArray((PdfArray)k);
    } else if (k instanceof PdfDictionary) {
      inspectChildDictionary((PdfDictionary)k);
    } 
  }
  
  public void inspectChildArray(PdfArray k) throws IOException {
    if (k == null)
      return; 
    for (int i = 0; i < k.size(); i++)
      inspectChild(k.getDirectObject(i)); 
  }
  
  public void inspectChildDictionary(PdfDictionary k) throws IOException {
    if (k == null)
      return; 
    PdfName s = k.getAsName(PdfName.S);
    if (s != null) {
      String tagN = PdfName.decodeName(s.toString());
      String tag = fixTagName(tagN);
      this.out.print("<");
      this.out.print(tag);
      this.out.print(">");
      PdfDictionary dict = k.getAsDict(PdfName.PG);
      if (dict != null)
        parseTag(tagN, k.getDirectObject(PdfName.K), dict); 
      inspectChild(k.getDirectObject(PdfName.K));
      this.out.print("</");
      this.out.print(tag);
      this.out.println(">");
    } else {
      inspectChild(k.getDirectObject(PdfName.K));
    } 
  }
  
  private static String fixTagName(String tag) {
    StringBuilder sb = new StringBuilder();
    for (int k = 0; k < tag.length(); k++) {
      char c = tag.charAt(k);
      boolean nameStart = (c == ':' || (c >= 'A' && c <= 'Z') || c == '_' || (c >= 'a' && c <= 'z') || (c >= 'À' && c <= 'Ö') || (c >= 'Ø' && c <= 'ö') || (c >= 'ø' && c <= '˿') || (c >= 'Ͱ' && c <= 'ͽ') || (c >= 'Ϳ' && c <= '῿') || (c >= '‌' && c <= '‍') || (c >= '⁰' && c <= '↏') || (c >= 'Ⰰ' && c <= '⿯') || (c >= '、' && c <= '퟿') || (c >= '豈' && c <= '﷏') || (c >= 'ﷰ' && c <= '�'));
      boolean nameMiddle = (c == '-' || c == '.' || (c >= '0' && c <= '9') || c == '·' || (c >= '̀' && c <= 'ͯ') || (c >= '‿' && c <= '⁀') || nameStart);
      if (k == 0) {
        if (!nameStart)
          c = '_'; 
      } else if (!nameMiddle) {
        c = '-';
      } 
      sb.append(c);
    } 
    return sb.toString();
  }
  
  public void parseTag(String tag, PdfObject object, PdfDictionary page) throws IOException {
    if (object instanceof PdfNumber) {
      PdfNumber mcid = (PdfNumber)object;
      RenderFilter filter = new MarkedContentRenderFilter(mcid.intValue());
      TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
      FilteredTextRenderListener listener = new FilteredTextRenderListener(strategy, new RenderFilter[] { filter });
      PdfContentStreamProcessor processor = new PdfContentStreamProcessor(listener);
      processor.processContent(PdfReader.getPageContent(page), page.getAsDict(PdfName.RESOURCES));
      this.out.print(XMLUtil.escapeXML(listener.getResultantText(), true));
    } else if (object instanceof PdfArray) {
      PdfArray arr = (PdfArray)object;
      int n = arr.size();
      for (int i = 0; i < n; i++) {
        parseTag(tag, arr.getPdfObject(i), page);
        if (i < n - 1)
          this.out.println(); 
      } 
    } else if (object instanceof PdfDictionary) {
      PdfDictionary mcr = (PdfDictionary)object;
      parseTag(tag, mcr.getDirectObject(PdfName.MCID), mcr.getAsDict(PdfName.PG));
    } 
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\TaggedPdfReaderTool.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */