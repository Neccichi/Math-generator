package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfReader;
import java.io.IOException;

public final class PdfTextExtractor {
  public static String getTextFromPage(PdfReader reader, int pageNumber, TextExtractionStrategy strategy) throws IOException {
    PdfReaderContentParser parser = new PdfReaderContentParser(reader);
    return ((TextExtractionStrategy)parser.<TextExtractionStrategy>processContent(pageNumber, strategy)).getResultantText();
  }
  
  public static String getTextFromPage(PdfReader reader, int pageNumber) throws IOException {
    return getTextFromPage(reader, pageNumber, new LocationTextExtractionStrategy());
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\PdfTextExtractor.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */