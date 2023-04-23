package com.mycompany.boniuk_math.com.itextpdf.text.pdf.parser;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDictionary;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfReader;
import java.io.IOException;

public class PdfReaderContentParser {
  private final PdfReader reader;
  
  public PdfReaderContentParser(PdfReader reader) {
    this.reader = reader;
  }
  
  public <E extends RenderListener> E processContent(int pageNumber, E renderListener) throws IOException {
    PdfDictionary pageDic = this.reader.getPageN(pageNumber);
    PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
    PdfContentStreamProcessor processor = new PdfContentStreamProcessor((RenderListener)renderListener);
    processor.processContent(ContentByteUtils.getContentBytesForPage(this.reader, pageNumber), resourcesDic);
    return renderListener;
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\parser\PdfReaderContentParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */