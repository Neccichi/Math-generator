package com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfDeveloperExtension;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;

public interface PdfVersion {
  void setPdfVersion(char paramChar);
  
  void setAtLeastPdfVersion(char paramChar);
  
  void setPdfVersion(PdfName paramPdfName);
  
  void addDeveloperExtension(PdfDeveloperExtension paramPdfDeveloperExtension);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\interfaces\PdfVersion.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */