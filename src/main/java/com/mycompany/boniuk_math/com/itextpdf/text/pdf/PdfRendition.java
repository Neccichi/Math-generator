package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.io.IOException;

public class PdfRendition extends PdfDictionary {
  PdfRendition(String file, PdfFileSpecification fs, String mimeType) throws IOException {
    put(PdfName.S, new PdfName("MR"));
    put(PdfName.N, new PdfString("Rendition for " + file));
    put(PdfName.C, new PdfMediaClipData(file, fs, mimeType));
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfRendition.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */