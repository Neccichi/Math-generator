package com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfObject;

public interface PdfViewerPreferences {
  void setViewerPreferences(int paramInt);
  
  void addViewerPreference(PdfName paramPdfName, PdfObject paramPdfObject);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\interfaces\PdfViewerPreferences.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */