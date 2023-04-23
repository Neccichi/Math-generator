package com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAction;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;

public interface PdfDocumentActions {
  void setOpenAction(String paramString);
  
  void setOpenAction(PdfAction paramPdfAction);
  
  void setAdditionalAction(PdfName paramPdfName, PdfAction paramPdfAction) throws DocumentException;
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\interfaces\PdfDocumentActions.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */