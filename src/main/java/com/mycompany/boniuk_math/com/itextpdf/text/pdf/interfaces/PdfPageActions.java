package com.mycompany.boniuk_math.com.itextpdf.text.pdf.interfaces;

import com.mycompany.boniuk_math.com.itextpdf.text.DocumentException;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfAction;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfName;
import com.mycompany.boniuk_math.com.itextpdf.text.pdf.PdfTransition;

public interface PdfPageActions {
  void setPageAction(PdfName paramPdfName, PdfAction paramPdfAction) throws DocumentException;
  
  void setDuration(int paramInt);
  
  void setTransition(PdfTransition paramPdfTransition);
}


/* Location:              C:\Users\windo\Desktop\itextpdf-5.1.1.jar!\com\itextpdf\text\pdf\interfaces\PdfPageActions.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */