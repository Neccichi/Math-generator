package com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces;

import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAcroForm;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAnnotation;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfFormField;

public interface PdfAnnotations {
  PdfAcroForm getAcroForm();
  
  void addAnnotation(PdfAnnotation paramPdfAnnotation);
  
  void addCalculationOrder(PdfFormField paramPdfFormField);
  
  void setSigFlags(int paramInt);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\interfaces\PdfAnnotations.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */