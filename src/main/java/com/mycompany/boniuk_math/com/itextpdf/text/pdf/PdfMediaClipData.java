package com.mycompany.boniuk_math.com.itextpdf.text.pdf;

import java.io.IOException;

public class PdfMediaClipData extends PdfDictionary {
  PdfMediaClipData(String file, PdfFileSpecification fs, String mimeType) throws IOException {
    put(PdfName.TYPE, new PdfName("MediaClip"));
    put(PdfName.S, new PdfName("MCD"));
    put(PdfName.N, new PdfString("Media clip for " + file));
    put(new PdfName("CT"), new PdfString(mimeType));
    PdfDictionary dic = new PdfDictionary();
    dic.put(new PdfName("TF"), new PdfString("TEMPACCESS"));
    put(new PdfName("P"), dic);
    put(PdfName.D, fs.getReference());
  }
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\PdfMediaClipData.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */